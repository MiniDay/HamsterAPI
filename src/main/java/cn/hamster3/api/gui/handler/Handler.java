package cn.hamster3.api.gui.handler;

import cn.hamster3.api.gui.Gui;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;

@SuppressWarnings("unused")
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
    public boolean canPutItem(int index, ItemStack stack) {
        return false;
    }

    /**
     * 当这个Handler所属的Inventory被点击时发生
     *
     * @param event Inventory点击事件
     */
    public void click(InventoryClickEvent event) {
    }

    /**
     * 成功向GUI中放入一个物品时会由SwapItemListener调用这个事件
     * 请先确保你已经注册了SwapItemListener监听器
     * 该对象中存储的ItemStack是从Inventory中clone的复制品
     * 与Inventory中的物品没有任何关联
     * 因此可以直接修改Inventory中的物品而不会影响到getItem()获取的物品
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

    /**
     * 获取handler内某个位置上的物品
     *
     * @param index 索引
     * @return 物品
     */
    public ItemStack getItem(int index) {
        return placedItem.get(index);
    }

    /**
     * 从handler中取走某个位置上的一个物品
     * 如果物品数量大于1，则物品数量-1
     * 否则将物品置为null
     * 这个操作将会更新视图中的显示物品
     *
     * @param index 物品索引
     */
    public void takeItem(int index) {
        ItemStack stack = getItem(index);
        if (stack != null) {
            int amount = stack.getAmount();
            if (amount <= 1) {
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

    /**
     * 返回这个handler所使用的的gui
     *
     * @return gui
     */
    public Gui getGui() {
        return gui;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * 获取所有被setItem放置的物品
     *
     * @return 所有物品
     */
    public Collection<ItemStack> getPlacedItem() {
        return placedItem.values();
    }
}
