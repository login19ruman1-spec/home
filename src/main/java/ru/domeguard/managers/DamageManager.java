package ru.domeguard.managers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DamageManager implements Listener {
    
    private final JavaPlugin plugin;
    private final DomeManager domeManager;
    private final CurseManager curseManager;
    
    public DamageManager(JavaPlugin plugin, DomeManager domeManager, CurseManager curseManager) {
        this.plugin = plugin;
        this.domeManager = domeManager;
        this.curseManager = curseManager;
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        
        // Если игрок проклят - удваиваем урон
        if (curseManager.isPlayerCursed(player.getUniqueId())) {
            event.setDamage(event.getDamage() * 2);
            player.sendMessage("§cПроклятие усиливает урон!");
        }
    }
}
