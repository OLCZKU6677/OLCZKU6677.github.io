package pl.olczku.skyluckCore.stattrak;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public class StatTrakListener implements Listener {

    private final StatTrakManager manager;

    public StatTrakListener(StatTrakManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        if (cursor != null && current != null && cursor.getType() != Material.AIR && cursor.hasItemMeta()) {
            if (manager.isActivator(cursor) && manager.determineType(current.getType()) != null) {
                event.setCancelled(true);

                // Sprawdź czy przedmiot już ma StatTrak
                if (manager.hasStatTrak(current)) {
                    event.getWhoClicked().sendMessage("§cTen przedmiot już posiada StatTrak!");
                    return;
                }

                boolean success = manager.applyStatTrak(current);
                if (success) {
                    cursor.setAmount(cursor.getAmount() - 1);
                    event.getWhoClicked().sendMessage("§aPomyślnie nałożono StatTrak na przedmiot!");
                } else {
                    event.getWhoClicked().sendMessage("§cNie udało się nałożyć StatTrak na przedmiot!");
                }
            }
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        if (result != null && manager.hasStatTrak(result)) {
            manager.updateItemDisplay(result);
            event.setResult(result);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) return;

        ItemStack mainHand = killer.getInventory().getItemInMainHand();
        ItemStack offHand = killer.getInventory().getItemInOffHand();

        checkAndIncrementStatTrak(mainHand, killer, StatTrakType.KILLS);
        checkAndIncrementStatTrak(offHand, killer, StatTrakType.KILLS);
    }

    private void checkAndIncrementStatTrak(ItemStack item, Player player, StatTrakType expectedType) {
        if (item != null && manager.hasStatTrak(item) && manager.getStatTrakType(item) == expectedType) {
            manager.incrementCounter(item, player);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (!manager.hasStatTrak(itemInHand)) return;

        StatTrakType type = manager.getStatTrakType(itemInHand);
        Material brokenBlock = event.getBlock().getType();
        Block block = event.getBlock();

        if (type == StatTrakType.BLOCKS_BROKEN || type == StatTrakType.LOGS_BROKEN) {
            if (type == StatTrakType.LOGS_BROKEN && manager.isWoodLog(brokenBlock)) {
                manager.incrementCounter(itemInHand, player);
            } else if (type == StatTrakType.BLOCKS_BROKEN) {
                manager.incrementCounter(itemInHand, player);
            }
        } else if (type == StatTrakType.CROPS_HARVESTED) {
            // Sprawdzamy czy to dojrzała uprawa
            if (manager.isFullyGrownCrop(brokenBlock) && manager.isMatureCrop(block)) {
                manager.incrementCounter(itemInHand, player);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        for (ItemStack armorPiece : player.getInventory().getArmorContents()) {
            if (armorPiece != null && manager.hasStatTrak(armorPiece) &&
                    manager.getStatTrakType(armorPiece) == StatTrakType.DAMAGE_TAKEN) {
                manager.incrementCounter(armorPiece, player);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        if (mainHand != null && mainHand.getType() == Material.SHIELD &&
                manager.hasStatTrak(mainHand) && manager.getStatTrakType(mainHand) == StatTrakType.DAMAGE_BLOCKED) {
            manager.incrementCounter(mainHand, player);
        } else if (offHand != null && offHand.getType() == Material.SHIELD &&
                manager.hasStatTrak(offHand) && manager.getStatTrakType(offHand) == StatTrakType.DAMAGE_BLOCKED) {
            manager.incrementCounter(offHand, player);
        }
    }
}