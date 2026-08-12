package ru.domeguard.managers;

import org.bukkit.plugin.java.JavaPlugin;

public class DamageManager {
    
    private final JavaPlugin plugin;
    private final DomeManager domeManager;
    private final CurseManager curseManager;
    
    public DamageManager(JavaPlugin plugin, DomeManager domeManager, CurseManager curseManager) {
        this.plugin = plugin;
        this.domeManager = domeManager;
        this.curseManager = curseManager;
    }
    
    // Ваша логика управления уроном
}
