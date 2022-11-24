package com.carpercreative.minecraft.nkhpvp.spells;

import com.carpercreative.minecraft.nkhpvp.NKHPvP;
import com.carpercreative.minecraft.nkhpvp.PvpPlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.Random;

public abstract class Spell implements MetadataValue {

    private final String spellName;
    private final String castSound, hitSound;

    public Spell(String spellName, String castSound, String hitSound) {
        this.spellName = spellName;
        this.castSound = castSound;
        this.hitSound = hitSound;
        //Ding sound custom.gameplay_element.ding
    }

    /**
     * Run when the spell snowball impacts a player who is in the game
     *
     * @param spellCaster    The player who cast the spell
     * @param spellRecipient The player who has been directly hit by it
     * @param e              The event that has been triggered by the hit
     */
    public abstract void onHitPlayer(PvpPlayer spellCaster, PvpPlayer spellRecipient, EntityDamageByEntityEvent e);

    /**
     * Run when the snowball hits anything, including a block or a player
     *
     * @param spellCaster The player who cast the spell
     * @param l           The location of the snowball on impact
     */
    public abstract void onHit(PvpPlayer spellCaster, Location l);

    /**
     * Get the cooldown for this spell
     *
     * @return the time in milliseconds that should pass before this spell can be used again
     */
    public abstract long getCooldown();

    protected void spawnSplashParticles(Location l, double red, double green) {
        double distance = 2.5;
        Random r = new Random();
        for (int i = 0; i < 25; i++) {
            l.getWorld().spawnParticle(Particle.SPELL_MOB, l, 0, red, 0, green, 1);
        }
    }

    public void playCastSound(Location loc) {
        loc.getWorld().playSound(loc, castSound, SoundCategory.MASTER, 20f, 20f);
    }

    public void playHitSound(Location loc) {
        loc.getWorld().playSound(loc, castSound, SoundCategory.MASTER, 20f, 20f);
    }

    @Override
    public Object value() {
        return spellName;
    }

    @Override
    public int asInt() {
        return 0;
    }

    @Override
    public float asFloat() {
        return 0;
    }

    @Override
    public double asDouble() {
        return 0;
    }

    @Override
    public long asLong() {
        return 0;
    }

    @Override
    public short asShort() {
        return 0;
    }

    @Override
    public byte asByte() {
        return 0;
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public String asString() {
        return spellName;
    }

    @Override
    public Plugin getOwningPlugin() {
        return NKHPvP.getInstance();
    }

    @Override
    public void invalidate() {

    }
}
