package com.carpercreative.minecraft.nkhpvp;

import com.carpercreative.minecraft.nkhpvp.spells.Spell;
import com.carpercreative.minecraft.nkhpvp.util.SpellCaster;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class Listeners implements Listener {

    private final NKHPvP plugin;

    public Listeners(NKHPvP plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        //TODO: Check if players should be auto added if the game is enabled
        if (plugin.gameManager.isEnabled() && e.getPlayer().getGameMode() == GameMode.SURVIVAL)
            plugin.gameManager.addPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        plugin.gameManager.removePlayer(e.getPlayer());
    }

    @EventHandler
    public void onSnowballSpawn(EntitySpawnEvent e) {
        //Check that the game is running
        if (!plugin.gameManager.isGameStarted())
            return;
        if (!(e.getEntity() instanceof Snowball)) {
            //We only want snowballs
            return;
        }
        Snowball ball = (Snowball) e.getEntity();
        if (!(ball.getShooter() instanceof Player)) {
            //Shouldn't happen but best be safe
            return;
        }
        Player player = (Player) ball.getShooter();
        PvpPlayer pvpPlayer = plugin.gameManager.getPlayer(player.getUniqueId());
        //Make sure this player was a part of the game
        if (pvpPlayer == null)
            return;
        ItemStack heldItem = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
        if (heldItem == null || !heldItem.getType().equals(Material.SNOWBALL)
                || !heldItem.getItemMeta().hasDisplayName()) {
            //Must be a named snowball
            return;
        }
        //Reset the snowballs to 4 to make sure they don't run out
        heldItem.setAmount(4);
        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), heldItem);
        //Get the spell being cast
        String spellName = heldItem.getItemMeta().getDisplayName();
        Spell spell = plugin.spellManager.getSpellByName(spellName);
        if (spell == null) {
            //Spell with the snowballs name doesn't exist
            return;
        }
        ball.setMetadata("Spell", spell);
        ball.setMetadata("SpellCaster", new SpellCaster(pvpPlayer));
    }

    @EventHandler
    public void onSnowballHitPlayer(EntityDamageByEntityEvent e) {
        //Check that the game is running
        if (!plugin.gameManager.isGameStarted())
            return;
        if (!(e.getEntity() instanceof Player)) {
            //We only want players who have been damaged
            return;
        }
        if (!(e.getDamager() instanceof Snowball)) {
            //We only want players who have been damaged by a snowball
            return;
        }
        //Get the snowball
        Snowball ball = (Snowball) e.getDamager();
        //Check it is a spell snowball
        if (ball.getMetadata("Spell").size() == 0)
            //It's not one of our snowballs
            return;
        //Get the spell attached to the snowball
        Spell spell = (Spell) ball.getMetadata("Spell").get(0);
        PvpPlayer damageGiver = ((SpellCaster) ball.getMetadata("SpellCaster").get(0)).getSpellCaster();
        //Run the effect on the player
        PvpPlayer damageTaker = plugin.gameManager.getPlayer(e.getEntity().getUniqueId());
        spell.onHitPlayer(damageGiver, damageTaker, e);
        //Negate the normal damage done to the player and instead allow the spells to do damage
        e.setDamage(0);
    }

    @EventHandler
    public void onSnowballHitAnything(ProjectileHitEvent e) {
        //Check that the game is running
        if (!plugin.gameManager.isGameStarted())
            return;
        if (!(e.getEntity() instanceof Snowball)) {
            //We only want players who have been damaged by a snowball
            return;
        }
        //Get the snowball
        Snowball ball = (Snowball) e.getEntity();
        //Check it is a spell snowball
        if (ball.getMetadata("Spell").size() == 0)
            //It's not one of our snowballs
            return;
        //Get the spell attached to the snowball
        Spell spell = (Spell) ball.getMetadata("Spell").get(0);
        PvpPlayer damageGiver = ((SpellCaster) ball.getMetadata("SpellCaster").get(0)).getSpellCaster();
        spell.onHit(damageGiver, e.getEntity().getLocation());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        //Check that the game is running
        if (!plugin.gameManager.isGameStarted())
            return;
        PvpPlayer deadPlayer = plugin.gameManager.getPlayer(e.getEntity().getUniqueId());
        //Make sure this player was a part of the game
        if (deadPlayer == null)
            return;
        deadPlayer.addDeath();
        e.setKeepInventory(false);
        e.getDrops().clear();
        //This last damage should be what killed the player
        EntityDamageEvent lastDamage = e.getEntity().getLastDamageCause();
        if (lastDamage == null)
            return;
        //Check if the type of damage is tracked to a spell
        PvpPlayer deathCause = plugin.spellManager.getDamageGiver(lastDamage);
        if (deathCause == null)
            return;
        //Add the kill to the appropriate player
        deathCause.addKill();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        PvpPlayer p = plugin.gameManager.getPlayer(e.getPlayer().getUniqueId());
        //Make sure this player was a part of the game
        if (p == null)
            return;
        if (p.getTeam() == null) {
            e.setRespawnLocation(plugin.gameManager.getLobbyLocationVaried());
        } else {
            e.setRespawnLocation(p.getTeam().getTeamSpawnVaried());
            p.loadKit();
        }
    }


}
