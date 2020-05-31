package cn.hamster3.api.gui;

import cn.hamster3.api.HamsterAPI;
import cn.hamster3.api.gui.swapper.MapSwapper;
import cn.hamster3.api.gui.swapper.Swapper;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一个GUI对象
 * <p>
 * 建议通过ConfigurationSection来实例化这个对象
 * <p>
 * 详细格式请参考TestGUI.yml
 */

@SuppressWarnings("all")
public class Gui implements InventoryHolder {
    protected HashMap<String, ItemStack> buttonType;
    protected HashMap<String, Integer> buttonIndex;
    private String title;
    private List<String> graphic;
    private Swapper swapper;
    private int size;
    private Inventory inventory;

    public Gui(ConfigurationSection config) {
        this(HamsterAPI.replaceColorCode(config.getString("title")),
                config.getStringList("graphic"),
                new MapSwapper(config.getConfigurationSection("items")));
        buttonIndex = new HashMap<>();
        if (config.contains("buttonIndex")) {
            buttonIndex = new HashMap<>();
            ConfigurationSection buttonIndexConfig = config.getConfigurationSection("buttonIndex");
            for (String key : buttonIndexConfig.getKeys(false)) {
                buttonIndex.put(key, getIndex(buttonIndexConfig.getString(key).charAt(0)));
            }
        }
        if (config.contains("buttonType")) {
            buttonType = new HashMap<>();
            ConfigurationSection buttonTypeConfig = config.getConfigurationSection("buttonType");
            for (String key : buttonTypeConfig.getKeys(false)) {
                buttonType.put(key, getSwapper().getItemStackByChar(buttonTypeConfig.getString(key).charAt(0)));
            }
        }
    }

    public Gui(String title, List<String> graphic, Swapper swapper) {
        if (graphic.size() == 0) {
            throw new IllegalArgumentException("graphic size can not be less than 1");
        }
        this.title = title;
        this.graphic = graphic;
        this.swapper = swapper;
        size = graphic.size();
        if (size > 6) size = 6;
        inventory = Bukkit.createInventory(this, size * 9, title);
        for (int i = 0; i < graphic.size(); i++) {
            String s = graphic.get(i);
            for (int j = 0; j < s.length() && j < 9; j++) {
                inventory.setItem(i * 9 + j, swapper.getItemStackByChar(graphic.get(i).charAt(j)));
            }
        }
    }

    //todo 这个方法可能有bug
    public Character indexOf(int i) {
        if (i < 0) return null;
        if (i / 9 >= graphic.size()) return null;
        String s = graphic.get(i / 9);
        return s.charAt(i % 9);
    }

    public ArrayList<Integer> getAllIndex(char c) {
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < graphic.size(); i++) {
            String s = graphic.get(i);
            for (int j = 0; j < s.length() && j < 9; j++) {
                if (s.charAt(j) == c) {
                    integers.add(i * 9 + j);
                }
            }
        }
        return integers;
    }

    public int getIndex(char c) {
        for (int i = 0; i < graphic.size(); i++) {
            String s = graphic.get(i);
            for (int j = 0; j < s.length() && j < 9; j++) {
                if (s.charAt(j) == c) {
                    return i * 9 + j;
                }
            }
        }
        return -1;
    }

    public Inventory createNewInventory() {
        Inventory inv = Bukkit.createInventory(this, size * 9, title);
        inv.setContents(inventory.getContents());
        return inv;
    }

    public Inventory createNewInventory(InventoryHolder holder) {
        Inventory inv = Bukkit.createInventory(holder, size * 9, title);
        inv.setContents(inventory.getContents());
        return inv;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getGraphic() {
        return graphic;
    }

    public Swapper getSwapper() {
        return swapper;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public String getActionType(ItemStack clickedStack) {
        if (buttonType == null) {
            return "not defined";
        }
        if (HamsterAPI.isEmptyItemStack(clickedStack)) {
            return "null";
        }
        for (Map.Entry<String, ItemStack> entry : buttonType.entrySet()) {
            if (clickedStack.isSimilar(entry.getValue())) {
                return entry.getKey();
            }
        }
        return "null";
    }

    public String getActionType(int index) {
        if (buttonIndex == null) {
            return "not defined";
        }
        for (Map.Entry<String, Integer> entry : buttonIndex.entrySet()) {
            if (entry.getValue() == index) {
                return entry.getKey();
            }
        }
        return "null";
    }
}
