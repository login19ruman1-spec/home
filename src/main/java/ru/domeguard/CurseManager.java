package ru.domeguard;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class CurseManager {
    private final DomeGuardPlugin plugin;
    private final File file;
    private final YamlConfiguration data;
    private final Set<UUID> cursed = new HashSet<>();

    public CurseManager(DomeGuardPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "curses.yml");
        this.data = YamlConfiguration.loadConfiguration(file);

        for (String value : data.getStringList("sleep-cursed")) {
            try {
                cursed.add(UUID.fromString(value));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public boolean isCursed(UUID uuid) {
        return cursed.contains(uuid);
    }

    public boolean curse(Player player) {
        return curse(player.getUniqueId());
    }

    public boolean curse(UUID uuid) {
        if (!cursed.add(uuid)) {
            return false;
        }
        save();
        return true;
    }

    public boolean cure(Player player) {
        return cure(player.getUniqueId());
    }

    public boolean cure(UUID uuid) {
        if (!cursed.remove(uuid)) {
            return false;
        }
        save();
        return true;
    }

    public void save() {
        data.set("sleep-cursed", cursed.stream().map(UUID::toString).toList());
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            data.save(file);
        } catch (IOException exception) {
            plugin.getLogger().warning("Could not save curses.yml: " + exception.getMessage());
        }
    }
}
