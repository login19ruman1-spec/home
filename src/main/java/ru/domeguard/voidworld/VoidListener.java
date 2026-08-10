package ru.domeguard.voidworld;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class VoidListener implements Listener {

    private final VoidManager voidManager;
    private final double netherRoofY;

    public VoidListener(
            VoidManager voidManager,
            double netherRoofY
    ) {

        this.voidManager = voidManager;
        this.netherRoofY = netherRoofY;
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {

        Player player = event.getPlayer();

        if (
                player.getWorld().getEnvironment()
                        != World.Environment.NETHER
        ) {
            return;
        }

        Location location = event.getFrom();

        if (location.getY() < netherRoofY) {
            return;
        }

        event.setCancelled(true);

        voidManager.sendToVoid(
                player,
                location
        );
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {

        if (
                event.getCause()
                        != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL
        ) {
            return;
        }

        Player player = event.getPlayer();

        if (
                player.getWorld().getEnvironment()
                        != World.Environment.NETHER
        ) {
            return;
        }

        if (event.getFrom().getY() < netherRoofY) {
            return;
        }

        event.setCancelled(true);

        voidManager.sendToVoid(
                player,
                event.getFrom()
        );
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        if (!voidManager.isVoidWorld(player)) {
            return;
        }

        Location location = player.getLocation();

        if (
                location.distanceSquared(
                        new Location(
                                player.getWorld(),
                                0.5,
                                100,
                                0.5
                        )
                ) <= 9
        ) {

            voidManager.returnPlayer(player);
            return;
        }

        if (
                Bukkit.getConfig().getBoolean(
                        "void.particles",
                        true
                )
        ) {

            player.getWorld().spawnParticle(
                    Particle.PORTAL,
                    location.clone().add(0, 1, 0),
                    5,
                    0.4,
                    0.5,
                    0.4,
                    0.02
            );
        }
    }
}
