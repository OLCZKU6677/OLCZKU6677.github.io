package pl.olczku.wOOLCODESPRAWDZANIE;

import org.bukkit.plugin.java.JavaPlugin;
import pl.olczku.wOOLCODESPRAWDZANIE.commands.*;
import pl.olczku.wOOLCODESPRAWDZANIE.listeners.PlayerChatListener;
import pl.olczku.wOOLCODESPRAWDZANIE.utils.ConfigManager;
import pl.olczku.wOOLCODESPRAWDZANIE.utils.MessagesManager;

public final class WOOLCODESPRAWDZANIE extends JavaPlugin {

    private ConfigManager configManager;
    private MessagesManager messagesManager;

    @Override
    public void onEnable() {
        // Inicjalizacja managerów
        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this);

        // Rejestracja komend
        getCommand("sprawdz").setExecutor(new SprawdzCommand(this));
        getCommand("czysty").setExecutor(new CzystyCommand(this));
        getCommand("reload").setExecutor(new ReloadCommand(this));
        getCommand("ustaw").setExecutor(new UstawCommand(this));
        getCommand("brakwspolpracy").setExecutor(new BrakWspolpracyCommand(this));
        getCommand("cheaty").setExecutor(new CheatyCommand(this));

        // Rejestracja listenerów
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }
}
