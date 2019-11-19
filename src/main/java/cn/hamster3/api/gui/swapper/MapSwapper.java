package cn.hamster3.api.gui.swapper;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MapSwapper implements Swapper {
    private Map<Character, ItemStack> items;

    public MapSwapper(Map<Character, ItemStack> items) {
        this.items = items;
    }

    public MapSwapper(ConfigurationSection config) {
        items = new HashMap<>();
        for (String s : config.getKeys(false)) {
            if (s.length() != 1) {
                continue;
            }
            items.put(s.charAt(0), config.getItemStack(s));
        }
    }

    public MapSwapper() {
        items = new HashMap<>();
    }

    public void put(char c, ItemStack itemStack) {
        items.put(c, itemStack);
    }

    @Override
    public ItemStack getItemStackByChar(char c) {
        return items.get(c);
    }
}
