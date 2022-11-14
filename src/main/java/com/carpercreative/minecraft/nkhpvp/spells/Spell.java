package com.carpercreative.minecraft.nkhpvp.spells;

import com.carpercreative.minecraft.nkhpvp.NKHPvP;
import com.carpercreative.minecraft.nkhpvp.PvpPlayer;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Spell implements MetadataValue {

    private final String spellName;

    public Spell(String spellName) {
        this.spellName = spellName;
    }

    public abstract void applyEffect(PvpPlayer spellCaster, PvpPlayer spellRecipient, EntityDamageByEntityEvent e);

    @Override
    public @Nullable Object value() {
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
    public @NotNull String asString() {
        return spellName;
    }

    @Override
    public @Nullable Plugin getOwningPlugin() {
        return NKHPvP.getInstance();
    }

    @Override
    public void invalidate() {

    }
}
