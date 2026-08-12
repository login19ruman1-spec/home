package ru.domeguard;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class BorderPortalListener implements Listener {
    
    private static final int BORDER = 5000;
    private static final String VOID_WORLD_NAME = "void_world";
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        
        if (!world.getEnvironment().equals(World.Environment.NETHER)) {
            return;
        }
        
        Location loc = player.getLocation();
        
        if (Math.abs(loc.getX()) >= BORDER || Math.abs(loc.getZ()) >= BORDER) {
            World voidWorld = player.getServer().getWorld(VOID_WORLD_NAME);
            if (voidWorld != null) {
                Location voidLoc = new Location(voidWorld, 0, 64, 0);
                player.teleport(voidLoc);
                player.sendMessage("§aВы вошли в Бездну!");
            }
        }
    }
}
