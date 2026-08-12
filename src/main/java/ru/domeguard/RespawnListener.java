package ваш.пакет.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import ваш.пакет.managers.CurseManager; // замените на ваш менеджер

public class PlayerRespawnListener implements Listener {
    
    private final CurseManager curseManager;
    
    public PlayerRespawnListener(CurseManager curseManager) {
        this.curseManager = curseManager;
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        
        // Проверяем, проклят ли игрок
        if (curseManager.isPlayerCursed(player.getUniqueId())) {
            // Возвращаем все эффекты
            curseManager.applyCurseEffects(player);
            
            // Блокируем сон
            curseManager.blockSleep(player);
            
            // Отправляем сообщение
            player.sendMessage("§cВы всё ещё прокляты! Съешьте тушёный суп.");
        }
    }
}
