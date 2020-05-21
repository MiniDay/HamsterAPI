package cn.hamster3.api.debug.command;

import cn.hamster3.api.HamsterAPI;
import cn.hamster3.api.command.CommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

public class YamlDebugCommand extends CommandExecutor {
    private HamsterAPI hamster;

    public YamlDebugCommand(HamsterAPI hamster) {
        super("yml", "将测试数据存放至yml文件中");
        this.hamster = hamster;
    }

    @Override
    public boolean isPlayerCommand() {
        return false;
    }

    @Override
    @SuppressWarnings({"ConstantConditions", "deprecation"})
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
