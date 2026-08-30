package pl.olczku;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class SelfRole extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {
        if (e.getName().equals("selfrole")) {
            Member member = e.getMember();
            if (member == null || !member.getRoles().stream().anyMatch(role -> role.getId().equals("1346518286954659890"))) {
                EmbedBuilder selfRoleEmbed = new EmbedBuilder();
                selfRoleEmbed.setDescription("``` WOOLCODE SELFROLE  ``` \n" +
                        "Wybierz swoje rolę z listy poniżej, aby uzyskać dostęp do odpowiednich kanałów.\n" +
                        "**MEDIA PING** - Ping z kanałów związanych z mediami\n" +
                        "**OGŁOSZENIA PING** - Ping z kanałów związanych z ogłoszeniami\n" +
                        "**GIVEAWAY PING** - Ping z kanałów związanych z giveawayami\n" +
                        "**ANKIETY PING** - Ping z kanałów związanych z ankietami\n" +
                        "\n" +
                        "Aby wybrać role kliknij poniższe przyciski");
                selfRoleEmbed.setImage("https://cdn.discordapp.com/attachments/134651828695465989/1346518286954659890/unknown.png");
                selfRoleEmbed.setColor(Color.green);

                e.replyEmbeds(selfRoleEmbed.build()).addActionRow(
                        Button.primary("media", "MEDIA PING"),
                        Button.primary("ogloszenia", "OGŁOSZENIA PING"),
                        Button.primary("giveaway", "GIVEAWAY PING"),
                        Button.primary("ankiety", "ANKIETY PING")
                ).queue();



            } else {
                e.reply("Nie możesz korzystać z tej komendy").setEphemeral(true).queue();
            }
        }
    }
    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {
        if (e.getComponentId().equalsIgnoreCase("media")) {
            Guild guild = e.getGuild();
            Member member = e.getMember();
            net.dv8tion.jda.api.entities.Role role = guild.getRoleById("1346257043216007291");
            guild.addRoleToMember(member, role).queue();
        }
        if (e.getComponentId().equalsIgnoreCase("ogloszenia")) {
            Guild guild = e.getGuild();
            Member member = e.getMember();
            net.dv8tion.jda.api.entities.Role role = guild.getRoleById("1346257043216007291");
            guild.addRoleToMember(member, role).queue();
        }
        if (e.getComponentId().equalsIgnoreCase("giveaway")) {
            Guild guild = e.getGuild();
            Member member = e.getMember();
            net.dv8tion.jda.api.entities.Role role = guild.getRoleById("1346257043216007291");
            guild.addRoleToMember(member, role).queue();
        }
        if (e.getComponentId().equalsIgnoreCase("ankiety")) {
            Guild guild = e.getGuild();
            Member member = e.getMember();
            net.dv8tion.jda.api.entities.Role role = guild.getRoleById("1346257043216007291");
            guild.addRoleToMember(member, role).queue();
        }
    }
}
