package pl.olczku;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Main {
    public static void main(String[] args) {
        try {
            // Pamiętaj, aby token był bezpieczny i nie był publicznie dostępny.
            String token = "OTk5OTk5OTk5OTk5OTk5OTk5.XXXXXX.XXXXXXXXXXXXXXXXXXXXXXXXXXX"; // Wstaw tutaj swój prawdziwy token

            JDABuilder jdaBuilder = JDABuilder.createDefault(token);

            jdaBuilder.setActivity(Activity.playing("Wbij na dc.firenmc.pl"));

            jdaBuilder.enableIntents(
                    GatewayIntent.MESSAGE_CONTENT,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_PRESENCES, // Włączone dla lepszego działania Powitania
                    GatewayIntent.GUILD_VOICE_STATES // Potrzebne, jeśli bot ma funkcje głosowe
            );

            jdaBuilder.enableCache(CacheFlag.MEMBER_OVERRIDES);
            jdaBuilder.enableCache(CacheFlag.VOICE_STATE); // Włączone dla funkcji głosowych

            // Dodaj listenery
            jdaBuilder.addEventListeners(new Verification());
            jdaBuilder.addEventListeners(new TicketCommand());
            jdaBuilder.addEventListeners(new RekrutacjaCommand());
            jdaBuilder.addEventListeners(new ModerationCommands());
            jdaBuilder.addEventListeners(new Powitania());

            // Zbuduj instancję JDA
            JDA jdaInstance = jdaBuilder.build();

            // Poczekaj, aż JDA będzie gotowe
            jdaInstance.awaitReady();

            // Zarejestruj komendy slash
            registerSlashCommands(jdaInstance);

            System.out.println("Bot działa");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Wystąpił błąd przy uruchamianiu bota.");
        }
    }

    private static void registerSlashCommands(JDA jda) {
        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(
                Commands.slash("verify-setup", "Ustawia system weryfikacji"),
                Commands.slash("close", "Zamyka ticket"),
                Commands.slash("closestop", "Anuluje zamknięcie ticketa"),
                Commands.slash("ticket-setup", "Ustawia system ticketów"),
                Commands.slash("dodaj", "Dodaje użytkownika do ticketa")
                        .addOption(OptionType.USER, "user", "Użytkownik do dodania", true),
                Commands.slash("rekru-setup", "Ustawia system rekrutacji"),
                Commands.slash("pomoceadm", "Wyświetla pomoc dla administratorów"),
                Commands.slash("ban", "Banuje użytkownika")
                        .addOption(OptionType.USER, "user", "Użytkownik do zbanowania", true)
                        .addOption(OptionType.STRING, "reason", "Powód bana", false),
                Commands.slash("tempban", "Tymczasowo banuje użytkownika")
                        .addOption(OptionType.USER, "user", "Użytkownik do zbanowania", true)
                        .addOption(OptionType.STRING, "duration", "Czas trwania (np. 1d, 12h, 30m)", true)
                        .addOption(OptionType.STRING, "reason", "Powód bana", false),
                Commands.slash("mute", "Wycisza użytkownika")
                        .addOption(OptionType.USER, "user", "Użytkownik do wyciszenia", true)
                        .addOption(OptionType.STRING, "reason", "Powód wyciszenia", false),
                Commands.slash("tempmute", "Tymczasowo wycisza użytkownika")
                        .addOption(OptionType.USER, "user", "Użytkownik do wyciszenia", true)
                        .addOption(OptionType.STRING, "duration", "Czas trwania (np. 1d, 12h, 30m)", true)
                        .addOption(OptionType.STRING, "reason", "Powód wyciszenia", false),
                Commands.slash("unmute", "Odcisza użytkownika")
                        .addOption(OptionType.USER, "user", "Użytkownik do odciszenia", true),
                Commands.slash("warn", "Ostrzega użytkownika")
                        .addOption(OptionType.USER, "user", "Użytkownik do ostrzeżenia", true)
                        .addOption(OptionType.STRING, "reason", "Powód ostrzeżenia", true),
                Commands.slash("unwarn", "Usuwa ostrzeżenie użytkownikowi")
                        .addOption(OptionType.USER, "user", "Użytkownik", true)
                        .addOption(OptionType.STRING, "warn_id", "ID ostrzeżenia do usunięcia", true),
                Commands.slash("clear", "Usuwa wiadomości")
                        .addOption(OptionType.INTEGER, "amount", "Liczba wiadomości do usunięcia (1-100)", true),
                Commands.slash("repadd", "Dodaje punkty reputacji użytkownikowi")
                        .addOption(OptionType.USER, "user", "Użytkownik", true)
                        .addOption(OptionType.INTEGER, "points", "Liczba punktów do dodania", true),
                Commands.slash("repremove", "Usuwa punkty reputacji użytkownikowi")
                        .addOption(OptionType.USER, "user", "Użytkownik", true)
                        .addOption(OptionType.INTEGER, "points", "Liczba punktów do usunięcia", true),
                Commands.slash("ping", "Sprawdza opóźnienie bota")
        ).queue();

        System.out.println("Wszystkie komendy slash zostały zarejestrowane.");
    }
}