package ru.domeguard;

public class DomeMenu {
    
    private final DomeGuardPlugin plugin;
    private final DomeManager domeManager;
    private final DamageManager damageManager;
    
    // Если у вас такой конструктор:
    public DomeMenu(DomeGuardPlugin plugin, DomeManager domeManager, DamageManager damageManager) {
        this.plugin = plugin;
        this.domeManager = domeManager;
        this.damageManager = damageManager;
    }
}
