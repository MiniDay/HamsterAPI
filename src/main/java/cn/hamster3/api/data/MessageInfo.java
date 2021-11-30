package cn.hamster3.api.data;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class MessageInfo {
    private final String message;

    private final String actionbar;
    private final String title;
    private final String subtitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    private final Sound sound;
    private final float volume;
    private final float pitch;

    public MessageInfo(ConfigurationSection config) {
        if (config.contains("message")) {
            message = config.getString("message", "").replace("&", "§");
        } else {
            message = null;
        }
        if (config.contains("actionbar")) {
            actionbar = config.getString("actionbar", "").replace("&", "§");
        } else {
            actionbar = null;
        }
        if (config.contains("title")) {
            title = config.getString("title", "").replace("&", "§");
        } else {
            title = null;
        }
        if (config.contains("subtitle")) {
            subtitle = config.getString("subtitle", "").replace("&", "§");
        } else {
            subtitle = null;
        }

        fadeIn = config.getInt("fadeIn", 20);
        stay = config.getInt("stay", 60);
        fadeOut = config.getInt("fadeOut", 20);

        if (config.contains("sound")) {
            sound = Sound.valueOf(config.getString("sound"));
        } else {
            sound = null;
        }
        volume = (float) config.getDouble("volume", 1);
        pitch = (float) config.getDouble("pitch", 1);
    }

    public void show(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            show(player);
        }
    }

    public void show(HumanEntity player) {
        show((Player) player);
    }

    public void show(Player player) {
        if (message != null) {
            player.sendMessage(message);
        }
        if (actionbar != null) {
            player.sendMessage(actionbar);
        }
        if (title != null || subtitle != null) {
            String sendTitle = title == null ? "" : title;
            String sendSubtitle = subtitle == null ? "" : subtitle;
            player.sendTitle(sendTitle, sendSubtitle, fadeIn, stay, fadeOut);
        }
        if (sound != null) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public void show(Player player, Map<String, String> replacer) {
        if (message != null) {
            String sendMessage = message;
            for (Map.Entry<String, String> entry : replacer.entrySet()) {
                sendMessage = sendMessage.replace(entry.getKey(), entry.getValue());
            }
            player.sendMessage(sendMessage);
        }
        if (actionbar != null) {
            String sendActionbar = actionbar;
            for (Map.Entry<String, String> entry : replacer.entrySet()) {
                sendActionbar = sendActionbar.replace(entry.getKey(), entry.getValue());
            }
            player.sendMessage(sendActionbar);
        }
        if (title != null || subtitle != null) {
            String sendTitle = title == null ? "" : title;
            String sendSubtitle = subtitle == null ? "" : subtitle;
            for (Map.Entry<String, String> entry : replacer.entrySet()) {
                sendTitle = sendTitle.replace(entry.getKey(), entry.getValue());
                sendSubtitle = sendSubtitle.replace(entry.getKey(), entry.getValue());
            }
            player.sendTitle(sendTitle, sendSubtitle, fadeIn, stay, fadeOut);
        }
        if (sound != null) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public String getMessage() {
        return message;
    }

    public String getActionbar() {
        return actionbar;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public int getStay() {
        return stay;
    }

    public int getFadeOut() {
        return fadeOut;
    }

    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "message='" + message + '\'' +
                ", actionbar='" + actionbar + '\'' +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", fadeIn=" + fadeIn +
                ", stay=" + stay +
                ", fadeOut=" + fadeOut +
                ", sound=" + sound +
                ", volume=" + volume +
                ", pitch=" + pitch +
                '}';
    }
}
