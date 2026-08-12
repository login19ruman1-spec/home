package ru.domeguard.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DomeManager {
    
    private final JavaPlugin plugin;
    private final Map<UUID, Long> playerLastCheck = new HashMap<>();
    private final int DOME_RADIUS = 50;
    private final int LETHAL_RADIUS = 51;
    
    public DomeManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public boolean isInsideDome(Player player) {
        Location center = new Location(player.getWorld(), 0, 0, 0);
        return player.getLocation().distance(center) <= DOME_RADIUS;
    }
    
    public boolean isAtLethalDistance(Player player) {
        Location center = new Location(player.getWorld(), 0, 0, 0);
        return player.getLocation().distance(center) >= LETHAL_RADIUS;
    }
    
    public void applyZoneEffects(Player player) {
        Location center = new Location(player.getWorld(), 0, 0, 0);
        double distance = player.getLocation().distance(center);
        
        // Эффекты в зависимости от расстояния
        if (distance <= 10) {
            // Близкая зона - слабые эффекты
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0));
        } else if (distance <= 50) {
            // Средняя зона - сильные эффекты
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, 1));
        }
    }
    
    public void checkPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        
        // Проверяем не чаще чем раз в секунду
        if (playerLastCheck.containsKey(uuid)) {
            if (currentTime - playerLastCheck.get(uuid) < 1000) {
                return;
            }
        }
        playerLastCheck.put(uuid, currentTime);
        
        if (isAtLethalDistance(player)) {
            // Игрок на смертельном расстоянии
            player.damage(10.0);
            player.sendMessage("§cВы слишком далеко от купола! Получен урон.");
        } else if (!isInsideDome(player)) {
            // Игрок вне купола, но не на смертельном расстоянии
            applyZoneEffects(player);
        }
    }
}
