package com.carpercreative.minecraft.nkhpvp.spells;

import com.carpercreative.minecraft.nkhpvp.NKHPvP;
import com.carpercreative.minecraft.nkhpvp.PvpPlayer;
import com.carpercreative.minecraft.nkhpvp.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class HealingSpell extends Spell {

    private final HashMap<PvpPlayer, BukkitTask> tasks = new HashMap<>();
    private final int secondsToExist = 3;

    public HealingSpell() {
        super("Heal", "custom.spell.alohomora.cast", "");
    }

    @Override
    public void onHitPlayer(PvpPlayer spellCaster, PvpPlayer spellRecipient, EntityDamageByEntityEvent e) {
        //All effects are in onHit so that they effect the whole team who is in range
    }

    @Override
    public void onHit(PvpPlayer spellCaster, Location l) {
        AtomicInteger ticks = new AtomicInteger(secondsToExist * 4);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(NKHPvP.getInstance(), () -> {
            //Cancel the task if we have been going for the given ticks
            if (ticks.get() <= 0 || !((NKHPvP) NKHPvP.getInstance()).gameManager.isGameStarted()) {
                cancelBukkitTask(spellCaster);
                return;
            }
            ticks.getAndDecrement();
            //Get nearby players and give regen
            double distance = 4.0;
            Collection<Entity> nearby = l.getWorld().getNearbyEntities(l, distance, distance, distance);
            nearby.removeIf(entity -> !(entity instanceof Player));
            for (Entity player : nearby) {
                if (!(player instanceof Player)) {
                    continue;
                }
                //Check if same team
                PvpPlayer target = ((NKHPvP) NKHPvP.getInstance()).gameManager.getPlayer(player.getUniqueId());
                if (target == null)
                    continue;
                if (target.getTeam().getTeam() != spellCaster.getTeam().getTeam())
                    continue;
                //Give player 6 ticks of regen so that it will last until this code runs again, this shows regen on hearts
                target.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 6, 1));
                //Actually heal the player as well
                double maxHealth = target.getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                target.getBukkitPlayer().setHealth(Math.min(target.getBukkitPlayer().getHealth() + 0.5d, maxHealth));
            }
            //Spawn particles
            Random r = new Random();
            boolean students = spellCaster.getTeam().getTeam().equals(Team.STUDENT);
            for (int i = 0; i < 50; i++) {
                Location varied = l.clone().subtract(distance / 2, 0, distance / 2);
                varied = varied.add(r.nextDouble() * distance, 0, r.nextDouble() * distance);
                varied.getWorld().spawnParticle(Particle.SPELL_MOB, varied, 0, students ? 1 : 0, students ? 0 : 1, 0, 1, null, true);
            }
        }, 1, 5);
        cancelBukkitTask(spellCaster);
        tasks.put(spellCaster, task);
    }

    private void cancelBukkitTask(PvpPlayer player) {
        BukkitTask task = tasks.get(player);
        if (task != null)
            task.cancel();
    }

    @Override
    public long getCooldown() {
        float seconds = secondsToExist * 2;
        return (long) (seconds * 1000L);
    }
}
