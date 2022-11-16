package com.carpercreative.minecraft.nkhpvp;

import net.lapismc.lapiscore.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Millisecond;

import java.util.*;

public class GameManager {

    private final NKHPvP plugin;
    private final List<PvpPlayer> allPlayers = new ArrayList<>();
    public PrettyTime prettyTime;
    public Random random = new Random();
    public PvpTeam deathEaters = new PvpTeam(Team.DEATH_EATER, this);
    public PvpTeam students = new PvpTeam(Team.STUDENT, this);
    private boolean isEnabled = false;
    private boolean isGameStarted = false;
    //Time remaining in seconds
    private long timeRemaining;
    private BukkitTask timerTask;
    private Location lobbyLocation;

    public GameManager(NKHPvP plugin) {
        this.plugin = plugin;
        prettyTime = new PrettyTime();
        prettyTime.removeUnit(JustNow.class);
        prettyTime.removeUnit(Millisecond.class);
        lobbyLocation = new LocationUtils().parseStringToLocation(plugin.getConfig().getString("Locations.Lobby"));
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
        Location l = Bukkit.getServer().getWorld("Ruined Hogwarts").getSpawnLocation();
        for (PvpPlayer p : allPlayers) {
            p.getBukkitPlayer().teleport(l);
            //TODO: Clear scoreboard stuffs
        }
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
        PvpPlayer p = new PvpPlayer(player);
        allPlayers.add(p);
        teleportToLobby(p);
        if (isGameStarted) {
            //TODO: Do we drop them into the game?
        } else {
            //TODO: Enable whatever scoreboards or anything that is needed
        }
    }

    public PvpPlayer getPlayer(UUID uuid) {
        for (PvpPlayer p : allPlayers) {
            if (p.getBukkitPlayer().getUniqueId() == uuid)
                return p;
        }
        return null;
    }

    public void removePlayer(Player player) {
        //TODO: add checks to make sure we actually had this player
        PvpPlayer p = getPlayer(player.getUniqueId());
        if (p != null) {
            allPlayers.remove(p);
            p.getTeam().removePlayer(p);
            //TODO: remove them from the team
        }
    }

    public void inductPlayerToTeam(PvpPlayer player) {
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
    }

    public void startGame() {
        //TODO: Maybe start a 5 second timer before the game starts
        //Reset team scores
        deathEaters.resetScores();
        students.resetScores();
        //TODO: Reset scoreboards or whatever
        for (PvpPlayer p : allPlayers) {
            p.resetScores();
            inductPlayerToTeam(p);
        }
        //Start timer
        timerTask = Bukkit.getScheduler().runTaskTimer(plugin, getTimerTickTask(), 20, 20);
        plugin.tasks.addTask(timerTask);
        isGameStarted = true;
        //TODO: Give players kits
    }

    public void endGame() {
        isGameStarted = false;
        //TODO: Report game results in some way (Possibly titles for immediate and boss bar for long term)
        //This needs to be done before we teleport and clear teams so that each team gets their own stats
        for (PvpPlayer p : allPlayers) {
            //This is mainly so players who are currently dead respawn in the correct place
            p.setTeam(null);
            //Teleport players to lobby
            teleportToLobby(p);
        }
        //TODO: Clear kits
    }

    public Location getLobbyLocationVaried() {
        //How far from the lobby location will players teleport too (this is the diameter not radius)
        int variance = 20;
        return variedLocation(variance, lobbyLocation);
    }

    public void teleportToLobby(PvpPlayer p) {
        p.getBukkitPlayer().teleport(getLobbyLocationVaried());
    }

    public void setLobbySpawn(Location loc) {
        this.lobbyLocation = loc;
        plugin.getConfig().set("Locations.Lobby", new LocationUtils().parseLocationToString(loc));
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
    }

    public Runnable getTimerTickTask() {
        return () -> {
            //Clean up this task if the timer has reached 0
            if (timeRemaining <= 0) {
                endGame();
                timerTask.cancel();
            }
            //Decrement the timer
            timeRemaining--;
            //TODO: Update scoreboard or boss bar or whatever for the time
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
        return prettyTime.format(reduceDurationList(prettyTime.calculatePreciseDuration(new Date(epoch))));
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
