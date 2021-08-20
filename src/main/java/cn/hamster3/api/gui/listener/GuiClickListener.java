package cn.hamster3.api.gui.listener;

import cn.hamster3.api.gui.handler.Handler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class GuiClickListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(inventory.getHolder() instanceof Handler)) {
            return;
        }
        Handler handler = (Handler) inventory.getHolder();
        if (event.getClickedInventory() == inventory) {
            handler.click(event);
        } else {
            handler.clickBottom(event);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof Handler)) {
            return;
        }
        Handler handler = (Handler) inventory.getHolder();
        handler.drag(event);
    }
}
