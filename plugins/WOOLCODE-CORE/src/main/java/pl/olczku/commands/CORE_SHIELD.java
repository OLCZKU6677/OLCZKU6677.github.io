package pl.olczku.commands;


import org.bukkit.plugin.java.JavaPlugin;
import pl.olczku.commands.*;
import pl.olczku.commands.commands.*;

public final class CORE_SHIELD extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Plugin CORE-SHIELD włącza się...");

        // Załaduj konfigurację
        saveDefaultConfig();

        // Zarejestruj komendy
        getCommand("gm").setExecutor(new GmCommand());
        getCommand("heal").setExecutor(new HealCommand());
        getCommand("feed").setExecutor(new FeedCommand());
        getCommand("repair").setExecutor(new RepairCommand());
        getCommand("pomoc").setExecutor(new HelpCommand());
        getCommand("oi").setExecutor(new OiCommand());
        getCommand("ec").setExecutor(new EcCommand());
        getCommand("core-reload").setExecutor(new ReloadCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().severe("Plugin CORE-SHIELD wyłącza się...");
    }
}
