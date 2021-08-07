package cn.hamster3.api.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.*;

/**
 * 日志记录器
 *
 * @since 2.3.6
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class LogUtils {
    public static final String DIVIDING_LINE = "==================================================";

    public static final ArrayList<LogUtils> ALL_INSTANCES = new ArrayList<>();
    public static YamlConfiguration DEFAULT_CONFIG;

    private final Plugin plugin;

    private final Logger logger;
    private PrintWriter fileWriter;
    private SimpleDateFormat dateFormat;

    private boolean debug;
    private boolean usePluginLogger;

    /**
     * 以默认配置实例化一个日志记录器
     * 默认的配置在 plugins/AirGame-API/defaultLogSettings.yml 中
     *
     * @param plugin 使用这个日志记录器的插件对象
     */
    public LogUtils(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        File logSettingsFile = new File(plugin.getDataFolder(), "logSettings.yml");
        if (logSettingsFile.exists()) {
            init(YamlConfiguration.loadConfiguration(logSettingsFile));
        } else if (plugin.getResource("logSettings.yml") != null) {
            plugin.saveResource("logSettings.yml", true);
            init(YamlConfiguration.loadConfiguration(logSettingsFile));
        } else {
            init(DEFAULT_CONFIG);
        }
    }

    /**
     * 以特定配置文件实例化一个日志记录器
     *
     * @param plugin 使用这个日志记录器的插件对象
     * @param config 日志记录器的配置文件
     */
    public LogUtils(Plugin plugin, ConfigurationSection config) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        init(config);
    }

    /**
     * 初始化这个日志器
     *
     * @param config 配置对象
     */
    @SuppressWarnings("ConstantConditions")
    private void init(ConfigurationSection config) {
        usePluginLogger = config.getBoolean("usePluginLogger", false);

        info("==================================================");
        info("开始初始化 日志器.");
        info("==================================================");
        if (config.getBoolean("usePluginLogger", false)) {
            info("使用Bukkit自带日志器, 日志将会打印至控制台.");
        }

        debug = config.getBoolean("debug", false);
        if (debug) {
            info("已启用调试信息输出.");
        } else {
            info("已关闭调试信息输出.");
        }
        if (!config.getBoolean("logSaveToFile", true)) {
            warning("未启用文件存储, 日志将不会保存至磁盘!");
            return;
        }

        File logFolder;
        if (config.contains("logFolder")) {
            File logFolderLocation = new File(config.getString("logFolder", "logs/plugins"));
            if (logFolderLocation.exists() || logFolderLocation.mkdirs()) {
                logFolder = new File(logFolderLocation, plugin.getName());
            } else {
                warning("配置文件中的日志存储位置不可用, 更改至插件目录内!");
                logFolder = new File(plugin.getDataFolder(), "logs");
            }
        } else {
            logFolder = new File(plugin.getDataFolder(), "logs");
        }
        info("日志存储位置为: " + logFolder.getAbsolutePath());

        if (logFolder.mkdirs()) {
            info("创建日志存档文件夹...");
        }

        long now = System.currentTimeMillis();
        try {
            String time = String.valueOf(now);
            File file = new File(logFolder, time + ".log");
            int i = 1;
            // 如果文件名已被占用则在后面加一个 -n
            while (file.exists()) {
                file = new File(logFolder, time + "-" + i + ".log");
                i++;
            }
            if (config.getBoolean("bufferWriter", true)) {
                fileWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)));
            } else {
                fileWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            error(e, "初始化日志器时出现了一个错误: ");
        }

        dateFormat = new SimpleDateFormat(config.getString("dateFormat", "yyyy-MM-dd HH:mm:ss:SSS"));

        info("当前时间戳: " + now);
        info("当前日志输出信息: %s-%s", plugin.getName(), plugin.getDescription().getVersion());
        info("工作路径: " + new File("./").getAbsolutePath());

        int saveDays = config.getInt("saveDays", 7);
        if (saveDays < 1) {
            return;
        }
        info("日志存储有效期为 %d 天.", saveDays);
        for (File file : logFolder.listFiles()) {
            if (now - file.lastModified() > 86400 * 1000 * 7) {
                if (file.delete()) {
                    info("日志文件 %s 由于超过存储期限而被删除.", file.getName());
                }
            }
        }
        ALL_INSTANCES.add(this);
        info("==================================================");
        info("成功初始化 日志器.");
        info("==================================================");
    }

    public void infoDividingLine() {
        info("==================================================");
    }

    /**
     * 记录一条信息
     *
     * @param info 信息
     */
    public void info(String info) {
        if (usePluginLogger) {
            logger.info(info);
        }
        if (fileWriter != null) {
            fileWriter.format(
                    "[%s] [INFO] %s\n",
                    dateFormat.format(new Date()),
                    info
            );
        }
    }

    /**
     * 记录一条信息，使用 String.format() 替换参数
     *
     * @param info   信息
     * @param params 参数
     * @see String#format(String, Object...)
     */
    public void info(String info, Object... params) {
        info(String.format(info, params));
    }

    /**
     * 记录一条警告
     *
     * @param warning 警告
     */
    public void warning(String warning) {
        if (usePluginLogger) {
            logger.warning(warning);
        }
        if (fileWriter != null) {
            fileWriter.format(
                    "[%s] [WARNING] %s\n",
                    dateFormat.format(new Date()),
                    warning
            );
        }
    }

    /**
     * 记录一条警告，使用 String.format() 替换参数
     *
     * @param warning 警告
     * @param params  参数
     * @see String#format(String, Object...)
     */
    public void warning(String warning, Object... params) {
        warning(String.format(warning, params));
    }

    /**
     * 记录一条调试信息
     *
     * @param debug 调试信息
     */
    public void debug(String debug) {
        if (!this.debug) {
            return;
        }
        if (usePluginLogger) {
            logger.info(debug);
        }
        if (fileWriter != null) {
            fileWriter.format(
                    "[%s] [DEBUG] %s\n",
                    dateFormat.format(new Date()),
                    debug
            );
        }
    }

    /**
     * 记录一条调试信息，使用 String.format() 替换参数
     *
     * @param debug  调试信息
     * @param params 参数
     * @see String#format(String, Object...)
     */
    public void debug(String debug, Object... params) {
        debug(String.format(debug, params));
    }

    /**
     * 记录一条调试异常
     *
     * @param e 异常对象
     */
    public void debug(Throwable e) {
        if (!this.debug) {
            return;
        }
        error(e);
    }

    /**
     * 记录一条调试异常
     *
     * @param e     异常对象
     * @param debug 附加信息
     */
    public void debug(Throwable e, String debug) {
        if (!this.debug) {
            return;
        }
        error(e, debug);
    }

    /**
     * 记录一条调试异常
     *
     * @param e      异常对象
     * @param debug  附加信息
     * @param params 格式化对象
     */
    public void debug(Throwable e, String debug, Object... params) {
        if (!this.debug) {
            return;
        }
        error(e, debug, params);
    }

    /**
     * 记录一个异常
     *
     * @param e 异常对象
     */
    public void error(Throwable e) {
        e.printStackTrace();
        if (fileWriter != null) {
            e.printStackTrace(fileWriter);
        }
    }

    /**
     * 记录一个异常，附带描述消息
     *
     * @param e       异常对象
     * @param message 附加描述消息
     */
    public void error(Throwable e, String message) {
        warning(message);
        error(e);
    }

    /**
     * 记录一个异常，附带描述消息，使用 String.format() 替换参数
     *
     * @param e       异常对象
     * @param message 附加描述消息
     * @param params  参数
     * @see String#format(String, Object...)
     */
    public void error(Throwable e, String message, Object... params) {
        warning(message, params);
        error(e);
    }

    /**
     * 将日志输出缓存立即清空
     */
    public void flush() {
        if (fileWriter != null) {
            fileWriter.flush();
        }
    }

    public void close() {
        if (this.fileWriter != null) {
            this.fileWriter.close();
        }
    }

    /**
     * 获取 Bukkit 插件自带的日志器对象
     *
     * @return 日志器对象
     */

    public Logger getLogger() {
        return logger;
    }

    /**
     * 获取插件对象
     *
     * @return 插件对象
     */

    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * 获取 文件输出器对象
     *
     * @return 文件输出器对象
     */
    public PrintWriter getFileWriter() {
        return fileWriter;
    }

    /**
     * 是否启用调试信息输出
     *
     * @return 是否启用调试信息输出
     */
    public boolean isDebug() {
        return debug;
    }
}
