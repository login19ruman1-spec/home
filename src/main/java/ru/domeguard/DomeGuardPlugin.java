package ru.domeguard;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class DomeGuardPlugin extends JavaPlugin {
    private DomeManager domeManager;
    private DamageManager damageManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        domeManager = new DomeManager(this);
        damageManager = new DamageManager(this, domeManager);

        getCommand("dome").setExecutor(new DomeCommand(domeManager));

        Bukkit.getPluginManager().registerEvents(new DomeMenu(this, domeManager), this);
        Bukkit.getPluginManager().registerEvents(new RespawnListener(this), this);

        damageManager.start();
        getLogger().info("DomeGuard v1.2.0 enabled.");
    }

    @Override
    public void onDisable() {
        if (damageManager != null) {
            damageManager.stop();
        }
    }
}
