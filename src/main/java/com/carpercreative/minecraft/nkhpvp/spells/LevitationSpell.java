package com.carpercreative.minecraft.nkhpvp.spells;

import com.carpercreative.minecraft.nkhpvp.NKHPvP;
import com.carpercreative.minecraft.nkhpvp.PvpPlayer;
import com.carpercreative.minecraft.nkhpvp.Team;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LevitationSpell extends Spell {

    public LevitationSpell() {
        super("Levitation");
    }

    @Override
    public void onHitPlayer(PvpPlayer spellCaster, PvpPlayer spellRecipient, EntityDamageByEntityEvent e) {
        spellRecipient.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 1));
        //Track fall damage for this player for 1 second longer than the levitation
        ((NKHPvP) NKHPvP.getInstance()).spellManager.trackDamage(spellRecipient, spellCaster,
                EntityDamageEvent.DamageCause.FALL, 60);
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
