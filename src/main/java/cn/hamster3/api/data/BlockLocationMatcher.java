package cn.hamster3.api.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

@SuppressWarnings("unused")
public class BlockLocationMatcher {
    private String worldName;
    private int x;
    private int y;
    private int z;

    private World world;
    private Location location;

    public BlockLocationMatcher(Location location) {
        world = location.getWorld();
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();

        worldName = world.getName();
    }

    public BlockLocationMatcher(ConfigurationSection config) {
        worldName = config.getString("worldName");
        x = config.getInt("x");
        x = config.getInt("x");
        x = config.getInt("x");

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
