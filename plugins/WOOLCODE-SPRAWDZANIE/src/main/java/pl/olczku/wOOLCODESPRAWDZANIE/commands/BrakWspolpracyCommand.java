package pl.olczku.wOOLCODESPRAWDZANIE.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.olczku.wOOLCODESPRAWDZANIE.WOOLCODESPRAWDZANIE;
import pl.olczku.wOOLCODESPRAWDZANIE.utils.MessagesManager;

public class BrakWspolpracyCommand implements CommandExecutor {

    private final WOOLCODESPRAWDZANIE plugin;
    private final MessagesManager messagesManager;

    public BrakWspolpracyCommand(WOOLCODESPRAWDZANIE plugin) {
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

        if (!player.hasPermission("woolcodesprawdzanie.brakwspolpracy")) {
            player.sendMessage(messagesManager.getMessage("noHasPermission")
                    .replace("{PERMISSION}", "woolcodesprawdzanie.brakwspolpracy"));
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

        // Logika banowania za brak współpracy
        player.sendMessage(messagesManager.getMessage("checkPlayerNoCooperation")
                .replace("{PLAYER}", target.getName())
                .replace("{ADMIN}", player.getName()));

        return true;
    }
}
