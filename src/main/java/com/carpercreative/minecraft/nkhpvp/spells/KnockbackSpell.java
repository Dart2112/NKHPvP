package com.carpercreative.minecraft.nkhpvp.spells;

import com.carpercreative.minecraft.nkhpvp.NKHPvP;
import com.carpercreative.minecraft.nkhpvp.PvpPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public class KnockbackSpell extends Spell {

    public KnockbackSpell() {
        super("Knockback");
    }

    @Override
    public void applyEffect(PvpPlayer spellCaster, PvpPlayer spellRecipient, EntityDamageByEntityEvent e) {
        Player p = spellRecipient.getBukkitPlayer();
        //Get the snowballs velocity
        Vector velocity = e.getDamager().getVelocity();
        //Multiply the snowballs velocity by this value
        float multiplier = 3;
        //Apply the multiplication and add it to the players current velocity
        p.setVelocity(p.getVelocity().add(velocity.multiply(multiplier)));
        //Track fall damage that could be caused by this spell
        ((NKHPvP) NKHPvP.getInstance()).spellManager.trackDamage(spellRecipient, spellCaster,
                EntityDamageEvent.DamageCause.FALL, 40);
    }
}
