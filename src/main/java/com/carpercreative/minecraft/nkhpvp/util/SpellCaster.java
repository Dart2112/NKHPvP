package com.carpercreative.minecraft.nkhpvp.util;

import com.carpercreative.minecraft.nkhpvp.NKHPvP;
import com.carpercreative.minecraft.nkhpvp.PvpPlayer;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpellCaster implements MetadataValue {

    private final PvpPlayer spellCaster;

    public SpellCaster(PvpPlayer player) {
        this.spellCaster = player;
    }

    public PvpPlayer getSpellCaster() {
        return spellCaster;
    }

    @Override
    public @Nullable Object value() {
        return spellCaster;
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
        return spellCaster.getBukkitPlayer().getName();
    }

    @Override
    public @Nullable Plugin getOwningPlugin() {
        return NKHPvP.getInstance();
    }

    @Override
    public void invalidate() {

    }
}
