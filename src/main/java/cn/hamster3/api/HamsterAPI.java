package cn.hamster3.api;

import cn.hamster3.api.debug.command.HamsterCommand;
import cn.hamster3.api.gui.listener.GuiClickListener;
import cn.hamster3.api.gui.swapper.Swapper;
import cn.hamster3.api.runnable.DailyTask;
import cn.hamster3.api.runnable.DailyTaskThread;
import cn.hamster3.api.utils.Calculator;
import cn.hamster3.api.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.md_5.bungee.api.chat.*;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@SuppressWarnings({"unused", "RedundantSuppression", "deprecation"})
public final class HamsterAPI extends JavaPlugin {
    /**
     * @since 2.4.5
     */
    public static final Gson GSON = new GsonBuilder().create();
    /**
     * @since 2.4.5
     */
    public static final JsonParser JSON_PARSER = new JsonParser();

    private static final Calculator calculator = new Calculator();

    private static HamsterAPI instance;

    private static Chat chat;
    private static Economy economy;
    private static Permission permission;

    private static PlayerPointsAPI playerPointsAPI;

    private static DailyTaskThread dailyTaskThread;

    private static String nmsVersion;
    private static boolean useOldMethods = false;

    private static LogUtils logUtils;

    /**
     * 计算一条字符串数学公式
     *
     * @param s 要计算的数学运算
     * @return 计算结果
     */
    public static double calculate(String s) {
        return calculator.calculate(s);
    }

