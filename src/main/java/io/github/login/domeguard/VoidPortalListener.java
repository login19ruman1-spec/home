package io.github.alex123.domeguard;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class VoidPortalListener implements Listener {

    private final JavaPlugin plugin;
    private final VoidWorld voidWorld;

    public VoidPortalListener(
            JavaPlugin plugin,
            VoidWorld voidWorld
    ) {
        this.plugin = plugin;
        this.voidWorld = voidWorld;
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {

        Player player = event.getPlayer();

        /*
         * Проверяем, находится ли игрок
         * в нашем Void мире.
         */

        if (!player.getWorld()
                .getName()
                .equals(VoidWorld.WORLD_NAME)) {

            return;
        }

        /*
         * Игрок вошёл в портал
         * внутри Void.
         */

        World nether = Bukkit.getWorld("world_nether");

        if (nether == null) {

            player.sendMessage(
                    "§cNether мир не найден!"
            );

            return;
        }

        event.setCancelled(true);

        /*
         * Координаты выхода.
         */

        Location returnLocation =
                new Location(
                        nether,
                        0.5,
                        80,
                        0.5
                );

        player.teleport(returnLocation);

        player.sendMessage(
                "§5Ты вернулся из Пустоты."
        );
    }

    @EventHandler
    public void onVoidFall(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        /*
         * Работаем только внутри Void.
         */

        if (!player.getWorld()
                .getName()
                .equals(VoidWorld.WORLD_NAME)) {

            return;
        }

        Location to = event.getTo();

        if (to == null) {
            return;
        }

        /*
         * Игрок упал слишком низко.
         *
         * Возвращаем его к порталу.
         */

        if (to.getY() < -64) {

            Location portal =
                    voidWorld
                            .getVoidPortalLocation()
                            .clone()
                            .add(0, 1, 0);

            event.setTo(portal);

            player.sendMessage(
                    "§7Пустота вернула тебя обратно."
            );
        }
    }
}
