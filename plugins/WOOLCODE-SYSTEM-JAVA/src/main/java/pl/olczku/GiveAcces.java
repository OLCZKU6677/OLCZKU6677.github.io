package pl.olczku;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.awt.*;

public class GiveAcces extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (e.getName().equals("weryfikacja-ss")) {
            Member member = e.getMember();
            if (member == null || !member.getRoles().stream().anyMatch(role -> role.getId().equals("1341867856958328902"))) {
                e.reply("Nie masz uprawnień do używania tej komendy.").setEphemeral(true).queue();
                return;
            }
            OptionMapping dostepOption = e.getOption("dostep");
            if (dostepOption != null) {
                String choice = dostepOption.getAsString();
                if (choice.equalsIgnoreCase("dostęp")) {
                    EmbedBuilder accesEmbed = new EmbedBuilder();
                    accesEmbed.setDescription("```WoolCode - Nadano dostęp```\n" +
                            "<@" + e.getOption("uzytkownik").getAsUser().getId() + "> Gratulacje, uzyskałeś dostęp\n" +
                            "Dziękujemy za spełnienie wymagań\n" +
                            "\n" +
                            "Od teraz możesz korzystać z https://discord.com/channels/1280903359422922845/1305448619544940594\n" +
                            "\n" +
                            "Nadano przez:\n" +
                            "<@" + e.getUser().getId() + ">"
                    );
                    accesEmbed.setColor(Color.GREEN);
                    accesEmbed.setImage("https://i.imgur.com/e0dHJXO.png");

                    Role rola = e.getGuild().getRoleById("1342589406992924722");
                    e.getGuild().addRoleToMember(member, rola);
                    e.replyEmbeds(accesEmbed.build()).queue();
                } else if (choice.equalsIgnoreCase("brak dostępu")) {
                    String komentarz = e.getOption("komentarz") != null ? e.getOption("komentarz").getAsString() : "Brak komentarza.";
                    EmbedBuilder noaccesEbmed = new EmbedBuilder();
                    noaccesEbmed.setDescription("```WoolCode - Brak Dostępu```\n" +
                            "<@" + e.getOption("uzytkownik").getAsUser().getId() + "> Niestety nie spełniłeś wymagań\n" +
                            "Komentarz:\n" +
                            "**" + komentarz + "**" + "\n" +
                            "\n" +
                            "Wyślij wymagania jeszcze jescze raz a administracja  \n" +
                            "Napewno je sprawdzi jak najszybciej \n" +
                            "\n" +
                            "Nadane przez:\n" +
                            "<@" + e.getUser().getId() + ">"
                    );
                    noaccesEbmed.setColor(Color.RED);
                    noaccesEbmed.setImage("https://i.imgur.com/e0dHJXO.png");
                    e.replyEmbeds(noaccesEbmed.build()).queue();

                }
            } else {
                e.reply("Nie wybrano opcji dostępu").setEphemeral(true).queue();
            }
        }
    }
}
