package pl.olczku.skyluckCore.Przekierowanie;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pl.olczku.skyluckCore.artefakty.ArtefaktManager;
import pl.olczku.skyluckCore.artefakty.ArtefaktCommand;
import pl.olczku.skyluckCore.artefakty.ArtefaktGUIListener;
import pl.olczku.skyluckCore.blokowanie.OffhandXpBlocker;
import pl.olczku.skyluckCore.clearitems.ItemClear;
import pl.olczku.skyluckCore.clearitems.ItemClearCommand;
import pl.olczku.skyluckCore.cooldown.SkyluckCooldown;
import pl.olczku.skyluckCore.drop.DropCommand;
import pl.olczku.skyluckCore.drop.ResetChestsCommand;
import pl.olczku.skyluckCore.fly.FlyCommand;
import pl.olczku.skyluckCore.gamma.GammaCommand;
import pl.olczku.skyluckCore.gamma.GammaListener;
import pl.olczku.skyluckCore.generator.GeneratorCommand;
import pl.olczku.skyluckCore.generator.GeneratorListener;
import pl.olczku.skyluckCore.generator.GeneratorManager;
import pl.olczku.skyluckCore.skrzynkapoziom.SkrzynkaCommand;
import pl.olczku.skyluckCore.skrzynkapoziom.SkrzynkaListener;
import pl.olczku.skyluckCore.skrzynkapoziom.WymianaCommand;
import pl.olczku.skyluckCore.skrzynkapoziom.WymianaListener;
import pl.olczku.skyluckCore.stattrak.StatTrakCommand;
import pl.olczku.skyluckCore.stattrak.StatTrakListener;
import pl.olczku.skyluckCore.stattrak.StatTrakManager;
import pl.olczku.skyluckCore.util.Messages;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class OlczkuDannyPrzekKom extends JavaPlugin {
    private CommandMap commandMap;

    private Messages messages;
    private String allowedWorld;
    private File dataFile;
    private FileConfiguration dataCfg;
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    private File skrzynkiFile;
    private FileConfiguration skrzynkiConfig;

    private File generatoryFile;
    private FileConfiguration generatoryConfig;

    private File achievementsFile;
    private FileConfiguration achievementsConfig;

    private File achievementsDataFile;
    private FileConfiguration achievementsDataConfig;

    private GeneratorManager generatorManager;
    private StatTrakManager statTrakManager;
    private ArtefaktManager artefaktManager;
    private ItemClear itemClear;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.messages = new Messages(getConfig());
        this.allowedWorld = getConfig().getString("allowed_world", "world");
        loadData();
        loadSkrzynkiConfig();
        loadGeneratoryConfig();
        loadAchievementsConfig();
        loadAchievementsDataConfig();

        this.generatorManager = new GeneratorManager(this);
        this.statTrakManager = new StatTrakManager(this);
        this.artefaktManager = new ArtefaktManager(this);
        this.itemClear = new ItemClear(this);

        // Rejestracja eventów
        Bukkit.getPluginManager().registerEvents(new OffhandXpBlocker(this), this);
        Bukkit.getPluginManager().registerEvents(new SkyluckCooldown(this), this);
        Bukkit.getPluginManager().registerEvents(new SkrzynkaListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ShutdownListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GeneratorListener(generatorManager), this);
        Bukkit.getPluginManager().registerEvents(new StatTrakListener(statTrakManager), this);
        Bukkit.getPluginManager().registerEvents(new ArtefaktGUIListener(artefaktManager, this), this);
        Bukkit.getPluginManager().registerEvents(new GammaListener(new GammaCommand(messages)), this);

        WymianaListener wymianaListener = new WymianaListener();
        Bukkit.getPluginManager().registerEvents(wymianaListener, this);

        // Rejestracja komend
        if (getCommand("core") != null) getCommand("core").setExecutor(new AdminCommand(this));
        if (getCommand("drop") != null) getCommand("drop").setExecutor(new DropCommand(this));
        if (getCommand("resetskrzynki") != null) getCommand("resetskrzynki").setExecutor(new ResetChestsCommand(this));
        if (getCommand("skrzynka") != null) getCommand("skrzynka").setExecutor(new SkrzynkaCommand(this));
        if (getCommand("wymiana") != null) getCommand("wymiana").setExecutor(new WymianaCommand(this, wymianaListener));
        if (getCommand("generator") != null) getCommand("generator").setExecutor(new GeneratorCommand(this, generatorManager));
        if (getCommand("stattrak") != null) getCommand("stattrak").setExecutor(new StatTrakCommand(this, statTrakManager));
        if (getCommand("artefakty") != null) getCommand("artefakty").setExecutor(new ArtefaktCommand(artefaktManager, messages));
        if (getCommand("clearitems") != null) getCommand("clearitems").setExecutor(new ItemClearCommand(itemClear, messages));
        if (getCommand("fly") != null) getCommand("fly").setExecutor(new FlyCommand(this, messages));
        GammaCommand gammaCommand = new GammaCommand(messages);
        if (getCommand("gamma") != null) getCommand("gamma").setExecutor(gammaCommand);
        Bukkit.getPluginManager().registerEvents(new GammaListener(gammaCommand), this);

        // Pobranie CommandMap
        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            this.commandMap = (CommandMap) f.get(Bukkit.getServer());
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Brak dostepu do CommandMap!", e);
        }
        registerCommandRedirects();
    }

    @Override
    public void onDisable() {
        saveData();
        artefaktManager.zapiszKonfiguracje();
        saveAchievementsDataConfig();
        getLogger().info("Dane zostały zapisane.");
    }

    public void reloadAndRegister() {
        reloadConfig();
        this.messages = new Messages(getConfig());
        this.allowedWorld = getConfig().getString("allowed_world", "world");
        registerCommandRedirects();
        saveData();
    }

    private void registerCommandRedirects() {
        if (commandMap == null) return;
        ConfigurationSection sec = getConfig().getConfigurationSection("przekierowania");
        if (sec == null) return;
        for (String alias : sec.getKeys(false)) {
            String target = sec.getString(alias);
            if (target == null || target.isEmpty()) continue;
            if (commandMap.getCommand(alias) != null && !(commandMap.getCommand(alias) instanceof RedirectCommand)) continue;
            commandMap.register(getDescription().getName(), new RedirectCommand(alias, target));
        }
    }

    private void loadData() {
        try {
            if (!getDataFolder().exists()) getDataFolder().mkdirs();
            dataFile = new File(getDataFolder(), "data.yml");
            if (!dataFile.exists()) dataFile.createNewFile();
            dataCfg = YamlConfiguration.loadConfiguration(dataFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Nie moge utworzyc data.yml", e);
        }
        cooldowns.clear();
        ConfigurationSection root = dataCfg.getConfigurationSection("cooldowns");
        if (root != null) {
            for (String uuidStr : root.getKeys(false)) {
                try {
                    UUID u = UUID.fromString(uuidStr);
                    ConfigurationSection sec = root.getConfigurationSection(uuidStr);
                    Map<String, Long> map = new HashMap<>();
                    if (sec != null) for (String chestKey : sec.getKeys(false)) {
                        long ts = sec.getLong(chestKey, 0L);
                        if (ts > 0) map.put(chestKey, ts);
                    }
                    cooldowns.put(u, map);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void saveData() {
        if (dataCfg == null) {
            dataCfg = new YamlConfiguration();
            dataFile = new File(getDataFolder(), "data.yml");
        }
        dataCfg.set("cooldowns", null);
        for (Map.Entry<UUID, Map<String, Long>> e : cooldowns.entrySet()) {
            String base = "cooldowns." + e.getKey();
            for (Map.Entry<String, Long> c : e.getValue().entrySet()) dataCfg.set(base + "." + c.getKey(), c.getValue());
        }
        try {
            dataCfg.save(dataFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Nie moge zapisac data.yml", ex);
        }
    }

    public static String chestKey(Location loc) {
        return loc.getWorld().getName()+":"+loc.getBlockX()+":"+loc.getBlockY()+":"+loc.getBlockZ();
    }

    public String addDropToChest(Location chestLoc, ItemStack item, double baseChance) {
        String key = "drops." + chestKey(chestLoc);
        FileConfiguration cfg = getConfig();
        ConfigurationSection chestSec = cfg.getConfigurationSection(key);
        if (chestSec == null) chestSec = cfg.createSection(key);
        int next = 1;
        for (String child : chestSec.getKeys(false)) {
            try { next = Math.max(next, Integer.parseInt(child) + 1); } catch (NumberFormatException ignored) {}
        }
        String id = String.valueOf(next);
        ItemStack copy = item.clone();
        ConfigurationSection entry = chestSec.createSection(id);
        entry.set("item", copy);
        entry.set("chance", Math.max(0.0, Math.min(100.0, baseChance)));
        entry.set("material", copy.getType().name());
        entry.set("amount", copy.getAmount());
        saveConfig();
        return id;
    }

    public static Material safeMaterial(String name) {
        try { return Material.valueOf(name); } catch (Exception e) { return null; }
    }

    public void loadSkrzynkiConfig() {
        skrzynkiFile = new File(getDataFolder(), "skrzynki.yml");
        if (!skrzynkiFile.exists()) {
            saveResource("skrzynki.yml", false);
        }
        skrzynkiConfig = YamlConfiguration.loadConfiguration(skrzynkiFile);
    }

    public void reloadSkrzynkiConfig() {
        if (skrzynkiFile == null) {
            skrzynkiFile = new File(getDataFolder(), "skrzynki.yml");
        }
        skrzynkiConfig = YamlConfiguration.loadConfiguration(skrzynkiFile);
    }

    public void saveSkrzynkiConfig() {
        if (skrzynkiConfig == null || skrzynkiFile == null) {
            return;
        }
        try {
            getSkrzynkiConfig().save(skrzynkiFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Nie udało się zapisać pliku skrzynki.yml", e);
        }
    }

    public void loadGeneratoryConfig() {
        generatoryFile = new File(getDataFolder(), "generatory.yml");
        if (!generatoryFile.exists()) {
            saveResource("generatory.yml", false);
        }
        generatoryConfig = YamlConfiguration.loadConfiguration(generatoryFile);
    }

    public void reloadGeneratoryConfig() {
        if (generatoryFile == null) {
            generatoryFile = new File(getDataFolder(), "generatory.yml");
        }
        generatoryConfig = YamlConfiguration.loadConfiguration(generatoryFile);
    }

    public void loadAchievementsConfig() {
        achievementsFile = new File(getDataFolder(), "achievements.yml");
        if (!achievementsFile.exists()) {
            saveResource("achievements.yml", false);
        }
        achievementsConfig = YamlConfiguration.loadConfiguration(achievementsFile);
    }

    public void loadAchievementsDataConfig() {
        achievementsDataFile = new File(getDataFolder(), "achievements_data.yml");
        if (!achievementsDataFile.exists()) {
            try {
                achievementsDataFile.createNewFile();
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Nie udało się utworzyć pliku achievements_data.yml", e);
            }
        }
        achievementsDataConfig = YamlConfiguration.loadConfiguration(achievementsDataFile);
    }

    public void saveAchievementsDataConfig() {
        if (achievementsDataConfig == null || achievementsDataFile == null) {
            return;
        }
        try {
            achievementsDataConfig.save(achievementsDataFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Nie udało się zapisać pliku achievements_data.yml", e);
        }
    }

    public FileConfiguration getSkrzynkiConfig() {
        return skrzynkiConfig;
    }

    public FileConfiguration getGeneratoryConfig() {
        return generatoryConfig;
    }

    public FileConfiguration getAchievementsConfig() {
        return achievementsConfig;
    }

    public FileConfiguration getAchievementsDataConfig() {
        return achievementsDataConfig;
    }

    public FileConfiguration getArtefaktyConfig() {
        return artefaktManager.getArtefaktyConfig();
    }

    public Messages messages() { return messages; }
    public String allowedWorld() { return allowedWorld; }
    public Map<UUID, Map<String, Long>> cooldowns() { return cooldowns; }
    public StatTrakManager getStatTrakManager() { return statTrakManager; }
    public ArtefaktManager getArtefaktManager() { return artefaktManager; }
    public ItemClear getItemClear() { return itemClear; }
}