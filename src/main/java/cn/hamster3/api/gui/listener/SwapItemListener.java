package cn.hamster3.api.gui.listener;

import cn.hamster3.api.HamsterAPI;
import cn.hamster3.api.gui.Gui;
import cn.hamster3.api.gui.handler.Handler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SwapItemListener implements Listener {
    private Class<? extends Handler> handlerClass;

    public SwapItemListener(Class<? extends Handler> handler) {
        this.handlerClass = handler;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    @Deprecated
    public void putItem(InventoryClickEvent event) {
        int rawSlot = event.getRawSlot();
        if (rawSlot < 0) {
            return;
        }
        if (rawSlot != event.getSlot()) {
            return;
        }
        Inventory inventory = event.getView().getTopInventory();
        InventoryHolder holder = inventory.getHolder();
        if (holder == null || holder.getClass() != handlerClass) {
            return;
        }
        Handler handler = (Handler) holder;
        Gui gui = handler.getGui();
        ItemStack guiItem = gui.getInventory().getItem(rawSlot);
        ItemStack cursor = event.getCursor();

        event.setCancelled(true);
        if (!handler.canPutItem(rawSlot, cursor)) {
            return;
        }

        ItemStack putItem;
        if (HamsterAPI.isEmptyItemStack(cursor)) {
            putItem = guiItem;
        } else {
            putItem = cursor.clone();
            ItemMeta meta = putItem.getItemMeta();
            meta.setDisplayName(meta.getDisplayName() + " ");
            putItem.setItemMeta(meta);
        }

        inventory.setItem(rawSlot, cursor == null ? guiItem : putItem);
        event.setCursor(handler.getItem(rawSlot));
        handler.setItem(rawSlot, cursor);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void returnItem(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (holder == null || holder.getClass() != handlerClass) {
            return;
        }
        Handler handler = (Handler) holder;
        for (ItemStack stack : handler.getPlacedItem()) {
            HamsterAPI.giveItem(event.getPlayer(), stack);
        }
    }
}
