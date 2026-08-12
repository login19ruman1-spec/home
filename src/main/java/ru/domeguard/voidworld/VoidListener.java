package ru.domeguard.voidworld;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
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

    /**
     * Игрок пытается использовать портал
     * выше заданной границы Незера.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPortal(PlayerPortalEvent event) {

        Player player = event.getPlayer();

        if (!plugin.getConfig().getBoolean(
                "void.enabled",
                true
        )) {
            return;
        }

        if (player.getWorld().getEnvironment()
                != World.Environment.NETHER) {
            return;
        }

        Location from = event.getFrom();

        if (from.getY() < netherRoofY) {
            return;
        }

        /*
         * Отменяем обычную обработку портала.
         */
        event.setCancelled(true);

        /*
         * Отправляем игрока в DomeVoid.
         */
        voidManager.sendToVoid(
                player,
                from
        );
    }

    /**
     * Дополнительная защита от обычного
     * Nether Portal teleport event.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent event) {

        if (!plugin.getConfig().getBoolean(
                "void.enabled",
                true
        )) {
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

        voidManager.sendToVoid(
                player,
                from
        );
    }

    /**
     * Игрок находится внутри DomeVoid.
     *
     * Здесь:
     * - он может свободно ходить;
     * - может строить;
     * - может ломать блоки;
     * - получает частицы;
     * - не может упасть в обычный Void.
     */
    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        if (!voidManager.isVoidWorld(player)) {
            return;
        }

        Location location = player.getLocation();

        /*
         * Если игрок по какой-то причине провалился
         * слишком низко — возвращаем его на платформу.
         *
         * Это дополнительная защита.
         */
        int minimumY = plugin.getConfig().getInt(
                "void.minimum-safe-y",
                20
        );

        if (location.getY() <= minimumY) {

            int spawnY = plugin.getConfig().getInt(
                    "void.spawn-y",
                    100
            );

            Location safeLocation = new Location(
                    player.getWorld(),
                    0.5,
                    spawnY + 1.0,
                    0.5
            );

            player.teleport(
                    safeLocation,
                    PlayerTeleportEvent.TeleportCause.PLUGIN
            );

            player.setFallDistance(0);

            return;
        }

        /*
         * Фиолетовые частицы вокруг игрока.
         */
        if (plugin.getConfig().getBoolean(
                "void.particles",
                true
        )) {

            player.getWorld().spawnParticle(
                    Particle.PORTAL,
                    location.clone().add(
                            0,
                            1,
                            0
                    ),
                    5,
                    0.35,
                    0.5,
                    0.35,
                    0.02
            );
        }
    }

    /**
     * Полностью отключаем Void damage
     * внутри DomeVoid.
     *
     * Игрок не должен умереть от пустоты.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVoidDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!voidManager.isVoidWorld(player)) {
            return;
        }

        if (event.getCause()
                == EntityDamageEvent.DamageCause.VOID) {

            event.setCancelled(true);

            player.setFallDistance(0);
        }
    }

    /**
     * Дополнительная защита от любых
     * телепортов через обычный Nether Portal
     * внутри DomeVoid.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void preventVoidPortalTeleport(
            PlayerTeleportEvent event
    ) {

        Player player = event.getPlayer();

        if (!voidManager.isVoidWorld(player)) {
            return;
        }

        if (event.getCause()
                != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            return;
        }

        /*
         * Не позволяем Minecraft самостоятельно
         * выкинуть игрока из DomeVoid.
         *
         * Наш возврат будет обрабатываться отдельно.
         */
        event.setCancelled(true);
    }
}
```
