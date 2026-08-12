package ru.domeguard;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class DomeGuardPlugin extends JavaPlugin {
    private DomeManager dome;
    private DamageManager damage;
    private CurseManager curse;
    private VoidManager voidManager;

    @Override public void onEnable() {
        saveDefaultConfig();
        curse = new CurseManager(this);
        dome = new DomeManager(this);
        damage = new DamageManager(this, dome, curse);
        voidManager = new VoidManager(this);
        DomeMenu menu = new DomeMenu(this, dome, damage, voidManager);
        getCommand("dome").setExecutor(new DomeCommand(dome, menu));
        getServer().getPluginManager().registerEvents(menu, this);
        getServer().getPluginManager().registerEvents(new BoundaryListener(this, curse), this);
        getServer().getPluginManager().registerEvents(new VoidListener(this, voidManager), this);
        getServer().getPluginManager().registerEvents(new RespawnListener(this), this);
        damage.start();
        getLogger().info("DomeGuard v1.5.0 enabled.");
    }
    @Override public void onDisable() { if (damage != null) damage.stop(); if (curse != null) curse.save(); }
    public DomeManager getDome(){return dome;} public DamageManager getDamage(){return damage;} public CurseManager getCurseManager(){return curse;} public VoidManager getVoidManager(){return voidManager;}
}
