package pl.olczku.wOOLCODEAUTORESTARTZAM;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RestartCommand implements CommandExecutor {

    private final WOOLCODEAUTORESTARTZAM plugin;
    private final String prefix = ChatColor.GOLD + "[AutoReload] " + ChatColor.GRAY;

    public RestartCommand(WOOLCODEAUTORESTARTZAM plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("woolcode.autorestart.admin")) {
            sender.sendMessage(prefix + ChatColor.RED + "Nie masz uprawnień do użycia tej komendy.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(prefix + "Dostępne komendy:");
            sender.sendMessage(ChatColor.YELLOW + "/autorestart now" + ChatColor.GRAY + " - natychmiastowe przeładowanie.");
            sender.sendMessage(ChatColor.YELLOW + "/autorestart cancel" + ChatColor.GRAY + " - anuluje zaplanowane przeładowanie.");
            sender.sendMessage(ChatColor.YELLOW + "/autorestart reload" + ChatColor.GRAY + " - przeładowuje konfigurację.");
            sender.sendMessage(ChatColor.YELLOW + "/autorestart time" + ChatColor.GRAY + " - pokazuje czas do przeładowania.");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "now":
                sender.sendMessage(prefix + "Rozpoczynanie sekwencji przeładowania...");
                plugin.initiateReloadSequence();
                break;
            case "cancel":
                plugin.cancelAllTasks();
                sender.sendMessage(prefix + "Automatyczne przeładowanie zostało anulowane.");
                break;
            case "reload":
                plugin.reload();
                sender.sendMessage(prefix + "Konfiguracja została przeładowana. Przeładowanie zaplanowano na nowo.");
                break;
            case "time":
                sender.sendMessage(prefix + "Czas do przeładowania: " + ChatColor.YELLOW + plugin.getTimeUntilReload());
                break;
            default:
                sender.sendMessage(prefix + ChatColor.RED + "Nieznana komenda. Użyj /autorestart po pomoc.");
                break;
        }
        return true;
    }
}