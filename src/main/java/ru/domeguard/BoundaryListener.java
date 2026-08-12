package ru.domeguard;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public final class BoundaryListener implements Listener {
    private final DomeGuardPlugin plugin;
    private final CurseManager curse;

    public BoundaryListener(DomeGuardPlugin plugin, CurseManager curse) {
        this.plugin = plugin;
        this.curse = curse;
    }

    @EventHandler
    public void onBed(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        if (!curse.isCursed(player.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onStew(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.SUSPICIOUS_STEW) {
            return;
        }

        Player player = event.getPlayer();
        if (!curse.isCursed(player.getUniqueId())) {
            return;
        }

        if (plugin.getConfig().getBoolean("sleep-curse.remove-with-suspicious-stew", true)) {
            curse.cure(player);
        }
    }
}
