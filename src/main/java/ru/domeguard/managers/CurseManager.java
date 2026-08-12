package ru.domeguard.managers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CurseManager {
    
    private final JavaPlugin plugin;
    private final Set<UUID> cursedPlayers = new HashSet<>();
    
    public CurseManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public boolean isPlayerCursed(UUID uuid) {
        return cursedPlayers.contains(uuid);
    }
    
    public void applyCurseEffects(Player player) {
        // Эффекты проклятия
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 1));
        player.sendMessage("§cВы прокляты! Съешьте тушёный суп, чтобы снять проклятие.");
    }
    
    public void blockSleep(Player player) {
        player.setSleepingIgnored(true);
        player.sendMessage("§cВы не можете спать из-за проклятия!");
    }
    
    public void unblockSleep(Player player) {
        player.setSleepingIgnored(false);
    }
    
    public void addCursedPlayer(UUID uuid) {
        cursedPlayers.add(uuid);
        plugin.getLogger().info("Игрок " + uuid + " добавлен в список проклятых.");
    }
    
    public void removeCursedPlayer(UUID uuid) {
        cursedPlayers.remove(uuid);
        Player player = plugin.getServer().getPlayer(uuid);
        if (player != null) {
            unblockSleep(player);
            player.sendMessage("§aПроклятие снято! Теперь вы можете спать.");
        }
        plugin.getLogger().info("Игрок " + uuid + " удалён из списка проклятых.");
    }
    
    public void clearEffects(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }
}
