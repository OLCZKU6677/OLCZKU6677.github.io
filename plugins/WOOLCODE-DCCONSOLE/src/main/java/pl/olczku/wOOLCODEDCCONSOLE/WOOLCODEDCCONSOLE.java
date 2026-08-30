package pl.olczku.wOOLCODEDCCONSOLE;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.security.auth.login.LoginException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public final class WOOLCODEDCCONSOLE extends JavaPlugin implements Listener {

    private TextChannel channel;
    private static final String AUTHOR = "WOOLCODE"; // Oczekiwana wartość autora

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this);

        // Sprawdzenie autora
        if (!getDescription().getAuthors().contains(AUTHOR)) {
            getLogger().severe("Plugin nie może być uruchomiony, ponieważ autor nie zgadza się z wymaganą wartością: " + AUTHOR);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Wczytanie konfiguracji
        saveDefaultConfig();
        String botToken = getConfig().getString("bot_token");
        String channelId = getConfig().getString("channel_id");

        try {
            JDABuilder builder = JDABuilder.createDefault(botToken);
            channel = builder.build().awaitReady().getTextChannelById(channelId);
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        // Dodanie handlera do logów
        addLogHandler();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void addLogHandler() {
        java.util.logging.Logger logger = Bukkit.getLogger();
        logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (channel != null) {
                    String message = record.getMessage();
                    // Wysyłanie logów do Discorda
                    channel.sendMessage("`" + message + "`").queue();
                }
            }

            @Override
            public void flush() {
                // No action needed
            }

            @Override
            public void close() throws SecurityException {
                // No action needed
            }
        });
    }
}
