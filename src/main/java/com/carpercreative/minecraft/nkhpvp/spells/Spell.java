package com.carpercreative.minecraft.nkhpvp.spells;

import com.carpercreative.minecraft.nkhpvp.NKHPvP;
import com.carpercreative.minecraft.nkhpvp.PvpPlayer;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public abstract class Spell implements MetadataValue {

    private final String spellName;

    public Spell(String spellName) {
        this.spellName = spellName;
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
