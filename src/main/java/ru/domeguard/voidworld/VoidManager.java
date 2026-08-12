```java
package ru.domeguard.voidworld;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
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

    /*
     * Для каждого игрока сохраняем место,
     * куда он должен вернуться из DomeVoid.
     */
    private final Map<UUID, Location> returnLocations =
            new ConcurrentHashMap<>();

    private static final int DEFAULT_SPAWN_Y = 100;

    public VoidManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Создаёт или получает DomeVoid.
     */
    public void createWorld() {

        String worldName = plugin.getConfig().getString(
                "void.world-name",
                "DomeVoid"
        );

        voidWorld = Bukkit.getWorld(worldName);

        if (voidWorld != null) {
            prepareWorld();
            return;
        }

        WorldCreator creator = new WorldCreator(worldName);

        creator.environment(World.Environment.NORMAL);
        creator.generateStructures(false);
        creator.generator(new VoidWorldGenerator());

        voidWorld = creator.createWorld();

        if (voidWorld == null) {
            plugin.getLogger().severe(
                    "Не удалось создать мир DomeVoid!"
            );
            return;
        }

        prepareWorld();

        plugin.getLogger().info(
                "Мир DomeVoid успешно создан."
        );
    }

    /**
     * Настраиваем мир так, чтобы он был настоящим
     * тёмным пустым пространством.
     */
    private void prepareWorld() {

        if (voidWorld == null) {
            return;
        }

        int spawnY = plugin.getConfig().getInt(
                "void.spawn-y",
                DEFAULT_SPAWN_Y
        );

        voidWorld.setSpawnLocation(
                0,
                spawnY + 1,
                0
        );

        voidWorld.setTime(18000L);

        voidWorld.setStorm(false);
        voidWorld.setThundering(false);

        voidWorld.setGameRule(
                GameRule.DO_DAYLIGHT_CYCLE,
                false
        );

        voidWorld.setGameRule(
                GameRule.DO_WEATHER_CYCLE,
                false
        );

        voidWorld.setGameRule(
                GameRule.DO_MOB_SPAWNING,
                false
        );

        voidWorld.setGameRule(
                GameRule.DO_INSOMNIA,
                false
        );

        /*
         * Создаём стартовую платформу только если
         * она ещё не существует.
         */
        createSpawnPlatform();
    }

    /**
     * Создаёт настоящую платформу из блоков.
     *
     * Игрок может ходить по ней,
     * ломать её и строить свои конструкции.
     */
    private void createSpawnPlatform() {

        if (voidWorld == null) {
            return;
        }

        int y = plugin.getConfig().getInt(
                "void.spawn-y",
                DEFAULT_SPAWN_Y
        );

        int radius = plugin.getConfig().getInt(
                "void.platform-radius",
                10
        );

        Material platformMaterial;

        String configuredMaterial = plugin.getConfig().getString(
                "void.platform-material",
                "OBSIDIAN"
        );

        try {
            platformMaterial = Material.valueOf(
                    configuredMaterial.toUpperCase()
            );
        } catch (IllegalArgumentException exception) {

            plugin.getLogger().warning(
                    "Неверный void.platform-material: "
                            + configuredMaterial
                            + ". Используется OBSIDIAN."
            );

            platformMaterial = Material.OBSIDIAN;
        }

        /*
         * Платформа создаётся только один раз.
         *
         * Мы проверяем центральный блок.
         * Если там уже есть блок — считаем,
         * что платформа уже создана.
         */
        if (voidWorld.getBlockAt(0, y, 0).getType()
                != Material.AIR) {
            return;
        }

        for (int x = -radius; x <= radius; x++) {

            for (int z = -radius; z <= radius; z++) {

                voidWorld.getBlockAt(
                        x,
                        y,
                        z
                ).setType(platformMaterial, false);
            }
        }

        createReturnPortal(
                new Location(
                        voidWorld,
                        0.5,
                        y + 1,
                        0.5
                )
        );
    }

    /**
     * Отправляет игрока в DomeVoid.
     */
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

        /*
         * Сохраняем точную координату Незера.
         */
        returnLocations.put(
                player.getUniqueId(),
                returnLocation.clone()
        );

        int spawnY = plugin.getConfig().getInt(
                "void.spawn-y",
                DEFAULT_SPAWN_Y
        );

        Location destination = new Location(
                voidWorld,
                0.5,
                spawnY + 1.0,
                0.5
        );

        player.teleport(
                destination,
                PlayerTeleportEvent.TeleportCause.PLUGIN
        );

        player.setFallDistance(0);
        player.setFireTicks(0);

        /*
         * Если включено затемнение —
         * накладываем его только на игрока.
         */
        if (plugin.getConfig().getBoolean(
                "void.darkness",
                false
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

        player.sendMessage(
                ChatColor.DARK_PURPLE
                        + "Ты оказался в пустом пространстве."
        );

        player.sendMessage(
                ChatColor.LIGHT_PURPLE
                        + "Найди портал, чтобы вернуться."
        );

        /*
         * На случай если мир был загружен,
         * но платформа отсутствует.
         */
        createSpawnPlatform();
    }

    /**
     * Создаёт портал обратно в Незер.
     *
     * Это НЕ настоящий Nether Portal,
     * чтобы Minecraft не начал самостоятельно
     * искать другой мир.
     *
     * VoidListener обрабатывает вход игрока
     * в эту конструкцию.
     */
    private void createReturnPortal(Location center) {

        if (voidWorld == null) {
            return;
        }

        int x = center.getBlockX();
        int y = center.getBlockY();
        int z = center.getBlockZ();

        /*
         * Основание.
         */
        for (int dx = -1; dx <= 1; dx++) {

            voidWorld.getBlockAt(
                    x + dx,
                    y - 1,
                    z
            ).setType(
                    Material.OBSIDIAN,
                    false
            );
        }

        /*
         * Левая и правая стойки.
         */
        for (int dy = 0; dy <= 3; dy++) {

            voidWorld.getBlockAt(
                    x - 1,
                    y + dy,
                    z
            ).setType(
                    Material.OBSIDIAN,
                    false
            );

            voidWorld.getBlockAt(
                    x + 1,
                    y + dy,
                    z
            ).setType(
                    Material.OBSIDIAN,
                    false
            );
        }

        /*
         * Верх.
         */
        for (int dx = -1; dx <= 1; dx++) {

            voidWorld.getBlockAt(
                    x + dx,
                    y + 3,
                    z
            ).setType(
                    Material.OBSIDIAN,
                    false
            );
        }

        /*
         * Внутреннее пространство портала.
         */
        for (int dy = 0; dy < 3; dy++) {

            voidWorld.getBlockAt(
                    x,
                    y + dy,
                    z
            ).setType(
                    Material.NETHER_PORTAL,
                    false
            );
        }
    }

    /**
     * Проверяет, находится ли игрок в DomeVoid.
     */
    public boolean isVoidWorld(Player player) {

        return voidWorld != null
                && player.getWorld().equals(voidWorld);
    }

    /**
     * Возвращает игрока в его сохранённое место.
     */
    public void returnPlayer(Player player) {

        Location location =
                returnLocations.remove(
                        player.getUniqueId()
                );

        /*
         * Если сохранённой позиции нет,
         * возвращаем на spawn основного мира.
         */
        if (location == null) {

            location =
                    Bukkit.getWorlds()
                            .get(0)
                            .getSpawnLocation();
        }

        player.removePotionEffect(
                PotionEffectType.DARKNESS
        );

        player.setFallDistance(0);

        player.teleport(
                location,
                PlayerTeleportEvent.TeleportCause.PLUGIN
        );

        player.sendMessage(
                ChatColor.LIGHT_PURPLE
                        + "Ты вернулся из пустого пространства."
        );
    }

    public World getWorld() {
        return voidWorld;
    }
}
```
