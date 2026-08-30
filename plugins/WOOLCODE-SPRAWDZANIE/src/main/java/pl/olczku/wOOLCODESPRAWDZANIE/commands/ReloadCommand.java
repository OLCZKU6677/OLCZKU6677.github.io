package pl.olczku.wOOLCODESPRAWDZANIE.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.olczku.wOOLCODESPRAWDZANIE.WOOLCODESPRAWDZANIE;
import pl.olczku.wOOLCODESPRAWDZANIE.utils.MessagesManager;

public class ReloadCommand implements CommandExecutor {

    private final WOOLCODESPRAWDZANIE plugin;
    private final MessagesManager messagesManager;

    public ReloadCommand(WOOLCODESPRAWDZANIE plugin) {
        this.plugin = plugin;
        this.messagesManager = plugin.getMessagesManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("woolcodesprawdzanie.reload")) {
            sender.sendMessage(messagesManager.getMessage("noHasPermission")
                    .replace("{PERMISSION}", "woolcodesprawdzanie.reload"));
            return true;
        }

        plugin.getConfigManager().reloadConfig();
        messagesManager.reloadMessages();

        sender.sendMessage(messagesManager.getMessage("reloadConfiguration"));

        return true;
    }
}
