package pl.olczku;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.time.Instant;

public class Verification extends ListenerAdapter {

    // TUTAJ WPISZ ID ROLI, KTÓRĄ BOT MA NADAWAĆ
    private static final String ROLE_ID_TO_GRANT = "1326355624065237149";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // Ta komenda tworzy panel weryfikacyjny
        if (event.getName().equals("verify-setup")) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("` HESEN.PL x WERYFIKACJA `");
            embed.setColor(new Color(255, 255, 255)); // Biały kolor, jak w ticketach
            embed.setDescription(
                    "> Aby uzyskać pełny dostęp do naszego serwera, musisz się zweryfikować.\n\n" +
                            "> Kliknij przycisk poniżej, aby potwierdzić, że zapoznałeś się z regulaminem i dołączyć do naszej społeczności.\n"
            );
            // Możesz dodać ten sam obrazek co w ticketach, jeśli chcesz
            embed.setImage("https://cdn.discordapp.com/attachments/1325878026759573597/1396849880407740486/image.png?ex=687f9553&is=687e43d3&hm=142c64033904f3e208c165a63eaacc97d0251d3667d56214f7c46cdd28ba9553&");

            Button verifyButton = Button.success("grant_verification_role", "✅ Zweryfikuj się");

            event.replyEmbeds(embed.build()).addActionRow(verifyButton).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        // Ta metoda obsługuje kliknięcie przycisku
        if (event.getComponentId().equals("grant_verification_role")) {
            Member member = event.getMember();
            Guild guild = event.getGuild();

            if (member == null || guild == null) {
                event.reply("Wystąpił nieoczekiwany błąd. Spróbuj ponownie.").setEphemeral(true).queue();
                return;
            }

            Role role = guild.getRoleById(ROLE_ID_TO_GRANT);

            // Sprawdzamy, czy rola do nadania w ogóle istnieje
            if (role == null) {
                System.err.println("KRYTYCZNY BŁĄD: Rola o ID " + ROLE_ID_TO_GRANT + " nie została znaleziona!");
                event.reply("Wystąpił błąd konfiguracji bota. Skontaktuj się z administracją.").setEphemeral(true).queue();
                return;
            }

            // Sprawdzamy, czy użytkownik już ma tę rolę
            if (member.getRoles().contains(role)) {
                event.reply("Jesteś już zweryfikowany!").setEphemeral(true).queue();
                return;
            }

            // Nadajemy rolę
            guild.addRoleToMember(member, role).queue(
                    // Co zrobić, gdy się uda:
                    success -> {
                        EmbedBuilder successEmbed = new EmbedBuilder();
                        successEmbed.setTitle("✅ Weryfikacja pomyślna!");
                        successEmbed.setDescription("Gratulacje! Otrzymałeś dostęp do wszystkich kanałów na serwerze Hesen.pl.");
                        successEmbed.setColor(Color.GREEN);
                        successEmbed.setTimestamp(Instant.now());

                        event.replyEmbeds(successEmbed.build()).setEphemeral(true).queue();
                    },
                    // Co zrobić, gdy wystąpi błąd (np. brak uprawnień bota):
                    error -> {
                        System.err.println("Błąd podczas nadawania roli: " + error.getMessage());
                        event.reply("Nie udało się nadać roli z powodu błędu. Skontaktuj się z administracją.").setEphemeral(true).queue();
                    }
            );
        }
    }
}