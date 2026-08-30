package pl.olczku.wOOLCODESPRAWDZANIE.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.olczku.wOOLCODESPRAWDZANIE.WOOLCODESPRAWDZANIE;
import pl.olczku.wOOLCODESPRAWDZANIE.utils.MessagesManager;

public class SprawdzCommand implements CommandExecutor {

    private final WOOLCODESPRAWDZANIE plugin;
    private final MessagesManager messagesManager;

    public SprawdzCommand(WOOLCODESPRAWDZANIE plugin) {
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

        if (!player.hasPermission("woolcodesprawdzanie.sprawdz")) {
            player.sendMessage(messagesManager.getMessage("no_permission"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(messagesManager.getMessage("usage_sprawdz"));
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(messagesManager.getMessage("player_not_found"));
            return true;
        }

        player.sendMessage(messagesManager.getMessage("player_being_checked")
                .replace("%player%", target.getName())
                .replace("%admin%", player.getName()));

        return true;
    }
}
