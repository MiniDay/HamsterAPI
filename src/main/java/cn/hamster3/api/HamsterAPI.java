package cn.hamster3.api;

import cn.hamster3.api.command.CommandExecutor;
import cn.hamster3.api.gui.swapper.Swapper;
import cn.hamster3.api.runnable.DailyRunnable;
import cn.hamster3.util.calculator.Calculator;
import com.sun.istack.internal.NotNull;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public final class HamsterAPI extends JavaPlugin {
    private static HamsterAPI instance;
    private static Economy economy;
    private static Calculator calculator = new Calculator();
    private static ArrayList<DailyRunnable> daily = new ArrayList<>();
    private static ArrayList<DailyRunnable> asynchronouslyDaily = new ArrayList<>();
    private static DailyThread dailyThread;

    private static String nmsServer;
    private static boolean useOldMethods = false;

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
    public static void sendConsoleMessage(@NotNull final String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    /**
     * 发送一条控制台消息
     *
     * @param message 要发送的消息
     */
    public static void sendConsoleMessage(@NotNull final String[] message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    /**
     * 发送一条控制台消息
     *
     * @param message 要发送的消息
     */
    public static void sendConsoleMessage(@NotNull final List<String> message) {
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
     * @param strings 要替换的字符串
     * @return 替换后的字符串
     */
    public static String[] replaceColorCode(final String[] strings) {
        if (strings == null) return null;
        for (int i = 0; i < strings.length; i++) {
            strings[i] = replaceColorCode(strings[i]);
        }
        return strings;
    }

    /**
     * 自动替换颜色代码
     *
     * @param strings 要替换的字符串
     * @return 替换后的字符串
     */
    public static List<String> replaceColorCode(final List<String> strings) {
        if (strings == null) return null;
        for (int i = 0; i < strings.size(); i++) {
            strings.set(i, replaceColorCode(strings.get(i)));
        }
        return strings;
    }

    /**
     * 快捷创建GUI
     *
     * @param owner   箱子的主人，可以设为null
     * @param title   箱子的标题
     * @param swapper 字符转换器
     * @param fills   填充的图形
     * @return 创建后的GUI
     */
    public static Inventory getInventory(final InventoryHolder owner, final String title, @NotNull final Swapper swapper, @NotNull String... fills) {
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
     *
     * @param owner   箱子的主人，可以设为null
     * @param title   箱子的标题
     * @param swapper 字符转换器
     * @param fills   填充的图形
     * @return 创建后的GUI
     */
    public static Inventory getInventory(final InventoryHolder owner, final String title, @NotNull final Swapper swapper, @NotNull List<String> fills) {
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
     * 创建一个每日运行的线程
     * 该线程会在每天的0点运行一次
     *
     * @param runnable       要运行的方法
     * @param asynchronously 是否在服务器主线程上运行
     * @param plugin         运行这个线程的插件
     * @param name           线程的名字
     * @return 是否成功创建线程
     */
    public static boolean addDailyRunnable(Runnable runnable, boolean asynchronously, Plugin plugin, String name) {
        if (runnable == null) return false;
        if (plugin == null || !plugin.isEnabled()) return false;
        if (name == null) return false;
        if (!plugin.isEnabled()) return false;
        DailyRunnable dailyRunnable = new DailyRunnable(plugin, name, runnable);
        if (asynchronously) asynchronouslyDaily.add(dailyRunnable);
        else daily.add(dailyRunnable);
        return true;
    }

    /**
     * 取消已经创建了的每日线程
     *
     * @param name 线程的名字
     * @return 是否成功取消
     */
    public static boolean cancelDailyRunnable(String name) {
        boolean flag = false;
        for (DailyRunnable runnable : new ArrayList<>(daily)) {
            if (runnable.getName().equals(name)) {
                daily.remove(runnable);
                flag = true;
            }
        }
        for (DailyRunnable runnable : new ArrayList<>(asynchronouslyDaily)) {
            if (runnable.getName().equals(name)) {
                asynchronouslyDaily.remove(runnable);
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 取消该插件已经创建了的所有每日线程
     *
     * @param plugin 插件实例
     * @return 是否成功取消
     */
    public static boolean cancelDailyRunnable(Plugin plugin) {
        boolean flag = false;
        for (DailyRunnable runnable : new ArrayList<>(daily)) {
            if (runnable.getPlugin().equals(plugin)) {
                daily.remove(runnable);
                flag = true;
            }
        }
        for (DailyRunnable runnable : new ArrayList<>(asynchronouslyDaily)) {
            if (runnable.getPlugin().equals(plugin)) {
                asynchronouslyDaily.remove(runnable);
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 取消该插件已经创建了的所有名称匹配的每日线程
     *
     * @param plugin 插件实例
     * @param name   线程名称
     * @return 是否成功取消
     */
    public static boolean cancelDailyRunnable(Plugin plugin, String name) {
        boolean flag = false;
        for (DailyRunnable runnable : new ArrayList<>(daily)) {
            if (runnable.getName().equals(name) && runnable.getPlugin().equals(plugin)) {
                daily.remove(runnable);
                flag = true;
            }
        }
        for (DailyRunnable runnable : new ArrayList<>(asynchronouslyDaily)) {
            if (runnable.getName().equals(name) && runnable.getPlugin().equals(plugin)) {
                asynchronouslyDaily.remove(runnable);
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 取消该每日线程
     *
     * @param runnable 线程实例
     * @return 是否成功取消
     */
    public static boolean cancelDailyRunnable(Runnable runnable) {
        boolean flag = false;
        for (DailyRunnable dailyRunnable : new ArrayList<>(daily)) {
            if (dailyRunnable.getRunnable().equals(runnable)) {
                daily.remove(dailyRunnable);
                flag = true;
            }
        }
        for (DailyRunnable dailyRunnable : new ArrayList<>(asynchronouslyDaily)) {
            if (dailyRunnable.getRunnable().equals(runnable)) {
                asynchronouslyDaily.remove(dailyRunnable);
                flag = true;
            }
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
        if (!isSetupPlayerPoints()) return null;
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlayerPoints");
        if (plugin instanceof PlayerPoints) {
            return ((PlayerPoints) plugin).getAPI();
        }
        return null;
    }

    /**
     * 给玩家金币
     *
     * @param player 玩家
     * @param money  钱的数量
     */
    public static void giveMoney(@NotNull final OfflinePlayer player, @NotNull final double money) {
        if (isSetupEconomy()) {
            economy.depositPlayer(player, money);
        }
    }

    /**
     * 从玩家账户上取走钱
     *
     * @param player 玩家
     * @param money  钱的数量
     */
    public static void takeMoney(@NotNull final OfflinePlayer player, @NotNull final double money) {
        if (isSetupEconomy()) {
            economy.withdrawPlayer(player, money);
        }
    }

    /**
     * 检查玩家有多少钱
     *
     * @param player 玩家
     * @return 玩家的钱的数量（若没有安装经济插件则返回NaN
     */
    public static double seeMoney(@NotNull final OfflinePlayer player) {
        if (!isSetupEconomy()) {
            return Double.NaN;
        }
        return economy.getBalance(player);
    }

    /**
     * 检测玩家是否有足够的钱
     *
     * @param player 玩家
     * @param money  金钱的数量
     * @return 是否有足够的钱（若没有安装经济插件则返回false
     */
    public static boolean hasMoney(@NotNull final OfflinePlayer player, @NotNull final double money) {
        if (!isSetupEconomy()) {
            return false;
        }
        return economy.has(player, money);
    }

    /**
     * 返回Economy实例
     *
     * @return Economy实例
     */
    public static Economy getEconomy() {
        return economy;
    }

    /**
     * 使用对应的图形和swapper填充GUI
     *
     * @param inventory 要填充的GUI
     * @param swapper   Item交换器
     * @param graphics  GUI图形
     */
    public static void fillInventory(Inventory inventory, Swapper swapper, List<String> graphics) {
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
    public static void fillInventory(Inventory inventory, Swapper swapper, String[] graphics) {
        fillInventory(inventory, swapper, Arrays.asList(graphics));
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
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsServer + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object packet;
            Class<?> packetPlayOutChatClass = Class.forName("net.minecraft.server." + nmsServer + ".PacketPlayOutChat");
            Class<?> packetClass = Class.forName("net.minecraft.server." + nmsServer + ".Packet");
            if (useOldMethods) {
                Class<?> chatSerializerClass = Class.forName("net.minecraft.server." + nmsServer + ".ChatSerializer");
                Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsServer + ".IChatBaseComponent");
                Method m3 = chatSerializerClass.getDeclaredMethod("a", String.class);
                Object cbc = iChatBaseComponentClass.cast(m3.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}"));
                packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(cbc, (byte) 2);
            } else {
                Class<?> chatComponentTextClass = Class.forName("net.minecraft.server." + nmsServer + ".ChatComponentText");
                Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsServer + ".IChatBaseComponent");
                try {
                    Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + nmsServer + ".ChatMessageType");
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
    public static void usePlayerCommandBypassPermission(Player player, String command) {
        boolean isOp = player.isOp();
        player.setOp(true);
        Bukkit.dispatchCommand(player, command);
        player.setOp(isOp);
    }

    /**
     * 过滤字符串列表
     *
     * @param strings 要过滤的字符串
     * @param start   过滤的关键字
     * @return 原字符串列表中只以关键字起始的字符串列表
     */
    public static List<String> startWith(List<String> strings, String start) {
        if (start == null || start.length() == 0) return strings;
        ArrayList<String> list = new ArrayList<>();
        for (String s : strings) {
            if (s.startsWith(start)) {
                list.add(s);
            }
        }
        return list;
    }

    /**
     * 获取玩家的头颅
     *
     * @param uuid 要获取的玩家
     * @return 玩家的头颅物品
     */
    public static ItemStack getPlayerHead(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        return getPlayerHead(player.getName());
    }

    /**
     * 获取玩家的头颅
     *
     * @param name 要获取的玩家
     * @return 玩家的头颅物品
     */
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

    public static boolean isEmptyItemStack(ItemStack stack) {
        return stack == null || stack.getType() == Material.AIR || stack.getAmount() == 0;
    }

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

    public static void giveItem(HumanEntity player, ItemStack stack) {
        if (isEmptyItemStack(stack)) {
            return;
        }
        World world = player.getWorld();
        for (ItemStack dropItem : player.getInventory().addItem(stack).values()) {
            world.dropItem(player.getLocation(), dropItem);
        }
    }

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

    @Override
    public void onEnable() {
        instance = this;
        ConfigurationSerialization.registerClass(DisplayMessage.class);
        HamsterAPI.sendConsoleMessage("§e§l[HamsterAPI] §a插件正在初始化...");

        if (!HamsterAPI.isSetupVault()) {
            sendConsoleMessage("§e§l[HamsterAPI] §c未检测到Vault插件!");
        } else {
            sendConsoleMessage("§e§l[HamsterAPI] §a已连接Vault!");
            RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
                HamsterAPI.sendConsoleMessage("§e§l[HamsterAPI] §a经济系统挂接成功...");
            } else {
                HamsterAPI.sendConsoleMessage("§e§l[HamsterAPI] §c未检测到经济插件!");
            }
        }
        dailyThread = new DailyThread();
        dailyThread.start();
        getCommand("HamsterAPI").setExecutor(new CommandExecutor(this));
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        nmsServer = Bukkit.getServer().getClass().getPackage().getName();
        nmsServer = nmsServer.substring(nmsServer.lastIndexOf(".") + 1);

        if (nmsServer.equalsIgnoreCase("v1_8_R1") || nmsServer.startsWith("v1_7_")) {
            useOldMethods = true;
        }
        HamsterAPI.sendConsoleMessage("§e§l[HamsterAPI] §a插件已启动!");
    }

    @Override
    public void onDisable() {
        if (dailyThread != null) {
            dailyThread.close();
        }
    }

    private final static class DailyThread extends Thread {
        private boolean stop;
        private long time;

        private DailyThread() {
            Calendar now = Calendar.getInstance();
            Calendar nextTime = Calendar.getInstance();
            nextTime.set(Calendar.MILLISECOND, 0);
            nextTime.set(Calendar.SECOND, 0);
            nextTime.set(Calendar.MINUTE, 0);
            nextTime.set(Calendar.HOUR_OF_DAY, 24);
            time = nextTime.getTime().getTime() - now.getTime().getTime();
        }

        @Override
        public void run() {
            if (stop) return;

            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (true) {
                if (stop) return;

                Bukkit.getScheduler().runTask(instance, () -> {
                    for (DailyRunnable runnable : asynchronouslyDaily) {
                        runnable.run();
                    }
                });

                for (DailyRunnable runnable : new ArrayList<>(daily)) {
                    runnable.run();
                }

                try {
                    Thread.sleep(86400000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void close() {
            this.stop = true;
        }
    }

}

