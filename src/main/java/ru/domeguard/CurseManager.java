package ru.domeguard;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

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
        this.file = new File(plugin.getDataFolder(), "players.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
        for (String value : data.getStringList("sleep-cursed")) {
            try { cursed.add(UUID.fromString(value)); } catch (IllegalArgumentException ignored) {}
        }
    }

    public boolean isCursed(UUID uuid) { return cursed.contains(uuid); }

    public void curse(Player player) {
        if (cursed.add(player.getUniqueId())) {
            save();
            player.sendMessage("§5☠ Граница прокляла тебя. §7Ты больше не сможешь спать без загадочного рагу.");
        }
    }

    public boolean cure(Player player) {
        if (!cursed.remove(player.getUniqueId())) return false;
        save();
        player.sendMessage("§a✦ Проклятие сна снято. §7Теперь ты снова можешь спать.");
        return true;
    }

    public void save() {
        data.set("sleep-cursed", cursed.stream().map(UUID::toString).toList());
        try { data.save(file); } catch (IOException e) { plugin.getLogger().severe("Не удалось сохранить players.yml: " + e.getMessage()); }
    }
}
