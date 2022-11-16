package com.carpercreative.minecraft.nkhpvp;

import com.carpercreative.minecraft.nkhpvp.commands.PvPCommand;
import net.lapismc.lapiscore.LapisCoreConfiguration;
import net.lapismc.lapiscore.LapisCorePlugin;
import net.lapismc.lapiscore.utils.LapisCoreFileWatcher;

public final class NKHPvP extends LapisCorePlugin {

    public SpellManager spellManager;
    public GameManager gameManager;
    private LapisCoreFileWatcher fileWatcher;

    @Override
    public void onEnable() {
        gameManager = new GameManager(this);
        spellManager = new SpellManager(this);
        new Listeners(this);
        registerConfiguration(new LapisCoreConfiguration(this, 1, 1));
        fileWatcher = new LapisCoreFileWatcher(this);
        new PvPCommand(this);
    }

    @Override
    public void onDisable() {
        fileWatcher.stop();
        super.onDisable();
    }
}
