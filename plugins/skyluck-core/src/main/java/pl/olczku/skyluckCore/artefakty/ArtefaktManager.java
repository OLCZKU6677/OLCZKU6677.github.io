package pl.olczku.skyluckCore.artefakty;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ArtefaktManager {
    private final OlczkuDannyPrzekKom plugin;
    private File artefaktyFile;
    private FileConfiguration artefaktyConfig;
    private final Map<UUID, Set<ArtefaktType>> graczeArtefakty = new HashMap<>();
    private final Map<UUID, Set<ArtefaktType>> wylaczoneArtefakty = new HashMap<>();

    public ArtefaktManager(OlczkuDannyPrzekKom plugin) {
        this.plugin = plugin;
        zaladujKonfiguracje();
    }

    public void zaladujKonfiguracje() {
        artefaktyFile = new File(plugin.getDataFolder(), "artefakty.yml");
        if (!artefaktyFile.exists()) {
            plugin.saveResource("artefakty.yml", false);
        }
        artefaktyConfig = YamlConfiguration.loadConfiguration(artefaktyFile);
        zaladujDaneGraczy();
        zaladujWylaczoneArtefakty();
    }

    private void zaladujDaneGraczy() {
        graczeArtefakty.clear();
        for (String uuidStr : artefaktyConfig.getKeys(false)) {
            if (uuidStr.equals("artefakty") || uuidStr.equals("gui") || uuidStr.equals("nagrody") || uuidStr.equals("wiadomosci")) {
                continue;
            }
            try {
                UUID uuid = UUID.fromString(uuidStr);
                List<String> artefakty = artefaktyConfig.getStringList(uuidStr);
                Set<ArtefaktType> typy = new HashSet<>();
                for (String art : artefakty) {
                    try {
                        typy.add(ArtefaktType.valueOf(art));
                    } catch (IllegalArgumentException ignored) {}
                }
                graczeArtefakty.put(uuid, typy);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    private void zaladujWylaczoneArtefakty() {
        wylaczoneArtefakty.clear();
        ConfigurationSection wylaczoneSec = artefaktyConfig.getConfigurationSection("wylaczone");
        if (wylaczoneSec != null) {
            for (String uuidStr : wylaczoneSec.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    List<String> artefakty = wylaczoneSec.getStringList(uuidStr);
                    Set<ArtefaktType> typy = new HashSet<>();
                    for (String art : artefakty) {
                        try {
                            typy.add(ArtefaktType.valueOf(art));
                        } catch (IllegalArgumentException ignored) {}
                    }
                    wylaczoneArtefakty.put(uuid, typy);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void dodajArtefakt(Player gracz, ArtefaktType typ) {
        UUID uuid = gracz.getUniqueId();
        Set<ArtefaktType> artefakty = graczeArtefakty.computeIfAbsent(uuid, k -> new HashSet<>());

        if (!artefakty.contains(typ)) {
            artefakty.add(typ);
            zapiszDaneGracza(uuid);
            gracz.sendMessage(ChatColor.translateAlternateColorCodes('&', typ.getWiadomoscOtrzymania(plugin)));
            sprawdzWszystkieArtefakty(gracz);
        } else {
            String wiadomosc = artefaktyConfig.getString("wiadomosci.juz_posiadasz", "&aJuż posiadasz ten artefakt!");
            gracz.sendMessage(ChatColor.translateAlternateColorCodes('&', wiadomosc));
        }
    }

    public boolean czyPosiadaArtefakt(Player gracz, ArtefaktType typ) {
        return graczeArtefakty.getOrDefault(gracz.getUniqueId(), Collections.emptySet()).contains(typ);
    }

    public boolean czyArtefaktWlaczony(Player gracz, ArtefaktType typ) {
        return !wylaczoneArtefakty.getOrDefault(gracz.getUniqueId(), Collections.emptySet()).contains(typ);
    }

    public void przelaczArtefakt(Player gracz, ArtefaktType typ) {
        if (!czyPosiadaArtefakt(gracz, typ)) {
            String wiadomosc = artefaktyConfig.getString("wiadomosci.brak_artefaktu", "&cNie posiadasz tego artefaktu!");
            gracz.sendMessage(ChatColor.translateAlternateColorCodes('&', wiadomosc));
            return;
        }

        UUID uuid = gracz.getUniqueId();
        Set<ArtefaktType> wylaczone = wylaczoneArtefakty.computeIfAbsent(uuid, k -> new HashSet<>());

        if (wylaczone.contains(typ)) {
            wylaczone.remove(typ);
            gracz.sendMessage(ChatColor.translateAlternateColorCodes('&', typ.getWiadomoscWlaczenia(plugin)));
        } else {
            wylaczone.add(typ);
            gracz.sendMessage(ChatColor.translateAlternateColorCodes('&', typ.getWiadomoscWylaczenia(plugin)));
        }
        zapiszWylaczoneArtefakty(uuid);
    }

    public void otworzGUI(Player gracz) {
        String guiTitle = artefaktyConfig.getString("gui.tytul", "§6System Artefaktów");
        if (guiTitle.length() > 32) {
            guiTitle = guiTitle.substring(0, 32);
        }

        Inventory gui = Bukkit.createInventory(null, 54, guiTitle);

        for (ArtefaktType typ : ArtefaktType.values()) {
            int slot = typ.getGUISlot(plugin);
            if (slot >= 0 && slot < 54) {
                boolean posiadany = czyPosiadaArtefakt(gracz, typ);
                boolean wlaczony = czyArtefaktWlaczony(gracz, typ);
                ItemStack item = ArtefaktItem.stworzItem(plugin, typ, posiadany, wlaczony);
                gui.setItem(slot, item);
            }
        }

        gracz.openInventory(gui);
    }

    public void dajFizycznyArtefakt(Player gracz, ArtefaktType typ) {
        if (czyPosiadaArtefakt(gracz, typ)) {
            ItemStack fizycznyArtefakt = typ.getPhysicalItem(plugin);
            gracz.getInventory().addItem(fizycznyArtefakt);
            gracz.sendMessage(ChatColor.translateAlternateColorCodes('&', typ.getWiadomoscOtrzymania(plugin)));
        } else {
            String wiadomosc = artefaktyConfig.getString("wiadomosci.brak_artefaktu", "&cNie posiadasz tego artefaktu!");
            gracz.sendMessage(ChatColor.translateAlternateColorCodes('&', wiadomosc));
        }
    }

    private void sprawdzWszystkieArtefakty(Player gracz) {
        Set<ArtefaktType> artefakty = graczeArtefakty.getOrDefault(gracz.getUniqueId(), Collections.emptySet());
        if (artefakty.size() == ArtefaktType.values().length) {
            String wiadomoscGlobalna = artefaktyConfig.getString("nagrody.wiadomosc",
                            "§6§lGratulacje! Gracz {gracz} zdobył wszystkie artefakty!")
                    .replace("{gracz}", gracz.getName());

            String wiadomoscGracz = artefaktyConfig.getString("nagrody.wiadomosc_gracz",
                    "§6§lGratulacje! Zdobyłeś wszystkie artefakty!");

            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', wiadomoscGlobalna));
            gracz.sendMessage(ChatColor.translateAlternateColorCodes('&', wiadomoscGracz));
        }
    }

    private void zapiszDaneGracza(UUID uuid) {
        Set<ArtefaktType> artefakty = graczeArtefakty.get(uuid);
        if (artefakty != null) {
            List<String> artefaktyStr = new ArrayList<>();
            for (ArtefaktType typ : artefakty) {
                artefaktyStr.add(typ.name());
            }
            artefaktyConfig.set(uuid.toString(), artefaktyStr);
        }
    }

    private void zapiszWylaczoneArtefakty(UUID uuid) {
        Set<ArtefaktType> wylaczone = wylaczoneArtefakty.get(uuid);
        if (wylaczone != null && !wylaczone.isEmpty()) {
            List<String> artefaktyStr = new ArrayList<>();
            for (ArtefaktType typ : wylaczone) {
                artefaktyStr.add(typ.name());
            }
            artefaktyConfig.set("wylaczone." + uuid.toString(), artefaktyStr);
        } else {
            artefaktyConfig.set("wylaczone." + uuid.toString(), null);
        }
    }

    public void zapiszKonfiguracje() {
        try {
            artefaktyConfig.save(artefaktyFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Nie udało się zapisać pliku artefakty.yml");
        }
    }

    public FileConfiguration getArtefaktyConfig() {
        return artefaktyConfig;
    }

    public OlczkuDannyPrzekKom getPlugin() {
        return plugin;
    }
}