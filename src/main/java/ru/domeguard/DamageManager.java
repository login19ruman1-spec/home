package ru.domeguard;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public final class DamageManager {
    private final DomeGuardPlugin plugin;
    private final DomeManager dome;
    private BukkitTask task;
    private int tickCounter;

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
        int blindnessAmplifier = plugin.getConfig().getInt("damage.blindness-amplifier", 2);
        int wardenSoundEveryTicks = Math.max(20, plugin.getConfig().getInt("damage.warden-sound-every-ticks", 40));

        tickCounter += 10;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isDead() || dome.isInside(player.getLocation())) {
                continue;
            }

            double distance = dome.distanceOutside(player.getLocation());

            if (distance >= warning) {
                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.NAUSEA,
                        30,
                        nauseaAmplifier,
                        true,
                        false,
                        true
                ));

                // Warden-like darkness: Darkness itself does not become visually
                // stronger with amplifier, so we layer Blindness on top of it.
                if (distance >= darknessDistance) {
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.DARKNESS,
                            40,
                            darknessAmplifier,
                            true,
                            false,
                            true
                    ));
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.BLINDNESS,
                            30,
                            blindnessAmplifier,
                            true,
                            false,
                            true
                    ));

                    if (tickCounter % wardenSoundEveryTicks == 0) {
                        player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1.5f, 0.65f);
                        player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_AMBIENT, 0.8f, 0.55f);
                    }
                }

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

            if (distance >= deathDistance) {
                player.setHealth(0.0);
            }
        }
    }
}
