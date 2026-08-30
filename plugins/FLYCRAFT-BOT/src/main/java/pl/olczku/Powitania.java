package pl.olczku;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import java.awt.Color;


public class Powitania extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        // Uzyskujemy kanał powitań po ID
        TextChannel welcomeChannel = event.getGuild().getTextChannelById("1367808576705400890");

        // Jeśli kanał nie istnieje, nic nie robimy
        if (welcomeChannel == null) {
            return;
        }

        // Tworzymy embed do powitania
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.YELLOW);  // Ustawiamy kolor na żółty
        embed.setTitle("Witaj na serwerze, " + event.getMember().getEffectiveName() + "!");
        embed.setDescription("Cieszymy się, że dołączyłeś do naszej społeczności! 😄\n"
                + "Prosimy zapoznaj się z regulaminem i baw się dobrze!\n\n"
                + "**Kilka przydatnych informacji:**\n"
                + "1. Sprawdź kanał #regulamin.\n"
                + "2. Poznaj naszą społeczność.\n"
                + "3. Skontaktuj się z administracją w razie jakichkolwiek pytań.\n\n"
                + "W razie jakichkolwiek problemów, śmiało pisz do nas!");
        embed.setThumbnail(event.getUser().getAvatarUrl());  // Avatar użytkownika
        embed.setFooter("Miłego pobytu na serwerze!", null);

        // Wysyłamy wiadomość powitalną na kanał
        welcomeChannel.sendMessageEmbeds(embed.build()).queue();
    }
}
