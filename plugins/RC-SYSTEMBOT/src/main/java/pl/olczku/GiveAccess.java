package pl.olczku;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.util.Objects;

public class GiveAccess extends ListenerAdapter {

    // ID roli, która może używać tej komendy (Zaktualizowane na 1407044803946090508)
    private static final String REQUIRED_VERIFIER_ROLE_ID = "1407044803946090508";
    // ID roli, która ma być nadana po pomyślnej weryfikacji (Rola Dostępu)
    private static final String ACCESS_ROLE_ID = "1342589406992924722";

    /**
     * Statyczna metoda do konfiguracji komendy Slash dla Main.java
     */
    public static CommandData getCommandData() {
        // Opcja wyboru dostępu
        OptionData dostepOption = new OptionData(OptionType.STRING, "dostep", "Wybierz, czy użytkownik otrzymuje dostęp, czy nie.", true)
                .addChoice("Dostęp", "dostęp")
                .addChoice("Brak Dostępu", "brak dostępu");

        // Opcja wyboru użytkownika
        OptionData userOption = new OptionData(OptionType.USER, "uzytkownik", "Użytkownik, którego weryfikujesz.", true);

        // Opcja komentarza (opcjonalna, tylko dla 'brak dostępu')
        OptionData commentOption = new OptionData(OptionType.STRING, "komentarz", "Komentarz w przypadku braku dostępu.", false);

        return Commands.slash("weryfikacja-ss", "Zarządza dostępem po weryfikacji screenów.")
                .addOptions(dostepOption, userOption, commentOption);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!e.getName().equals("weryfikacja-ss")) {
            return;
        }

        Member member = e.getMember();

        // 1. Walidacja uprawnień (sprawdzenie wymaganej roli)
        if (member == null || !member.getRoles().stream().anyMatch(role -> role.getId().equals(REQUIRED_VERIFIER_ROLE_ID))) {
            e.reply("❌ Nie masz uprawnień do używania tej komendy. Wymagana rola: <@&" + REQUIRED_VERIFIER_ROLE_ID + ">").setEphemeral(true).queue();
            return;
        }

        e.deferReply(false).queue(); // Odpowiedz publicznie, bo to jest akcja administracyjna

        OptionMapping dostepOption = e.getOption("dostep");
        OptionMapping userOption = e.getOption("uzytkownik");

        if (dostepOption == null || userOption == null) {
            e.getHook().sendMessage("❌ Błąd: Musisz podać użytkownika i opcję dostępu.").setEphemeral(true).queue();
            return;
        }

        try {
            String choice = dostepOption.getAsString();
            Member targetMember = Objects.requireNonNull(e.getGuild()).retrieveMember(userOption.getAsUser()).complete();

            if (choice.equalsIgnoreCase("dostęp")) {
                // LOGIKA NADANIA DOSTĘPU
                EmbedBuilder accesEmbed = new EmbedBuilder();
                accesEmbed.setDescription("```RealmCode - Nadano dostęp```\n" +
                        targetMember.getAsMention() + " **Gratulacje**, uzyskałeś dostęp!\n" +
                        "Dziękujemy za spełnienie wymagań.\n" +
                        "\n" +
                        "Od teraz możesz korzystać z https://discord.com/channels/1280903359422922845/1305448619544940594\n" +
                        "\n" +
                        "Nadano przez: " + e.getMember().getAsMention()
                );
                accesEmbed.setColor(Color.GREEN);

                // Dodanie roli
                Role rola = e.getGuild().getRoleById(ACCESS_ROLE_ID);
                if (rola != null) {
                    e.getGuild().addRoleToMember(targetMember, rola).queue(
                            null,
                            error -> e.getHook().sendMessage("❌ Błąd dodawania roli: " + error.getMessage()).setEphemeral(true).queue()
                    );
                }

                e.getHook().editOriginalEmbeds(accesEmbed.build()).queue();

            } else if (choice.equalsIgnoreCase("brak dostępu")) {
                // LOGIKA BRAKU DOSTĘPU
                OptionMapping komentarzOption = e.getOption("komentarz");
                String komentarz = komentarzOption != null ? komentarzOption.getAsString() : "Brak komentarza.";

                EmbedBuilder noaccesEbmed = new EmbedBuilder();
                noaccesEbmed.setDescription("```RealmCode - Brak Dostępu```\n" +
                        targetMember.getAsMention() + " Niestety, **nie spełniłeś wymagań**.\n" +
                        "\n" +
                        "**Komentarz Weryfikatora:**\n" +
                        "**" + komentarz + "**\n" +
                        "\n" +
                        "Wyślij wymagania jeszcze raz, a administracja \n" +
                        "na pewno je sprawdzi jak najszybciej.\n" +
                        "\n" +
                        "Nadane przez: " + e.getMember().getAsMention()
                );
                noaccesEbmed.setColor(Color.RED);

                // Usunięcie roli dostępu, jeśli ją miał (opcjonalnie, ale bezpieczne)
                Role rola = e.getGuild().getRoleById(ACCESS_ROLE_ID);
                if (rola != null && targetMember.getRoles().contains(rola)) {
                    e.getGuild().removeRoleFromMember(targetMember, rola).queue();
                }

                e.getHook().editOriginalEmbeds(noaccesEbmed.build()).queue();

            } else {
                e.getHook().sendMessage("❌ Nieprawidłowa opcja dostępu. Wybierz 'Dostęp' lub 'Brak Dostępu'.").setEphemeral(true).queue();
            }
        } catch (Exception ex) {
            e.getHook().sendMessage("❌ Wystąpił błąd podczas przetwarzania komendy: " + ex.getMessage()).setEphemeral(true).queue();
            ex.printStackTrace();
        }
    }
}
