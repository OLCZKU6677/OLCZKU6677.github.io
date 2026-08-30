package pl.olczku.wOOLCODEANTYSWEAP;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class WOOLCODEANTYSWEAP extends JavaPlugin implements Listener {

    private List<String> bannedWords;
    private boolean kickPlayer;
    private String warnMessage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        bannedWords = config.getStringList("banned-words");
        kickPlayer = config.getBoolean("kick-player");
        warnMessage = ChatColor.translateAlternateColorCodes('&', config.getString("warn-message"));

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("WOOLCODE-ANTYSWAP uruchomiony!");
    }

    @Override
    public void onDisable() {
        getLogger().info("WOOLCODE-ANTYSWAP wyłączony.");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage().toLowerCase();

        for (String word : bannedWords) {
            if (message.contains(word.toLowerCase())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(warnMessage);

                Bukkit.getScheduler().runTask(this, () -> {
                    for (int i = 0; i < 100; i++) {
                        Bukkit.getServer().broadcastMessage("");
                    }
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute " + event.getPlayer().getName() + " 20m");
                });
                break;
            }
        }
    }
}
