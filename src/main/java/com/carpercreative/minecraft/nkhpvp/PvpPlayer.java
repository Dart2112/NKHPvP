package com.carpercreative.minecraft.nkhpvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.text.DecimalFormat;

public class PvpPlayer {

    private final NKHPvP plugin;
    private final Player bukkitPlayer;
    private int kills, deaths;
    private double damageDealt;
    private PvpTeam team;
    private final Scoreboard board;
    private final Objective boardObjective;

    public PvpPlayer(Player player, NKHPvP plugin) {
        this.plugin = plugin;
        this.bukkitPlayer = player;
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        boardObjective = board.registerNewObjective(player.getName(), "dummy",
                plugin.config.getMessage("Scoreboard.Title"));
        boardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
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

    public String getKdRatio() {
        if (getDeaths() == 0)
            return String.valueOf(getKills());
        DecimalFormat df = new DecimalFormat("#.##");
        double kd = ((double) getKills()) / getDeaths();
        return df.format(kd);
    }

    public double getDamageDealt() {
        return damageDealt;
    }

    public String getDamageDealtRounded() {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(getDamageDealt());
    }

    public void updateScoreboard() {
        if (getTeam() == null)
            return;
        // Students
        // Kills: x
        // Damage: x
        // --------
        // Death Eaters
        // Kills: x
        // Damage: x
        // --------
        // Username
        // Team: Students
        // Kills: x
        // KD Ratio: x.xx
        // Damage: x.xx
        Score studentsTitle = boardObjective.getScore(ChatColor.RED + "Students");
        studentsTitle.setScore(13);
        Score studentKills = boardObjective.getScore(plugin.config.getMessage("Scoreboard.Kills")
                + plugin.gameManager.students.getKills() + " ");
        studentKills.setScore(12);
        Score studentDamage = boardObjective.getScore(plugin.config.getMessage("Scoreboard.Damage")
                + plugin.gameManager.students.getDamageDealtRounded() + " ");
        studentDamage.setScore(11);
        Score spacer1 = boardObjective.getScore(plugin.config.getMessage("Scoreboard.Spacer"));
        spacer1.setScore(10);
        Score deathEatersTitle = boardObjective.getScore(ChatColor.BLUE + "Death Eaters");
        deathEatersTitle.setScore(9);
        Score deathEatersKills = boardObjective.getScore(plugin.config.getMessage("Scoreboard.Kills")
                + plugin.gameManager.deathEaters.getKills() + "  ");
        deathEatersKills.setScore(8);
        Score deathEatersDamage = boardObjective.getScore(plugin.config.getMessage("Scoreboard.Damage")
                + plugin.gameManager.deathEaters.getDamageDealtRounded() + "  ");
        deathEatersDamage.setScore(7);
        Score spacer2 = boardObjective.getScore(plugin.config.getMessage("Scoreboard.Spacer") + " ");
        spacer2.setScore(6);
        Score username = boardObjective.getScore(ChatColor.GOLD + getBukkitPlayer().getName());
        username.setScore(5);
        Score teamName = boardObjective.getScore(plugin.config.getMessage("Scoreboard.Team") + getTeam().getNiceTeamName());
        teamName.setScore(4);
        Score personalKills = boardObjective.getScore(plugin.config.getMessage("Scoreboard.Kills")
                + getKills());
        personalKills.setScore(3);
        Score personalKD = boardObjective.getScore(plugin.config.getMessage("Scoreboard.KD")
                + getKdRatio());
        personalKD.setScore(2);
        Score personalDamage = boardObjective.getScore(plugin.config.getMessage("Scoreboard.Damage")
                + getDamageDealtRounded());
        personalDamage.setScore(1);

        getBukkitPlayer().setScoreboard(board);
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
