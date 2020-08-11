package cn.hamster3.api.runnable;

import org.bukkit.plugin.Plugin;

public abstract class DailyTask implements Runnable {
    private final Plugin plugin;
    private final String name;
    private final boolean sync;

    public DailyTask(Plugin plugin, String name, boolean sync) {
        this.plugin = plugin;
        this.name = name;
        this.sync = sync;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public String getName() {
        return name;
    }

    public boolean isSync() {
        return sync;
    }
}
