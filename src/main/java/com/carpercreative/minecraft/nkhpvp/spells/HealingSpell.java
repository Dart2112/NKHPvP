package com.carpercreative.minecraft.nkhpvp.spells;

import com.carpercreative.minecraft.nkhpvp.NKHPvP;
import com.carpercreative.minecraft.nkhpvp.PvpPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class HealingSpell extends Spell {

    private final HashMap<PvpPlayer, BukkitTask> tasks = new HashMap<>();
    private final int secondsToExist = 5;

    public HealingSpell() {
        super("Heal");
    }

    @Override
    public void onHitPlayer(PvpPlayer spellCaster, PvpPlayer spellRecipient, EntityDamageByEntityEvent e) {
        //TODO: Maybe heal directly as well
    }

    @Override
    public void onHit(PvpPlayer spellCaster, Location l) {
        AtomicInteger ticks = new AtomicInteger(secondsToExist * 20);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(NKHPvP.getInstance(), () -> {
            //Cancel the task if we have been going for the given ticks
            if (ticks.get() <= 0) {
                cancelBukkitTask(spellCaster);
                return;
            }
            ticks.getAndDecrement();
            //Get nearby players and give regen
            double distance = 3.0;
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
                //Give player 6 ticks of regen so that it will last until this code runs again
                target.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 6, 1));
            }
        }, 1, 5);
        tasks.put(spellCaster, task);
    }

    private void cancelBukkitTask(PvpPlayer player) {
        BukkitTask task = tasks.get(player);
        task.cancel();
    }

    @Override
    public long getCooldown() {
        float seconds = secondsToExist * 2;
        return (long) (seconds * 1000L);
    }
}
