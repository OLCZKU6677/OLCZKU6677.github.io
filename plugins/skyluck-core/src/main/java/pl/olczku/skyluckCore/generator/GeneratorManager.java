package pl.olczku.skyluckCore.generator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;

import java.util.*;

public class GeneratorManager {

    private final OlczkuDannyPrzekKom plugin;
    private final Map<Location, Long> generators = new HashMap<>(); // Mapa przechowująca lokalizację i czas ostatniej regeneracji
    private final Random random = new Random();
    private final long intervalTicks;

    public GeneratorManager(OlczkuDannyPrzekKom plugin) {
        this.plugin = plugin;
        this.intervalTicks = plugin.getGeneratoryConfig().getLong("interval", 20L);
        startGeneratorTask();
        registerCraftingRecipe();
    }

    public void addGenerator(Location location) {
        generators.put(location, System.currentTimeMillis());
        Block block = location.getBlock();
        generateBlock(block);
    }

    public void removeGenerator(Location location) {
        generators.remove(location);
    }

    public boolean isGenerator(Location location) {
        return generators.containsKey(location);
    }

    private void startGeneratorTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            new HashMap<>(generators).forEach((location, lastRegenerationTime) -> {
                // Sprawdzamy, czy minął wymagany interwał dla tego generatora
                if (currentTime - lastRegenerationTime >= intervalTicks * 50) { // Konwersja ticków na milisekundy (1 tick = 50 ms)
                    Block currentBlock = location.getBlock();
                    if (currentBlock.getType() == Material.AIR) {
                        generateBlock(currentBlock);
                    }
                    // Aktualizujemy czas ostatniej regeneracji dla tego generatora
                    generators.put(location, currentTime);
                }
            });
        }, 0L, 1L); // Uruchamiamy zadanie co tick, aby precyzyjnie sprawdzać interwały
    }

    private void generateBlock(Block block) {
        ConfigurationSection section = plugin.getGeneratoryConfig().getConfigurationSection("blocks");
        if (section == null) {
            block.setType(Material.COBBLESTONE);
            return;
        }

        List<String> materials = new ArrayList<>(section.getKeys(false));
        double totalWeight = 0;
        for (String mat : materials) {
            totalWeight += section.getDouble(mat);
        }

        if (totalWeight <= 0) {
            block.setType(Material.COBBLESTONE);
            return;
        }

        double randomValue = random.nextDouble() * totalWeight;
        double currentWeight = 0;

        for (String mat : materials) {
            currentWeight += section.getDouble(mat);
            if (randomValue <= currentWeight) {
                try {
                    block.setType(Material.valueOf(mat.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Nieprawidłowa nazwa materiału w generatory.yml: " + mat);
                    block.setType(Material.COBBLESTONE);
                }
                return;
            }
        }
    }

    public ItemStack getGeneratorItem() {
        ItemStack generator = new ItemStack(Material.END_STONE);
        ItemMeta meta = generator.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§eGenerator Stone");
            meta.setLore(Collections.singletonList("§7Postaw na ziemi, aby aktywować."));
            generator.setItemMeta(meta);
        }
        return generator;
    }

    public void registerCraftingRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "custom_generator");

        if (Bukkit.getRecipe(key) != null) {
            Bukkit.removeRecipe(key);
        }

        ConfigurationSection craftingSection = plugin.getGeneratoryConfig().getConfigurationSection("crafting_recipe");

        if (craftingSection == null || !craftingSection.getBoolean("enabled", false)) {
            return;
        }

        List<String> shape = craftingSection.getStringList("shape");
        if (shape.isEmpty() || shape.size() > 3) {
            plugin.getLogger().warning("Nieprawidłowy kształt receptury generatora w generatory.yml. Musi mieć od 1 do 3 rzędów.");
            return;
        }

        ShapedRecipe recipe = new ShapedRecipe(key, getGeneratorItem());
        recipe.shape(shape.toArray(new String[0]));

        ConfigurationSection ingredientsSection = craftingSection.getConfigurationSection("ingredients");
        if (ingredientsSection == null) {
            plugin.getLogger().warning("Brak zdefiniowanych składników dla receptury generatora w generatory.yml.");
            return;
        }

        try {
            for (String ingredientKey : ingredientsSection.getKeys(false)) {
                if (ingredientKey.length() != 1) {
                    plugin.getLogger().warning("Nieprawidłowy klucz składnika w recepturze generatora: '" + ingredientKey + "'. Musi to być pojedynczy znak.");
                    return;
                }
                char keyChar = ingredientKey.charAt(0);
                String materialName = ingredientsSection.getString(ingredientKey);
                Material ingredientMaterial = Material.valueOf(materialName.toUpperCase());
                recipe.setIngredient(keyChar, ingredientMaterial);
            }
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Nieprawidłowy materiał składnika w recepturze generatora: " + e.getMessage());
            return;
        }

        Bukkit.addRecipe(recipe);
        plugin.getLogger().info("Pomyślnie załadowano recepturę dla generatora.");
    }
}