package ru.domeguard;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.List;

public final class DamageManager {
    private final DomeGuardPlugin plugin;
    private final DomeManager dome;
    private final CurseManager curse;
    private final Set<UUID> damageImmune = new HashSet<>();
    private BukkitTask task;
    private int soundTimer;

    public DamageManager(DomeGuardPlugin plugin, DomeManager dome, CurseManager curse) {
        this.plugin = plugin; this.dome = dome; this.curse = curse;
        damageImmune.addAll(plugin.getConfig().getStringList("damage-immune-players").stream().map(s -> { try { return UUID.fromString(s); } catch (Exception e) { return null; } }).filter(java.util.Objects::nonNull).toList());
    }

    public void start() { task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 5L, 5L); }
    public void stop() { if (task != null) task.cancel(); }
    public boolean isDamageImmune(UUID id) { return damageImmune.contains(id); }
    public int getDamageImmuneCount() { return damageImmune.size(); }
    public void toggleDamageImmunity(UUID id) { if (!damageImmune.remove(id)) damageImmune.add(id); saveImmune(); }
    private void saveImmune() { plugin.getConfig().set("damage-immune-players", damageImmune.stream().map(UUID::toString).toList()); plugin.saveConfig(); }

    private void tick() {
        if (!dome.isEnabled()) return;
        double warning = plugin.getConfig().getDouble("damage.warning-distance", 0.0);
        double firstEnd = plugin.getConfig().getDouble("damage.first-phase-end", 10.0);
        double darknessStart = plugin.getConfig().getDouble("damage.darkness-start", 10.0);
        double deathDistance = plugin.getConfig().getDouble("damage.death-distance", 51.0);
        double clearDistance = plugin.getConfig().getDouble("damage.clear-effects-distance", 41.0);
        double boundaryDps = plugin.getConfig().getDouble("damage.damage-per-second-at-boundary", 0.5);
        double maxDps = plugin.getConfig().getDouble("damage.damage-per-second-at-death", 20.0);
        int nauseaMax = plugin.getConfig().getInt("damage.nausea-max-amplifier", 2);
        int slownessMax = plugin.getConfig().getInt("damage.slowness-max-amplifier", 2);
        int weaknessMax = plugin.getConfig().getInt("damage.weakness-max-amplifier", 1);
        int fatigueMax = plugin.getConfig().getInt("damage.mining-fatigue-max-amplifier", 1);
        int blindnessMax = plugin.getConfig().getInt("damage.blindness-max-amplifier", 3);
        int minInterval = Math.max(5, plugin.getConfig().getInt("damage.warden-sound-min-interval-ticks", 8));
        int maxInterval = Math.max(minInterval, plugin.getConfig().getInt("damage.warden-sound-max-interval-ticks", 40));

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isDead()) continue;
            if (dome.isInside(p.getLocation())) {
                double d = dome.distanceOutside(p.getLocation());
                if (d > 0 && d <= clearDistance) clearDomeEffects(p);
                continue;
            }
            double distance = dome.distanceOutside(p.getLocation());
            if (distance < warning) continue;
            if (distance >= deathDistance) {
                curse.curse(p);
                if (!isDamageImmune(p.getUniqueId())) p.setHealth(0.0);
                continue;
            }
            double phaseOne = clamp((distance - warning) / Math.max(0.001, firstEnd - warning));
            addEffect(p, PotionEffectType.NAUSEA, scaled(phaseOne, nauseaMax), 12);
            addEffect(p, PotionEffectType.SLOWNESS, scaled(phaseOne, slownessMax), 12);
            addEffect(p, PotionEffectType.WEAKNESS, scaled(phaseOne, weaknessMax), 12);
            addEffect(p, PotionEffectType.MINING_FATIGUE, scaled(phaseOne, fatigueMax), 12);
            if (distance >= darknessStart) {
                double deep = clamp((distance - darknessStart) / Math.max(0.001, deathDistance - darknessStart));
                addEffect(p, PotionEffectType.DARKNESS, 0, 12);
                addEffect(p, PotionEffectType.BLINDNESS, scaled(deep, blindnessMax), 12);
                addEffect(p, PotionEffectType.SLOWNESS, Math.max(scaled(phaseOne, slownessMax), scaled(deep, slownessMax + 1)), 12);
                addEffect(p, PotionEffectType.WEAKNESS, Math.max(scaled(phaseOne, weaknessMax), scaled(deep, weaknessMax + 1)), 12);
                playWarden(p, deep, minInterval, maxInterval);
            }
            if (!isDamageImmune(p.getUniqueId())) {
                double progress = clamp((distance - warning) / Math.max(0.001, deathDistance - warning));
                double dps = boundaryDps + (maxDps - boundaryDps) * Math.pow(progress, 2.0);
                p.damage(dps * 0.25);
            }
        }
    }

    private void clearDomeEffects(Player p) {
        for (PotionEffectType type : List.of(
                PotionEffectType.NAUSEA,
                PotionEffectType.SLOWNESS,
                PotionEffectType.WEAKNESS,
                PotionEffectType.MINING_FATIGUE,
                PotionEffectType.DARKNESS,
                PotionEffectType.BLINDNESS)) {
            p.removePotionEffect(type);
        }
    }
    private void playWarden(Player p, double progress, int min, int max) {
        soundTimer += 5;
        int interval = Math.max(min, (int)Math.round(max - (max-min)*progress));
        if (soundTimer < interval) return; soundTimer = 0;
        p.playSound(p.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 0.65f + (float)progress*1.35f, 0.75f - (float)progress*0.18f);
        if (progress > 0.65) p.playSound(p.getLocation(), Sound.ENTITY_WARDEN_AMBIENT, 0.45f + (float)progress*0.55f, 0.55f);
    }
    private void addEffect(Player p, PotionEffectType type, int amp, int duration) { p.addPotionEffect(new PotionEffect(type, duration, Math.max(0, amp), true, false, true)); }
    private int scaled(double progress, int max) { return max <= 0 ? 0 : Math.min(max, (int)Math.floor(progress*(max+1))); }
    private double clamp(double v) { return Math.max(0, Math.min(1, v)); }
}
