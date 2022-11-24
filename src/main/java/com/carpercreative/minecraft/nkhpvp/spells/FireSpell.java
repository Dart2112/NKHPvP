package com.carpercreative.minecraft.nkhpvp.spells;

import com.carpercreative.minecraft.nkhpvp.NKHPvP;
import com.carpercreative.minecraft.nkhpvp.PvpPlayer;
import com.carpercreative.minecraft.nkhpvp.Team;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class FireSpell extends Spell {


    public FireSpell() {
        super("Fire");
    }

    @Override
    public void onHitPlayer(PvpPlayer spellCaster, PvpPlayer spellRecipient, EntityDamageByEntityEvent e) {
        int fireTicks = 40;
        spellRecipient.getBukkitPlayer().setFireTicks(fireTicks);
        //Track fire damage for this player for the fire ticks
        ((NKHPvP) NKHPvP.getInstance()).spellManager.trackDamage(spellRecipient, spellCaster,
                EntityDamageEvent.DamageCause.FIRE_TICK, fireTicks);
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
        float seconds = 1f;
        return (long) (seconds * 1000L);
    }
}
