package ru.domeguard.voidworld;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class VoidListener implements Listener {

    private final JavaPlugin plugin;
    private final VoidManager voidManager;
    private final double netherRoofY;

    public VoidListener(
            JavaPlugin plugin,
            VoidManager voidManager,
            double netherRoofY
    ) {
        this.plugin = plugin;
        this.voidManager = voidManager;
        this.netherRoofY = netherRoofY;
    }

    /*
     * Перехватываем попытку использовать портал
     * около/выше верхней границы Незера.
     */
    @EventHandler
    public void onPortal(PlayerPortalEvent event) {

        Player player = event.getPlayer();

        if (!plugin.getConfig().getBoolean("void.enabled", true)) {
            return;
        }

        if (player.getWorld().getEnvironment() != World.Environment.NETHER) {
            return;
        }

        Location from = event.getFrom();

        if (from.getY() < netherRoofY) {
            return;
        }

        // Не даём Minecraft выполнить обычный переход.
        event.setCancelled(true);

        // Отправляем игрока в Void.
        voidManager.sendToVoid(player, from);
    }

    /*
     * Дополнительная защита для телепорта через Nether Portal.
     */
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {

        if (!plugin.getConfig().getBoolean("void.enabled", true)) {
            return;
        }

        if (event.getCause()
                != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            return;
        }

        Player player = event.getPlayer();

        if (player.getWorld().getEnvironment()
                != World.Environment.NETHER) {
            return;
        }

        Location from = event.getFrom();

        if (from.getY() < netherRoofY) {
            return;
        }

        event.setCancelled(true);

        voidManager.sendToVoid(player, from);
    }

    /*
     * Проверяем игрока внутри Void.
     */
    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        if (!voidManager.isVoidWorld(player)) {
            return;
        }

        Location location = player.getLocation();

        /*
         * Фиолетовые частицы вокруг игрока.
         */
        if (plugin.getConfig().getBoolean("void.particles", true)) {

            player.getWorld().spawnParticle(
                    Particle.PORTAL,
                    location.clone().add(0, 1, 0),
                    8,
                    0.5,
                    0.5,
                    0.5,
                    0.03
            );
        }

        /*
         * Координаты портала обратно.
         *
         * Сейчас портал находится около:
         * X = 0.5
         * Y = 100
         * Z = 0.5
         */
        if (plugin.getConfig().getBoolean(
                "void.return-portal",
                true
        )) {

            Location portalLocation = new Location(
                    player.getWorld(),
                    0.5,
                    plugin.getConfig().getDouble(
                            "void.spawn-y",
                            100
                    ) + 1,
                    0.5
            );

            double radius = plugin.getConfig().getDouble(
                    "void.return-portal-radius",
                    2.5
            );

            if (location.distanceSquared(portalLocation)
                    <= radius * radius) {

                voidManager.returnPlayer(player);
            }
        }
    }
}
