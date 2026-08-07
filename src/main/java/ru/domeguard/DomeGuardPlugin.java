package ru.domeguard;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class DomeGuardPlugin extends JavaPlugin {
    private DomeManager domeManager;
    private DamageManager damageManager;
    private CurseManager curseManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        curseManager = new CurseManager(this);
        domeManager = new DomeManager(this);
        damageManager = new DamageManager(this, domeManager, curseManager);
        getCommand("dome").setExecutor(new DomeCommand(domeManager));
        DomeMenu menu = new DomeMenu(this, domeManager, damageManager);
        Bukkit.getPluginManager().registerEvents(menu, this);
        Bukkit.getPluginManager().registerEvents(new RespawnListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BoundaryListener(this, curseManager), this);
        damageManager.start();
        getLogger().info("DomeGuard v1.4.0 enabled.");
    }
    @Override public void onDisable() { if (damageManager != null) damageManager.stop(); if (curseManager != null) curseManager.save(); }
}
