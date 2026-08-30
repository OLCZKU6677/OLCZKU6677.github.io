package com.olczku.metrics;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.UUID;

/**
 * Lekki, asynchroniczny moduł telemetryczny do raportowania aktywności pluginu
 * na Twojej stronie portfolio (liczniki serwerów i graczy w czasie rzeczywistym).
 *
 * Użycie w onEnable():
 *   PluginMetrics.start(this, "http://twoja-domena.pl/api/heartbeat.php");
 */
public class PluginMetrics {

    private static final long INTERVAL_TICKS = 20L * 60 * 10; // co 10 minut (12000 ticków)

    public static void start(JavaPlugin plugin, String endpointUrl) {
        String serverId = getOrCreateServerId(plugin);

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try {
                int onlinePlayers = Bukkit.getOnlinePlayers().size();
                String serverVersion = Bukkit.getVersion();
                String pluginVersion = plugin.getDescription().getVersion();
                String pluginName = plugin.getDescription().getName();

                sendHeartbeat(endpointUrl, pluginName, serverId, onlinePlayers, serverVersion, pluginVersion);
            } catch (Exception ignored) {
                // Ciche ignorowanie błędów, aby nigdy nie zakłócać działania serwera
            }
        }, 20L * 15, INTERVAL_TICKS); // pierwszy ping po 15 sekundach od startu, potem co 10 minut
    }

    private static void sendHeartbeat(String endpoint, String plugin, String serverId, int players, String srvVer, String plVer) {
        try {
            URL url = URI.create(endpoint).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("User-Agent", "MinecraftPlugin-Metrics/" + plugin);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);

            String json = String.format(
                "{\"plugin\":\"%s\",\"server_id\":\"%s\",\"players\":%d,\"server_version\":\"%s\",\"plugin_version\":\"%s\"}",
                escapeJson(plugin),
                escapeJson(serverId),
                players,
                escapeJson(srvVer),
                escapeJson(plVer)
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            conn.disconnect();
        } catch (Exception ignored) {
            // Ignorujemy błędy połączenia (np. brak internetu na maszynie)
        }
    }

    private static String getOrCreateServerId(JavaPlugin plugin) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File idFile = new File(dataFolder, ".server_id");
        Properties props = new Properties();

        if (idFile.exists()) {
            try (FileReader reader = new FileReader(idFile)) {
                props.load(reader);
                String id = props.getProperty("server_id");
                if (id != null && !id.trim().isEmpty()) {
                    return id.trim();
                }
            } catch (Exception ignored) {}
        }

        String newId = UUID.randomUUID().toString().replace("-", "");
        props.setProperty("server_id", newId);
        try (FileWriter writer = new FileWriter(idFile)) {
            props.store(writer, "Anonymous Server ID for portfolio metrics");
        } catch (Exception ignored) {}

        return newId;
    }

    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
