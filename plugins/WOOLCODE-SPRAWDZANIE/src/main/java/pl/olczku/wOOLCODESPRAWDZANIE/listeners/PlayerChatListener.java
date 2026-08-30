package pl.olczku.wOOLCODESPRAWDZANIE.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.olczku.wOOLCODESPRAWDZANIE.WOOLCODESPRAWDZANIE;
import pl.olczku.wOOLCODESPRAWDZANIE.utils.MessagesManager;

public class PlayerChatListener implements Listener {

    private final WOOLCODESPRAWDZANIE plugin;
    private final MessagesManager messagesManager;

    public PlayerChatListener(WOOLCODESPRAWDZANIE plugin) {
        this.plugin = plugin;
        this.messagesManager = plugin.getMessagesManager();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("woolcodesprawdzanie.seemsg")) {
            event.getRecipients().clear();
            event.getRecipients().add(player);
        }
    }
}
