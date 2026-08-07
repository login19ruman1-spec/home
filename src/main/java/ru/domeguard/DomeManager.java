package ru.domeguard;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public final class DomeManager {
    private final DomeGuardPlugin plugin;

    private String worldName;
    private double centerX;
    private double centerZ;
    private double radiusX;
    private double radiusZ;
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

        // v1.2: independent horizontal semi-axes.
        radiusX = Math.max(1.0, c.getDouble("dome.radius-x", c.getDouble("dome.radius", 500)));
        radiusZ = Math.max(1.0, c.getDouble("dome.radius-z", c.getDouble("dome.radius", 500)));

        minY = c.getDouble("dome.min-y", -64);
        maxY = c.getDouble("dome.max-y", 320);

        if (maxY <= minY) {
            maxY = minY + 1;
        }

        save();
    }

    public void save() {
        plugin.getConfig().set("dome.world", worldName);
        plugin.getConfig().set("dome.center-x", centerX);
        plugin.getConfig().set("dome.center-z", centerZ);
        plugin.getConfig().set("dome.radius-x", radiusX);
        plugin.getConfig().set("dome.radius-z", radiusZ);
        // Keep old key for compatibility with old configs.
        plugin.getConfig().set("dome.radius", Math.max(radiusX, radiusZ));
        plugin.getConfig().set("dome.min-y", minY);
        plugin.getConfig().set("dome.max-y", maxY);
        plugin.saveConfig();
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("dome.enabled", true);
    }

    public boolean isInside(Location loc) {
        if (!sameWorld(loc)) {
            return true;
        }

        double dx = loc.getX() - centerX;
        double dz = loc.getZ() - centerZ;
        double normalizedX = dx / radiusX;
        double normalizedZ = dz / radiusZ;
        double horizontalValue = normalizedX * normalizedX + normalizedZ * normalizedZ;

        return horizontalValue <= 1.0
                && loc.getY() >= minY
                && loc.getY() <= maxY;
    }

    /**
     * Returns the approximate number of blocks outside the nearest boundary.
     * For the horizontal boundary this uses the ellipse's local radial scale,
     * so X and Z can have different sizes.
     */
    public double distanceOutside(Location loc) {
        if (!sameWorld(loc)) {
            return 0.0;
        }

        double dx = loc.getX() - centerX;
        double dz = loc.getZ() - centerZ;
        double normalizedDistance = Math.sqrt(
                (dx * dx) / (radiusX * radiusX)
                        + (dz * dz) / (radiusZ * radiusZ)
        );

        // Convert normalized distance to an approximate block distance using
        // the nearest local horizontal radius. This keeps damage progressive.
        double localRadius = Math.min(radiusX, radiusZ);
        double horizontal = Math.max(0.0, (normalizedDistance - 1.0) * localRadius);

        double top = Math.max(0.0, loc.getY() - maxY);
        double bottom = Math.max(0.0, minY - loc.getY());

        return Math.max(horizontal, Math.max(top, bottom));
    }

    private boolean sameWorld(Location loc) {
        World world = loc.getWorld();
        return world != null && world.getName().equals(worldName);
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
    public double getRadiusX() { return radiusX; }
    public double getRadiusZ() { return radiusZ; }
    public double getMinY() { return minY; }
    public double getMaxY() { return maxY; }

    public void setRadiusX(double value) {
        radiusX = Math.max(1.0, value);
        save();
    }

    public void setRadiusZ(double value) {
        radiusZ = Math.max(1.0, value);
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
