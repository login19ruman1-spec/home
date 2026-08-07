package ru.domeguard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class RespawnListener implements Listener {
    private final DomeGuardPlugin plugin;
    private final Map<UUID, Long> deathUntil = new HashMap<>();

    public RespawnListener(DomeGuardPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        long delayTicks = Math.max(0L,
                plugin.getConfig().getLong("respawn.delay-ticks", 60L));

        long until = System.currentTimeMillis() + (delayTicks * 50L);
        deathUntil.put(player.getUniqueId(), until);

        // Automatically respawn after the configured delay.
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;

            if (player.isDead()) {
                deathUntil.remove(player.getUniqueId());
                player.spigot().respawn();
            }
        }, delayTicks);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        deathUntil.remove(event.getPlayer().getUniqueId());
    }
}
