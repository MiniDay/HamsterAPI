package cn.hamster3.api.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

@SuppressWarnings({"unused", "ConstantConditions", "RedundantSuppression"})
public class BlockLocationMatcher {
    private final String worldName;
    private final int x;
    private final int y;
    private final int z;

    private final World world;
    private final Location location;

    public BlockLocationMatcher(Location location) {
        world = location.getWorld();
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();

        worldName = world.getName();
        this.location = location;
    }

    public BlockLocationMatcher(ConfigurationSection config) {
        worldName = config.getString("worldName");
        x = config.getInt("x");
        y = config.getInt("y");
        z = config.getInt("z");

        world = Bukkit.getWorld(worldName);
        location = new Location(world, x, y, z);
    }

    public boolean match(Location location) {
        if (world != location.getWorld()) {
            return false;
        }
        if (x != location.getBlockX()) {
            return false;
        }
        if (y != location.getBlockY()) {
            return false;
        }
        return z == location.getBlockZ();
    }

    public Location getLocation() {
        return location;
    }

    public String getWorldName() {
        return worldName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockLocationMatcher that = (BlockLocationMatcher) o;

        if (x != that.x) return false;
        if (y != that.y) return false;
        if (z != that.z) return false;
        return worldName.equals(that.worldName);
    }

    @Override
    public int hashCode() {
        int result = worldName.hashCode();
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "BlockLocationMatcher{" +
                "worldName='" + worldName + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
