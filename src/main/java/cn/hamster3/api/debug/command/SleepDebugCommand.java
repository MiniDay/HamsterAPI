package cn.hamster3.api.debug.command;

import cn.hamster3.api.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SleepDebugCommand extends CommandExecutor {
    public SleepDebugCommand() {
        super("sleep", "暂停服务器主线程", "毫秒数");
    }

    @Override
    public boolean isPlayerCommand() {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        long time;
        try {
            time = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§c暂停时间必须是一个整数!");
            return true;
        }
        if (time < 0) {
            sender.sendMessage("§c暂停时间必须大于0!");
            return true;
        }
        sender.sendMessage("§a暂停主线程 " + time + " 毫秒!");
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sender.sendMessage("§a服务器主线程已恢复运行!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
