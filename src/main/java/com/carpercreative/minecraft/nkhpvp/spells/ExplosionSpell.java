package com.carpercreative.minecraft.nkhpvp.spells;

import com.carpercreative.minecraft.nkhpvp.NKHPvP;
import com.carpercreative.minecraft.nkhpvp.PvpPlayer;
import com.carpercreative.minecraft.nkhpvp.Team;
import com.carpercreative.minecraft.nkhpvp.util.InstantFirework;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collection;

public class ExplosionSpell extends Spell {

    private final FireworkEffect deathEatersFirework, studentsFirework;

    public ExplosionSpell() {
        super("Explosion");
        deathEatersFirework = FireworkEffect.builder().flicker(false).trail(false)
                .with(FireworkEffect.Type.BALL).withColor(Color.GREEN).build();
        studentsFirework = FireworkEffect.builder().flicker(false).trail(false)
                .with(FireworkEffect.Type.BALL).withColor(Color.RED).build();
    }

    @Override
    public void onHitPlayer(PvpPlayer spellCaster, PvpPlayer spellRecipient, EntityDamageByEntityEvent e) {
        //Nothing here since we want this spell to run on any impact
    }

    @Override
    public void onHit(PvpPlayer spellCaster, Location l) {
        if (l.getWorld() == null)
            return;
        //Get the team we need to deal damage too and the explosion colour
        Team toDamage = Team.STUDENT == spellCaster.getTeam().getTeam() ? Team.DEATH_EATER : Team.STUDENT;
        FireworkEffect explosionEffect = toDamage == Team.STUDENT ? deathEatersFirework : studentsFirework;
        //Spawn firework in team colour
        new InstantFirework(explosionEffect, l);
        //Run all this one second later to align with the firework explosion
        Bukkit.getScheduler().runTaskLater(NKHPvP.getInstance(), () -> {
            //Loop over nearby players and apply explosion damage
            //The distance from the center to search for players
            double distance = 3.0;
            double maxDamage = 5 * 2;
            Collection<Entity> nearby = l.getWorld().getNearbyEntities(l, distance, distance, distance);
            nearby.removeIf(entity -> !(entity instanceof Player));
            for (Entity player : nearby) {
                if (!(player instanceof Player)) {
                    continue;
                }
                //Check if enemy team
                PvpPlayer target = ((NKHPvP) NKHPvP.getInstance()).gameManager.getPlayer(player.getUniqueId());
                if (target == null)
                    continue;
                if (target.getTeam().getTeam() != toDamage)
                    continue;
                //Damage fall off so that players closer to the center take more damage
                double scaledDamage = (distance - player.getLocation().distance(l)) * (maxDamage / distance);
                //Track damage for this player for the next 2 ticks
                ((NKHPvP) NKHPvP.getInstance()).spellManager.trackDamage(target, spellCaster,
                        EntityDamageEvent.DamageCause.ENTITY_ATTACK, 2);
                //Apply scaled damage to the player
                Bukkit.getScheduler().runTask(NKHPvP.getInstance(),
                        () -> target.getBukkitPlayer().damage(scaledDamage, spellCaster.getBukkitPlayer()));
            }
        }, 20);
    }

    @Override
    public long getCooldown() {
        float seconds = 3f;
        return (long) (seconds * 1000L);
    }
}
