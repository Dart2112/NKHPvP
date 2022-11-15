package com.carpercreative.minecraft.nkhpvp.commands;

import com.carpercreative.minecraft.nkhpvp.NKHPvP;
import net.lapismc.lapiscore.commands.LapisCoreCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PvPCommand extends LapisCoreCommand {

    private final NKHPvP plugin;

    protected PvPCommand(NKHPvP plugin) {
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


        // /pvp setSpawn (lobby, student, death_eater)
        if (args.length == 2 && args[0].equalsIgnoreCase("setSpawn")) {
            if (isNotPlayer(sender, "Error.MustBePlayer")) {
                return;
            }
            Player p = (Player) sender;
            String spawnToSet = args[1];
            switch (spawnToSet) {
                case "lobby":
                    plugin.gameManager.setLobbySpawn(p.getLocation());
                    break;
                case "student":
                    plugin.gameManager.students.setTeamSpawn(p.getLocation());
                    break;
                case "deatheater":
                    plugin.gameManager.deathEaters.setTeamSpawn(p.getLocation());
                    break;
                default:
                    sendMessage(sender, "SetSpawn.WrongSpawn");
                    return;
            }
            sendMessage(sender, "SetSpawn.Success");
        }

    }
}
