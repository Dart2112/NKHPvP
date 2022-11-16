package com.carpercreative.minecraft.nkhpvp.spells;

import com.carpercreative.minecraft.nkhpvp.NKHPvP;
import com.carpercreative.minecraft.nkhpvp.PvpPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageSpell extends Spell {

    public DamageSpell() {
        super("Damage");
    }

    @Override
    public void applyEffect(PvpPlayer spellCaster, PvpPlayer spellRecipient, EntityDamageByEntityEvent e) {
        double damage = 3;
        //Track damage for this player for the next 2 ticks
        ((NKHPvP) NKHPvP.getInstance()).spellManager.trackDamage(spellRecipient, spellCaster,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, 2);
        //TODO: See if this way of applying damage is tracked properly
        Bukkit.getScheduler().runTask(NKHPvP.getInstance(),
                () -> spellRecipient.getBukkitPlayer().damage(damage, spellCaster.getBukkitPlayer()));
    }
}
