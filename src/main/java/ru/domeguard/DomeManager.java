package ru.domeguard;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public final class DomeManager {
    private final DomeGuardPlugin plugin;

    private String worldName;
    private double centerX;
    private double centerZ;
    private double radius;
    private double minY;
    private double maxY;

    public DomeManager(DomeGuardPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        FileConfiguration c = plugin.getConfig();

        worldName = c.getString("dome.world", "world");
        centerX = c.getDouble("dome.center-x", 0);
        centerZ = c.getDouble("dome.center-z", 0);
        radius = Math.max(1.0, c.getDouble("dome.radius", 500));
        minY = c.getDouble("dome.min-y", -64);
        maxY = c.getDouble("dome.max-y", 320);

        if (maxY <= minY) {
            maxY = minY + 1;
        }
    }

    public void save() {
        plugin.getConfig().set("dome.world", worldName);
        plugin.getConfig().set("dome.center-x", centerX);
        plugin.getConfig().set("dome.center-z", centerZ);
        plugin.getConfig().set("dome.radius", radius);
        plugin.getConfig().set("dome.min-y", minY);
        plugin.getConfig().set("dome.max-y", maxY);
        plugin.saveConfig();
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("dome.enabled", true);
    }

    public boolean isInside(Location loc) {
        World world = loc.getWorld();
        if (world == null || !world.getName().equals(worldName)) {
            return true;
        }

        double dx = loc.getX() - centerX;
        double dz = loc.getZ() - centerZ;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        return horizontalDistance <= radius
                && loc.getY() >= minY
                && loc.getY() <= maxY;
    }

    public double distanceOutside(Location loc) {
        if (loc.getWorld() == null || !loc.getWorld().getName().equals(worldName)) {
            return 0.0;
        }

        double dx = loc.getX() - centerX;
        double dz = loc.getZ() - centerZ;
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        double side = Math.max(0.0, horizontal - radius);
        double top = Math.max(0.0, loc.getY() - maxY);
        double bottom = Math.max(0.0, minY - loc.getY());

        return Math.max(side, Math.max(top, bottom));
    }

    public void setFromLocation(Location loc) {
        if (loc.getWorld() != null) {
            worldName = loc.getWorld().getName();
        }
        centerX = loc.getX();
        centerZ = loc.getZ();
        save();
    }

    public DomeGuardPlugin getPlugin() { return plugin; }
    public String getWorldName() { return worldName; }
    public double getCenterX() { return centerX; }
    public double getCenterZ() { return centerZ; }
    public double getRadius() { return radius; }
    public double getMinY() { return minY; }
    public double getMaxY() { return maxY; }

    public void setRadius(double value) {
        radius = Math.max(1.0, value);
        save();
    }

    public void setMinY(double value) {
        minY = Math.min(value, maxY - 1.0);
        save();
    }

    public void setMaxY(double value) {
        maxY = Math.max(value, minY + 1.0);
        save();
    }
}
