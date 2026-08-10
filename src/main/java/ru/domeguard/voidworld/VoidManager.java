package ru.domeguard.voidworld;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class VoidManager {

    private final JavaPlugin plugin;

    private World voidWorld;

    private final Map<UUID, Location> returnLocations =
            new ConcurrentHashMap<>();

    public VoidManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void createWorld() {

        String worldName = plugin.getConfig().getString(
                "void.world-name",
                "DomeVoid"
        );

        voidWorld = Bukkit.getWorld(worldName);

        if (voidWorld != null) {
            return;
        }

        WorldCreator creator = new WorldCreator(worldName);

        creator.environment(World.Environment.NORMAL);
        creator.generateStructures(false);
        creator.generator(new VoidWorldGenerator());

        voidWorld = creator.createWorld();

        if (voidWorld != null) {

            voidWorld.setSpawnLocation(
                    0,
                    plugin.getConfig().getInt(
                            "void.spawn-y",
                            100
                    ),
                    0
            );

            plugin.getLogger().info(
                    "DomeVoid создан."
            );
        }
    }

    public World getWorld() {
        return voidWorld;
    }

    public void sendToVoid(
            Player player,
            Location returnLocation
    ) {

        if (voidWorld == null) {
            createWorld();
        }

        if (voidWorld == null) {
            return;
        }

        returnLocations.put(
                player.getUniqueId(),
                returnLocation.clone()
        );

        Location destination = new Location(
                voidWorld,
                0.5,
                plugin.getConfig().getDouble(
                        "void.spawn-y",
                        100
                ),
                0.5
        );

        player.teleport(
                destination,
                PlayerTeleportEvent.TeleportCause.PLUGIN
        );

        player.setFallDistance(0);
        player.setFireTicks(0);

        if (plugin.getConfig().getBoolean(
                "void.darkness",
                true
        )) {

            player.addPotionEffect(
                    new PotionEffect(
                            PotionEffectType.DARKNESS,
                            Integer.MAX_VALUE,
                            0,
                            false,
                            false,
                            false
                    )
            );
        }

        createReturnPortal(destination);
    }

    private void createReturnPortal(Location center) {

        World world = center.getWorld();

        if (world == null) {
            return;
        }

        for (int x = -1; x <= 1; x++) {

            for (int y = 0; y <= 2; y++) {

                if (
                        x == -1 ||
                        x == 1 ||
                        y == 0 ||
                        y == 2
                ) {

                    world.getBlockAt(
                            center.clone().add(x, y, 0)
                    ).setType(Material.OBSIDIAN);
                }
            }
        }

        world.getBlockAt(
                center.clone().add(0, 1, 0)
        ).setType(Material.NETHER_PORTAL);
    }

    public boolean isVoidWorld(Player player) {

        return voidWorld != null &&
                player.getWorld().equals(voidWorld);
    }

    public void returnPlayer(Player player) {

        Location location =
                returnLocations.remove(
                        player.getUniqueId()
                );

        if (location == null) {

            location =
                    Bukkit.getWorlds()
                            .get(0)
                            .getSpawnLocation();
        }

        player.removePotionEffect(
                PotionEffectType.DARKNESS
        );

        player.teleport(
                location,
                PlayerTeleportEvent.TeleportCause.PLUGIN
        );
    }
}
