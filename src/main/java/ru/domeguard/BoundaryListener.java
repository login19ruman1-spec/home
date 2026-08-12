package ru.domeguard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.domeguard.managers.CurseManager;
import ru.domeguard.managers.DomeManager;

public class BoundaryListener implements Listener {
    
    private final DomeManager domeManager;
    private final CurseManager curseManager;
    
    public BoundaryListener(JavaPlugin plugin, CurseManager curseManager) {
        this.domeManager = new DomeManager(plugin);
        this.curseManager = curseManager;
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Проверяем позицию игрока
        domeManager.checkPlayer(player);
        
        // Если игрок на смертельном расстоянии - проклинаем
        if (domeManager.isAtLethalDistance(player)) {
            if (!curseManager.isPlayerCursed(player.getUniqueId())) {
                curseManager.addCursedPlayer(player.getUniqueId());
                curseManager.applyCurseEffects(player);
                curseManager.blockSleep(player);
                player.sendMessage("§cВы пересекли смертельную границу! Вы прокляты!");
            }
        }
    }
}
