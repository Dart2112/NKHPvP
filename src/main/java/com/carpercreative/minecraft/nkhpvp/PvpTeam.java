package com.carpercreative.minecraft.nkhpvp;

import net.lapismc.lapiscore.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PvpTeam {

    private final NKHPvP plugin;
    private final GameManager gm;
    private final Team team;
    private final List<PvpPlayer> players = new ArrayList<>();
    private int kills, deaths;
    private double damageDealt;
    private Location teamSpawn;

    public PvpTeam(Team team, NKHPvP plugin, GameManager gm) {
        this.plugin = plugin;
        this.team = team;
        this.gm = gm;
        teamSpawn = new LocationUtils().parseStringToLocation
                (plugin.getConfig().getString("Locations." + getNiceTeamName()));
    }

    public void addPlayer(PvpPlayer p) {
        //Register the player with this team
        players.add(p);
        p.setTeam(this);
        //TODO: Scoreboard teams ect
        //Teleport to spawn area
        teleportToSpawn(p);
        //Heal the player
        p.getBukkitPlayer().setHealth(p.getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        //Set inventory to team kit
        p.loadKit();
        //Tell the player which team they are on
        String msg = plugin.config.getMessage("Start.Player").replace("[TEAM_NAME]", getNiceTeamName());
        p.getBukkitPlayer().sendMessage(msg);
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

    public void setTeamSpawn(Location loc) {
        this.teamSpawn = loc;
        plugin.getConfig().set("Locations." + getNiceTeamName(),
                new LocationUtils().parseLocationToString(loc));
        plugin.saveConfig();
    }

    public ItemStack[] getTeamKit() {
        Object data = NKHPvP.getInstance().getConfig().get("Kits." + getNiceTeamName());
        if (data instanceof ArrayList) {
            return ((ArrayList<ItemStack>) data).toArray(new ItemStack[]{});
        } else {
            return null;
        }
    }

    public void setTeamKit(Inventory inv) {
        ItemStack[] items = inv.getContents();
        plugin.getConfig().set("Kits." + getNiceTeamName(), items);
        plugin.saveConfig();
    }

    public String getNiceTeamName() {
        return team == Team.STUDENT ? "Students" : "DeathEaters";
    }

    public void resetScores() {
        kills = 0;
        deaths = 0;
        damageDealt = 0;
    }

}
