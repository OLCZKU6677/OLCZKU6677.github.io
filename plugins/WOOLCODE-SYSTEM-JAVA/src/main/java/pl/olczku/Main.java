package pl.olczku;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
    public static void main(String[] args) {
        try {
            String token = "MTM0MTg2ODE5NzQwNTY1OTI2Ng.G5y0u1.KbpEytXAaPA2wcHfb1Hb-qF24G1mPK1NDv6vW0";

            JDABuilder jda = JDABuilder.createDefault(token);

            jda.setActivity(Activity.playing("Wbij na dc.woolcode.pl"));

            OptionData option = new OptionData(OptionType.STRING, "dostep", "Czy dostaje dostęp?", true)
                    .addChoice("dostęp", "dostęp")
                    .addChoice("brak dostępu", "brak dostępu");
            OptionData optionEmbed = new OptionData(OptionType.STRING, "embed", "Rodzaj embeda", true)
                    .addChoice("rekrutacja", "Rekrutacja")
                    .addChoice("wymagania", "Wymagania")
                    .addChoice("rules", "Regulamin")
                    .addChoice("boost", "Boost");

            jda.addEventListeners(new GiveAcces());
            jda.addEventListeners(new Verification());
            jda.addEventListeners(new Giveaway());
            jda.addEventListeners(new SelfRole());
            jda.addEventListeners(new ClearCommand());
            jda.addEventListeners(new ClearCommand.Embeds());
            jda.addEventListeners(new Opinions());

            jda.enableIntents(GatewayIntent.MESSAGE_CONTENT);

            jda.build().updateCommands().addCommands(
                    Commands.slash("drop", "Daje dostęp do kanału"),
                    Commands.slash("verifypanel", "Wysyła panel z weryfikacją"),
                    Commands.slash("weryfikacja-ss", "Weryfikacja screen-shotów")
                            .addOptions(option)
                            .addOption(OptionType.USER, "uzytkownik", "Osoba która prosi o dostęp", true)
                            .addOption(OptionType.STRING, "komentarz", "Komentarz przy braku dostępu", false),
                    Commands.slash("giveaway", "Rozpoczyna giveaway")
                            .addOption(OptionType.STRING, "prize", "Nagroda dla zwycięzcy", true)
                            .addOption(OptionType.INTEGER, "winners", "Liczba zwycięzców", true)
                            .addOption(OptionType.INTEGER, "time", "Czas trwania giveaway w godzinach", true),
                    Commands.slash("selfrole", "Wysyła panel z rolami do wyboru"),
                    Commands.slash("clear", "Czyści podaną liczbę wiadomości")
                            .addOption(OptionType.STRING, "amount", "Ilość wiadomości do usunięcia", true),
                    Commands.slash("embed", "Wysyła embeda")
                            .addOptions(optionEmbed),
                    Commands.slash("ticket-setup", "Ustawia kanał do ticketa"),
                    Commands.slash("ticketclose", "Zamyka ticket"), // Dodana komenda do zamykania ticketa
                    Commands.slash("dodaj", "Dodaje użytkownika do ticketa") // Dodana komenda do dodawania użytkownika
                            .addOption(OptionType.USER, "user", "Użytkownik do dodania", true)
            ).queue();

            System.out.println("Bot działa");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Wystąpił błąd przy uruchamianiu bota.");
        }
    }
}
