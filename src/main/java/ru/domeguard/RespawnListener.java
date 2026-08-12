package ru.domeguard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class RespawnListener implements Listener {
    private final DomeGuardPlugin plugin;
    private final Set<UUID> curseAfterRespawn = new HashSet<>();

    public RespawnListener(DomeGuardPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Проклятие выдаём именно умершему игроку, если смерть произошла
        // за пределами настроенной территории верхнего мира.
        if (!plugin.getConfig().getBoolean("sleep-curse.enabled", true)) {
            return;
        }

        if (player.getWorld().getName().equals(
                plugin.getConfig().getString("dome.world", "world"))) {
            DomeManager dome = plugin.getDome();
            if (dome != null && dome.distanceOutside(player.getLocation()) > 0) {
                curseAfterRespawn.add(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (!curseAfterRespawn.remove(uuid)) {
            return;
        }

        // Ставим на следующий тик, чтобы гарантированно сработать после respawn.
        plugin.getServer().getScheduler().runTask(plugin, () ->
                plugin.getCurseManager().curse(uuid)
        );
    }
}
