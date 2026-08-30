package pl.olczku.wOOLCODESPRAWDZANIE.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.olczku.wOOLCODESPRAWDZANIE.WOOLCODESPRAWDZANIE;
import pl.olczku.wOOLCODESPRAWDZANIE.utils.MessagesManager;

public class CzystyCommand implements CommandExecutor {

    private final WOOLCODESPRAWDZANIE plugin;
    private final MessagesManager messagesManager;

    public CzystyCommand(WOOLCODESPRAWDZANIE plugin) {
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

        if (!player.hasPermission("woolcodesprawdzanie.czysty")) {
            player.sendMessage(messagesManager.getMessage("noHasPermission")
                    .replace("{PERMISSION}", "woolcodesprawdzanie.czysty"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(messagesManager.getMessage("usageCommand"));
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(messagesManager.getMessage("playerIsOffline")
                    .replace("{PLAYER}", args[0]));
            return true;
        }

        // Logika czyszczenia gracza
        player.sendMessage(messagesManager.getMessage("banPlayerSuccess"));

        return true;
    }
}
