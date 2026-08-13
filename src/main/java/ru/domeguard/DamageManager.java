package ru.domeguard;
import org.bukkit.*;import org.bukkit.entity.Player;import org.bukkit.potion.*;import org.bukkit.scheduler.BukkitTask;import java.util.*;
public final class DamageManager {private final DomeGuardPlugin plugin;private final DomeManager dome;private final CurseManager curse;private final Set<UUID> immune=new HashSet<>();private BukkitTask task;
 public DamageManager(DomeGuardPlugin p,DomeManager d,CurseManager c){plugin=p;dome=d;curse=c;for(String s:p.getConfig().getStringList("damage-immune-players"))try{immune.add(UUID.fromString(s));}catch(Exception ignored){}}
 public void start(){task=plugin.getServer().getScheduler().runTaskTimer(plugin,this::tick,20,20);}public void stop(){if(task!=null)task.cancel();}public boolean isDamageImmune(UUID u){return immune.contains(u);}public int getDamageImmuneCount(){return immune.size();}
 public void toggleDamageImmunity(UUID u){if(!immune.add(u))immune.remove(u);plugin.getConfig().set("damage-immune-players",immune.stream().map(UUID::toString).toList());plugin.saveConfig();}
 private void tick(){if(!dome.isEnabled())return;for(Player p:plugin.getServer().getOnlinePlayers()){double out=dome.distanceOutside(p.getLocation());if(out<=0){clear(p);continue;}if(!isDamageImmune(p.getUniqueId())){double max=dome.getPlugin().getConfig().getDouble("damage.death-distance",51);double dps=plugin.getConfig().getDouble("damage.damage-per-second-at-boundary",.5);if(out>=max)dps=plugin.getConfig().getDouble("damage.damage-per-second-at-death",20);p.damage(dps);}if(out>=plugin.getConfig().getDouble("damage.darkness-start",10))add(p,PotionEffectType.DARKNESS,40,plugin.getConfig().getInt("damage.darkness-amplifier",0));if(out>=plugin.getConfig().getDouble("damage.first-phase-end",10)){add(p,PotionEffectType.NAUSEA,40,scaled(out,plugin.getConfig().getInt("damage.nausea-max-amplifier",2)));add(p,PotionEffectType.SLOWNESS,40,scaled(out,plugin.getConfig().getInt("damage.slowness-max-amplifier",2)));}}}
                                  // В классе DomeManager
private final JavaPlugin plugin;

public JavaPlugin getPlugin() {
    return plugin;
}
 private int scaled(double x,int max){double death=plugin.getConfig().getDouble("damage.death-distance",51);return Math.max(0,Math.min(max,(int)Math.floor((x/death)*max)));}
 private void add(Player p,PotionEffectType t,int dur,int amp){p.addPotionEffect(new PotionEffect(t,dur,amp,false,false,false));}private void clear(Player p){for(PotionEffectType t:new PotionEffectType[]{PotionEffectType.DARKNESS,PotionEffectType.NAUSEA,PotionEffectType.SLOWNESS,PotionEffectType.WEAKNESS,PotionEffectType.MINING_FATIGUE,PotionEffectType.BLINDNESS})p.removePotionEffect(t);}
}
