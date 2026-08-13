package io.github.alex123.domeguard;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

public class VoidWorld {

    public static final String WORLD_NAME = "domeguard_void";

    private final JavaPlugin plugin;

    private World world;

    public VoidWorld(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void create() {

        world = Bukkit.getWorld(WORLD_NAME);

        if (world != null) {
            plugin.getLogger().info("Void мир уже существует.");
            return;
        }

        WorldCreator creator = new WorldCreator(WORLD_NAME);

        creator.environment(World.Environment.NORMAL);
        creator.generator(new VoidWorldGenerator());
        creator.generateStructures(false);

        world = Bukkit.createWorld(creator);

        if (world == null) {
            plugin.getLogger().severe(
                    "Не удалось создать Void мир!"
            );
            return;
        }

        world.setGameRule(
                GameRule.DO_MOB_SPAWNING,
                false
        );

        world.setGameRule(
                GameRule.DO_WEATHER_CYCLE,
                false
        );

        world.setGameRule(
                GameRule.DO_DAYLIGHT_CYCLE,
                false
        );

        world.setGameRule(
                GameRule.DO_FIRE_TICK,
                false
        );

        world.setGameRule(
                GameRule.DO_TILE_DROPS,
                false
        );

        world.setGameRule(
                GameRule.DO_ENTITY_DROPS,
                false
        );

        world.setTime(18000);

        world.setStorm(false);
        world.setThundering(false);

        plugin.getLogger().info(
                "Void Universe создана: " + WORLD_NAME
        );
    }

    public World getWorld() {
        return world;
    }

    public Location getVoidPortalLocation() {

        if (world == null) {
            return null;
        }

        return new Location(
                world,
                0.5,
                100.0,
                0.5
        );
    }

    public void createPortal() {

        if (world == null) {
            return;
        }

        Location center = getVoidPortalLocation();

        int x = center.getBlockX();
        int y = center.getBlockY();
        int z = center.getBlockZ();

        /*
         * Платформа под порталом.
         */

        for (int px = -2; px <= 2; px++) {

            for (int pz = -2; pz <= 2; pz++) {

                world.getBlockAt(
                        x + px,
                        y - 1,
                        z + pz
                ).setType(Material.OBSIDIAN);
            }
        }

        /*
         * Левая и правая стороны рамки.
         */

        for (int py = 0; py <= 3; py++) {

            world.getBlockAt(
                    x - 2,
                    y + py,
                    z
            ).setType(Material.OBSIDIAN);

            world.getBlockAt(
                    x + 2,
                    y + py,
                    z
            ).setType(Material.OBSIDIAN);
        }

        /*
         * Верх и низ рамки.
         */

        for (int px = -2; px <= 2; px++) {

            world.getBlockAt(
                    x + px,
                    y,
                    z
            ).setType(Material.OBSIDIAN);

            world.getBlockAt(
                    x + px,
                    y + 3,
                    z
            ).setType(Material.OBSIDIAN);
        }

        /*
         * Сам портал.
         */

        for (int px = -1; px <= 1; px++) {

            for (int py = 1; py <= 2; py++) {

                world.getBlockAt(
                        x + px,
                        y + py,
                        z
                ).setType(Material.NETHER_PORTAL);
            }
        }

        plugin.getLogger().info(
                "Void портал создан на X=" +
                        x +
                        " Y=" +
                        y +
                        " Z=" +
                        z
        );
    }
}
