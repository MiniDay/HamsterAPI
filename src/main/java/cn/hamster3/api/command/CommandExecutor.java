package cn.hamster3.api.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public abstract class CommandExecutor implements TabExecutor {
    private String name;
    private String description;
    private String permission;
    private String permissionMessage;

    private String[] parameters;

    public CommandExecutor(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public CommandExecutor(String name, String description, String... parameters) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
    }

    public CommandExecutor(String name, String description, String permission, String permissionMessage) {
        this(name, description);
        this.permission = permission;
        this.permissionMessage = permissionMessage;
    }

    public CommandExecutor(String name, String description, String permission, String permissionMessage, String... parameters) {
        this(name, description, parameters);
        this.permission = permission;
        this.permissionMessage = permissionMessage;
    }

    public boolean checkPermission(CommandSender sender) {
        if (permission == null) {
            return true;
        }
        return sender.hasPermission(permission);
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPermissionMessage() {
        return permissionMessage;
    }

    public String[] getParameters() {
        return parameters;
    }

    public abstract boolean isPlayerCommand();

    @Override
    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);

    @Override
    public abstract List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommandExecutor that = (CommandExecutor) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "CommandExecutor{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", permission='" + permission + '\'' +
                ", permissionMessage='" + permissionMessage + '\'' +
                '}';
    }
}
