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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Verification extends ListenerAdapter {

    private String correctButtonId;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // Sprawdzamy, czy użytkownik ma odpowiednią rolę przed wykonaniem jakiejkolwiek komendy
        if (!hasRequiredRole(event.getMember())) {
            event.reply("❌ **Nie masz wymaganej roli, aby korzystać z komend!**").setEphemeral(true).queue();
            return;
        }

        if (event.getName().equals("verify-setup")) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("🎉 **Witamy na serwerze FirenMC!**");
            embed.setDescription("""
                     > 🔒 **Aby uzyskać dostęp do kanałów, musisz się zweryfikować!**
                     > 📜 **Weryfikacja jest obowiązkowa dla wszystkich nowych użytkowników.**
                     > ✳️ **Kliknij przycisk **„✅ Zweryfikuj się”** poniżej, aby rozpocząć proces weryfikacji.**
                     > 📌 **Po kliknięciu, wybierz odpowiedni przycisk zgodnie z podpowiedzią!**
                    
                    
                    
                    **📜 Pamiętaj:**
                       • **Przestrzegaj zasad regulaminu.**
                       • **Weryfikacja to obowiązek każdego nowego użytkownika.**
                       • **Niepowodzenie skutkuje koniecznością ponownej próby.**
                       • **Weryfikacja jest konieczna, aby uzyskać dostęp do kanałów.**
                       • **Powodzenia!**
                    
                          **Życzymy Ci miłego pobytu!** 💛
                    """);
            embed.setColor(Color.YELLOW);

            Button verifyButton = Button.primary("start_verification", "✅ Zweryfikuj się");

            event.replyEmbeds(embed.build()).addActionRow(verifyButton).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("start_verification")) {
            System.out.println("[DEBUG] Kliknięto przycisk \"Zweryfikuj się\".");

            List<Button> buttons = new ArrayList<>();
            buttons.add(Button.primary("verify_1", "😀"));
            buttons.add(Button.danger("verify_2", "🎮"));
            buttons.add(Button.success("verify_3", "🎨"));
            buttons.add(Button.secondary("verify_4", "😎"));
            buttons.add(Button.primary("verify_5", "🎯"));
            buttons.add(Button.danger("verify_6", "🎵"));
            buttons.add(Button.success("verify_7", "📚"));
            buttons.add(Button.secondary("verify_8", "🎲"));
            buttons.add(Button.primary("verify_9", "🎤"));
            buttons.add(Button.danger("verify_10", "🎸"));

            Random random = new Random();
            Button correctButton = buttons.get(random.nextInt(buttons.size()));
            correctButtonId = correctButton.getId();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("🛡️ **Weryfikacja użytkownika**");
            embed.setDescription("> 🟨 Kliknij **" + getButtonStyleName(correctButton) + "** przycisk z emotką **" + correctButton.getLabel() + "**, aby się zweryfikować!");
            embed.setColor(Color.YELLOW);

            var reply = event.replyEmbeds(embed.build()).setEphemeral(true);
            for (int i = 0; i < buttons.size(); i += 5) {
                reply = reply.addActionRow(buttons.subList(i, Math.min(i + 5, buttons.size())));
            }
            reply.queue();

        } else if (event.getComponentId().startsWith("verify_")) {
            System.out.println("[DEBUG] Kliknięto przycisk: " + event.getComponentId());

            if (event.getComponentId().equals(correctButtonId)) {
                Member member = event.getMember();
                Guild guild = event.getGuild();

                if (guild != null && member != null) {
                    Role role = guild.getRoleById("1374436934373343293");
                    if (role != null) {
                        guild.addRoleToMember(member, role).queue();
                    }
                }

                EmbedBuilder successEmbed = new EmbedBuilder();
                successEmbed.setTitle("✅ **Weryfikacja zakończona sukcesem!**");
                successEmbed.setDescription("🎊 Pomyślnie uzyskałeś dostęp do serwera!\nMiłego pobytu! 💬");
                successEmbed.setColor(Color.YELLOW);

                event.replyEmbeds(successEmbed.build()).setEphemeral(true).queue();
            } else {
                EmbedBuilder failEmbed = new EmbedBuilder();
                failEmbed.setTitle("❌ **Niepoprawna odpowiedź**");
                failEmbed.setDescription("🔁 Spróbuj ponownie klikając właściwy przycisk.\nPamiętaj, aby dobrze przeczytać instrukcję! 📘");
                failEmbed.setColor(Color.YELLOW);

                event.replyEmbeds(failEmbed.build()).setEphemeral(true).queue();
            }
        }
    }

    private boolean hasRequiredRole(Member member) {
        if (member != null) {
            Role requiredRole = member.getGuild().getRoleById("1367808576705400882");
            return requiredRole != null && member.getRoles().contains(requiredRole);
        }
        return false;
    }

    private String getButtonStyleName(Button button) {
        return switch (button.getStyle()) {
            case PRIMARY -> "🔵 Niebieski";
            case DANGER -> "🔴 Czerwony";
            case SUCCESS -> "🟢 Zielony";
            case SECONDARY -> "⚪ Szary";
            default -> "❓ Nieznany";
        };
    }
}
