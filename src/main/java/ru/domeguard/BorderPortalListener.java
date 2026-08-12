package ru.domeguard;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BorderPortalListener implements Listener {
    
    private final JavaPlugin plugin;
    private static final int BORDER = 5000;
    private static final String VOID_WORLD_NAME = "void_world";
    
    public BorderPortalListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
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
            World voidWorld = plugin.getServer().getWorld(VOID_WORLD_NAME);
            if (voidWorld != null) {
                Location voidLoc = new Location(voidWorld, 0, 64, 0);
                player.teleport(voidLoc);
                player.sendMessage("§aВы вошли в Бездну!");
                plugin.getLogger().info("Игрок " + player.getName() + " телепортирован в Void.");
            } else {
                player.sendMessage("§cМир Void не найден! Обратитесь к администратору.");
            }
        }
    }
}
