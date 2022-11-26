package com.carpercreative.minecraft.nkhpvp.spells;

import com.carpercreative.minecraft.nkhpvp.NKHPvP;
import com.carpercreative.minecraft.nkhpvp.PvpPlayer;
import com.carpercreative.minecraft.nkhpvp.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageSpell extends Spell {

    public DamageSpell() {
        super("Damage", "custom.spell.patronus.cast", "");
    }

    @Override
    public void onHitPlayer(PvpPlayer spellCaster, PvpPlayer spellRecipient, EntityDamageByEntityEvent e) {
        if (isFriendlyFire(spellCaster, spellRecipient))
            return;
        double heartsOfDamage = 3;
        //Track damage for this player for the next 2 ticks
        ((NKHPvP) NKHPvP.getInstance()).spellManager.trackDamage(spellRecipient, spellCaster,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, 2);
        Bukkit.getScheduler().runTask(NKHPvP.getInstance(),
                () -> spellRecipient.getBukkitPlayer().damage(heartsOfDamage * 2, spellCaster.getBukkitPlayer()));
    }

    @Override
    public void onHit(PvpPlayer spellCaster, Location l) {
        //Spawn splash particles, red for students and green for deathEaters
        boolean students = spellCaster.getTeam().getTeam().equals(Team.STUDENT);
        for (int i = 0; i < 25; i++) {
            spawnSplashParticles(l, students);
        }
    }

    @Override
    public long getCooldown() {
        float seconds = 0.5f;
        return (long) (seconds * 1000L);
    }
}
