package pl.olczku.skyluckCore.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;
import java.util.Map;

public class Messages {
    private final FileConfiguration cfg;

    public Messages(FileConfiguration cfg) {
        this.cfg = cfg;
    }

    public String get(String key, Map<String, String> vars) {
        String raw = cfg.getString("messages." + key, "");
        String prefix = cfg.getString("messages.prefix", "");
        raw = raw.replace("{prefix}", prefix);
        if (vars != null) for (Map.Entry<String, String> e : vars.entrySet()) raw = raw.replace("{" + e.getKey() + "}", String.valueOf(e.getValue()));
        return ChatColor.translateAlternateColorCodes('&', raw);
    }

    public String get(String key) {
        return get(key, null);
    }

    public void send(CommandSender to, String key, Map<String, String> vars) {
        String msg = get(key, vars);
        if (msg != null && !msg.isEmpty()) to.sendMessage(msg);
    }

    public void send(CommandSender to, String key) {
        send(to, key, null);
    }


    public String getArtefaktOtrzymany() {
        return get("artefakty.otrzymany");
    }

    public String getArtefaktWszystkie() {
        return get("artefakty.wszystkie");
    }

    public String getArtefaktNieznany() {
        return get("artefakty.nieznany");
    }

    public String getArtefaktBrakUprawnien() {
        return get("artefakty.brak_uprawnien");
    }

    public String getArtefaktTylkoGracze() {
        return get("artefakty.tylko_gracze");
    }

    public String getArtefaktUsage() {
        return get("artefakty.usage");
    }

    public String replacePlaceholders(String text, String... placeholders) {
        if (text == null) return "";
        String result = text;
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                result = result.replace(placeholders[i], placeholders[i + 1]);
            }
        }
        return result;
    }


    public String getWithPlaceholders(String key, Map<String, String> placeholders) {
        String message = get(key);
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return message;
    }
}