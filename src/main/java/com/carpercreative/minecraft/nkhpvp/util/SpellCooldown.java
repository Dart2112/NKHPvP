package com.carpercreative.minecraft.nkhpvp.util;

import com.carpercreative.minecraft.nkhpvp.spells.Spell;

public class SpellCooldown {

    long spellCastTime;
    Spell spell;

    public SpellCooldown(Spell spell) {
        spellCastTime = System.currentTimeMillis();
        this.spell = spell;
    }

    public boolean isExpired() {
        long duration = spell.getCooldown();
        long timeSinceCast = System.currentTimeMillis() - spellCastTime;
        return duration < timeSinceCast;
    }

    public Spell getSpell() {
        return spell;
    }
}
