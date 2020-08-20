package cn.hamster3.api.utils;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * 日志记录器
 *
 * @since 2.3.6
 */
@SuppressWarnings("unused")
public class LogUtils extends Formatter {
    private static final SimpleDateFormat LOG_FORMAT = new SimpleDateFormat("HH-mm-ss");
    private static final SimpleDateFormat LOG_NAME_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final Logger logger;

    public LogUtils(Plugin plugin) {
        this(plugin.getLogger(), new File(plugin.getDataFolder(), "logs"));
    }

    public LogUtils(Logger logger, File logFolder) {
        this.logger = logger;
        if (logFolder.mkdirs()) {
            info("创建日志存档文件夹...");
        }

        String time = LOG_NAME_FORMAT.format(new Date());
        int i = 1;
        File file = new File(logFolder, time + "-" + i + ".log");
        while (file.exists()) {
            i++;
            file = new File(logFolder, time + "-" + i + ".log");
        }

        info("创建日志存储文件: " + file.getName());

        try {
            FileHandler handler = new FileHandler(file.getAbsolutePath(), false);
            handler.setEncoding("UTF-8");
            handler.setFormatter(this);
            logger.addHandler(handler);
        } catch (IOException e) {
            warning("初始化日志文件输出管道时发生了一个异常: ");
            e.printStackTrace();
        }

        info("已初始化日志记录器...");
    }

    public void info(String info) {
        logger.info(info);
    }

    public void info(String info, Object... params) {
        logger.info(String.format(info, params));
    }

    public void info(String... infos) {
        for (String info : infos) {
            info(info);
        }
    }

    public void info(Collection<String> infos) {
        for (String info : infos) {
            info(info);
        }
    }

    public void warning(String warning) {
        logger.warning(warning);
    }

    public void warning(String warning, Object... params) {
        logger.warning(String.format(warning, params));
    }

    public void warning(String... warnings) {
        for (String warning : warnings) {
            warning(warning);
        }
    }

    public void warning(Collection<String> warnings) {
        for (String warning : warnings) {
            warning(warning);
        }
    }

    @Override
    public String format(LogRecord record) {
        return String.format(
                "[%s] [%s] %s\n",
                LOG_FORMAT.format(new Date(record.getMillis())),
                record.getLevel().getName(),
                record.getMillis()
        );
    }

    public Logger getLogger() {
        return logger;
    }
}
