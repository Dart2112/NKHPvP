package com.carpercreative.minecraft.nkhpvp;

import com.carpercreative.minecraft.nkhpvp.spells.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpellManager implements Listener {

    private final NKHPvP plugin;
    List<Spell> spells = new ArrayList<>();
    HashMap<PvpPlayer, List<DamageType>> damageToTrack = new HashMap<>();


    public SpellManager(NKHPvP plugin) {
        this.plugin = plugin;
        loadSpells();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Get a spell with the given name
     *
     * @param name The name of the spell you wish to retrieve
     * @return the spell with the given name, null if no such spell can be found
     */
    public Spell getSpellByName(String name) {
        for (Spell s : spells) {
            if (s.asString().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        PvpPlayer player = plugin.gameManager.getPlayer(e.getEntity().getUniqueId());
        if (player == null || damageToTrack.get(player) == null)
            return;
        for (DamageType damageType : damageToTrack.get(player)) {
            if (e.getCause() != damageType.cause) {
                //Only track the damage type that we have stored
                continue;
            }
            //The damage has been done by the tracked cause, so we count this damage for the player who cast the spell
            damageType.damageGiver.addDamageDealt(e.getDamage());
            break;
        }
    }

    /**
     * Get the player who we are tracking for this damage event
     *
     * @param e The entity damage entity event you wish to check
     * @return The player who caused this damage via spell, null if a player wasn't responsible
     */
    public PvpPlayer getDamageGiver(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return null;
        }
        PvpPlayer player = plugin.gameManager.getPlayer(e.getEntity().getUniqueId());
        for (DamageType damageType : damageToTrack.get(player)) {
            if (e.getCause() != damageType.cause) {
                //Only track the damage type that we have stored
                continue;
            }
            return damageType.damageGiver;
        }
        return null;
    }

    /**
     * Track damage done to a player via a spell, this allows accurate damage tracking per player
     *
     * @param damageTaker The player who will take damage
     * @param damageGiver The player who should be awarded with damage credit
     * @param damageCause The type of damage we should track
     * @param ticks       The number of ticks from now that we should track this damage
     */
    public void trackDamage(PvpPlayer damageTaker, PvpPlayer damageGiver, EntityDamageEvent.DamageCause damageCause, int ticks) {
        //Create a damage type object to store the type of damage we are going to track and who should get the credit for that damage
        DamageType damageType = new DamageType();
        damageType.damageGiver = damageGiver;
        damageType.cause = damageCause;

        //Get the currently applied damage types for the player who has been hit by a spell
        List<DamageType> damageTypes;
        if (damageToTrack.containsKey(damageTaker)) {
            damageTypes = damageToTrack.get(damageTaker);
        } else {
            damageTypes = new ArrayList<>();
        }

        //Check if there is a damage type with the same cause
        DamageType toReplace = null;
        for (DamageType d : damageTypes) {
            if (d.cause == damageCause) {
                toReplace = d;
            }
        }

        //If there is one with the same cause we need to remove it as we will be replacing it
        if (toReplace != null) {
            damageTypes.remove(toReplace);
        }

        //Add our new damage type
        damageTypes.add(damageType);
        damageToTrack.put(damageTaker, damageTypes);

        //Schedule a task to remove the damage type after x ticks
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            List<DamageType> d = damageToTrack.get(damageTaker);
            d.remove(damageType);
            damageToTrack.put(damageTaker, d);
        }, ticks);
    }

    private void loadSpells() {
        spells.add(new FireSpell());
        spells.add(new KnockbackSpell());
        spells.add(new DamageSpell());
        spells.add(new ExplosionSpell());
        spells.add(new LevitationSpell());
        spells.add(new HealingSpell());
    }

    protected static class DamageType {

        public EntityDamageEvent.DamageCause cause;
        public PvpPlayer damageGiver;

    }

}
