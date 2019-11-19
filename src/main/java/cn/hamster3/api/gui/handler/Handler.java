package cn.hamster3.api.gui.handler;

import cn.hamster3.api.gui.Gui;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;

public abstract class Handler implements InventoryHolder {
    private Gui gui;
    private Inventory inventory;
    private HashMap<Integer, ItemStack> placedItem;

    public Handler(Gui gui) {
        this.gui = gui;
        placedItem = new HashMap<>();
        this.inventory = gui.createNewInventory(this);
    }

    /**
     * 是否允许使用某个物品替换某个位置上的物品
     *
     * @param index 替换位置的索引
     * @param stack 将要替换的物品
     * @return true代表允许
     */
    public abstract boolean canPutItem(int index, ItemStack stack);

    /**
     * 成功向GUI中放入一个物品时会由SwapItemListener调用这个事件
     * 请先确保你已经注册了SwapItemListener监听器
     * 该对象中存储的ItemStack是从Inventory中clone的复制品
     * 因此可以直接修改Inventory中的物品
     *
     * @param index 放入的槽位
     * @param stack 放入的物品
     */
    public void setItem(int index, ItemStack stack) {
        if (stack == null) {
            placedItem.remove(index);
        } else {
            placedItem.put(index, stack);
        }
    }

    public ItemStack getItem(int index) {
        return placedItem.get(index);
    }

    public void takeItem(int index) {
        ItemStack stack = getItem(index);
        if (stack != null) {
            int amount = stack.getAmount();
            if (amount <=  1) {
                stack = null;
            } else {
                stack.setAmount(amount - 1);
            }
        }
        setItem(index, stack);
        if (stack != null) {
            getInventory().setItem(index, stack);
        } else {
            getInventory().setItem(index, getGui().getInventory().getItem(index));
        }
    }

    public Gui getGui() {
        return gui;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Collection<ItemStack> getPlacedItem() {
        return placedItem.values();
    }
}
