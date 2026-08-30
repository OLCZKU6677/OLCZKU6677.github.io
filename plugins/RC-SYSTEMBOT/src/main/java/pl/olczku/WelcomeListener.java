package pl.olczku;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.time.OffsetDateTime;
import java.time.Duration;

public class WelcomeListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(WelcomeListener.class);
    private final String welcomeChannelId;
    private final String memberCountChannelId;
    private final String guildId;

    public WelcomeListener(Properties config) {
        this.welcomeChannelId = config.getProperty("welcome.channel.id");
        this.memberCountChannelId = config.getProperty("member.count.channel.id");
        this.guildId = config.getProperty("guild.id");

        if (welcomeChannelId == null || welcomeChannelId.isBlank()) {
            logger.warn("welcome.channel.id nie jest ustawiony w config.properties. Powitania nie będą wysyłane.");
        }
        if (memberCountChannelId == null || memberCountChannelId.isBlank()) {
            logger.warn("member.count.channel.id nie jest ustawiony w config.properties. Licznik członków nie będzie aktualizowany.");
        }
        if (guildId == null || guildId.isBlank()) {
            logger.error("guild.id nie jest ustawiony w config.properties. System powitań może nie działać prawidłowo.");
        }
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if (!event.getGuild().getId().equals(guildId)) {
            return;
        }

        Guild guild = event.getGuild();
        Member member = event.getMember();
        User user = member.getUser();

        if (welcomeChannelId != null && !welcomeChannelId.isBlank()) {
            TextChannel welcomeChannel = guild.getTextChannelById(welcomeChannelId);
            if (welcomeChannel != null) {
                OffsetDateTime joinTime = member.getTimeJoined();

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(107,219,161));

                String description =
                        "```Witaj " + user.getName() + "!```" + "\n" +
                                "<:member1:1423382429518794782> Witamy nową osobę " + member.getAsMention() + "\n" +
                                "<:kal:1423382857644118176> Dołączył: <t:" + joinTime.toEpochSecond() + ":R>\n" +
                                "<:web:1423383300415815771> Aktualnie jest nas: **" + guild.getMemberCount() + "";

                embed.setDescription(description);
                embed.setThumbnail(user.getEffectiveAvatarUrl());
                embed.setImage("https://cdn.discordapp.com/attachments/1407053232458694760/1423386087912374505/bannerPowitania.jpg?ex=68e01f18&is=68decd98&hm=1d95d29717f918b63afb98f1d458614aa3f892ca7d3d59b9a1ab5b11fa9de62b&");

                welcomeChannel.sendMessageEmbeds(embed.build()).queue();
            } else {
                logger.warn("Kanał powitalny o ID {} nie został znaleziony.", welcomeChannelId);
            }
        }

        if (memberCountChannelId != null && !memberCountChannelId.isBlank()) {
            updateMemberCount(guild);
        }
    }

    private void updateMemberCount(@NotNull Guild guild) {
        MessageChannel channel = guild.getJDA().getChannelById(MessageChannel.class, memberCountChannelId);

        if (channel != null) {
            String newName = "👥 | Użytkownicy: " + guild.getMemberCount();

            if (channel.getType() == ChannelType.VOICE) {
                VoiceChannel voiceChannel = (VoiceChannel) channel;
                voiceChannel.getManager().setName(newName).queue(
                        s -> logger.info("Zaktualizowano licznik członków w kanale głosowym: {}", newName),
                        e -> logger.error("Błąd aktualizacji licznika członków w kanale głosowym {}: {}", memberCountChannelId, e.getMessage())
                );
            } else if (channel.getType() == ChannelType.TEXT) {
                TextChannel textChannel = (TextChannel) channel;
                textChannel.getManager().setName(newName).queue(
                        s -> logger.info("Zaktualizowano licznik członków w kanale tekstowym: {}", newName),
                        e -> logger.error("Błąd aktualizacji licznika członków w kanale tekstowym {}: {}", memberCountChannelId, e.getMessage())
                );
            } else {
                logger.warn("Kanał licznika członków o ID {} nie jest kanałem głosowym ani tekstowym. Nie można zaktualizować nazwy.", memberCountChannelId);
            }
        } else {
            logger.warn("Kanał licznika członków o ID {} nie został znaleziony.", memberCountChannelId);
        }
    }
}