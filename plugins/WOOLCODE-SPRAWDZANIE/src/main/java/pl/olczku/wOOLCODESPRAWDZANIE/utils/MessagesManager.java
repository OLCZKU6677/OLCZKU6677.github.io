package pl.olczku.wOOLCODESPRAWDZANIE.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MessagesManager {

    private final JavaPlugin plugin;
    private FileConfiguration messages;

    public MessagesManager(JavaPlugin plugin) {
        this.plugin = plugin;
        reloadMessages();
    }

    public void reloadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String key) {
        return messages.getString(key, "&cMessage not found: " + key);
    }
}
