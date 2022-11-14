package com.carpercreative.minecraft.nkhpvp;

import org.bukkit.Bukkit;
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
    public PvpTeam deatheaters = new PvpTeam(Team.deatheater, this);
    public PvpTeam hogwarts = new PvpTeam(Team.hogwarts, this);
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
    }

    public void addPlayer(Player player) {
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
        PvpPlayer p = getPlayer(player.getUniqueId());
        if (p != null) {
            allPlayers.remove(p);
            p.getTeam().removePlayer(p);
            //TODO: remove them from the team
        }
    }

    public void inductPlayerToTeam(PvpPlayer player) {
        //Add players to the smaller team
        if (hogwarts.getTeamPlayers().size() < deatheaters.getTeamPlayers().size()) {
            hogwarts.addPlayer(player);
        } else if (deatheaters.getTeamPlayers().size() < hogwarts.getTeamPlayers().size()) {
            deatheaters.addPlayer(player);
        } else {
            //If both teams are equal size, place on a random team
            if (random.nextBoolean()) {
                deatheaters.addPlayer(player);
            } else {
                hogwarts.addPlayer(player);
            }
        }
    }

    public void startGame() {
        //Reset team scores
        deatheaters.resetScores();
        hogwarts.resetScores();
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
