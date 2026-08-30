package pl.olczku;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.awt.Color;

public class WelcomeListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        TextChannel welcomeChannel = guild.getTextChannelById("1304174588145700864"); // Zmień na ID kanału powitań

        if (welcomeChannel != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("👋 • WoolCode - Powitania", null);
            embed.setDescription(
                    "> Witaj " + member.getAsMention() + " na oficjalnym Discordzie **WoolCode**!       \n" +
                            "> Jesteś **" + guild.getMemberCount() + "** osobą na naszym serwerze.\n" +
                            "> Pamiętaj, aby przestrzegać \uD83D\uDCC4 https://discord.com/channels/1280903359422922845/1280904630124937286\n\n" +
                            "> Mamy nadzieję, że zostaniesz z nami na dłużej!"
            );
            embed.setColor(new Color(126, 3, 252)); // Discord embed color
            embed.setThumbnail(member.getEffectiveAvatarUrl()); // Avatar użytkownika
            embed.setImage("https://i.imgur.com/PUPZY2e.png"); // Baner powitalny

            welcomeChannel.sendMessageEmbeds(embed.build()).queue();
        }
    }
}
