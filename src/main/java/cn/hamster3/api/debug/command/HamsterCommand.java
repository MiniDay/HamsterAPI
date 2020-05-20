package cn.hamster3.api.debug.command;

import cn.hamster3.api.HamsterAPI;
import cn.hamster3.api.command.CommandManager;
import org.bukkit.command.PluginCommand;

public class HamsterCommand extends CommandManager {
    public HamsterCommand(PluginCommand command, HamsterAPI hamster) {
        super(
                command,
                new YamlDebugCommand(hamster),
                new SleepDebugCommand()
        );
    }

    @Override
    public boolean isPlayerCommand() {
        return false;
    }
}
