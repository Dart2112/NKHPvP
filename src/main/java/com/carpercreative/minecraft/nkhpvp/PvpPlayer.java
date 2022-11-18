package com.carpercreative.minecraft.nkhpvp;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    public void loadKit() {
        //Load the team kit
        ItemStack[] kit = team.getTeamKit();
        //Check it isn't null, it returns null if it isn't set in the config
        if (kit == null)
            return;
        //Set the kit into the players inventory
        getBukkitPlayer().getInventory().setContents(kit);
    }

    public void clearInventory() {
        getBukkitPlayer().getInventory().clear();
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

    public Player getBukkitPlayer() {
        return bukkitPlayer;
    }

}
