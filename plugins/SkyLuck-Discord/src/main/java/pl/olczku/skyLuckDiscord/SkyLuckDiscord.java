package pl.olczku.skyLuckDiscord;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class SkyLuckDiscord extends Plugin {

    private String apiUrl;
    private String apiSecret;

    @Override
    public void onEnable() {
        if (!loadConfig()) {
            getLogger().severe("Nie można załadować konfiguracji! Plugin zostaje wyłączony.");
            return;
        }

        // Sprawdzanie poprawności konfiguracji
        this.apiUrl = getConfig().getString("api-settings.api-url");
        this.apiSecret = getConfig().getString("api-settings.api-secret");
        int interval = getConfig().getInt("api-settings.update-interval-seconds", 30);

        if (apiUrl == null || apiSecret == null || interval <= 0) {
            getLogger().severe("Błąd konfiguracji: Ustawienia API są niepoprawne lub puste! Sprawdź config.yml.");
            return;
        }

        getLogger().info("SkyLuckDiscord został włączony. Wysyłanie statusu co " + interval + "s do " + apiUrl);

        // Uruchomienie zadania cyklicznego w osobnym wątku
        ProxyServer.getInstance().getScheduler().schedule(this, this::sendPlayerStats, 0L, interval, TimeUnit.SECONDS);
    }

    // Metoda ładowania config.yml
    private boolean loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                if (in != null) {
                    Files.copy(in, file.toPath());
                } else {
                    // Tworzenie minimalnej konfiguracji, jeśli domyślna nie jest w pliku JAR
                    Configuration config = new Configuration();
                    config.set("api-settings.api-url", "http://TWOJ_IP_BOTA:3000/update_status");
                    config.set("api-settings.api-secret", "TwojBardzoTajnyKlucz2025");
                    config.set("api-settings.update-interval-seconds", 30);
                    ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void sendPlayerStats() {
        // Używamy getOnlineCount, który liczy graczy we wszystkich serwerach podpiętych do Bungee/Velocity
        int onlinePlayers = ProxyServer.getInstance().getOnlineCount();
        int maxPlayers = ProxyServer.getInstance().getConfig().getPlayerLimit();
        if (maxPlayers <= 0) maxPlayers = 1000; // Domyślna wartość, jeśli limit jest nieokreślony

        // Przygotowanie danych JSON
        String jsonInputString = String.format("{\"online\": %d, \"max\": %d}", onlinePlayers, maxPlayers);

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Konfiguracja połączenia
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-API-Secret", apiSecret); // Klucz zabezpieczający
            connection.setConnectTimeout(5000); // 5 sekund timeout
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);

            // Wysłanie danych
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Odbiór odpowiedzi (sprawdzenie, czy bot odpowiedział pomyślnie)
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                getLogger().warning("Błąd wysyłania statystyk. Kod odpowiedzi: " + responseCode);
            }

            connection.disconnect();

        } catch (Exception e) {
            getLogger().severe("Błąd połączenia z botem Discord: " + e.getMessage());
        }
    }
}