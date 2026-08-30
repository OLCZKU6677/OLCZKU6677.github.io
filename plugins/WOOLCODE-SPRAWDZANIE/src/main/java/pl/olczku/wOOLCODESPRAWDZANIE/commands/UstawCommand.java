package pl.olczku.wOOLCODESPRAWDZANIE.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.olczku.wOOLCODESPRAWDZANIE.WOOLCODESPRAWDZANIE;
import pl.olczku.wOOLCODESPRAWDZANIE.utils.MessagesManager;

public class UstawCommand implements CommandExecutor {

    private final WOOLCODESPRAWDZANIE plugin;
    private final MessagesManager messagesManager;

    public UstawCommand(WOOLCODESPRAWDZANIE plugin) {
        this.plugin = plugin;
        this.messagesManager = plugin.getMessagesManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("woolcodesprawdzanie.ustaw")) {
            player.sendMessage(messagesManager.getMessage("no_permission"));
            return true;
        }

        // Ustawienie punktu sprawdzania
        plugin.getConfig().set("check_location", player.getLocation());
        plugin.saveConfig();

        player.sendMessage(messagesManager.getMessage("check_point_set"));

        return true;
    }
}
