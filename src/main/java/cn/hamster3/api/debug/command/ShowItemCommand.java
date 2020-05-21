package cn.hamster3.api.debug.command;

import cn.hamster3.api.HamsterAPI;
import cn.hamster3.api.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ShowItemCommand extends CommandExecutor {
    public ShowItemCommand() {
        super("show", "展示手中的物品");
    }

    @Override
    public boolean isPlayerCommand() {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.spigot().sendMessage(HamsterAPI.getItemDisplayInfo("§a[展示物品 ", ((Player) sender).getItemInHand(), " §a]"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
