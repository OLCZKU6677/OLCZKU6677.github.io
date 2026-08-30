package pl.olczku.lightgrow;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class LightGrow extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("LightGrow enabled – crops can grow in darkness!");

        // Zadanie co 5 sek (100 ticków)
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getWorlds().forEach(world -> {
                    for (Chunk chunk : world.getLoadedChunks()) {
                        int minY = world.getMinHeight();
                        int maxY = world.getMaxHeight();

                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                for (int y = minY; y < maxY; y++) {
                                    Block block = chunk.getBlock(x, y, z);

                                    if (isCrop(block.getType()) && block.getBlockData() instanceof Ageable ageable) {
                                        if (ageable.getAge() < ageable.getMaximumAge()) {
                                            // 20% szansy na wzrost (jak random tick)
                                            if (Math.random() < 0.2) {
                                                ageable.setAge(ageable.getAge() + 1);
                                                block.setBlockData(ageable, true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }.runTaskTimer(this, 100L, 100L); // start po 5 sek, potem co 5 sek
    }

    @Override
    public void onDisable() {
        getLogger().info("LightGrow disabled.");
    }

    private boolean isCrop(Material type) {
        return type == Material.WHEAT
                || type == Material.CARROTS
                || type == Material.POTATOES
                || type == Material.BEETROOTS
                || type == Material.NETHER_WART;
    }
}
