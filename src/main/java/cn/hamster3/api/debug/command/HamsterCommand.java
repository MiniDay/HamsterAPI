package cn.hamster3.api.debug.command;

import cn.hamster3.api.HamsterAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HamsterCommand implements TabExecutor {
    private HamsterAPI hamster;

    public HamsterCommand(HamsterAPI hamster) {
        this.hamster = hamster;
    }

    @Override
    @Deprecated
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 1) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("yml")) {
            YamlConfiguration yml = new YamlConfiguration();
            yml.set("version", Bukkit.getVersion());
            yml.set("bukkitVersion", Bukkit.getBukkitVersion());
            if (sender instanceof Player) {
                Player player = (Player) sender;
                yml.set("location", player.getLocation());
                yml.set("hand", player.getInventory().getItemInHand());
            }
            ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta meta = stack.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("test lore");
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
            meta.setDisplayName("test item");
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.values());
            stack.setItemMeta(meta);
            yml.set("testItem", stack);
            try {
                yml.save(new File(hamster.getDataFolder(), "test.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            sender.sendMessage("§a信息已保存至test.yml");
        }
        if (args[0].equalsIgnoreCase("sleep")) {
            long time;
            try {
                time = Long.parseLong(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§c暂停时间必须是一个整数!");
                return true;
            }
            sender.sendMessage("§a暂停主线程 " + time + " 毫秒!");
            try {
                Thread.sleep(time);
            } catch (Exception e) {
                e.printStackTrace();
            }
            sender.sendMessage("§a服务器主线程已恢复运行!");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§e======================");
        sender.sendMessage("§a/hAPI yml");
        sender.sendMessage("§b保存测试信息到YAML文件中");
        sender.sendMessage("§a/hAPI sleep <毫秒数>");
        sender.sendMessage("§b暂停主线程n毫秒");
        sender.sendMessage("§e======================");
    }
}
