package cn.hamster3.api.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unused")
public abstract class CommandManager extends CommandExecutor {
    private final HashSet<CommandExecutor> subCommands;

    public CommandManager(PluginCommand command) {
        super(command.getName(), command.getDescription(), command.getPermission(), command.getPermissionMessage());
        subCommands = new HashSet<>();
    }

    public CommandManager(PluginCommand command, CommandExecutor... commandExecutors) {
        this(command);
        subCommands.addAll(Arrays.asList(commandExecutors));
    }

    protected String helpTop(CommandSender sender, Command command, String label, String[] args) {
        return "§e==================== [" + getName() + "使用帮助] ====================";
    }

    protected String helpButton(CommandSender sender, Command command, String label, String[] args) {
        return "§e==================== [" + getName() + "使用帮助] ====================";
    }

    protected void sendHelp(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§e==================== [" + getName() + "使用帮助] ====================");
        for (CommandExecutor executor : subCommands) {
            if (!executor.checkPermission(sender)) {
                continue;
            }
            StringBuilder usage = new StringBuilder(String.format("§a/%s %s ", getName(), executor.getName()));
            if (executor.getParameters() != null) {
                for (String s : executor.getParameters()) {
                    usage.append("[").append(s).append("] ");
                }
            }
            sender.sendMessage(usage.toString());
            sender.sendMessage("§b" + executor.getDescription());
        }
        sender.sendMessage("§e==================== [" + getName() + "使用帮助] ====================");
    }

    protected boolean defaultCommand(CommandSender sender, Command command, String label, String[] args) {
        sendHelp(sender, command, label, args);
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean isPlayer = sender instanceof Player;
        if (isPlayerCommand() && !isPlayer) {
            sender.sendMessage(getNotPlayerMessage());
            return true;
        }
        if (!checkPermission(sender)) {
            sender.sendMessage(getPermissionMessage());
            return true;
        }
        if (args.length != 0) {
            CommandExecutor executor = getCommandExecutor(args[0]);
            if (executor != null) {
                if (executor.isPlayerCommand() && !isPlayer) {
                    sender.sendMessage(executor.getNotPlayerMessage());
                    return true;
                }
                if (executor.checkPermission(sender)) {
                    return executor.onCommand(sender, command, label, args);
                }
                sender.sendMessage(executor.getPermissionMessage());
                return true;
            }
        }
        return defaultCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!checkPermission(sender)) {
            return null;
        }
        if (args.length == 1) {
            ArrayList<String> strings = new ArrayList<>();
            for (CommandExecutor executor : subCommands) {
                if (!executor.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    continue;
                }
                strings.add(executor.getName());
            }
            return strings;
        }
        CommandExecutor executor = getCommandExecutor(args[0]);
        if (executor == null) {
            return null;
        }
        if (executor.checkPermission(sender)) {
            return executor.onTabComplete(sender, command, alias, args);
        }
        return null;
    }

    public void addCommandExecutor(CommandExecutor... commandExecutors) {
        subCommands.addAll(Arrays.asList(commandExecutors));
    }

    private CommandExecutor getCommandExecutor(String name) {
        for (CommandExecutor executor : subCommands) {
            if (executor.getName().equalsIgnoreCase(name)) {
                return executor;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "CommandManager{" +
                "subCommands=" + subCommands +
                "} " + super.toString();
    }
}
