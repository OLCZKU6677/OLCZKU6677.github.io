package pl.olczku.commands.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Set;

public class HelpCommand implements CommandExecutor {

    private JavaPlugin plugin = null;

    public HelpCommand() {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("pomoc") || command.getName().equalsIgnoreCase("help")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (sender.hasPermission("code.pomoc")) {
                    FileConfiguration config = plugin.getConfig();

                    // Pobierz dane z konfiguracji
                    String title = ChatColor.translateAlternateColorCodes('&', config.getString("gui.title"));
                    int size = config.getInt("gui.size");

                    Inventory inv = Bukkit.createInventory(null, size, title);

                    // Dodaj tło
                    String backgroundMaterial = config.getString("gui.items.background.material");
                    if (backgroundMaterial != null) {
                        ItemStack background = new ItemStack(Material.valueOf(backgroundMaterial));
                        ItemMeta backgroundMeta = background.getItemMeta();
                        backgroundMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("gui.items.background.display_name")));
                        backgroundMeta.setLore(config.getStringList("gui.items.background.lore"));
                        background.setItemMeta(backgroundMeta);

                        for (int i = 0; i < size; i++) {
                            inv.setItem(i, background);
                        }
                    }

                    // Dodaj wszystkie itemy z sekcji "items"
                    ConfigurationSection itemsSection = config.getConfigurationSection("gui.items");
                    if (itemsSection != null) {
                        Set<String> itemKeys = itemsSection.getKeys(false);
                        for (String itemKey : itemKeys) {
                            if (itemKey.equals("background")) continue; // Pomijamy tło

                            String material = itemsSection.getString(itemKey + ".material");
                            String displayName = itemsSection.getString(itemKey + ".display_name");
                            List<String> lore = itemsSection.getStringList(itemKey + ".lore");
                            int slot = itemsSection.getInt(itemKey + ".slot");

                            if (material != null && slot >= 0 && slot < size) {
                                ItemStack item = new ItemStack(Material.valueOf(material));
                                ItemMeta meta = item.getItemMeta();
                                if (meta != null) {
                                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
                                    meta.setLore(lore);
                                    item.setItemMeta(meta);
                                }
                                inv.setItem(slot, item);
                            }
                        }
                    }

                    p.openInventory(inv);
                }
            }
        }
        return false;
    }
}
