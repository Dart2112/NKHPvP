package com.carpercreative.minecraft.nkhpvp;

import net.lapismc.lapiscore.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Millisecond;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameManager {

    private final NKHPvP plugin;
    private final List<PvpPlayer> allPlayers = new ArrayList<>();
    public PrettyTime prettyTime;
    public Random random = new Random();
    private final BossBar progressBar;
    public PvpTeam deathEaters;
    private boolean isEnabled = false;
    private boolean isGameStarted = false;
    //Time remaining in seconds
    private long timeRemaining;
    public PvpTeam students;
    private long gameLength;
    private BukkitTask timerTask;
    private Location lobbyLocation;
    private BukkitTask countdownTask;

    public GameManager(NKHPvP plugin) {
        this.plugin = plugin;
        prettyTime = new PrettyTime();
        prettyTime.removeUnit(JustNow.class);
        prettyTime.removeUnit(Millisecond.class);
        students = new PvpTeam(Team.STUDENT, plugin, this);
        deathEaters = new PvpTeam(Team.DEATH_EATER, plugin, this);
        lobbyLocation = new LocationUtils().parseStringToLocation(plugin.getConfig().getString("Locations.Lobby"));
        progressBar = Bukkit.createBossBar("Time Remaining: ", BarColor.BLUE, BarStyle.SOLID);
        progressBar.setVisible(false);
    }

    public void enable() {
        isEnabled = true;
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            //Only grab players who are in adventure or survival mode
            if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
                addPlayer(p);
            }
        }
    }

    public void disable() {
        isEnabled = false;
        isGameStarted = false;
        for (PvpPlayer p : allPlayers) {
            p.getBukkitPlayer().teleport(getLobbyLocationVaried());
            p.getBukkitPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
        progressBar.setVisible(false);
        allPlayers.clear();
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public void addPlayer(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        PvpPlayer p;
        if (getPlayer(player.getUniqueId()) == null) {
            p = new PvpPlayer(player, plugin);
            allPlayers.add(p);
        } else {
            p = getPlayer(player.getUniqueId());
        }
        teleportToLobby(p);
        if (isGameStarted) {
            inductPlayerToTeam(p);
            player.sendMessage(plugin.config.getMessage("PlayerAddedDuringGame"));
        } else {
            player.sendMessage(plugin.config.getMessage("PlayerAdded"));
        }
    }

    public PvpPlayer getPlayer(UUID uuid) {
        for (PvpPlayer p : allPlayers) {
            if (p.getBukkitPlayer().getUniqueId() == uuid)
                return p;
        }
        return null;
    }

    public List<PvpPlayer> getAllPlayers() {
        return allPlayers;
    }

    public void removePlayer(Player player) {
        PvpPlayer p = getPlayer(player.getUniqueId());
        if (p != null) {
            if (isGameStarted()) {
                player.getInventory().clear();
                progressBar.removePlayer(player);
            }
            allPlayers.remove(p);
            //Clear our scoreboard
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            if (p.getTeam() != null)
                p.getTeam().removePlayer(p);
            player.sendMessage(plugin.config.getMessage("PlayerRemoved"));
        }
    }

    public void inductPlayerToTeam(PvpPlayer player) {
        if (player.getBukkitPlayer().getName().equalsIgnoreCase("ImpulseSV")) {
            students.addPlayer(player);
            return;
        }
        if (player.getBukkitPlayer().getName().equalsIgnoreCase("Skizzleman")) {
            deathEaters.addPlayer(player);
            return;
        }
        //Add players to the smaller team
        if (students.getTeamPlayers().size() < deathEaters.getTeamPlayers().size()) {
            students.addPlayer(player);
        } else if (deathEaters.getTeamPlayers().size() < students.getTeamPlayers().size()) {
            deathEaters.addPlayer(player);
        } else {
            //If both teams are equal size, place on a random team
            if (random.nextBoolean()) {
                deathEaters.addPlayer(player);
            } else {
                students.addPlayer(player);
            }
        }
        progressBar.addPlayer(player.getBukkitPlayer());
    }

    public void triggerStart() {
        AtomicInteger i = new AtomicInteger(5);
        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (i.get() == 0) {
                startGame();
            } else {
                for (PvpPlayer p : allPlayers) {
                    p.getBukkitPlayer().sendTitle(plugin.config.getMessage("Start.Countdown") + i, "", 2, 16, 2);
                }
                i.getAndDecrement();
            }
        }, 20, 20);
    }

    public void startGame() {
        countdownTask.cancel();
        //Reset team scores
        deathEaters.resetScores();
        students.resetScores();
        for (PvpPlayer p : allPlayers) {
            p.resetScores();
            inductPlayerToTeam(p);
            p.updateScoreboard();
        }
        //Start timer
        timerTask = Bukkit.getScheduler().runTaskTimer(plugin, getTimerTickTask(), 20, 20);
        plugin.tasks.addTask(timerTask);
        isGameStarted = true;
        progressBar.setVisible(true);
    }

    public void endGame() {
        isGameStarted = false;
        String titleMsg = plugin.config.getMessage("End.Winner");
        PvpTeam winner;
        //If both teams have the same number of kills we refer to damage
        if (students.getKills() == deathEaters.getKills()) {
            winner = students.getDamageDealt() > deathEaters.getDamageDealt() ? students : deathEaters;
        } else {
            winner = students.getKills() > deathEaters.getKills() ? students : deathEaters;
        }
        PvpTeam loser = winner == students ? deathEaters : students;
        titleMsg = titleMsg.replace("[TEAM_NAME]", winner.getNiceTeamName());
        titleMsg = titleMsg.replace("[SCORE]", winner.getKills() + " - " + loser.getKills());
        //This needs to be done before we teleport and clear teams so that each team gets their own stats
        for (PvpPlayer p : allPlayers) {
            //Safety check in case a player has joined but not been assigned a team when the game ends
            if (p.getTeam() == null)
                continue;
            //This is mainly so players who are currently dead respawn in the correct place
            p.setTeam(null);
            //Teleport players to lobby
            teleportToLobby(p);
            //Send title to players declaring winner
            p.getBukkitPlayer().sendTitle(titleMsg, "", 5, 10 * 20, 5);
            //Clear inventory
            p.getBukkitPlayer().getInventory().clear();
        }
        progressBar.setVisible(false);
    }

    public Location getLobbyLocationVaried() {
        //How far from the lobby location will players teleport too (this is the diameter not radius)
        int variance = 5;
        return variedLocation(variance, lobbyLocation);
    }

    public void teleportToLobby(PvpPlayer p) {
        p.getBukkitPlayer().teleport(getLobbyLocationVaried());
    }

    public void setLobbySpawn(Location loc) {
        this.lobbyLocation = loc;
        plugin.getConfig().set("Locations.Lobby", new LocationUtils().parseLocationToString(loc));
        plugin.saveConfig();
    }

    /**
     * Applies randomness to a location to stop players spawning/teleporting on top of each other
     *
     * @param variance The diameter from loc that the location is allowed to deviate by
     * @param loc      The center of the circle that the new location can be in
     * @return a location with random variance applied to it
     */
    public Location variedLocation(int variance, Location loc) {
        Random r = random;
        //Apply a slight random adjustment to x and z values to spread the players out
        return loc.add(new Vector(r.nextInt(variance) - variance / 2, 0, r.nextInt(variance) - variance / 2));
    }

    public void setTimer(int minutes) {
        timeRemaining = minutes * 60L;
        gameLength = minutes * 60L;
    }

    public void setTimerToZero() {
        timeRemaining = 0L;
    }

    public Runnable getTimerTickTask() {
        AtomicInteger i = new AtomicInteger(10);
        return () -> {
            //Clean up this task if the timer has reached 0
            if (timeRemaining <= 0) {
                endGame();
                timerTask.cancel();
            }
            //Decrement the timer
            timeRemaining--;
            double progress = Double.max(0, (float) timeRemaining / gameLength);
            progressBar.setProgress(progress);
            progressBar.setTitle("Time Remaining: " + getTimeDifference(System.currentTimeMillis() + (timeRemaining * 1000)));
            //Update all players scoreboards every 10 seconds
            if (i.get() > 0) {
                i.getAndDecrement();
            } else {
                i.set(10);
                for (PvpPlayer player : allPlayers)
                    player.updateScoreboard();
            }
        };
    }

    /**
     * Get the time difference between now and the time given in the epoch parameter as a human-readable string
     * <p>
     * e.g. 1 minute and 30 seconds
     *
     * @param epoch The time to calculate the difference to
     * @return A human-readable time difference
     */
    public String getTimeDifference(Long epoch) {
        return prettyTime.formatDuration(reduceDurationList(prettyTime.calculatePreciseDuration(new Date(epoch))));
    }

    /**
     * Reduces a duration list to the largest two time units
     *
     * @param durationList The list of durations to reduce
     * @return A list containing only the 2 largest durations
     */
    public List<Duration> reduceDurationList(List<Duration> durationList) {
        while (durationList.size() > 2) {
            Duration smallest = null;
            for (Duration current : durationList) {
                if (smallest == null || smallest.getUnit().getMillisPerUnit() > current.getUnit().getMillisPerUnit()) {
                    smallest = current;
                }
            }
            durationList.remove(smallest);
        }
        return durationList;
    }

}
