package ru.domeguard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class VoidManager {
    private final DomeGuardPlugin plugin;
    private World world;
    private final Map<UUID, Location> returns = new HashMap<>();

    public VoidManager(DomeGuardPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public boolean enabled() {
        return plugin.getConfig().getBoolean("nether-void.enabled", true);
    }

    public void toggle() {
        plugin.getConfig().set("nether-void.enabled", !enabled());
        plugin.saveConfig();
    }

    private void load() {
        String name = plugin.getConfig().getString("nether-void.world-name", "DomeVoid");
        world = Bukkit.getWorld(name);

        if (world == null) {
            WorldCreator creator = new WorldCreator(name)
                    .environment(World.Environment.NORMAL)
                    .generator(new VoidGenerator())
                    .generateStructures(false);
            world = creator.createWorld();
        }

        if (world == null) {
            plugin.getLogger().severe("Не удалось создать DomeVoid.");
            return;
        }

        world.setTime(18000L);
        world.setStorm(false);
        world.setThundering(false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);

        Location spawn = spawn();
        world.setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
        createPlatformAndPortal();
    }

    private Location spawn() {
        return new Location(
                world,
                plugin.getConfig().getDouble("nether-void.spawn-x", 0.5),
                plugin.getConfig().getDouble("nether-void.spawn-y", 100.0),
                plugin.getConfig().getDouble("nether-void.spawn-z", 0.5)
        );
    }

    private void createPlatformAndPortal() {
        if (world == null) return;

        Location center = spawn();
        int radius = plugin.getConfig().getInt("nether-void.platform-radius", 8);
        Material material = Material.OBSIDIAN;

        // Создаём только первоначальную площадку. Дальше игрок строит сам.
        if (world.getBlockAt(center.getBlockX(), center.getBlockY() - 1, center.getBlockZ()).getType() != Material.AIR) {
            return;
        }

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                world.getBlockAt(center.getBlockX() + x, center.getBlockY() - 1, center.getBlockZ() + z)
                        .setType(material, false);
            }
        }

        buildPortal(center);
    }

    private void buildPortal(Location center) {
        int x = center.getBlockX();
        int y = center.getBlockY();
        int z = center.getBlockZ();

        // Отдельный портал на краю стартовой площадки.
        int px = x + 5;

        for (int dy = 0; dy <= 3; dy++) {
            world.getBlockAt(px - 1, y + dy, z).setType(Material.OBSIDIAN, false);
            world.getBlockAt(px + 1, y + dy, z).setType(Material.OBSIDIAN, false);
        }
        for (int dx = -1; dx <= 1; dx++) {
            world.getBlockAt(px + dx, y, z).setType(Material.OBSIDIAN, false);
            world.getBlockAt(px + dx, y + 3, z).setType(Material.OBSIDIAN, false);
        }
        for (int dy = 1; dy <= 2; dy++) {
            world.getBlockAt(px, y + dy, z).setType(Material.NETHER_PORTAL, false);
        }
    }

    public void enter(Player player, Location from) {
        if (!enabled()) return;
        if (world == null) load();
        if (world == null) return;

        returns.put(player.getUniqueId(), from.clone());

        Location destination = spawn().clone().add(0, 0.1, 0);
        player.teleport(destination, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.setFallDistance(0);
        player.setFireTicks(0);
        player.setVelocity(player.getVelocity().setY(0));

        if (plugin.getConfig().getBoolean("nether-void.darkness", true)) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.DARKNESS,
                    Integer.MAX_VALUE,
                    0,
                    false,
                    false,
                    false
            ));
        }
    }

    public void checkReturn(Player player) {
        if (!isVoidWorld(player.getWorld())) return;

        player.setFallDistance(0);

        double minimumY = plugin.getConfig().getDouble("nether-void.minimum-y", 20.0);
        if (player.getLocation().getY() <= minimumY) {
            player.teleport(spawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.setFallDistance(0);
            return;
        }

        if (plugin.getConfig().getBoolean("nether-void.particles", true)) {
            world.spawnParticle(org.bukkit.Particle.PORTAL,
                    player.getLocation().add(0, 1, 0), 4, .25, .4, .25, .02);
        }

        Location center = spawn().clone().add(5, 1, 0);
        double radius = plugin.getConfig().getDouble("nether-void.portal-radius", 2.5);
        if (player.getLocation().distanceSquared(center) > radius * radius) return;

        returnPlayer(player);
    }

    public void returnPlayer(Player player) {
        Location back = returns.remove(player.getUniqueId());
        if (back == null) {
            back = Bukkit.getWorlds().get(0).getSpawnLocation();
        }

        player.removePotionEffect(PotionEffectType.DARKNESS);
        player.setFallDistance(0);
        player.teleport(back, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public boolean isVoidWorld(World world) {
        return this.world != null && this.world.equals(world);
    }

    public World getWorld() {
        return world;
    }
}
