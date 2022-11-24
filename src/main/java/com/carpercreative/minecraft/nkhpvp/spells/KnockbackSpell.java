package com.carpercreative.minecraft.nkhpvp.spells;

import com.carpercreative.minecraft.nkhpvp.NKHPvP;
import com.carpercreative.minecraft.nkhpvp.PvpPlayer;
import com.carpercreative.minecraft.nkhpvp.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public class KnockbackSpell extends Spell {

    public KnockbackSpell() {
        super("Knockback");
    }

    @Override
    public void onHitPlayer(PvpPlayer spellCaster, PvpPlayer spellRecipient, EntityDamageByEntityEvent e) {
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

    @Override
    public void onHit(PvpPlayer spellCaster, Location l) {
        boolean students = spellCaster.getTeam().getTeam().equals(Team.STUDENT);
        for (int i = 0; i < 25; i++) {
            spawnSplashParticles(l, students ? 1 : 0, students ? 0 : 1);
        }
    }

    @Override
    public long getCooldown() {
        float seconds = 2f;
        return (long) (seconds * 1000L);
    }
}
