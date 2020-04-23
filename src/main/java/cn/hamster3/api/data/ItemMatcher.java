package cn.hamster3.api.data;

import cn.hamster3.api.HamsterAPI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@SuppressWarnings("unused")
public class ItemMatcher {
    private int limit;
    private int id;
    private int subID;
    private Material type;
    private String name;
    private String keyLore;
    private List<String> lore;

    public ItemMatcher(ConfigurationSection config) {
        limit = config.getInt("limit", 10);
        if (config.contains("id")) {
            String idString = config.getString("id");
            if (idString.contains(":")) {
                String[] args = idString.split(":");
                id = Integer.parseInt(args[0]);
                subID = Integer.parseInt(args[1]);
            } else {
                id = Integer.parseInt(idString);
                subID = -1;
            }
        } else {
            id = subID = -1;
        }
        if (config.contains("name")) {
            type = Material.valueOf(config.getString("type"));
        }
        if (config.contains("name")) {
            name = HamsterAPI.replaceColorCode(config.getString("name"));
        }
        if (config.contains("keyLore")) {
            keyLore = HamsterAPI.replaceColorCode(config.getString("keyLore"));
        }
        if (config.contains("lore")) {
            lore = HamsterAPI.replaceColorCode(config.getStringList("lore"));
        }
    }

    public int getLimit() {
        return limit;
    }

    @SuppressWarnings("deprecation")
    public boolean match(ItemStack stack) {
        if (type != null) {
            if (stack.getType() != type) {
                return false;
            }
        }
        if (id != -1) {
            if (stack.getTypeId() != id) {
                return false;
            }
            if (subID != -1 && stack.getData().getData() != subID) {
                return false;
            }
        }
        if (name != null && !name.equals(HamsterAPI.getItemName(stack))) {
            return false;
        }
        ItemMeta meta = stack.getItemMeta();
        List<String> itemLore = meta.getLore();

        boolean flag = false;
        if (keyLore != null) {
            if (itemLore == null) {
                return false;
            }
            for (String s : itemLore) {
                if (s.contains(keyLore)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return false;
            }
        }
        if (lore != null) {
            if (itemLore == null) {
                return false;
            }
            if (lore.size() != itemLore.size()) {
                return false;
            }
            for (int i = 0; i < lore.size(); i++) {
                if (!lore.get(i).equals(itemLore.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Item{" +
                "limit=" + limit +
                ", id=" + id +
                ", subID=" + subID +
                ", name='" + name + '\'' +
                ", keyLore='" + keyLore + '\'' +
                ", lore=" + lore +
                '}';
    }
}
