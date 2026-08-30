package pl.olczku;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class ModerationCommands extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ModerationCommands.class);

    private static final String ADMIN_ROLE_ID = "1367808576701337602";
    private static final String LOG_CHANNEL_BAN = "1370776210417913978";
    private static final String LOG_CHANNEL_TEMPBAN = "1370776210417913978";
    private static final String LOG_CHANNEL_MUTE = "1370776306488184993";
    private static final String LOG_CHANNEL_TEMPMUTE = "1370776306488184993";
    private static final String LOG_CHANNEL_UNMUTE = "1370776306488184993";
    private static final String LOG_CHANNEL_WARN = "1370776399295811614";
    private static final String LOG_CHANNEL_CLEAR = "1370776489909555200";
    private static final String LOG_CHANNEL_REPUTATION = "1370776489909555200";
    private static final String LOG_CHANNEL_UNWARN = "1370776399295811614"; // ID kanału logów do unwarn

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) return;

        String command = event.getName();
        switch (command) {
            case "pomoceadm":
                sendAdminHelpEmbed(event);
                break;
            case "ban":
                banUser(event);
                break;
            case "tempban":
                tempBanUser(event);
                break;
            case "mute":
                muteUser(event);
                break;
            case "tempmute":
                tempMuteUser(event);
                break;
            case "unmute":
                unmuteUser(event);
                break;
            case "warn":
                warnUser(event);
                break;
            case "unwarn":
                unwarnUser(event); // Nowa komenda unwarn
                break;
            case "clear":
                clearMessages(event);
                break;
            case "repadd":
                addReputation(event);
                break;
            case "repremove":
                removeReputation(event);
                break;
            case "ping":
                pingCommand(event);
                break;
        }
    }

    private void logAction(Guild guild, String logChannelId, String title, String description, Color color) {
        TextChannel logChannel = guild.getTextChannelById(logChannelId);
        if (logChannel == null) {
            logger.warn("Nie znaleziono kanału logów o ID {}.", logChannelId);
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setColor(color)
                .setTimestamp(java.time.Instant.now())
                .setFooter("Wykonane przez: " + guild.getOwner().getUser().getAsTag());

        logChannel.sendMessageEmbeds(embed.build()).queue(
                success -> logger.info("Zalogowano akcję: {}", title),
                error -> logger.error("Nie udało się zalogować akcji: {}", error.getMessage())
        );
    }

    private void sendAdminHelpEmbed(SlashCommandInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Pomoc Administratora")
                .setDescription("Lista dostępnych komend administracyjnych:")
                .addField("/ban <użytkownik> <powód>", "Banuje użytkownika.", false)
                .addField("/tempban <użytkownik> <czas> <powód>", "Tymczasowo banuje użytkownika.", false)
                .addField("/mute <użytkownik>", "Wycisza użytkownika.", false)
                .addField("/tempmute <użytkownik> <czas>", "Tymczasowo wycisza użytkownika.", false)
                .addField("/unmute <użytkownik>", "Odcisza użytkownika.", false)
                .addField("/warn <użytkownik> <powód>", "Ostrzega użytkownika.", false)
                .addField("/unwarn <użytkownik>", "Usuwa ostrzeżenie użytkownikowi.", false)
                .addField("/clear <ilość>", "Usuwa wiadomości.", false)
                .addField("/repadd <użytkownik> <ilość>", "Dodaje punkty reputacji użytkownikowi.", false)
                .addField("/repremove <użytkownik> <ilość>", "Usuwa punkty reputacji użytkownikowi.", false)
                .addField("/ping", "Sprawdza opóźnienie bota.", false)
                .setColor(Color.BLUE);

        event.replyEmbeds(embed.build()).queue();
    }

    private void unwarnUser(SlashCommandInteractionEvent event) {
        Member target = event.getOption("user", OptionMapping::getAsMember);
        if (target == null) {
            event.reply("Nie znaleziono użytkownika.").setEphemeral(true).queue();
            return;
        }

        // Przykład: Unwarn - logowanie działania
        event.reply("Usunięto ostrzeżenie dla użytkownika " + target.getUser().getAsTag()).queue();

        logAction(event.getGuild(), LOG_CHANNEL_UNWARN, "Ostrzeżenie usunięte", "Użytkownik: " + target.getUser().getAsTag(), Color.GREEN);
    }

    private void banUser(SlashCommandInteractionEvent event) {
        Member target = event.getOption("user", OptionMapping::getAsMember);
        if (target == null) {
            event.reply("Nie znaleziono użytkownika.").setEphemeral(true).queue();
            return;
        }
        String reason = event.getOption("reason", OptionMapping::getAsString);
        event.getGuild().ban(Collections.singletonList(target), Duration.ZERO).reason(reason).queue();
        event.reply("Użytkownik " + target.getUser().getAsTag() + " został zbanowany.").queue();

        logAction(event.getGuild(), LOG_CHANNEL_BAN, "Użytkownik zbanowany", "Użytkownik: " + target.getUser().getAsTag() + "\nPowód: " + reason, Color.RED);
    }

    private void tempBanUser(SlashCommandInteractionEvent event) {
        Member target = event.getOption("user", OptionMapping::getAsMember);
        int duration = event.getOption("duration", OptionMapping::getAsInt);
        String reason = event.getOption("reason", OptionMapping::getAsString);

        if (target == null) {
            event.reply("Nie znaleziono użytkownika.").setEphemeral(true).queue();
            return;
        }

        event.getGuild().ban(Collections.singletonList(target), Duration.ofMinutes(duration)).reason(reason).queue();
        event.reply("Użytkownik " + target.getUser().getAsTag() + " został tymczasowo zbanowany na " + duration + " minut.").queue();

        logAction(event.getGuild(), LOG_CHANNEL_TEMPBAN, "Użytkownik tymczasowo zbanowany", "Użytkownik: " + target.getUser().getAsTag() + "\nCzas: " + duration + " minut\nPowód: " + reason, Color.ORANGE);

        event.getGuild().unban(target.getUser()).queueAfter(duration, TimeUnit.MINUTES);
    }

    private void muteUser(SlashCommandInteractionEvent event) {
        Member target = event.getOption("user", OptionMapping::getAsMember);
        Role muteRole = event.getGuild().getRoles().stream()
                .filter(role -> role.getName().equalsIgnoreCase("Muted"))
                .findFirst()
                .orElse(null);

        if (target == null) {
            event.reply("Nie znaleziono użytkownika.").setEphemeral(true).queue();
            return;
        }

        if (muteRole == null) {
            event.reply("Nie znaleziono roli Muted.").setEphemeral(true).queue();
            return;
        }

        event.getGuild().addRoleToMember(target, muteRole).queue();
        event.reply("Użytkownik " + target.getUser().getAsTag() + " został wyciszony.").queue();

        logAction(event.getGuild(), LOG_CHANNEL_MUTE, "Użytkownik wyciszony", "Użytkownik: " + target.getUser().getAsTag(), Color.YELLOW);
    }

    private void tempMuteUser(SlashCommandInteractionEvent event) {
        Member target = event.getOption("user", OptionMapping::getAsMember);
        int duration = event.getOption("duration", OptionMapping::getAsInt);
        Role muteRole = event.getGuild().getRoles().stream()
                .filter(role -> role.getName().equalsIgnoreCase("Muted"))
                .findFirst()
                .orElse(null);

        if (target == null) {
            event.reply("Nie znaleziono użytkownika.").setEphemeral(true).queue();
            return;
        }

        if (muteRole == null) {
            event.reply("Nie znaleziono roli Muted.").setEphemeral(true).queue();
            return;
        }

        event.getGuild().addRoleToMember(target, muteRole).queue();
        event.reply("Użytkownik " + target.getUser().getAsTag() + " został tymczasowo wyciszony na " + duration + " minut.").queue();

        logAction(event.getGuild(), LOG_CHANNEL_TEMPMUTE, "Użytkownik tymczasowo wyciszony", "Użytkownik: " + target.getUser().getAsTag() + "\nCzas: " + duration + " minut", Color.ORANGE);

        event.getGuild().removeRoleFromMember(target, muteRole).queueAfter(duration, TimeUnit.MINUTES);
    }

    private void unmuteUser(SlashCommandInteractionEvent event) {
        Member target = event.getOption("user", OptionMapping::getAsMember);
        Role muteRole = event.getGuild().getRoles().stream()
                .filter(role -> role.getName().equalsIgnoreCase("Muted"))
                .findFirst()
                .orElse(null);

        if (target == null) {
            event.reply("Nie znaleziono użytkownika.").setEphemeral(true).queue();
            return;
        }

        if (muteRole == null) {
            event.reply("Nie znaleziono roli Muted.").setEphemeral(true).queue();
            return;
        }

        event.getGuild().removeRoleFromMember(target, muteRole).queue();
        event.reply("Użytkownik " + target.getUser().getAsTag() + " został odciszony.").queue();

        logAction(event.getGuild(), LOG_CHANNEL_UNMUTE, "Użytkownik odciszony", "Użytkownik: " + target.getUser().getAsTag(), Color.GREEN);
    }

    private void warnUser(SlashCommandInteractionEvent event) {
        Member target = event.getOption("user", OptionMapping::getAsMember);
        String reason = event.getOption("reason", OptionMapping::getAsString);

        if (target == null) {
            event.reply("Nie znaleziono użytkownika.").setEphemeral(true).queue();
            return;
        }

        event.reply("Użytkownik " + target.getUser().getAsTag() + " został ostrzeżony.\nPowód: " + reason).queue();

        logAction(event.getGuild(), LOG_CHANNEL_WARN, "Użytkownik ostrzeżony", "Użytkownik: " + target.getUser().getAsTag() + "\nPowód: " + reason, Color.ORANGE);
    }

    private void clearMessages(SlashCommandInteractionEvent event) {
        int amount = event.getOption("amount", OptionMapping::getAsInt);
        if (amount < 1 || amount > 100) {
            event.reply("Podaj liczbę między 1 a 100.").setEphemeral(true).queue();
            return;
        }

        event.getChannel().purgeMessages(event.getChannel().getHistory().retrievePast(amount).complete());
        event.reply("Usunięto " + amount + " wiadomości.").queue();

        logAction(event.getGuild(), LOG_CHANNEL_CLEAR, "Wiadomości usunięte", "Liczba wiadomości: " + amount, Color.BLUE);
    }

    private void addReputation(SlashCommandInteractionEvent event) {
        Member target = event.getOption("user", OptionMapping::getAsMember);
        int amount = event.getOption("amount", OptionMapping::getAsInt);

        if (target == null) {
            event.reply("Nie znaleziono użytkownika.").setEphemeral(true).queue();
            return;
        }

        event.reply("Dodano " + amount + " punktów reputacji użytkownikowi " + target.getAsMention()).queue();

        logAction(event.getGuild(), LOG_CHANNEL_REPUTATION, "Dodano reputację", "Użytkownik: " + target.getUser().getAsTag() + "\nLiczba punktów: " + amount, Color.GREEN);
    }

    private void removeReputation(SlashCommandInteractionEvent event) {
        Member target = event.getOption("user", OptionMapping::getAsMember);
        int amount = event.getOption("amount", OptionMapping::getAsInt);

        if (target == null) {
            event.reply("Nie znaleziono użytkownika.").setEphemeral(true).queue();
            return;
        }

        event.reply("Usunięto " + amount + " punktów reputacji użytkownikowi " + target.getAsMention()).queue();

        logAction(event.getGuild(), LOG_CHANNEL_REPUTATION, "Usunięto reputację", "Użytkownik: " + target.getUser().getAsTag() + "\nLiczba punktów: " + amount, Color.RED);
    }

    private void pingCommand(SlashCommandInteractionEvent event) {
        long time = System.currentTimeMillis();
        event.reply("Pong!").queue(response -> {
            response.editOriginal(String.format("Pong: %d ms", System.currentTimeMillis() - time)).queue();
        });

        logAction(event.getGuild(), LOG_CHANNEL_REPUTATION, "Komenda Ping", "Komenda ping została wykonana.", Color.GREEN);
    }
}
