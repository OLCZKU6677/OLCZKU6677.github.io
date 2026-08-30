package pl.olczku.skyluckCore.skrzynkapoziom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SkrzynkaListener implements Listener {
    private final OlczkuDannyPrzekKom plugin;
    private final Random rng = new Random();

    public SkrzynkaListener(OlczkuDannyPrzekKom plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = event.getItemInHand();
        if (itemInHand == null || itemInHand.getType() != Material.CHEST || !itemInHand.hasItemMeta()) {
            return;
        }

        ItemMeta meta = itemInHand.getItemMeta();
        if (meta == null || !meta.hasCustomModelData()) {
            return;
        }

        int poziom = meta.getCustomModelData();
        if (poziom <= 0) {
            return;
        }

        event.setCancelled(true);
        if(player.getGameMode() != org.bukkit.GameMode.CREATIVE){
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        }

        ConfigurationSection skrzynkaConfig = plugin.getSkrzynkiConfig().getConfigurationSection("skrzynki." + poziom);
        if (skrzynkaConfig == null) {
            plugin.messages().send(player, "skrzynka_config_missing", Map.of("level", String.valueOf(poziom)));
            return;
        }

        String nazwaSkrzynki = skrzynkaConfig.getString("nazwa", "§6Skrzynka obrońców (Poziom " + poziom + ")");

        List<ItemStack> drops = new ArrayList<>();
        ConfigurationSection itemsSection = skrzynkaConfig.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemConfig = itemsSection.getConfigurationSection(key);
                if (itemConfig == null) continue;

                double chance = itemConfig.getDouble("chance", 0.0);
                if (rng.nextDouble() * 100 < chance) {
                    ItemStack drop = itemConfig.getItemStack("item");
                    if (drop != null) {
                        drops.add(drop);
                        break;
                    }
                }
            }
        }

        if (drops.isEmpty()) {
            plugin.messages().send(player, "skrzynka_empty");
        } else {
            for (ItemStack drop : drops) {
                player.getInventory().addItem(drop).values().forEach(remaining -> {
                    player.getWorld().dropItemNaturally(player.getLocation(), remaining);
                });
            }
            plugin.messages().send(player, "skrzynka_opened");
        }
    }
}