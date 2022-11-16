package com.carpercreative.minecraft.nkhpvp.commands;

import com.carpercreative.minecraft.nkhpvp.NKHPvP;
import com.carpercreative.minecraft.nkhpvp.Team;
import net.lapismc.lapiscore.commands.LapisCoreCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class PvPCommand extends LapisCoreCommand {

    private final NKHPvP plugin;

    public PvPCommand(NKHPvP plugin) {
        super(plugin, "pvp", "Commands to control the PvP plugin", new ArrayList<>());
        this.plugin = plugin;
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {

        //Check that it's either the console or an OPed player
        if (sender instanceof Player) {
            if (!sender.isOp()) {
                sendMessage(sender, "Error.NotPermitted");
                return;
            }
        }

        // /pvp (enable|disable)

        if (args.length == 1 && (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable"))) {
            boolean enable = args[0].equalsIgnoreCase("enable");
            //If it is already in this state
            if (enable == plugin.gameManager.isEnabled()) {
                sendMessage(sender, "Enable.Error");
                return;
            }
            if (enable) {
                plugin.gameManager.enable();
                sendMessage(sender, "Enable.EnableSuccess");
            } else {
                plugin.gameManager.disable();
                sendMessage(sender, "Enable.DisableSuccess");
            }
            return;
        }

        // /pvp start (minutes)

        if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
            int minutes;
            try {
                minutes = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sendMessage(sender, "Start.NumberFormat");
                return;
            }
            plugin.gameManager.setTimer(minutes);
            plugin.gameManager.startGame();
            sendMessage(sender, "Start.Success");
        }

        // /pvp setSpawn (lobby, student, deatheater)

        if (args.length == 2 && args[0].equalsIgnoreCase("setSpawn")) {
            if (isNotPlayer(sender, "Error.MustBePlayer")) {
                return;
            }
            Player p = (Player) sender;
            String spawnToSet = args[1];
            if (spawnToSet.equals("lobby")) {
                plugin.gameManager.setLobbySpawn(p.getLocation());
            } else if (spawnToSet.startsWith("student")) {
                plugin.gameManager.students.setTeamSpawn(p.getLocation());
            } else if (spawnToSet.startsWith("deatheater")) {
                plugin.gameManager.deathEaters.setTeamSpawn(p.getLocation());
            } else {
                sendMessage(sender, "SetSpawn.InvalidTeam");
                return;
            }
            sendMessage(sender, "SetSpawn.Success");
            return;
        }

        // /pvp setKit (student, deatheater)

        if (args.length == 2 && args[0].equalsIgnoreCase("setKit")) {
            if (isNotPlayer(sender, "Error.MustBePlayer")) {
                return;
            }
            Player p = (Player) sender;
            String spawnToSet = args[1];
            if (spawnToSet.startsWith("student")) {
                setKit(Team.STUDENT, p.getInventory());
            } else if (spawnToSet.startsWith("deatheater")) {
                setKit(Team.DEATH_EATER, p.getInventory());
            } else {
                sendMessage(sender, "SetKit.InvalidTeam");
                return;
            }
            sendMessage(sender, "SetKit.Success");
        }

    }

    public void setKit(Team team, Inventory inv) {
        if (team == Team.STUDENT) {
            plugin.gameManager.students.setTeamKit(inv);
        } else if (team == Team.DEATH_EATER) {
            plugin.gameManager.deathEaters.setTeamKit(inv);
        }
    }
}
