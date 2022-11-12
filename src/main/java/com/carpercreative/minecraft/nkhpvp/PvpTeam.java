package com.carpercreative.minecraft.nkhpvp;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

enum Team {
    deatheater, hogwarts
}

public class PvpTeam {

    GameManager gm;
    Team team;
    int kills, deaths;
    double damageDealt;
    Location teamSpawn;
    List<PvpPlayer> players = new ArrayList<>();

    public PvpTeam(Team team, GameManager gm) {
        this.team = team;
        this.gm = gm;
    }

    public void addPlayer(PvpPlayer p) {
        players.add(p);
        p.setTeam(this);
        //Teleport to spawn area
        teleportToSpawn(p);
        //TODO: Tell the player which team they are on
    }

    public void teleportToSpawn(PvpPlayer p) {
        //How far from the spawn location will players teleport too (this is the diameter not radius)
        int variance = 10;
        p.getBukkitPlayer().teleport(gm.variedLocation(variance, teamSpawn));
    }

    public void resetScores() {
        kills = 0;
        deaths = 0;
        damageDealt = 0;
    }

}
