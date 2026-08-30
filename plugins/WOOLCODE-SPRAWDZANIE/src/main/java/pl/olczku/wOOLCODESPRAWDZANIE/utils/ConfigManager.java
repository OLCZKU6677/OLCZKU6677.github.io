package pl.olczku.wOOLCODESPRAWDZANIE.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public String getCheckLocation() {
        return config.getString("check_location", "spawn");
    }

    public int getBanDurationBrakWspolpracy() {
        return config.getInt("ban_duration.brak_wspolpracy", 7);
    }

    public int getBanDurationCheaty() {
        return config.getInt("ban_duration.cheaty", 30);
    }
}
