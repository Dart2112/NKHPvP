package com.carpercreative.minecraft.nkhpvp;

import net.lapismc.lapiscore.LapisCoreConfiguration;
import net.lapismc.lapiscore.LapisCorePlugin;

public final class NKHPvP extends LapisCorePlugin {

    public GameManager gameManager;

    @Override
    public void onEnable() {
        gameManager = new GameManager(this);
        new Listeners(this);
        registerConfiguration(new LapisCoreConfiguration(this, 0, 0));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
