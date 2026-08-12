package ru.domeguard.managers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CurseManager {
    
    private final JavaPlugin plugin;
    private final Set<UUID> cursedPlayers = new HashSet<>();
    
    public CurseManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    // Проверка, проклят ли игрок
    public boolean isPlayerCursed(UUID uuid) {
        return cursedPlayers.contains(uuid);
    }
    
    // Применить эффекты проклятия
    public void applyCurseEffects(Player player) {
        // Здесь ваша логика применения эффектов
        // Например: наложение эффектов, сообщения и т.д.
        player.sendMessage("§cВы прокляты!");
    }
    
    // Заблокировать сон
    public void blockSleep(Player player) {
        // Ваша логика блокировки сна
        // Например: установка метаданных или флага
        player.setMetadata("domeguard_cursed", plugin.getServer().getPluginManager().getPlugin("DomeGuard").getPluginMeta());
    }
    
    // Добавить игрока в проклятые
    public void addCursedPlayer(UUID uuid) {
        cursedPlayers.add(uuid);
        // Сохранить в players.yml
    }
    
    // Удалить игрока из проклятых
    public void removeCursedPlayer(UUID uuid) {
        cursedPlayers.remove(uuid);
        // Сохранить в players.yml
    }
}
