package com.carpercreative.minecraft.nkhpvp;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

enum Team {
    deatheater, hogwarts
}

public class PvpTeam {

    private final GameManager gm;
    private final Team team;
    private final List<PvpPlayer> players = new ArrayList<>();
    private int kills, deaths;
    private double damageDealt;
    private Location teamSpawn;

    public PvpTeam(Team team, GameManager gm) {
        this.team = team;
        this.gm = gm;
    }

    public void addPlayer(PvpPlayer p) {
        players.add(p);
        p.setTeam(this);
        //TODO: Scoreboard teams ect
        //Teleport to spawn area
        teleportToSpawn(p);
        //TODO: Tell the player which team they are on
    }

    public void removePlayer(PvpPlayer p) {
        players.remove(p);
        p.setTeam(null);
    }

    public Location getTeamSpawnVaried() {
        //How far from the spawn location will players teleport too (this is the diameter not radius)
        int variance = 10;
        return gm.variedLocation(variance, teamSpawn);
    }

    public void teleportToSpawn(PvpPlayer p) {
        p.getBukkitPlayer().teleport(getTeamSpawnVaried());
    }

    public List<PvpPlayer> getTeamPlayers() {
        return players;
    }

    public void incrementKills() {
        kills++;
    }

    public void incrementDeaths() {
        deaths++;
    }

    public void incrementDamageDealt(double damage) {
        damageDealt += damage;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public double getDamageDealt() {
        return damageDealt;
    }

    public void resetScores() {
        kills = 0;
        deaths = 0;
        damageDealt = 0;
    }

}
