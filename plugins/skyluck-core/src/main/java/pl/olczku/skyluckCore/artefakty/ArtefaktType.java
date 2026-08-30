package pl.olczku.skyluckCore.artefakty;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;

public enum ArtefaktType {
    SZYBKOSCI("szybkosci", Material.FEATHER),
    GORNIKA("gornika", Material.DIAMOND_PICKAXE),
    WOJOWNIKA("wojownika", Material.IRON_SWORD),
    RYBAKA("rybaka", Material.FISHING_ROD),
    OBRONCY("obroncy", Material.SHIELD),
    SZCZESCIARZA("szczesciarza", Material.RABBIT_FOOT);

    private final String configKey;
    private final Material defaultMaterial;

    ArtefaktType(String configKey, Material defaultMaterial) {
        this.configKey = configKey;
        this.defaultMaterial = defaultMaterial;
    }

    public String getConfigKey() {
        return configKey;
    }

    public Material getDefaultMaterial() {
        return defaultMaterial;
    }

    public String getDisplayName(OlczkuDannyPrzekKom plugin) {
        return plugin.getArtefaktyConfig().getString("artefakty." + configKey + ".nazwa",
                getDefaultDisplayName());
    }

    public String getDescription(OlczkuDannyPrzekKom plugin) {
        String opis = plugin.getArtefaktyConfig().getString("artefakty." + configKey + ".opis");
        if (opis != null) {
            return opis.replace("{value}", String.valueOf(getEffectValue(plugin)));
        }
        return getDefaultDescription().replace("{value}", String.valueOf(getEffectValue(plugin)));
    }

    public double getEffectValue(OlczkuDannyPrzekKom plugin) {
        return plugin.getArtefaktyConfig().getDouble("artefakty." + configKey + ".efekt",
                getDefaultEffectValue());
    }

    public Material getMaterial(OlczkuDannyPrzekKom plugin) {
        String materialName = plugin.getArtefaktyConfig().getString("artefakty." + configKey + ".material");
        if (materialName != null) {
            try {
                return Material.valueOf(materialName.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Nieprawidłowy materiał dla artefaktu " + configKey + ": " + materialName);
            }
        }
        return defaultMaterial;
    }

    public int getGUISlot(OlczkuDannyPrzekKom plugin) {
        return plugin.getArtefaktyConfig().getInt("gui.sloty." + configKey, getDefaultSlot());
    }

    public String getWiadomoscOtrzymania(OlczkuDannyPrzekKom plugin) {
        String wiadomosc = plugin.getArtefaktyConfig().getString("artefakty." + configKey + ".wiadomosc_otrzymania");
        if (wiadomosc != null) {
            return wiadomosc;
        }
        return plugin.getArtefaktyConfig().getString("wiadomosci.otrzymanie_artefaktu", "&aOtrzymałeś artefakt: &f{artefakt}")
                .replace("{artefakt}", getDisplayName(plugin));
    }

    public String getWiadomoscWlaczenia(OlczkuDannyPrzekKom plugin) {
        return plugin.getArtefaktyConfig().getString("wiadomosci.artefakt_wlaczony", "&aArtefakt {artefakt} został włączony!")
                .replace("{artefakt}", getDisplayName(plugin));
    }

    public String getWiadomoscWylaczenia(OlczkuDannyPrzekKom plugin) {
        return plugin.getArtefaktyConfig().getString("wiadomosci.artefakt_wylaczony", "&cArtefakt {artefakt} został wyłączony!")
                .replace("{artefakt}", getDisplayName(plugin));
    }

    public ItemStack getPhysicalItem(OlczkuDannyPrzekKom plugin) {
        ItemStack item = new ItemStack(getMaterial(plugin));
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(getDisplayName(plugin));
            item.setItemMeta(meta);
        }
        return item;
    }

    private String getDefaultDisplayName() {
        switch (this) {
            case SZYBKOSCI: return "&aArtefakt Szybkości";
            case GORNIKA: return "&6Artefakt Górnika";
            case WOJOWNIKA: return "&cArtefakt Wojownika";
            case RYBAKA: return "&bArtefakt Rybaka";
            case OBRONCY: return "&eArtefakt Obrońcy";
            case SZCZESCIARZA: return "&dArtefakt Szczęściarza";
            default: return "&fArtefakt";
        }
    }

    private String getDefaultDescription() {
        switch (this) {
            case SZYBKOSCI: return "Daje +{value}% prędkości poruszania";
            case GORNIKA: return "Daje +{value}% szybkości kopania";
            case WOJOWNIKA: return "Zadaje +{value}% obrażeń";
            case RYBAKA: return "Ryby łowią się {value}% szybciej";
            case OBRONCY: return "Otrzymujesz -{value}% obrażeń";
            case SZCZESCIARZA: return "{value}% szans na podwójne przedmioty";
            default: return "Efekt artefaktu";
        }
    }

    private double getDefaultEffectValue() {
        switch (this) {
            case SZYBKOSCI: return 25.0;
            case GORNIKA: return 25.0;
            case WOJOWNIKA: return 10.0;
            case RYBAKA: return 50.0;
            case OBRONCY: return 10.0;
            case SZCZESCIARZA: return 10.0;
            default: return 0.0;
        }
    }

    private int getDefaultSlot() {
        switch (this) {
            case SZYBKOSCI: return 10;
            case GORNIKA: return 12;
            case WOJOWNIKA: return 14;
            case RYBAKA: return 16;
            case OBRONCY: return 28;
            case SZCZESCIARZA: return 30;
            default: return -1;
        }
    }
}