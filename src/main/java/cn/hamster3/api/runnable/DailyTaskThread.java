package cn.hamster3.api.runnable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class DailyTaskThread extends DailyThread {
    private final ArrayList<DailyTask> tasks;

    public DailyTaskThread() {
        tasks = new ArrayList<>();
    }

    @Override
    public void doTask() {
        for (DailyTask task : tasks) {
            if (task.isSync()) {
                try {
                    Bukkit.getScheduler().runTask(task.getPlugin(), task);
                } catch (IllegalArgumentException ignored) {
                }
            } else {
                try {
                    Bukkit.getScheduler().runTaskAsynchronously(task.getPlugin(), task);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    public ArrayList<DailyTask> getTasks() {
        return tasks;
    }

    public DailyTask getDailyTask(Plugin plugin, String name) {
        for (DailyTask task : tasks) {
            if (task.getPlugin().equals(plugin) && task.getName().equals(name)) {
                return task;
            }
        }
        return null;
    }
}
