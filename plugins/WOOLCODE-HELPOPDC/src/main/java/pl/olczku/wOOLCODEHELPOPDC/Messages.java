package pl.olczku.wOOLCODEHELPOPDC;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Messages {
    private final JavaPlugin plugin;
    private FileConfiguration messagesConfig;

    public Messages(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getString(String path) {
        return messagesConfig.getString(path, "&cBrak wiadomosci w pliku konfiguracyjnym dla sciezki: " + path);
    }
}
