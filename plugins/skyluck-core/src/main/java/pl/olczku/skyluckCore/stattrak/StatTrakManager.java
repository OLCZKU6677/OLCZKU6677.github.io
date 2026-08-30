package pl.olczku.skyluckCore.stattrak;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;

import java.util.List;

public class StatTrakManager {

    private OlczkuDannyPrzekKom plugin;

    public StatTrakManager(OlczkuDannyPrzekKom plugin) {
        this.plugin = plugin;
    }

    public StatTrakManager() {


    }

    public ItemStack getActivatorItem() {
        String materialName = plugin.getConfig().getString("stattrak.activator.material", "NETHER_STAR");
        Material material = Material.matchMaterial(materialName);
        if (material == null) material = Material.NETHER_STAR;

        ItemStack activator = new ItemStack(material);
        ItemMeta meta = activator.getItemMeta();
        meta.setDisplayName(plugin.getConfig().getString("stattrak.activator.name", "§6StatTrak Activator"));
        meta.setLore(plugin.getConfig().getStringList("stattrak.activator.lore"));
        activator.setItemMeta(meta);
        return activator;
    }

    public boolean isActivator(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        String configuredMaterial = plugin.getConfig().getString("stattrak.activator.material", "NETHER_STAR");
        Material material = Material.matchMaterial(configuredMaterial);
        if (material == null) material = Material.NETHER_STAR;

        if (item.getType() != material) return false;

        ItemMeta meta = item.getItemMeta();
        String configuredName = plugin.getConfig().getString("stattrak.activator.name", "§6StatTrak Activator");
        return meta.hasDisplayName() && meta.getDisplayName().equals(configuredName);
    }

    public StatTrakType determineType(Material material) {
        String materialName = material.name();

        if (materialName.endsWith("_SWORD") || materialName.equals("BOW") || materialName.equals("CROSSBOW")) {
            return StatTrakType.KILLS;
        } else if (materialName.endsWith("_AXE")) {
            return StatTrakType.LOGS_BROKEN;
        } else if (materialName.endsWith("_PICKAXE") || materialName.endsWith("_SHOVEL")) {
            return StatTrakType.BLOCKS_BROKEN;
        } else if (materialName.endsWith("_HOE")) {
            return StatTrakType.CROPS_HARVESTED;
        } else if (materialName.endsWith("_CHESTPLATE") || materialName.endsWith("_LEGGINGS") ||
                materialName.endsWith("_BOOTS") || materialName.endsWith("_HELMET")) {
            return StatTrakType.DAMAGE_TAKEN;
        } else if (material == Material.SHIELD) {
            return StatTrakType.DAMAGE_BLOCKED;
        }
        return null;
    }

    public boolean hasStatTrak(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return false;

        for (String line : meta.getLore()) {
            for (StatTrakType type : StatTrakType.values()) {
                if (line.startsWith(type.getLorePrefix())) {
                    return true;
                }
            }
        }
        return false;
    }

    public StatTrakType getStatTrakType(ItemStack item) {
        if (!hasStatTrak(item)) return null;
        ItemMeta meta = item.getItemMeta();
        for (String line : meta.getLore()) {
            for (StatTrakType type : StatTrakType.values()) {
                if (line.startsWith(type.getLorePrefix())) {
                    return type;
                }
            }
        }
        return null;
    }

    public boolean applyStatTrak(ItemStack item) {
        // Sprawdź czy przedmiot już ma StatTrak
        if (hasStatTrak(item)) {
            return false;
        }

        StatTrakType type = determineType(item.getType());
        if (type == null) return false;

        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : new java.util.ArrayList<>();
        lore.add(type.getLorePrefix() + "0");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return true;
    }

    public void incrementCounter(ItemStack item, Player player) {
        StatTrakType type = getStatTrakType(item);
        if (type == null) return;

        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            if (line.startsWith(type.getLorePrefix())) {
                try {
                    int count = Integer.parseInt(line.substring(type.getLorePrefix().length()));
                    lore.set(i, type.getLorePrefix() + (count + 1));
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    player.updateInventory();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void updateItemDisplay(ItemStack item) {
        if (!hasStatTrak(item)) return;
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            for (StatTrakType type : StatTrakType.values()) {
                if (line.startsWith(type.getLorePrefix())) {
                    String count = line.substring(type.getLorePrefix().length());
                    lore.set(i, type.getLorePrefix() + count);
                    break;
                }
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public boolean isWoodLog(Material material) {
        return material.name().endsWith("_LOG") || material.name().endsWith("_WOOD");
    }

    public boolean isFullyGrownCrop(Material material) {
        return material == Material.WHEAT || material == Material.CARROTS ||
                material == Material.POTATOES || material == Material.BEETROOTS ||
                material == Material.NETHER_WART || material == Material.COCOA;
    }

    public boolean isMatureCrop(Block block) {
        BlockData blockData = block.getBlockData();

        if (blockData instanceof Ageable) {
            Ageable ageable = (Ageable) blockData;
            return ageable.getAge() == ageable.getMaximumAge();
        }

        switch (block.getType()) {
            case NETHER_WART:
            case COCOA:
                return true;
            default:
                return false;
        }
    }
}