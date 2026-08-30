package pl.olczku.commands.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class RepairCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("repair")) {
            if (sender.hasPermission("code.repair")) {

                if (args.length == 0) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        ItemStack item = p.getInventory().getItemInMainHand();
                        if (item == null || item.getType() == Material.AIR || item.getItemMeta() == null) {
                            p.sendMessage(ChatColor.RED + "Nie masz przedmiotu w ręce!");
                            return true;
                        } else {
                            if (item.getItemMeta() instanceof Damageable) {
                                ItemMeta meta = item.getItemMeta();
                                ((Damageable) meta).setDamage(0);
                                item.setItemMeta(meta);
                                p.sendMessage(ChatColor.GREEN + "Item trzymany w ręku został naprawiony");
                            }
                        }
                    }
                } else if (args.length == 1 && args[0].equalsIgnoreCase("all")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        for (ItemStack item : p.getInventory().getContents()) {
                            if (item != null && item.getItemMeta() instanceof Damageable) {
                                ItemMeta meta = item.getItemMeta();
                                ((Damageable) meta).setDamage(0);
                                item.setItemMeta(meta);
                            }
                        }
                        p.sendMessage(ChatColor.GREEN + "Wszystkie przedmioty w ekwipunku zostały naprawione");
                    }
                } else if (args.length == 2) {
                    Player t = Bukkit.getPlayerExact(args[1]);
                    if (t == null) {
                        sender.sendMessage(ChatColor.RED + "Gracz " + args[1] + " nie jest online.");
                        return true;
                    }

                    if (args[0].equalsIgnoreCase("all")) {
                        for (ItemStack item : t.getInventory().getContents()) {
                            if (item != null && item.getItemMeta() instanceof Damageable) {
                                ItemMeta meta = item.getItemMeta();
                                ((Damageable) meta).setDamage(0);
                                item.setItemMeta(meta);
                            }
                        }
                        sender.sendMessage(ChatColor.GREEN + "Wszystkie przedmioty w ekwipunku gracza " + ChatColor.GOLD + t.getName() + ChatColor.GREEN + " zostały naprawione");
                        t.sendMessage(ChatColor.GREEN + "Wszystkie przedmioty w ekwipunku zostały naprawione");
                    } else {

                        ItemStack main = t.getInventory().getItemInMainHand();
                        if (main == null || main.getItemMeta() == null) {
                            sender.sendMessage(ChatColor.RED + "Gracz nie trzyma żadnego przedmiotu w ręce!");
                            return true;
                        }

                        if (main.getItemMeta() instanceof Damageable) {
                            ItemMeta meta = main.getItemMeta();
                            ((Damageable) meta).setDamage(0);
                            main.setItemMeta(meta);
                            sender.sendMessage(ChatColor.GREEN + "Naprawiłeś item graczowi " + ChatColor.GOLD + t.getName() + ChatColor.GREEN + " który trzyma w ręku");
                            t.sendMessage(ChatColor.GREEN + "Item trzymany w ręku został naprawiony");
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Błędne użycie komendy. Użyj: /repair [all] [gracz] ");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Nie masz uprawnień do korzystania z tej komendy");
            }
        }
        return false;
    }
}