    /**
     * 发送一条控制台消息
     *
     * @param message 要发送的消息
     */
    public static void sendConsoleMessage(final String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    /**
     * 发送一条控制台消息
     *
     * @param message 要发送的消息
     * @param objects format参数
     */
    public static void sendConsoleMessage(final String message, Object... objects) {
        Bukkit.getConsoleSender().sendMessage(String.format(message, objects));
    }

    /**
     * 发送一条控制台消息
     *
     * @param message 要发送的消息
     */
    public static void sendConsoleMessage(final String[] message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    /**
     * 发送多条控制台消息
     *
     * @param message 要发送的消息
     */
    public static void sendConsoleMessage(final Collection<String> message) {
        for (String string : message) {
            Bukkit.getConsoleSender().sendMessage(string);
        }
    }

    /**
     * 自动替换颜色代码
     *
     * @param string 要替换的字符串
     * @return 替换后的字符串
     */
    public static String replaceColorCode(final String string) {
        if (string == null) return null;
        return string.replace("&", "§");
    }

    /**
     * 自动替换颜色代码
     *
     * @param string  要替换的字符串
     * @param objects format参数
     * @return 替换后的字符串
     */
    public static String replaceColorCode(final String string, Object... objects) {
        if (string == null) return null;
        return String.format(string, objects).replace("&", "§");
    }

    /**
     * 自动替换颜色代码
     *
     * @param strings 要替换的字符串
     * @return 替换后的字符串
     */
    public static ArrayList<String> replaceColorCode(final Collection<String> strings) {
        if (strings == null) return null;
        ArrayList<String> list = new ArrayList<>();
        for (String s : strings) {
            list.add(replaceColorCode(s));
        }
        return list;
    }

    /**
     * 快捷创建GUI
     * 现在推荐使用cn.hamster3.api.gui.Gui类
     *
     * @param owner   箱子的主人，可以设为null
     * @param title   箱子的标题
     * @param swapper 字符转换器
     * @param fills   填充的图形
     * @return 创建后的GUI
     */
    @Deprecated
    public static Inventory getInventory(final InventoryHolder owner, final String title, final Swapper swapper, String... fills) {
        //如果填充图形的字符串长度超过6，则截取前面6个String
        if (fills.length > 6) {
            fills = Arrays.copyOf(fills, 6);
        }
        Inventory inventory = Bukkit.createInventory(owner, fills.length * 9, title);
        fillInventory(inventory, swapper, Arrays.asList(fills));
        return inventory;
    }

    /**
     * 快捷创建GUI
     * 已弃用，现在建议使用Gui类
     *
     * @param owner   箱子的主人，可以设为null
     * @param title   箱子的标题
     * @param swapper 字符转换器
     * @param fills   填充的图形
     * @return 创建后的GUI
     */
    @Deprecated
    public static Inventory getInventory(final InventoryHolder owner, final String title, final Swapper swapper, List<String> fills) {
        List<String> graphics = new ArrayList<>();
        //如果填充图形的字符串长度超过6，则截取前面6个String
        if (fills.size() > 6) {
            for (int i = 0; i < 6; i++) {
                graphics.add(fills.get(i));
            }
        }
        //如果不超过6个，则直接使用
        else {
            graphics = fills;
        }
        Inventory inventory = Bukkit.createInventory(owner, graphics.size() * 9, title);
        fillInventory(inventory, swapper, graphics);
        return inventory;
    }

    /**
     * 使用对应的图形和swapper填充GUI
     *
     * @param inventory 要填充的GUI
     * @param swapper   Item交换器
     * @param graphics  GUI图形
     */
    @Deprecated
    public static void fillInventory(final Inventory inventory, final Swapper swapper, final List<String> graphics) {
        HashMap<Character, ItemStack> map = new HashMap<>();
        for (String s : graphics) {
            for (int j = 0; j < s.length(); j++) {
                final char c = s.charAt(j);
                if (!map.containsKey(c)) {
                    map.put(c, swapper.getItemStackByChar(c));
                }
                if (j == 9) break;
            }
        }
        for (int i = 0; i < graphics.size(); i++) {
            for (int j = 0; j < graphics.get(i).length(); j++) {
                if (j == 9) break;
                inventory.setItem(i * 9 + j, map.get(graphics.get(i).charAt(j)));
            }
        }
    }

    /**
     * 使用对应的图形和swapper填充GUI
     *
     * @param inventory 要填充的GUI
     * @param swapper   Item交换器
     * @param graphics  GUI图形
     */
    @Deprecated
    public static void fillInventory(Inventory inventory, Swapper swapper, String[] graphics) {
        fillInventory(inventory, swapper, Arrays.asList(graphics));
    }

    /**
     * 创建一个每日运行的任务
     * 该任务会在每天的0点运行一次
     *
     * @param runnable       要运行的方法
     * @param asynchronously 是否异步运行
     * @param plugin         运行这个任务的插件对象
     * @param name           线程的名字（不可与其他线程名字重复
     * @return 是否成功创建线程
     */
    public static boolean addDailyRunnable(Runnable runnable, boolean asynchronously, Plugin plugin, String name) {
        if (runnable == null) {
            throw new IllegalArgumentException("runnable 不能为 null!");
        }
        if (plugin == null) {
            throw new IllegalArgumentException("plugin 不能为 null!");
        }
        if (name == null) {
            throw new IllegalArgumentException("name 不能为 null!");
        }
        if (dailyTaskThread.getDailyTask(plugin, name) != null) {
            return false;
        }

        DailyTask dailyTask = new DailyTask(plugin, name, !asynchronously) {
            @Override
            public void run() {
                runnable.run();
            }
        };
        return true;
    }

    /**
     * 取消该插件已经创建了的所有每日任务
     *
     * @param plugin 插件实例
     * @return 是否成功取消
     */
    public static boolean cancelDailyRunnable(Plugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("plugin 不能为 null!");
        }
        boolean flag = false;
        ArrayList<DailyTask> tasks = dailyTaskThread.getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            DailyTask task = tasks.get(i);
            if (!task.getPlugin().equals(plugin)) {
                continue;
            }
            tasks.remove(i);
            i--;
            flag = true;
        }
        return flag;
    }

