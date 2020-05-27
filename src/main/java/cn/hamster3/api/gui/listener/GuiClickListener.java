package cn.hamster3.api.gui.listener;

import cn.hamster3.api.gui.handler.Handler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GuiClickListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) {
            return;
        }
        if (!(inventory.getHolder() instanceof Handler)) {
            return;
        }
        Handler handler = (Handler) inventory.getHolder();
        handler.click(event);
    }
}
