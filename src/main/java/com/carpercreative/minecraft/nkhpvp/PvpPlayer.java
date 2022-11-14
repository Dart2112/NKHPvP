package com.carpercreative.minecraft.nkhpvp;

import org.bukkit.entity.Player;

public class PvpPlayer {

    private final Player bukkitPlayer;
    private int kills, deaths;
    private double damageDealt;
    private PvpTeam team;

    public PvpPlayer(Player player) {
        this.bukkitPlayer = player;
    }

    public PvpTeam getTeam() {
        return team;
    }

    public void setTeam(PvpTeam team) {
        this.team = team;
    }

    public void addKill() {
        kills++;
        team.incrementKills();
    }

    public void addDeath() {
        deaths++;
        team.incrementDeaths();
    }

    public void addDamageDealt(double damage) {
        this.damageDealt += damage;
        team.incrementDamageDealt(damage);
    }

    public void resetScores() {
        kills = 0;
        deaths = 0;
        damageDealt = 0;
    }

    public Player getBukkitPlayer() {
        return bukkitPlayer;
    }

}