    /**
     * 取消该插件已经创建了的所有每日线程
     *
     * @param plugin 插件实例
     * @param name   任务名称
     * @return 是否成功取消
     */
    public static boolean cancelDailyRunnable(Plugin plugin, String name) {
        if (plugin == null) {
            throw new IllegalArgumentException("plugin 不能为 null!");
        }
        if (name == null) {
            throw new IllegalArgumentException("name 不能为 null!");
        }
        boolean flag = false;
        ArrayList<DailyTask> tasks = dailyTaskThread.getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            DailyTask task = tasks.get(i);
            if (!task.getPlugin().equals(plugin)) {
                continue;
            }
            if (!task.getName().equals(name)) {
                continue;
            }
            tasks.remove(i);
            i--;
            flag = true;
        }
        return flag;
    }

    /**
     * 检查服务器是否安装了Vault
     *
     * @return true代表安装了，false代表未安装
     */
    public static boolean isSetupVault() {
        return Bukkit.getPluginManager().isPluginEnabled("Vault");
    }

    /**
     * 返回服务器是否安装了经济插件
     *
     * @return true代表安装了，false代表未安装
     */
    public static boolean isSetupEconomy() {
        return economy != null;
    }

    /**
     * 返回服务器是否安装了PlayerPoints插件
     *
     * @return true代表安装了，false代表未安装
     */
    public static boolean isSetupPlayerPoints() {
        return Bukkit.getPluginManager().isPluginEnabled("PlayerPoints");
    }

    /**
     * 返回服务器是否安装了ProtocolLib插件
     *
     * @return true代表安装了，false代表未安装
     */
    public static boolean isSetupProtocolLib() {
        return Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
    }

    /**
     * 返回PlayerPoints的API实例
     *
     * @return 若未安装PlayerPoints则返回null
     */
    public static PlayerPointsAPI getPlayerPointsAPI() {
        return playerPointsAPI;
    }

    /**
     * 给玩家金币
     *
     * @param player 玩家
     * @param money  钱的数量
     */
    public static void giveMoney(final OfflinePlayer player, final double money) {
        if (isSetupEconomy()) {
            economy.depositPlayer(player, money);
        }
    }

    public static boolean giveMoney(final UUID uuid, final double money) {
        if (isSetupEconomy()) {
            return economy.depositPlayer(Bukkit.getOfflinePlayer(uuid), money).transactionSuccess();
        }
        return false;
    }

    /**
     * 从玩家账户上取走钱
     *
     * @param player 玩家
     * @param money  钱的数量
     */
    public static void takeMoney(final OfflinePlayer player, final double money) {
        if (isSetupEconomy()) {
            economy.withdrawPlayer(player, money);
        }
    }

    public static boolean takeMoney(final UUID uuid, final double money) {
        if (isSetupEconomy()) {
            return economy.withdrawPlayer(Bukkit.getOfflinePlayer(uuid), money).transactionSuccess();
        }
        return false;
    }

    /**
     * 检查玩家有多少钱
     *
     * @param player 玩家
     * @return 玩家的钱的数量（若没有安装经济插件则返回NaN
     */
    public static double seeMoney(final OfflinePlayer player) {
        if (!isSetupEconomy()) {
            return Double.NaN;
        }
        return economy.getBalance(player);
    }

    public static double seeMoney(final UUID uuid) {
        if (!isSetupEconomy()) {
            return Double.NaN;
        }
        return economy.getBalance(Bukkit.getOfflinePlayer(uuid));
    }

    /**
     * 检测玩家是否有足够的钱
     *
     * @param player 玩家
     * @param money  金钱的数量
     * @return 是否有足够的钱（若没有安装经济插件则返回false
     */
    public static boolean hasMoney(final OfflinePlayer player, final double money) {
        if (!isSetupEconomy()) {
            return false;
        }
        return economy.has(player, money);
    }

    public static boolean hasMoney(final UUID uuid, final double money) {
        if (!isSetupEconomy()) {
            return false;
        }
        return economy.has(Bukkit.getOfflinePlayer(uuid), money);
    }

    public static void givePoint(final OfflinePlayer player, final int point) {
        if (playerPointsAPI != null) {
            playerPointsAPI.give(player.getUniqueId(), point);
        }
    }

    public static void givePoint(final UUID uuid, final int point) {
        if (playerPointsAPI != null) {
            playerPointsAPI.give(uuid, point);
        }
    }

    public static void takePoint(final OfflinePlayer player, final int point) {
        if (playerPointsAPI != null) {
            playerPointsAPI.take(player.getUniqueId(), point);
        }
    }

    public static void takePoint(final UUID uuid, final int point) {
        if (playerPointsAPI != null) {
            playerPointsAPI.take(uuid, point);
        }
    }

    public static void setPoint(final OfflinePlayer player, final int point) {
        if (playerPointsAPI != null) {
            playerPointsAPI.set(player.getUniqueId(), point);
        }
    }

    public static void setPoint(final UUID uuid, final int point) {
        if (playerPointsAPI != null) {
            playerPointsAPI.set(uuid, point);
        }
    }

    public static int seePoint(final OfflinePlayer player) {
        if (playerPointsAPI != null) {
            return playerPointsAPI.look(player.getUniqueId());
        }
        return 0;
    }

    public static int seePoint(final UUID uuid) {
        if (playerPointsAPI != null) {
            return playerPointsAPI.look(uuid);
        }
        return 0;
    }

    public static boolean hasPoint(final OfflinePlayer player, final int point) {
        if (playerPointsAPI != null) {
            return playerPointsAPI.look(player.getUniqueId()) >= point;
        }
        return false;
    }

    public static boolean hasPoint(final UUID uuid, final int point) {
        if (playerPointsAPI != null) {
            return playerPointsAPI.look(uuid) >= point;
        }
        return false;
    }

    /**
     * 返回 Vault 的 Chat 前置系统
     *
     * @return Chat 系统
     */
    public static Chat getChat() {
        return chat;
    }

    /**
     * 返回 Vault 的 Economy 前置系统
     *
     * @return Economy 系统
     */
    public static Economy getEconomy() {
        return economy;
    }

    /**
     * 返回 Vault 的 Permission 前置系统
     *
     * @return Permission 系统
     */
    public static Permission getPermission() {
        return permission;
    }

    /**
     * 将玩家传送到另一个服务器（指BC链接模式下
     *
     * @param player   玩家
     * @param serverID 服务器名称
     */
    public static void sendPlayerToBCServer(Player player, String serverID) {
        final ByteArrayOutputStream b = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(serverID);
            out.flush();
        } catch (IOException ignored) {

        }
        player.sendPluginMessage(instance, "BungeeCord", b.toByteArray());
    }

    /**
     * 给玩家发送一个热键栏消息
     *
     * @param player  要发送的玩家
     * @param message 要显示的消息
     */
    public static void sendActionBar(Player player, String message) {
        if (!player.isOnline()) {
            return;
        }
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object packet;
            Class<?> packetPlayOutChatClass = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
            Class<?> packetClass = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
            if (useOldMethods) {
                Class<?> chatSerializerClass = Class.forName("net.minecraft.server." + nmsVersion + ".ChatSerializer");
                Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");
                Method m3 = chatSerializerClass.getDeclaredMethod("a", String.class);
                Object cbc = iChatBaseComponentClass.cast(m3.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}"));
                packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(cbc, (byte) 2);
            } else {
                Class<?> chatComponentTextClass = Class.forName("net.minecraft.server." + nmsVersion + ".ChatComponentText");
                Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");
                try {
                    Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + nmsVersion + ".ChatMessageType");
                    Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
                    Object chatMessageType = null;
                    for (Object obj : chatMessageTypes) {
                        if (obj.toString().equals("GAME_INFO")) {
                            chatMessageType = obj;
                        }
                    }
                    Object chatTest = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                    packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, chatMessageTypeClass}).newInstance(chatTest, chatMessageType);
                } catch (ClassNotFoundException e) {
                    Object chatTest = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                    packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(chatTest, (byte) 2);
                }
            }
            Method craftPlayerHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle");
            Object craftPlayerHandle = craftPlayerHandleMethod.invoke(craftPlayer);
            Field playerConnectionField = craftPlayerHandle.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(craftPlayerHandle);
            Method sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", packetClass);
            sendPacketMethod.invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 给玩家发送一个热键栏消息
     *
     * @param player   要发送的玩家
     * @param message  要显示的消息
     * @param duration 持续时间
     */
    public static void sendActionBar(final Player player, final String message, int duration) {
        sendActionBar(player, message);

        if (duration >= 0) {
            Bukkit.getScheduler().runTaskLater(instance, () -> sendActionBar(player, ""), duration + 1);
        }
        while (duration > 40) {
            duration -= 40;
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(player, message);
                }
            }.runTaskLater(instance, duration);
        }
    }

    /**
     * 给全服玩家发送一个热键栏消息
     *
     * @param message 要显示的消息
     */
    public static void sendActionBarToAllPlayers(String message) {
        sendActionBarToAllPlayers(message, -1);
    }

    /**
     * 给全服玩家发送一个热键栏消息
     *
     * @param message  要显示的消息
     * @param duration 消息持续时间
     */
    public static void sendActionBarToAllPlayers(String message, int duration) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            sendActionBar(p, message, duration);
        }
    }

    /**
     * 让玩家以最高权限执行命令
     *
     * @param player  玩家
     * @param command 要执行的命令
     */
    public static void opCommand(Player player, String command) {
        boolean isOp = player.isOp();
        player.setOp(true);
        Bukkit.dispatchCommand(player, command);
        player.setOp(isOp);
    }

    /**
     * 过滤字符串集合
     * 将原集合中所有以start开头的字符串取出形成一个新的集合并返回
     *
     * @param strings 要过滤的字符串
     * @param start   关键字
     * @return 过滤后的字符串
     */
    public static ArrayList<String> startWith(Collection<String> strings, String start) {
        ArrayList<String> list = new ArrayList<>();
        if (strings == null || start == null) {
            return list;
        }
        for (String s : strings) {
            if (s.startsWith(start)) {
                list.add(s);
            }
        }
        return list;
    }

    /**
     * 忽略大小写过滤字符串集合
     * 将原集合中所有以start开头的字符串取出形成一个新的集合并返回
     *
     * @param strings 要过滤的字符串
     * @param start   关键字
     * @return 过滤后的字符串
     */
    public static ArrayList<String> startWithIgnoreCase(Collection<String> strings, String start) {
        ArrayList<String> list = new ArrayList<>();
        if (strings == null || start == null) {
            return list;
        }
        for (String s : strings) {
            if (s.toLowerCase().startsWith(start.toLowerCase())) {
                list.add(s);
            }
        }
        return list;
    }

    /**
     * 获取玩家的头颅
     * 在1.11以上的服务端中获取头颅材质是在服务器上运行的
     * 因此建议使用异步线程调用该方法
     *
     * @param uuid 要获取的玩家
     * @return 玩家的头颅物品
     */
    public static ItemStack getPlayerHead(UUID uuid) {
        return getPlayerHead(Bukkit.getOfflinePlayer(uuid));
    }

    /**
     * 获取玩家的头颅
     * 在1.11以上的服务端中获取头颅材质是在服务器上运行的
     * 因此建议使用异步线程调用该方法
     *
     * @param offlinePlayer 要获取的玩家
     * @return 玩家的头颅物品
     */
    @SuppressWarnings("deprecation")
    public static ItemStack getPlayerHead(OfflinePlayer offlinePlayer) {
        ItemStack stack;
        try {
            stack = new ItemStack(Material.valueOf("PLAYER_HEAD"));
        } catch (IllegalArgumentException e) {
            stack = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
        }
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(offlinePlayer);
        }
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * 获取玩家的头颅
     * 在1.11以上的服务端中获取头颅材质是在服务器上运行的
     * 因此建议使用异步线程调用该方法
     *
     * @param name 要获取的玩家
     * @return 玩家的头颅物品
     */
    @SuppressWarnings({"ConstantConditions", "deprecation"})
    public static ItemStack getPlayerHead(String name) {
        ItemStack stack;
        try {
            stack = new ItemStack(Material.valueOf("PLAYER_HEAD"));
        } catch (IllegalArgumentException e) {
            stack = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
        }
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(name);
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * 将一个以UUID为key的Map转换成以String为key的Map
     * 方便Yaml序列化
     *
     * @param map 要转换的Map
     * @return 转换后的Map
     */
    public static HashMap<String, ?> serialize(Map<UUID, ?> map) {
        HashMap<String, Object> hashMap = new HashMap<>();
        for (UUID key : map.keySet()) {
            hashMap.put(key.toString(), map.get(key));
        }
        return hashMap;
    }

    /**
     * 将一个UUID集合转换成String集合
     * 方便Yaml序列化
     *
     * @param uuidList 要转换的集合
     * @return 转换后的集合
     */
    public static ArrayList<String> serialize(List<UUID> uuidList) {
        ArrayList<String> strings = new ArrayList<>();
        for (UUID uuid : uuidList) {
            strings.add(uuid.toString());
        }
        return strings;
    }

    /**
     * 将一个以String为key的Map转换成以UUID为key的Map
     * 方便Yaml反序列化
     *
     * @param map 要转换的Map
     * @return 转换后的Map
     */
    public static HashMap<UUID, ?> deserialize(Map<String, ?> map) {
        HashMap<UUID, Object> hashMap = new HashMap<>();
        for (String key : map.keySet()) {
            hashMap.put(UUID.fromString(key), map.get(key));
        }
        return hashMap;
    }

    /**
     * 将一个String集合转换成UUID集合
     * 方便Yaml序列化
     *
     * @param stringList 要转换的集合
     * @return 转换后的集合
     */
    public static ArrayList<UUID> deserialize(List<String> stringList) {
        ArrayList<UUID> uuidList = new ArrayList<>();
        for (String string : stringList) {
            uuidList.add(UUID.fromString(string));
        }
        return uuidList;
    }

    /**
     * 判断物品是否为空
     * 当对象为null时返回true
     * 当物品的Material为AIR时返回true
     * 当物品的数量小于1时返回true
     *
     * @param stack 物品
     * @return 是否为空
     */
    public static boolean isEmptyItemStack(ItemStack stack) {
        return stack == null || stack.getType() == Material.AIR || stack.getAmount() < 1;
    }

    /**
     * 获取物品的名称
     * 当物品为null时返回"null"
     * 当物品拥有DisplayName时返回DisplayName
     * 否则返回物品的Material的name
     *
     * @param stack 物品
     * @return 物品的名称
     */
    @SuppressWarnings("ConstantConditions")
    public static String getItemName(ItemStack stack) {
        if (stack == null) {
            return "null";
        }
        if (stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();
            if (meta.hasDisplayName()) {
                return meta.getDisplayName();
            }
        }
        return stack.getType().name();
    }

    public static TextComponent getItemDisplayInfo(String startText, ItemStack stack, String endText) {
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_ITEM, getItemInfo(stack));

        TextComponent startComponent = new TextComponent(startText);
        startComponent.setHoverEvent(hoverEvent);
        TextComponent endComponent = new TextComponent(endText);
        endComponent.setHoverEvent(hoverEvent);

        BaseComponent itemComponent = getItemNameComponent(stack);
        itemComponent.setHoverEvent(hoverEvent);

        return new TextComponent(
                new ComponentBuilder()
                        .append(startComponent)
                        .append(itemComponent)
                        .append(endComponent)
                        .create()
        );
    }

    @SuppressWarnings("ConstantConditions")
    public static BaseComponent getItemNameComponent(ItemStack stack) {
        if (isEmptyItemStack(stack)) {
            return new TranslatableComponent("block.minecraft.air");
        } else if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
            return new TextComponent(getItemName(stack));
        }
        Material type = stack.getType();
        if (type.isBlock()) {
            return new TranslatableComponent("block.minecraft." + type.name().toLowerCase());
        } else {
            return new TranslatableComponent("item.minecraft." + type.name().toLowerCase());
        }
    }

    public static BaseComponent[] getItemInfo(ItemStack stack) {
        try {
            Class<?> nBTTagCompound = Class.forName("net.minecraft.server." + nmsVersion + ".NBTTagCompound");
            Object nBTTag = nBTTagCompound.newInstance();
            Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".inventory.CraftItemStack");
            Method asNMSCopy = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
            Object nmsItem = asNMSCopy.invoke(null, stack);
            Method saveMethod = nmsItem.getClass().getMethod("save", nBTTagCompound);
            saveMethod.invoke(nmsItem, nBTTag);
            return new ComponentBuilder(nBTTag.toString()).create();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ComponentBuilder("物品解析失败").create();
    }

    /**
     * 给予玩家一个物品, 当玩家背包满时
     * 将会在玩家附近生成这个物品的掉落物
     *
     * @param player 玩家
     * @param stack  物品
     */
    public static void giveItem(HumanEntity player, ItemStack stack) {
        if (isEmptyItemStack(stack)) {
            return;
        }
        World world = player.getWorld();
        for (ItemStack dropItem : player.getInventory().addItem(stack).values()) {
            world.dropItem(player.getLocation(), dropItem);
        }
    }

    /**
     * 在服务器的全部世界上寻找实体
     * 在1.9以上可以调用Bukkit.getEntity()实现同样的功能
     *
     * @param uuid 实体的uuid
     * @return 实体
     */
    public static Entity getEntity(UUID uuid) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getUniqueId().equals(uuid)) {
                    return entity;
                }
            }
        }
        return null;
    }

    /**
     * 获取服务器当前全部在线玩家的名字
     *
     * @return 在线玩家们的名字
     */
    public static ArrayList<String> getOnlinePlayersName() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            arrayList.add(player.getName());
        }
        return arrayList;
    }

    /**
     * 获取服务器当前全部在线玩家的名字
     *
     * @param startWith 前缀匹配
     * @return 前缀匹配后的在线玩家们的名字
     */
    public static ArrayList<String> getOnlinePlayersName(String startWith) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(startWith.toLowerCase())) {
                arrayList.add(player.getName());
            }
        }
        return arrayList;
    }

    /**
     * 获取服务器当前全部在线玩家的UUID
     *
     * @return 在线玩家们的UUID
     */
    public static ArrayList<UUID> getOnlinePlayersUUID() {
        ArrayList<UUID> arrayList = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            arrayList.add(player.getUniqueId());
        }
        return arrayList;
    }

    public static String getMCVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    public static String getNMSVersion() {
        return Bukkit.getServer().getClass().getName().split("\\.")[3];
    }

    public static Package getNMSPackage() {
        String nmsVersion = getNMSVersion();
        return Package.getPackage("net.minecraft.server." + nmsVersion);
    }

    public static Class<?> getNMSClass(String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + nmsVersion + "." + className);
    }

    /**
     * 创建MySQL连接
     *
     * @param host     主机地址
     * @param port     主机端口
     * @param user     用户名
     * @param password 密码
     * @return 连接对象
     * @throws SQLException SQL连接异常
     */
    public static Connection getMySQLConnection(String host, String port, String user, String password) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
        }
        return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&autoReconnect=true&useSSL=false", user, password);
    }

    /**
     * 创建MySQL连接
     *
     * @param host     主机地址
     * @param port     主机端口
     * @param user     用户名
     * @param password 密码
     * @param database 要使用的数据库
     * @return 连接对象
     * @throws SQLException SQL连接异常
     */
    public static Connection getMySQLConnection(String host, String port, String user, String password, String database) throws SQLException {
        Connection connection = getMySQLConnection(host, port, user, password);
        createDatabase(connection, database);
        return connection;
    }

    /**
     * 创建MySQL连接
     *
     * @param config config对象
     * @return 连接对象
     * @throws SQLException SQL连接异常
     */
    public static Connection getMySQLConnection(ConfigurationSection config) throws SQLException {
        Connection connection = getMySQLConnection(
                config.getString("host"),
                config.getString("port"),
                config.getString("user"),
                config.getString("password")
        );
        if (config.contains("database")) {
            createDatabase(connection, config.getString("database"));
        }
        return connection;
    }

    /**
     * 创建SQL连接
     *
     * @param config config对象
     * @return 连接对象
     * @throws SQLException           SQL连接异常
     * @throws ClassNotFoundException 找不到数据库驱动
     * @since 2.3.6
     */
    @SuppressWarnings("ConstantConditions")
    public static Connection getSQLConnection(ConfigurationSection config) throws SQLException, ClassNotFoundException {
        Class.forName(config.getString("driver"));
        Connection connection = DriverManager.getConnection(
                config.getString("url"),
                config.getString("user"),
                config.getString("password")
        );
        if (config.contains("database")) {
            createDatabase(connection, config.getString("database"));
        }
        return connection;
    }

    /**
     * 创建 SQL 连接池
     *
     * @param config SQL 配置
     * @return SQL 连接池
     */
    public static DataSource getHikariDataSource(final ConfigurationSection config) {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDriverClassName(config.getString("driver"));

        hikariConfig.setJdbcUrl(config.getString("url"));
        hikariConfig.setUsername(config.getString("user"));
        hikariConfig.setPassword(config.getString("password"));

        hikariConfig.setMaximumPoolSize(config.getInt("maximumPoolSize", 3));
        hikariConfig.setMinimumIdle(config.getInt("minimumIdle", 1));
        hikariConfig.setIdleTimeout(config.getLong("idleTimeout", 5 * 60 * 1000));
        hikariConfig.setMaxLifetime(config.getLong("maxLifetime", 0));

        return new HikariDataSource(hikariConfig);
    }

    private static void createDatabase(Connection connection, String database) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(String.format("CREATE DATABASE IF NOT EXISTS %s DEFAULT CHARACTER SET ='UTF8';", database));
        statement.execute(String.format("USE %s;", database));
        statement.close();
    }

    public static void reloadVault() {
        chat = null;
        economy = null;
        permission = null;
        if (!HamsterAPI.isSetupVault()) {
            logUtils.warning("未检测到Vault插件!");
            return;
        }
        logUtils.info("已连接Vault!");

        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);

        if (chatProvider != null) {
            chat = chatProvider.getProvider();
            logUtils.info("聊天系统挂接成功...");
        } else {
            logUtils.warning("未检测到聊天系统!");
        }

        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
            logUtils.info("经济系统挂接成功...");
        } else {
            logUtils.warning("未检测到经济系统!");
        }

        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
            logUtils.info("权限系统挂接成功...");
        } else {
            logUtils.warning("未检测到权限插件!");
        }
    }

    @Override
    public void onLoad() {
        final File defaultLogSettingsFile = new File(this.getDataFolder(), "defaultLogSettings.yml");
        if (!defaultLogSettingsFile.exists()) {
            this.saveResource("defaultLogSettings.yml", true);
        }
        LogUtils.DEFAULT_CONFIG = YamlConfiguration.loadConfiguration(defaultLogSettingsFile);
        instance = this;
        logUtils = new LogUtils(this);
        logUtils.infoDividingLine();
        logUtils.info("插件正在初始化中...");

        ConfigurationSerialization.registerClass(DisplayMessage.class);
        logUtils.info("已注册序列化信息...");

        dailyTaskThread = new DailyTaskThread();
        dailyTaskThread.start();
        logUtils.info("已启动每日任务线程...");

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        logUtils.info("已注册BungeeCord通道...");

        nmsVersion = getNMSVersion();
        if (nmsVersion.equalsIgnoreCase("v1_8_R1") || nmsVersion.startsWith("v1_7_")) {
            useOldMethods = true;
        }
        logUtils.info("已获取NMS版本: " + nmsVersion);


        logUtils.info("插件初始化完成!");
        logUtils.infoDividingLine();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onEnable() {
        logUtils.infoDividingLine();

        PluginCommand command = getCommand("HamsterAPI");
        HamsterCommand hamsterCommand = new HamsterCommand(command, this);
        command.setExecutor(hamsterCommand);
        logUtils.info("已注册命令执行器...");

        Bukkit.getPluginManager().registerEvents(new GuiClickListener(), this);
        logUtils.info("已注册GUI点击事件监听器...");

        Bukkit.getScheduler().runTask(this, () -> {
            logUtils.infoDividingLine();
            reloadVault();
            if (isSetupPlayerPoints()) {
                Plugin plugin = Bukkit.getPluginManager().getPlugin("PlayerPoints");
                logUtils.info("检测到 PlayerPoints 插件已启动...");
                if (plugin instanceof PlayerPoints) {
                    playerPointsAPI = ((PlayerPoints) plugin).getAPI();
                    logUtils.info("PlayerPoints 挂接成功...");
                }
            } else {
                logUtils.warning("未找到 PlayerPoints 前置!");
            }
            logUtils.infoDividingLine();
        });

        logUtils.infoDividingLine();
    }

    @Override
    public void onDisable() {
        logUtils.infoDividingLine();
        logUtils.info("插件正在关闭中...");
        if (dailyTaskThread != null && !dailyTaskThread.isStop()) {
            dailyTaskThread.setStop(true);
        }
        logUtils.info("插件成功关闭!");
        logUtils.infoDividingLine();
        logUtils.close();
    }
}