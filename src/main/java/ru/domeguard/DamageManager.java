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
    private int soundTimer;

    public DamageManager(DomeGuardPlugin plugin, DomeManager dome) {
        this.plugin = plugin;
        this.dome = dome;
    }

    public void start() {
        // 5 ticks = 0.25 second. This makes the progression feel much smoother.
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 5L, 5L);
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
        double firstPhaseEnd = plugin.getConfig().getDouble("damage.first-phase-end", 10.0);
        double darknessStart = plugin.getConfig().getDouble("damage.darkness-start", 10.0);
        double deathDistance = plugin.getConfig().getDouble("damage.death-distance", 31.0);

        double boundaryDps = plugin.getConfig().getDouble("damage.damage-per-second-at-boundary", 0.5);
        double maxDps = plugin.getConfig().getDouble("damage.damage-per-second-at-30-blocks", 18.0);

        int nauseaMax = plugin.getConfig().getInt("damage.nausea-max-amplifier", 2);
        int slownessMax = plugin.getConfig().getInt("damage.slowness-max-amplifier", 2);
        int weaknessMax = plugin.getConfig().getInt("damage.weakness-max-amplifier", 1);
        int miningFatigueMax = plugin.getConfig().getInt("damage.mining-fatigue-max-amplifier", 1);
        int darknessAmplifier = plugin.getConfig().getInt("damage.darkness-amplifier", 0);
        int blindnessMax = plugin.getConfig().getInt("damage.blindness-max-amplifier", 3);

        int minSoundInterval = Math.max(5,
                plugin.getConfig().getInt("damage.warden-sound-min-interval-ticks", 8));
        int maxSoundInterval = Math.max(minSoundInterval,
                plugin.getConfig().getInt("damage.warden-sound-max-interval-ticks", 40));

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isDead() || dome.isInside(player.getLocation())) {
                continue;
            }

            double distance = dome.distanceOutside(player.getLocation());
            if (distance < warning) {
                continue;
            }

            // 31 blocks = death. Check this before applying more effects.
            if (distance >= deathDistance) {
                player.setHealth(0.0);
                continue;
            }

            // t = 0 at the boundary, 1 at 10 blocks.
            double phaseOne = clamp((distance - warning) / Math.max(0.001, firstPhaseEnd - warning));

            // 0..10 blocks: nausea + slow + weakness + mining fatigue grow gradually.
            int nauseaAmp = scaledAmplifier(phaseOne, nauseaMax);
            int slownessAmp = scaledAmplifier(phaseOne, slownessMax);
            int weaknessAmp = scaledAmplifier(phaseOne, weaknessMax);
            int miningFatigueAmp = scaledAmplifier(phaseOne, miningFatigueMax);

            addEffect(player, PotionEffectType.NAUSEA, nauseaAmp, 12);
            addEffect(player, PotionEffectType.SLOWNESS, slownessAmp, 12);
            addEffect(player, PotionEffectType.WEAKNESS, weaknessAmp, 12);
            addEffect(player, PotionEffectType.MINING_FATIGUE, miningFatigueAmp, 12);

            if (distance >= darknessStart) {
                // 10..30 blocks. The visual blackout is layered and becomes stronger.
                double darknessProgress = clamp((distance - darknessStart)
                        / Math.max(0.001, deathDistance - darknessStart));

                int blindnessAmp = scaledAmplifier(darknessProgress, blindnessMax);
                addEffect(player, PotionEffectType.DARKNESS, darknessAmplifier, 12);
                addEffect(player, PotionEffectType.BLINDNESS, blindnessAmp, 12);

                // Extra Warden-like pressure in the deep zone.
                int slownessDeep = Math.max(slownessAmp,
                        scaledAmplifier(darknessProgress, slownessMax + 1));
                int weaknessDeep = Math.max(weaknessAmp,
                        scaledAmplifier(darknessProgress, weaknessMax + 1));
                addEffect(player, PotionEffectType.SLOWNESS, slownessDeep, 12);
                addEffect(player, PotionEffectType.WEAKNESS, weaknessDeep, 12);

                playWardenHeartbeat(player, darknessProgress, minSoundInterval, maxSoundInterval);
            }

            // Smooth damage curve: low at the boundary, rapidly increasing near 30 blocks.
            double progressToDeath = clamp((distance - warning)
                    / Math.max(0.001, deathDistance - warning));
            double curved = Math.pow(progressToDeath, 2.0);
            double dps = boundaryDps + (maxDps - boundaryDps) * curved;
            double damageThisTick = dps * 0.25; // task runs every 0.25 sec

            if (damageThisTick > 0.0) {
                player.damage(damageThisTick);
            }
        }
    }

    private void playWardenHeartbeat(Player player, double progress, int minInterval, int maxInterval) {
        // The heartbeat gets faster and louder from 10 to 30 blocks.
        int interval = (int) Math.round(maxInterval - (maxInterval - minInterval) * progress);
        interval = Math.max(minInterval, interval);

        soundTimer += 5;
        if (soundTimer < interval) {
            return;
        }
        soundTimer = 0;

        float volume = (float) (0.65 + progress * 1.35);
        float pitch = (float) (0.75 - progress * 0.18);
        player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, volume, pitch);

        if (progress > 0.65) {
            player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_AMBIENT,
                    0.45f + (float) progress * 0.55f, 0.55f);
        }
    }

    private void addEffect(Player player, PotionEffectType type, int amplifier, int duration) {
        player.addPotionEffect(new PotionEffect(
                type,
                duration,
                Math.max(0, amplifier),
                true,
                false,
                true
        ));
    }

    private int scaledAmplifier(double progress, int maxAmplifier) {
        if (maxAmplifier <= 0) {
            return 0;
        }
        return Math.min(maxAmplifier, (int) Math.floor(progress * (maxAmplifier + 1)));
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
