package pl.olczku.skyluckCore.stattrak;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;

public class StatTrakCommand implements CommandExecutor {

    private final StatTrakManager manager;
    private final OlczkuDannyPrzekKom plugin;

    public StatTrakCommand(OlczkuDannyPrzekKom plugin, StatTrakManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("skyluck.core.stattrak.admin")) {
            plugin.messages().send(sender, "no_permission");
            return true;
        }

        if (args.length != 2 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage("§cPoprawne użycie: /stattrak give <nick>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cGracz o podanym nicku nie jest online.");
            return true;
        }

        target.getInventory().addItem(manager.getActivatorItem());
        target.sendMessage("§aOtrzymałeś Aktywator StatTrak!");
        sender.sendMessage("§aPomyślnie nadano aktywator graczowi " + target.getName() + ".");
        return true;
    }
}