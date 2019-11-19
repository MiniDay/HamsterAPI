package cn.hamster3.api.command;

import cn.hamster3.api.HamsterAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CommandExecutor implements TabExecutor {
    private HamsterAPI hamster;

    public CommandExecutor(HamsterAPI hamster) {
        this.hamster = hamster;
    }

    @Override
    @Deprecated
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 1) {
            sendHelp(sender);
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c这个命令只能由玩家执行!");
            return true;
        }
        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("yml")) {
            YamlConfiguration yml = new YamlConfiguration();
            yml.set("location", player.getLocation());
            yml.set("hand", player.getInventory().getItemInHand());
            try {
                yml.save(new File(hamster.getDataFolder(), "test.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            sender.sendMessage("§a信息已保存至test.yml");
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
        sender.sendMessage("§e======================");
    }
}
