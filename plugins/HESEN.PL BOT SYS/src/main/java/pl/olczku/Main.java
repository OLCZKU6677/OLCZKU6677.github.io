package pl.olczku;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    public static void main(String[] args) {
        // WKLEJ TUTAJ SWÓJ NOWY, ZRESETOWANY TOKEN!
        String token = "MTM4NTY0OTI0MjM0MDg1NTg3OA.Gv5qVl.OHDnCJi3cvMqsNwAppCEQ00A9-5H_V779FO-1s";

        if (token == null || token.equals("WKLEJ_SWÓJ_NOWY_ZRESETOWANY_TOKEN_TUTAJ") || token.isEmpty()) {
            System.err.println("!!! BŁĄD: Token bota nie został ustawiony w kodzie !!!");
            System.err.println("Otwórz plik Main.java i wklej swój token w wyznaczone miejsce.");
            return;
        }

        try {
            JDABuilder builder = JDABuilder.createDefault(token);

            builder.setActivity(Activity.watching("Tickety"));
            builder.enableIntents(GatewayIntent.GUILD_MEMBERS);

            // Zostawiamy TYLKO TicketCommand, aby uniknąć konfliktów.
            // Jeśli chcesz, aby działał system weryfikacji, musisz wyłączyć system ticketów.
            // Oba na raz powodują błędy.
            builder.addEventListeners(new TicketCommand()/*, new Verification()*/);

            JDA jda = builder.build().awaitReady();

            jda.updateCommands().addCommands(
                    Commands.slash("ticket-setup", "Konfiguruje wiadomość do tworzenia ticketów."),
                    Commands.slash("close", "Zamyka bieżący kanał ticketa."),
                    Commands.slash("closestop", "Anuluje proces zamykania ticketa."),
                    Commands.slash("dodaj", "Dodaje użytkownika do bieżącego ticketa.")
                            .addOption(OptionType.USER, "user", "Użytkownik, którego chcesz dodać", true),
                    Commands.slash("verify-setup", "Ustawia wiadomość weryfikacyjną.")
            ).queue();

            System.out.println("Bot jest online, a komendy zostały zarejestrowane!");

        } catch (InterruptedException e) {
            System.err.println("Proces budowania bota został przerwany.");
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Wystąpił nieoczekiwany błąd podczas uruchamiania bota.");
            e.printStackTrace();
        }
    }
}