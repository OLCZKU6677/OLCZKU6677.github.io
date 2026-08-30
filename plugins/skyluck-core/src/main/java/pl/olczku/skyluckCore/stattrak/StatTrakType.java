package pl.olczku.skyluckCore.stattrak;

public enum StatTrakType {
    KILLS("§7Zabite potwory: §e"),
    BLOCKS_BROKEN("§7Wykopane bloki: §e"),
    LOGS_BROKEN("§7Wykopane drewno: §e"),
    DAMAGE_TAKEN("§7Przyjęte obrażenia: §e"),
    DAMAGE_BLOCKED("§7Zablokowane obrażenia: §e"),
    CROPS_HARVESTED("§7Zebrane plony: §e");

    private final String lorePrefix;

    StatTrakType(String lorePrefix) {
        this.lorePrefix = lorePrefix;
    }

    public String getLorePrefix() {
        return lorePrefix;
    }
}