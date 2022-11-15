package com.carpercreative.minecraft.nkhpvp.spells;

import com.carpercreative.minecraft.nkhpvp.NKHPvP;
import com.carpercreative.minecraft.nkhpvp.PvpPlayer;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public abstract class Spell implements MetadataValue {

    private final String spellName;

    public Spell(String spellName) {
        this.spellName = spellName;
    }

    public abstract void applyEffect(PvpPlayer spellCaster, PvpPlayer spellRecipient, EntityDamageByEntityEvent e);

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
