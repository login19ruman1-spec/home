package ru.domeguard;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.domeguard.voidworld.VoidListener;
import ru.domeguard.voidworld.VoidManager;

public final class DomeGuardPlugin extends JavaPlugin {
    private DomeManager domeManager;
    private DamageManager damageManager;
    private CurseManager curseManager;
    private VoidManager voidManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (getConfig().getBoolean("void.enabled", true)) {
    voidManager = new VoidManager(this);
    voidManager.createWorld();

    double roofY = getConfig().getDouble(
            "void.nether-roof-y",
            127.0
    );

    Bukkit.getPluginManager().registerEvents(
            new VoidListener(this, voidManager, roofY),
            this
    );
}
        curseManager = new CurseManager(this);
        domeManager = new DomeManager(this);
        damageManager = new DamageManager(this, domeManager, curseManager);
        DomeMenu menu = new DomeMenu(this, domeManager, damageManager);
        getCommand("dome").setExecutor(new DomeCommand(domeManager, menu));
        Bukkit.getPluginManager().registerEvents(menu, this);
        Bukkit.getPluginManager().registerEvents(new RespawnListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BoundaryListener(this, curseManager), this);
        damageManager.start();
        getLogger().info("DomeGuard v1.5.0 enabled.");
    }
    @Override public void onDisable() { if (damageManager != null) damageManager.stop(); if (curseManager != null) curseManager.save(); }
}
