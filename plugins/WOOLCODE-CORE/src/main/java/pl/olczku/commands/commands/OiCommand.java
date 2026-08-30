package pl.olczku.commands.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class OiCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                Inventory inventory = target.getInventory();
                ((Player) sender).openInventory(inventory);
                return true;
            } else {
                sender.sendMessage("Gracz nie jest online.");
            }
        }
        return false;
    }
}
