package pl.olczku.skyluckCore.skrzynkapoziom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WymianaListener implements Listener {

    private final String GUI_NAME = "§8Wymiana Skrzynek";
    private OlczkuDannyPrzekKom plugin = null;

    public WymianaListener() {
        this.plugin = plugin;
    }

    public void openWymianaGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_NAME);

        ItemStack background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta bgMeta = background.getItemMeta();
        bgMeta.setDisplayName(" ");
        background.setItemMeta(bgMeta);
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, background);
        }

        ItemStack placeholder = new ItemStack(Material.BARRIER);
        ItemMeta placeholderMeta = placeholder.getItemMeta();
        placeholderMeta.setDisplayName("§c<-- Wrzuć tutaj skrzynki");
        placeholderMeta.setLore(Arrays.asList("§7Umieść tutaj dokładnie 64", "§7skrzynki tego samego poziomu,", "§7aby je ulepszyć."));
        placeholder.setItemMeta(placeholderMeta);
        gui.setItem(11, placeholder);

        gui.setItem(15, null);

        ItemStack confirmButton = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName("§aPotwierdź wymianę");
        confirmMeta.setLore(Collections.singletonList("§7Kliknij, aby ulepszyć skrzynki!"));
        confirmButton.setItemMeta(confirmMeta);
        gui.setItem(13, confirmButton);

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_NAME)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        ItemStack currentItem = event.getCurrentItem();

        if (slot < 0) return;

        if (slot < 27) {
            if (currentItem != null && (currentItem.getType() == Material.BARRIER || currentItem.getType() == Material.GRAY_STAINED_GLASS_PANE || currentItem.getType() == Material.GREEN_STAINED_GLASS_PANE)) {
                event.setCancelled(true);
            }

            if (slot == 13) {
                event.setCancelled(true);
                Inventory gui = event.getClickedInventory();
                ItemStack inputItem = gui.getItem(11);

                if (inputItem == null || inputItem.getType() == Material.BARRIER || !inputItem.hasItemMeta() || !inputItem.getItemMeta().hasCustomModelData()) {
                    player.sendMessage("§cWłóż skrzynki do lewego slotu!");
                    return;
                }

                if (inputItem.getAmount() != 64) {
                    player.sendMessage("§cPotrzebujesz dokładnie 64 skrzynek!");
                    return;
                }

                int currentLevel = inputItem.getItemMeta().getCustomModelData();
                int nextLevel = currentLevel + 1;

                if (nextLevel > 3) {
                    player.sendMessage("§cTych skrzynek nie da się już ulepszyć!");
                    return;
                }

                gui.setItem(11, null);
                gui.setItem(15, createSkrzynka(nextLevel));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(GUI_NAME)) {
            return;
        }

        Inventory gui = event.getInventory();
        Player player = (Player) event.getPlayer();

        ItemStack inputItem = gui.getItem(11);
        if (inputItem != null && inputItem.getType() != Material.BARRIER) {
            player.getInventory().addItem(inputItem);
        }

        ItemStack outputItem = gui.getItem(15);
        if (outputItem != null) {
            player.getInventory().addItem(outputItem);
        }
    }

    private ItemStack createSkrzynka(int poziom) {
        ConfigurationSection skrzynkaConfig = plugin.getSkrzynkiConfig().getConfigurationSection("skrzynki." + poziom);

        String nazwaSkrzynki = "§6Skrzynka obrońców (Poziom " + poziom + ")";
        if (skrzynkaConfig != null && skrzynkaConfig.contains("nazwa")) {
            nazwaSkrzynki = skrzynkaConfig.getString("nazwa");
        }


        List<String> lore = Collections.singletonList("§7Postaw, aby otworzyć!");
        if (skrzynkaConfig != null && skrzynkaConfig.contains("lore")) {
            lore = skrzynkaConfig.getStringList("lore");
        }

        ItemStack skrzynka = new ItemStack(Material.CHEST);
        ItemMeta meta = skrzynka.getItemMeta();
        meta.setDisplayName(nazwaSkrzynki);
        meta.setLore(lore);
        meta.setCustomModelData(poziom);
        skrzynka.setItemMeta(meta);
        return skrzynka;
    }
}