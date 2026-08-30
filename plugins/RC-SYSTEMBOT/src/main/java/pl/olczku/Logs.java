package pl.olczku;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateParentEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdatePositionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.Event;

import java.awt.Color;
import java.time.Instant;
import java.util.Properties;

public class Logs extends ListenerAdapter {

    private final String logChannelId;
    private static final Color MESSAGE_DELETE_COLOR = new Color(244, 67, 54);
    private static final Color MESSAGE_EDIT_COLOR = new Color(255, 179, 0);
    private static final Color CHANNEL_CREATE_COLOR = new Color(67, 137, 244);
    private static final Color CHANNEL_DELETE_COLOR = new Color(244, 67, 54);
    private static final Color CHANNEL_UPDATE_COLOR = new Color(255, 179, 0);

    public Logs(Properties config) {
        this.logChannelId = config.getProperty("log.channel.id", "YOUR_LOG_CHANNEL_ID_HERE");
    }

    private void sendLog(Event event, EmbedBuilder embed) {
        if (logChannelId.equals("YOUR_LOG_CHANNEL_ID_HERE")) {
            return;
        }
        TextChannel logChannel = event.getJDA().getTextChannelById(logChannelId);
        if (logChannel != null) {
            logChannel.sendMessageEmbeds(embed.build()).queue();
        }
    }

    // Wiadomości

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        if (event.getChannel().getId().equals(logChannelId)) return;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🗑️ Wiadomość usunięta")
                .setDescription("Wiadomość została usunięta w kanale " + event.getChannel().getAsMention())
                .addField("Autor", "Brak danych (wiadomość usunięta)", true)
                .addField("Kanał", event.getChannel().getAsMention(), true)
                .setColor(MESSAGE_DELETE_COLOR)
                .setFooter("ID Wiadomości: " + event.getMessageId(), null)
                .setTimestamp(Instant.now());

        sendLog(event, embed);
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (event.getChannel().getId().equals(logChannelId)) return;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("✏️ Wiadomość edytowana")
                .setDescription("Wiadomość została edytowana w kanale " + event.getChannel().getAsMention())
                .addField("Autor", event.getAuthor().getAsMention(), true)
                .addField("Kanał", event.getChannel().getAsMention(), true)
                .addField("Stara treść", "```" + event.getMessage().getContentRaw() + "```", false)
                .addField("Nowa treść", "```" + event.getMessage().getContentDisplay() + "```", false)
                .setColor(MESSAGE_EDIT_COLOR)
                .setFooter("ID Wiadomości: " + event.getMessageId(), null)
                .setTimestamp(Instant.now());
        sendLog(event, embed);
    }

    // Kanały

    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        GuildChannel channel = event.getChannel().asGuildChannel();
        if (channel.getId().equals(logChannelId)) return;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("➕ Kanał utworzony")
                .setDescription("Nowy kanał `" + channel.getName() + "` został utworzony.")
                .addField("Typ kanału", channel.getType().name(), true)
                .addField("ID kanału", channel.getId(), true)
                .setColor(CHANNEL_CREATE_COLOR)
                .setFooter("ID Serwera: " + event.getGuild().getId(), null)
                .setTimestamp(Instant.now());
        sendLog(event, embed);
    }

    @Override
    public void onChannelDelete(ChannelDeleteEvent event) {
        GuildChannel channel = event.getChannel().asGuildChannel();
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("➖ Kanał usunięty")
                .setDescription("Kanał `" + channel.getName() + "` został usunięty.")
                .addField("Typ kanału", channel.getType().name(), true)
                .addField("ID kanału", channel.getId(), true)
                .setColor(CHANNEL_DELETE_COLOR)
                .setFooter("ID Serwera: " + event.getGuild().getId(), null)
                .setTimestamp(Instant.now());
        sendLog(event, embed);
    }

    @Override
    public void onChannelUpdateName(ChannelUpdateNameEvent event) {
        GuildChannel channel = event.getChannel().asGuildChannel();
        if (channel.getId().equals(logChannelId)) return;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("📝 Nazwa kanału zmieniona")
                .setDescription("Nazwa kanału " + channel.getAsMention() + " została zmieniona.")
                .addField("Stara nazwa", "```" + event.getOldValue() + "```", false)
                .addField("Nowa nazwa", "```" + event.getNewValue() + "```", false)
                .setColor(CHANNEL_UPDATE_COLOR)
                .setFooter("ID kanału: " + channel.getId(), null)
                .setTimestamp(Instant.now());
        sendLog(event, embed);
    }

    @Override
    public void onChannelUpdateParent(ChannelUpdateParentEvent event) {
        GuildChannel channel = event.getChannel().asGuildChannel();
        if (channel.getId().equals(logChannelId)) return;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("📂 Kategoria kanału zmieniona")
                .setDescription("Kategoria kanału " + channel.getAsMention() + " została zmieniona.")
                .addField("Stara kategoria", event.getOldValue() != null ? event.getOldValue().getName() : "Brak", true)
                .addField("Nowa kategoria", event.getNewValue() != null ? event.getNewValue().getName() : "Brak", true)
                .setColor(CHANNEL_UPDATE_COLOR)
                .setFooter("ID kanału: " + channel.getId(), null)
                .setTimestamp(Instant.now());
        sendLog(event, embed);
    }

    @Override
    public void onChannelUpdatePosition(ChannelUpdatePositionEvent event) {
        GuildChannel channel = event.getChannel().asGuildChannel();
        if (channel.getId().equals(logChannelId)) return;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("↕️ Pozycja kanału zmieniona")
                .setDescription("Pozycja kanału " + channel.getAsMention() + " została zmieniona.")
                .addField("Stara pozycja", String.valueOf(event.getOldValue()), true)
                .addField("Nowa pozycja", String.valueOf(event.getNewValue()), true)
                .setColor(CHANNEL_UPDATE_COLOR)
                .setFooter("ID kanału: " + channel.getId(), null)
                .setTimestamp(Instant.now());
        sendLog(event, embed);
    }
}