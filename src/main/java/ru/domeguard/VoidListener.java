package ru.domeguard;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public final class VoidListener implements Listener {
    private final VoidManager voidManager;
    private final DomeGuardPlugin plugin;

    public VoidListener(DomeGuardPlugin plugin, VoidManager voidManager) {
        this.plugin = plugin;
        this.voidManager = voidManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPortal(PlayerPortalEvent event) {
        if (!voidManager.enabled()) return;

        Player player = event.getPlayer();
        World world = event.getFrom().getWorld();
        if (world == null) return;

        // Верхняя граница Незера -> собственный DomeVoid.
        if (world.getEnvironment() == World.Environment.NETHER
                && event.getFrom().getY() >= plugin.getConfig().getDouble("nether-void.trigger-y", 127.0)) {
            event.setCancelled(true);
            voidManager.enter(player, event.getFrom());
            return;
        }

        // Любой настоящий портал внутри DomeVoid возвращает игрока назад.
        if (voidManager.isVoidWorld(world)) {
            event.setCancelled(true);
            voidManager.returnPlayer(player);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (voidManager.isVoidWorld(event.getPlayer().getWorld())) {
            voidManager.checkReturn(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVoidDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!voidManager.isVoidWorld(player.getWorld())) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            event.setCancelled(true);
            player.setFallDistance(0);
        }
    }
}
