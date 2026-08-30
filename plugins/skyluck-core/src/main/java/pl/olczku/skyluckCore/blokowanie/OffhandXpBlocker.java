package pl.olczku.skyluckCore.blokowanie;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;

public class OffhandXpBlocker implements Listener {
    private final OlczkuDannyPrzekKom plugin;

    public OffhandXpBlocker(OlczkuDannyPrzekKom plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        boolean isProtectedChest = false;
        if (holder instanceof Chest c && c.getWorld().getName().equalsIgnoreCase(plugin.allowedWorld())) {
            isProtectedChest = true;
        } else if (holder instanceof DoubleChest dc && dc.getLocation().getWorld() != null && dc.getLocation().getWorld().getName().equalsIgnoreCase(plugin.allowedWorld())) {
            isProtectedChest = true;
        }

        if (!isProtectedChest) {
            return;
        }

        ItemStack toOffhand;
        switch (event.getAction()) {
            case SWAP_WITH_CURSOR:
                toOffhand = event.getCurrentItem();
                break;
            case MOVE_TO_OTHER_INVENTORY:
                toOffhand = event.getCurrentItem();
                break;
            case HOTBAR_SWAP:
            case HOTBAR_MOVE_AND_READD:
                toOffhand = player.getInventory().getItem(event.getHotbarButton());
                break;
            default:
                if (event.getSlot() == 40) {
                    toOffhand = event.getCursor();
                } else {
                    return;
                }
        }

        if (toOffhand != null && toOffhand.getType() == Material.EXPERIENCE_BOTTLE) {
            event.setCancelled(true);

            Bukkit.getScheduler().runTask(plugin, () -> {
                ItemStack offhandItem = player.getInventory().getItemInOffHand();
                if (offhandItem.getType() == Material.EXPERIENCE_BOTTLE) {
                    player.getInventory().setItemInOffHand(null);
                    player.getInventory().addItem(offhandItem);
                    player.updateInventory();
                }
            });
        }
    }
}