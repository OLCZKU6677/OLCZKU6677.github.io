package pl.olczku.skyluckCore.artefakty;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;

import java.util.ArrayList;
import java.util.List;

public class ArtefaktItem {
    public static ItemStack stworzItem(OlczkuDannyPrzekKom plugin, ArtefaktType typ, boolean posiadany, boolean wlaczony) {
        ItemStack item = new ItemStack(typ.getMaterial(plugin));
        ItemMeta meta = item.getItemMeta();

        String displayName = ChatColor.translateAlternateColorCodes('&', typ.getDisplayName(plugin));
        String description = ChatColor.translateAlternateColorCodes('&', typ.getDescription(plugin));

        String posiadanyText = ChatColor.translateAlternateColorCodes('&',
                plugin.getArtefaktyConfig().getString("gui.tekst_posiadany", "&aPosiadasz ten artefakt!"));
        String niePosiadanyText = ChatColor.translateAlternateColorCodes('&',
                plugin.getArtefaktyConfig().getString("gui.tekst_nie_posiadany", "&cNie posiadasz tego artefaktu"));

        String statusText = ChatColor.translateAlternateColorCodes('&',
                wlaczony ? plugin.getArtefaktyConfig().getString("gui.tekst_wlaczony", " &a(WŁĄCZONY)") :
                        plugin.getArtefaktyConfig().getString("gui.tekst_wylaczony", " &c(WYŁĄCZONY)"));

        List<String> lore = new ArrayList<>();

        String[] linieOpisu = description.split("\n");
        for (String linia : linieOpisu) {
            lore.add(ChatColor.GRAY + linia);
        }

        lore.add("");
        lore.add(posiadany ? posiadanyText : niePosiadanyText);

        meta.setDisplayName(displayName + statusText);
        meta.setLore(lore);

        if (posiadany && wlaczony) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);
        return item;
    }
}