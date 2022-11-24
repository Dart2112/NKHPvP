package com.carpercreative.minecraft.nkhpvp.spells;

import com.carpercreative.minecraft.nkhpvp.PvpPlayer;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LevitationSpell extends Spell {

    public LevitationSpell() {
        super("Levitation");
    }

    @Override
    public void onHitPlayer(PvpPlayer spellCaster, PvpPlayer spellRecipient, EntityDamageByEntityEvent e) {
        spellRecipient.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 1));
    }

    @Override
    public void onHit(PvpPlayer spellCaster, Location l) {
        //TODO: use same particles as damage
    }

    @Override
    public long getCooldown() {
        float seconds = 1f;
        return (long) (seconds * 1000L);
    }
}
