package pl.olczku;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;

public class Verification extends ListenerAdapter {


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (e.getName().equals("verifypanel")) {
            Member member = e.getMember();
            if (member == null || !member.getRoles().stream().anyMatch(role -> role.getId().equals("1341867856958328902"))) {
                e.reply("Nie masz uprawnień do używania tej komendy.").setEphemeral(true).queue();
                return;
            }

            EmbedBuilder verifyEmbed = new EmbedBuilder();
            verifyEmbed.setDescription("```Zweryfikuj się - WoolCode Weryfikacja```\n" +
                    "Witaj na naszym serwerze! Aby uzyskać pełny dostęp, musisz przejść weryfikację\n" +
                    "\n" +
                    "**Jak się zweryfikować?**\n" +
                    "1. Kliknij przycisk **Zweryfikuj się ** poniżej\n" +
                    "2. Postępuj zgodnie z instrukcjami bota.\n" +
                    "3. Po pomyślnej weryfikacji otrzymasz dostęp do wszystkich kanałów.\n" +
                    "\n" +
                    "⚠\uFE0F **Nie udostępniaj swoich danych osobowych!**\n" +
                    "Administracja nigdy nie poprosi Cię o hasło ani inne prywatne informacje.\n" +
                    "\n" +
                    "\uD83D\uDD12*Bezpieczeństwo jest dla nas priorytetem!* ");
            verifyEmbed.setColor(Color.green);
            verifyEmbed.setImage("https://i.imgur.com/e0dHJXO.png");
            System.out.println("Użytkownik " + e.getUser().getName() + " wysłał panel z weryfikacją");

            e.getChannel().sendMessageEmbeds(verifyEmbed.build()).setActionRow(
                    Button.primary("verify", "✅ Zweryfikuj się")
            ).queue();
        }
    }


    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {
        if (e.getComponentId().equals("verify")) {
            e.reply("✅ Pomyślnie zweryfikowano!").setEphemeral(true).queue();

            Guild guild = e.getGuild();
            Member member = e.getMember();
            net.dv8tion.jda.api.entities.Role role = guild.getRoleById("1342589406992924722");
            guild.addRoleToMember(member, role).queue();


        }
    }
}
