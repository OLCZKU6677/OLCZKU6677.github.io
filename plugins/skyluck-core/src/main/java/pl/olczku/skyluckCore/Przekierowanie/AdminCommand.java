package pl.olczku.skyluckCore.Przekierowanie;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdminCommand implements CommandExecutor {
    private final OlczkuDannyPrzekKom plugin;
    public AdminCommand(OlczkuDannyPrzekKom plugin) { this.plugin = plugin; }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadAndRegister();
            plugin.reloadSkrzynkiConfig();
            plugin.messages().send(sender, "reload_done");
            return true;
        }
        plugin.messages().send(sender, "core_usage");
        return true;
    }
}