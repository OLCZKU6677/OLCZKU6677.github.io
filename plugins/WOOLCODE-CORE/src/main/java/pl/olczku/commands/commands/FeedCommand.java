package pl.olczku.commands.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("feed")) {
            if (sender.hasPermission("shieldcode.feed")) {
                if (args.length == 0) {

                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        p.setFoodLevel(20);
                        sender.sendMessage(ChatColor.GREEN + "Zostałeś Nakarmiony!");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Tylko gracz może użyć tej komendy!");
                    }
                    return true;


                } else {
                    Player t = Bukkit.getPlayerExact(args[0]);
                    if (t.isOnline()) {
                        t.setFoodLevel(20);
                        t.sendMessage(ChatColor.GREEN + "Zostałeś Nakarmiony");
                        sender.sendMessage(ChatColor.GREEN + "Nakarmiłeś gracza" + ChatColor.GOLD + t);
                    } else {

                        sender.sendMessage(ChatColor.RED + "Gracz którego próbujesz nakarmić nie jest online");
                    }
                }


            } else {
                sender.sendMessage(ChatColor.RED + "Nie możesz korzystać z tej komendy");
            }

        }
        return false;
    }

}
