package ваш.пакет.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class BorderPortalListener implements Listener {
    
    private static final int BORDER = 5000; // Граница ада
    private static final String VOID_WORLD_NAME = "void_world"; // Название мира Void
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        
        // Проверяем, что игрок в аду
        if (!world.getEnvironment().equals(World.Environment.NETHER)) {
            return;
        }
        
        Location loc = player.getLocation();
        
        // Проверяем, достиг ли игрок границы
        if (Math.abs(loc.getX()) >= BORDER || Math.abs(loc.getZ()) >= BORDER) {
            // Создаём портал в Void
            createVoidPortal(loc);
            
            // Телепортируем игрока
            World voidWorld = player.getServer().getWorld(VOID_WORLD_NAME);
            if (voidWorld != null) {
                Location voidLoc = new Location(voidWorld, 0, 64, 0);
                player.teleport(voidLoc);
                player.sendMessage("§aВы вошли в Бездну!");
            }
        }
    }
    
    private void createVoidPortal(Location loc) {
        // Создаём портал из обсидиана
        Location portalLoc = loc.clone();
        for (int y = 0; y < 4; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Location blockLoc = portalLoc.clone().add(x, y, z);
                    if (y == 0 || y == 3 || x == -1 || x == 1) {
                        // Рамка из обсидиана
                        blockLoc.getBlock().setType(Material.OBSIDIAN);
                    } else if (y >= 1 && y <= 2 && x == 0 && z == 0) {
                        // Внутренняя часть портала
                        blockLoc.getBlock().setType(Material.PORTAL); // или NETHER_PORTAL
                    }
                }
            }
        }
    }
}
