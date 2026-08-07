package ru.domeguard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public final class DamageManager {
    private final DomeGuardPlugin plugin;
    private final DomeManager dome;
    private BukkitTask task;

    public DamageManager(DomeGuardPlugin plugin, DomeManager dome) {
        this.plugin = plugin;
        this.dome = dome;
    }

    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 10L, 10L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
        }
    }

    private void tick() {
        if (!dome.isEnabled()) {
            return;
        }

        double warning = plugin.getConfig().getDouble("damage.warning-distance", 0.0);
        double darknessDistance = plugin.getConfig().getDouble("damage.darkness-distance", 8.0);
        double deathDistance = plugin.getConfig().getDouble("damage.death-distance", 15.0);
        double damagePerSecond = plugin.getConfig().getDouble("damage.damage-per-second", 1.0);
        double maxDamagePerSecond = plugin.getConfig().getDouble("damage.max-damage-per-second", 20.0);
        int nauseaAmplifier = plugin.getConfig().getInt("damage.nausea-amplifier", 0);
        int darknessAmplifier = plugin.getConfig().getInt("damage.darkness-amplifier", 0);

        // This task runs every 0.5 seconds.
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isDead() || dome.isInside(player.getLocation())) {
                continue;
            }

            double distance = dome.distanceOutside(player.getLocation());

            if (distance >= warning) {
                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.NAUSEA,
                        25,
                        nauseaAmplifier,
                        true,
                        false,
                        true
                ));

                // Damage grows with every block outside the dome.
                double secondsMultiplier = Math.max(1.0, distance - warning + 1.0);
                double damageThisHalfSecond = Math.min(
                        maxDamagePerSecond / 2.0,
                        (damagePerSecond * secondsMultiplier) / 2.0
                );

                if (damageThisHalfSecond > 0) {
                    player.damage(damageThisHalfSecond);
                }
            }

            if (distance >= darknessDistance) {
                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.DARKNESS,
                        25,
                        darknessAmplifier,
                        true,
                        false,
                        true
                ));
            }

            if (distance >= deathDistance) {
                player.setHealth(0.0);
            }
        }
    }
}
