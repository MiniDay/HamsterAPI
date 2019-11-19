package cn.hamster3.api;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("DisplayMessage")
public class DisplayMessage implements ConfigurationSerializable {
    private String message;

    private String actionBar;

    private String title;
    private String subTitle;

    private Sound sound;
    private int volume;
    private int pitch;

    public DisplayMessage(Map<String, Object> map) {
        message = HamsterAPI.replaceColorCode((String) map.getOrDefault("message", null));
        actionBar = HamsterAPI.replaceColorCode((String) map.getOrDefault("actionBar", null));

        title = HamsterAPI.replaceColorCode((String) map.getOrDefault("title", null));
        subTitle = HamsterAPI.replaceColorCode((String) map.getOrDefault("subTitle", null));

        String s = (String) map.getOrDefault("sound", null);
        if (s != null) {
            try {
                sound = Sound.valueOf(s);
                volume = (int) map.get("volume");
                pitch = (int) map.get("pitch");
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public DisplayMessage(String message, boolean isActionBar) {
        if (isActionBar) {
            this.actionBar = message;
        } else {
            this.message = message;
        }
    }

    public DisplayMessage(String title, String subTitle) {
        this.title = title;
        this.subTitle = subTitle;
    }

    public DisplayMessage(Sound sound, int volume, int pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public DisplayMessage(String message, String actionBar, String title, String subTitle, Sound sound, int volume, int pitch) {
        this.message = message;
        this.actionBar = actionBar;
        this.title = title;
        this.subTitle = subTitle;
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void show(Player player, Map<String, String> replace) {
        String s1, s2;
        if (message != null) {
            s1 = message;
            if (replace != null) {
                for (String key : replace.keySet()) {
                    s1 = s1.replace(key, replace.get(key));
                }
            }
            player.sendMessage(s1);
        }

        if (actionBar != null) {
            s1 = actionBar;
            if (replace != null) {
                for (String key : replace.keySet()) {
                    s1 = s1.replace(key, replace.get(key));
                }
            }
            HamsterAPI.sendActionBar(player, s1);
        }

        if (title != null || subTitle != null) {
            s1 = title == null ? "" : title;
            s2 = subTitle == null ? "" : subTitle;
            if (replace != null) {
                for (String key : replace.keySet()) {
                    s1 = s1.replace(key, replace.get(key));
                    s2 = s2.replace(key, replace.get(key));
                }
            }
            player.sendTitle(s1, s2);
        }

        if (sound != null) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public void show(CommandSender sender, Map<String, String> replace) {
        if (message != null) {
            String s1 = message;
            if (replace != null) {
                for (String key : replace.keySet()) {
                    s1 = s1.replace(key, replace.get(key));
                }
            }
            sender.sendMessage(s1);
        }
    }

    public void broadcast(Map<String, String> replace) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            show(player, replace);
        }
        show(Bukkit.getConsoleSender(), replace);
    }

    public ReplaceMessage createMessage() {
        return new ReplaceMessage();
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("actionBar", actionBar);
        map.put("title", title);
        map.put("subTitle", subTitle);
        if (sound != null) {
            map.put("sound", sound.name());
            map.put("volume", volume);
            map.put("pitch", pitch);
        } else {
            map.put("sound", null);
            map.put("volume", null);
            map.put("pitch", null);
        }
        return map;
    }

    private class ReplaceMessage {
        private HashMap<String, String> replace;

        private ReplaceMessage() {
            replace = new HashMap<>();
        }

        public ReplaceMessage replaceMessage(String key, String value) {
            replace.put(key, value);
            return this;
        }

        public void show(Player player) {
            DisplayMessage.this.show(player, replace);
        }

        public void show(CommandSender sender) {
            DisplayMessage.this.show(sender, replace);
        }

        public void broadcast() {
            DisplayMessage.this.broadcast(replace);
        }
    }
}
