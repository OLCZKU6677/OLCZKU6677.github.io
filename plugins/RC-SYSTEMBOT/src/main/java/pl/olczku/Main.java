package pl.olczku;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        try {
            Properties config = loadConfig();
            String token = config.getProperty("discord.token");
            String activity = config.getProperty("bot.activity", "Wbij na dc.realmcode.pl");

            if (token == null || token.isBlank()) {
                throw new IllegalStateException("Token Discord nie może być pusty! Sprawdź plik config.properties");
            }

            // Inicjalizacja systemów
            // UWAGA: Zakładamy, że klasa LegitCheckSystem i Logs istnieją
            LegitCheckSystem legitCheckSystem = new LegitCheckSystem(config);
            DropCommand dropCommand = new DropCommand();
            GiveAccess giveAccessCommand = new GiveAccess(); // <-- Inicjalizacja GiveAccess

            // Inicjalizacja i konfiguracja bota
            JDA jda = JDABuilder.createDefault(token)
                    .setActivity(Activity.playing(activity))
                    .enableIntents(
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_MESSAGES
                    )
                    .enableCache(CacheFlag.ONLINE_STATUS)
                    .addEventListeners(
                            new TicketCommand(),
                            new Logs(config),
                            legitCheckSystem,
                            dropCommand,
                            giveAccessCommand // <-- Dodanie GiveAccess do słuchaczy
                    )
                    .build().awaitReady();

            // Uruchomienie logiki powiadomień po pełnym starcie JDA


            // Konfiguracja opcji komend dla Ticketów
            OptionData userOption = new OptionData(OptionType.USER, "uzytkownik", "Użytkownik do dodania do ticketa.", true);

            // Rejestracja komend
            jda.updateCommands()
                    .addCommands(
                            // Komendy Ticketów
                            Commands.slash("ticket-setup", "Konfiguruje system ticketów RealmCode."),
                            Commands.slash("ticketclose", "Zamyka ticket."),
                            Commands.slash("ticketadd", "Dodaje użytkownika do ticketa.")
                                    .addOptions(userOption),

                            // Komendy Dropów
                            DropCommand.getCommandData(), // /drop
                            DropCommand.getDropPremiumCommandData(), // /drop-premium
                            DropCommand.getDropInfoCommandData(), // /dropinfo

                            // Komenda Weryfikacji
                            GiveAccess.getCommandData() // /weryfikacja-ss
                    )
                    .queue(
                            success -> System.out.println("✅ Komendy zarejestrowane pomyślnie"),
                            error -> System.err.println("❌ Błąd rejestracji komend: " + error)
                    );

            System.out.println("🤖 Bot uruchomiony pomyślnie!");

        } catch (Exception e) {
            System.err.println("‼️ Krytyczny błąd podczas uruchamiania bota:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static Properties loadConfig() throws IOException {
        Properties prop = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("Nie znaleziono pliku config.properties w resources!");
            }
            prop.load(input);
        }
        return prop;
    }
}
