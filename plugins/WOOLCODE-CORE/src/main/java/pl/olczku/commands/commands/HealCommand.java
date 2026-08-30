package pl.olczku.commands.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("heal")) {
            if (sender.hasPermission("code.heal")) {
                if (args.length == 0) {

                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                        sender.sendMessage(ChatColor.GREEN + "Zostałeś uleczony!");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Tylko gracz może użyć tej komendy!");
                    }
                    return true;


                } else {
                    Player t = Bukkit.getPlayerExact(args[0]);
                    if (t.isOnline()) {
                        t.resetMaxHealth();
                        t.sendMessage(ChatColor.GREEN + "Zostałeś uleczony");
                        sender.sendMessage(ChatColor.GREEN + "Uleczyłeś gracza" + ChatColor.GOLD + t);
                    } else {

                        sender.sendMessage(ChatColor.RED + "Gracz którego próbujesz uleczyć nie jest online");
                    }
                }


            } else {
                sender.sendMessage(ChatColor.RED + "Nie możesz korzystać z tej komendy");
            }

        }
        return false;
    }

}
