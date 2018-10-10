package me;

import java.io.File;
import java.net.InetSocketAddress;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigFile {
    private static Client ins = Client.getIns();
    private static File file;
    private static FileConfiguration config;
    static final String prefix = "[MultiServerChat] ";

    public static void loadConfig() {
        if (!ins.getDataFolder().exists()) {
            ins.getDataFolder().mkdirs();
        }
        file = new File(ins.getDataFolder(), "config.yml");
        if (!file.exists()) {
            ins.getLogger().info("配置文件不存在,载入默认配置文件!");
            ins.saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(file);
        ins.getLogger().info("配置文件已载入!");
    }

    public static InetSocketAddress getAddr() {
        return new InetSocketAddress(config.getString("host"), Integer.valueOf(config.getString("port")));
    }

    public static String getPwd() {
        return config.getString("password");
    }
}
