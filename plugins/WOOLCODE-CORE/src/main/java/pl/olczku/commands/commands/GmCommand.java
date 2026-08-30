package pl.olczku.commands.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GmCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("gm")) {
            if (sender.hasPermission("shieldcode.gm")) {

                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "Użycie: /gm <tryb> [gracz]");
                    return true;
                }


                if (sender instanceof Player) {
                    Player p = (Player) sender;


                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("creative")) {
                            p.setGameMode(GameMode.CREATIVE);
                            p.sendMessage(ChatColor.GREEN + "Twój tryb gry został zmieniony na " + ChatColor.GOLD + "CREATIVE");
                        } else if (args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("adventure")) {
                            p.setGameMode(GameMode.ADVENTURE);
                            p.sendMessage(ChatColor.GREEN + "Twój tryb gry został zmieniony na " + ChatColor.GOLD + "ADVENTURE");
                        } else if (args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("spectator")) {
                            p.setGameMode(GameMode.SPECTATOR);
                            p.sendMessage(ChatColor.GREEN + "Twój tryb gry został zmieniony na " + ChatColor.GOLD + "SPECTATOR");
                        } else if (args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("survival")) {
                            p.setGameMode(GameMode.SURVIVAL);
                            p.sendMessage(ChatColor.GREEN + "Twój tryb gry został zmieniony na " + ChatColor.GOLD + "SURVIVAL");
                        }
                    }

                    else if (args.length == 2) {
                        Player t = Bukkit.getPlayerExact(args[1]);
                        if (t == null) {
                            p.sendMessage(ChatColor.RED + "Gracz któremu chcesz zmienić tryb gry nie jest online");
                            return true;
                        }

                        if (args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("creative")) {
                            t.setGameMode(GameMode.CREATIVE);
                            t.sendMessage(ChatColor.GREEN + "Twój tryb gry został zmieniony na " + ChatColor.GOLD + "CREATIVE");
                            p.sendMessage(ChatColor.GREEN + "Zmieniłeś tryb gry graczowi " + ChatColor.GOLD + t.getName() + ChatColor.GREEN + " na " + ChatColor.GOLD + "CREATIVE");
                        } else if (args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("adventure")) {
                            t.setGameMode(GameMode.ADVENTURE);
                            t.sendMessage(ChatColor.GREEN + "Twój tryb gry został zmieniony na " + ChatColor.GOLD + "ADVENTURE");
                            p.sendMessage(ChatColor.GREEN + "Zmieniłeś tryb gry graczowi " + ChatColor.GOLD + t.getName() + ChatColor.GREEN + " na " + ChatColor.GOLD + "ADVENTURE");
                        } else if (args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("spectator")) {
                            t.setGameMode(GameMode.SPECTATOR);
                            t.sendMessage(ChatColor.GREEN + "Twój tryb gry został zmieniony na " + ChatColor.GOLD + "SPECTATOR");
                            p.sendMessage(ChatColor.GREEN + "Zmieniłeś tryb gry graczowi " + ChatColor.GOLD + t.getName() + ChatColor.GREEN + " na " + ChatColor.GOLD + "SPECTATOR");
                        } else if (args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("survival")) {
                            t.setGameMode(GameMode.SURVIVAL);
                            t.sendMessage(ChatColor.GREEN + "Twój tryb gry został zmieniony na " + ChatColor.GOLD + "SURVIVAL");
                            p.sendMessage(ChatColor.GREEN + "Zmieniłeś tryb gry graczowi " + ChatColor.GOLD + t.getName() + ChatColor.GREEN + " na " + ChatColor.GOLD + "SURVIVAL");
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Tylko gracz może wykonać tę komendę.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Nie masz uprawnień do używania tej komendy.");
            }
        }
        return false;
    }
}

