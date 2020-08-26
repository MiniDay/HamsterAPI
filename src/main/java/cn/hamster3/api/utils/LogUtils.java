package cn.hamster3.api.utils;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.logging.*;

/**
 * 日志记录器
 *
 * @since 2.3.6
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class LogUtils extends Formatter {
    private static final SimpleDateFormat LOG_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat LOG_FILE_NAME_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final Logger logger;
    private StreamHandler handler;
    private FileOutputStream outputStream;
    private PrintStream printStream;

    public LogUtils(Plugin plugin) {
        this(plugin, true);
    }

    public LogUtils(Plugin plugin, boolean saveToFile) {
        this.logger = plugin.getLogger();
        if (saveToFile) {
            info("已启用日志文件记录器...");
            initLogFile(new File(plugin.getDataFolder(), "logs"));
        } else {
            info("未启用日志文件记录器...");
        }
    }

    public LogUtils(Logger logger) {
        this.logger = logger;

        infoDividingLine();
        info("初始化日志记录器完成!");
        infoDividingLine();
    }

    public LogUtils(Logger logger, File logFolder) {
        this.logger = logger;

        initLogFile(logFolder);

        infoDividingLine();
        info("初始化日志记录器完成!");
        infoDividingLine();
    }

    private void initLogFile(File logFolder) {
        if (logFolder.mkdirs()) {
            info("创建日志存档文件夹...");
        }

        String time = LOG_FILE_NAME_FORMAT.format(new Date());
        int i = 1;
        File file = new File(logFolder, time + "-" + i + ".log");
        while (file.exists()) {
            i++;
            file = new File(logFolder, time + "-" + i + ".log");
        }

        info("创建日志存储文件: " + file.getName());
        try {
            outputStream = new FileOutputStream(file, false);
            printStream = new PrintStream(outputStream, true, "UTF-8");
            handler = new StreamHandler(outputStream, this);
            handler.setEncoding("UTF-8");
            logger.addHandler(handler);
            info("日志输出文件设置完成!");
        } catch (IOException e) {
            warning("初始化日志输出文件时发生了一个异常: ");
            e.printStackTrace();
        }
    }

    /**
     * 输出一行分隔线<p>
     * 除此之外并没有什么卵用<p>
     * 只是为了统一分隔线的长度而已（50字符
     *
     * @since 2.3.7
     */
    public void infoDividingLine() {
        logger.info("==================================================");
    }

    public void info(String info) {
        logger.info(info);
        flush();
    }

    public void info(String info, Object... params) {
        logger.info(String.format(info, params));
    }

    public void warning(String warning) {
        logger.warning(warning);
        flush();
    }

    public void warning(String warning, Object... params) {
        logger.warning(String.format(warning, params));
    }

    /**
     * 向日志文件中输出一条异常记录<p>
     * 同时也会在控制台打印这条记录
     *
     * @param e 异常
     * @since 2.3.9
     */
    public void error(Exception e) {
        if (printStream != null) {
            e.printStackTrace(printStream);
        }
        e.printStackTrace();
        flush();
    }

    public void error(String message, Exception e) {
        warning(message);
        error(e);
    }

    public void error(String message, Exception e, Object... args) {
        warning(message, args);
        error(e);
    }

    public void flush() {
        if (handler != null) {
            handler.flush();
        }
    }

    /**
     * 关闭日志输出的文件锁<p>
     * 这个方法应该在每一个插件的onDisable中调用一次<p>
     * 否则.log文件将会一直被占用而无法删除<p>
     * 直到服务器被/stop命令关闭为止
     *
     * @since 2.3.7
     */
    public void close() {
        if (handler != null) {
            handler.close();
        }
    }

    @Override
    public String format(LogRecord record) {
        return String.format(
                "[%s] [%s] %s\n",
                LOG_FORMAT.format(new Date(record.getMillis())),
                record.getLevel().getName(),
                record.getMessage()
        );
    }

    public Logger getLogger() {
        return logger;
    }

    public Handler getHandler() {
        return handler;
    }

    /**
     * 获取日志文件输出流
     *
     * @return 日志文件输出流对象
     * @since 2.3.9
     */
    public FileOutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * 获取日志文件打印流
     *
     * @return 获取日志文件打印流对象
     * @since 2.3.9
     */
    public PrintStream getPrintStream() {
        return printStream;
    }
}
