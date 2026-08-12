package ru.domeguard;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
public final class DomeManager {
 private final DomeGuardPlugin plugin; private String worldName; private double cx,cz,rx,rz,minY,maxY;
 public DomeManager(DomeGuardPlugin p){plugin=p;load();}
 public void load(){FileConfiguration c=plugin.getConfig();worldName=c.getString("dome.world","world");cx=c.getDouble("dome.center-x");cz=c.getDouble("dome.center-z");double r=c.getDouble("dome.radius",500);rx=Math.max(1,c.getDouble("dome.radius-x",r));rz=Math.max(1,c.getDouble("dome.radius-z",r));minY=c.getDouble("dome.min-y",-64);maxY=c.getDouble("dome.max-y",320);}
 public boolean isEnabled(){return plugin.getConfig().getBoolean("dome.enabled",true);}
 public boolean isInside(Location l){if(l==null||l.getWorld()==null||!l.getWorld().getName().equals(worldName))return true;double dx=Math.abs(l.getX()-cx),dz=Math.abs(l.getZ()-cz);return dx<=rx&&dz<=rz&&l.getY()>=minY&&l.getY()<=maxY;}
 public double distanceOutside(Location l){if(l==null||l.getWorld()==null||!l.getWorld().getName().equals(worldName))return 0;double dx=Math.max(0,Math.abs(l.getX()-cx)-rx),dz=Math.max(0,Math.abs(l.getZ()-cz)-rz),dy=Math.max(Math.max(minY-l.getY(),l.getY()-maxY),0);return Math.max(Math.max(dx,dz),dy);}
 public void save(){plugin.getConfig().set("dome.world",worldName);plugin.getConfig().set("dome.center-x",cx);plugin.getConfig().set("dome.center-z",cz);plugin.getConfig().set("dome.radius-x",rx);plugin.getConfig().set("dome.radius-z",rz);plugin.getConfig().set("dome.min-y",minY);plugin.getConfig().set("dome.max-y",maxY);plugin.saveConfig();}
 public void setRadiusX(double v){rx=Math.max(1,v);save();} public void setRadiusZ(double v){rz=Math.max(1,v);save();} public void setMinY(double v){minY=v;save();} public void setMaxY(double v){maxY=v;save();}
 public String getWorldName(){return worldName;} public double getCenterX(){return cx;} public double getCenterZ(){return cz;} public double getRadiusX(){return rx;} public double getRadiusZ(){return rz;} public double getMinY(){return minY;} public double getMaxY(){return maxY;}
}
