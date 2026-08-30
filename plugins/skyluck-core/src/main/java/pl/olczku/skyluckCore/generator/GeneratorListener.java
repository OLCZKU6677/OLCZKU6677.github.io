package pl.olczku.skyluckCore.generator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class GeneratorListener implements Listener {

    private final GeneratorManager generatorManager;

    public GeneratorListener(GeneratorManager generatorManager) {
        this.generatorManager = generatorManager;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();
        if (itemInHand.isSimilar(generatorManager.getGeneratorItem())) {
            Location location = event.getBlock().getLocation();
            generatorManager.addGenerator(location);
            event.getPlayer().sendMessage("§aPomyślnie postawiono generator!");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();

        if (generatorManager.isGenerator(location)) {
            if (block.getType() == Material.END_STONE) {
                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                boolean isUsingGoldenPickaxe = itemInHand.getType() == Material.GOLDEN_PICKAXE;

                if (isUsingGoldenPickaxe || player.isSneaking()) {
                    generatorManager.removeGenerator(location);
                    event.setDropItems(false);
                    block.getWorld().dropItemNaturally(location, generatorManager.getGeneratorItem());
                    player.sendMessage("§aPomyślnie zniszczono generator!");
                } else {
                    event.setCancelled(true);
                    player.sendMessage("§cMusisz użyć złotego kilofa lub kucać (shift), aby zniszczyć ten generator!");
                }
            }
        }
    }
}