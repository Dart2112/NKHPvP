package com.carpercreative.minecraft.nkhpvp.spells;

import com.carpercreative.minecraft.nkhpvp.NKHPvP;
import com.carpercreative.minecraft.nkhpvp.PvpPlayer;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class FireSpell extends Spell {


    public FireSpell() {
        super("Fire");
    }

    @Override
    public void applyEffect(PvpPlayer spellCaster, PvpPlayer spellRecipient, EntityDamageByEntityEvent e) {
        int fireTicks = 20;
        spellRecipient.getBukkitPlayer().setFireTicks(fireTicks);
        //Track fire damage for this player for the fire ticks
        ((NKHPvP) NKHPvP.getInstance()).spellManager.trackDamage(spellRecipient, spellCaster,
                EntityDamageEvent.DamageCause.FIRE_TICK, fireTicks);
    }
}
