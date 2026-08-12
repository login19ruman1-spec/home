package ru.domeguard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import ru.domeguard.managers.CurseManager;

public class RespawnListener implements Listener {
    
    private final CurseManager curseManager;
    
    public RespawnListener(CurseManager curseManager) {
        this.curseManager = curseManager;
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        
        // Если игрок проклят - применяем эффекты заново
        if (curseManager.isPlayerCursed(player.getUniqueId())) {
            curseManager.applyCurseEffects(player);
            curseManager.blockSleep(player);
            player.sendMessage("§cВы всё ещё прокляты! Съешьте тушёный суп.");
        }
    }
}
