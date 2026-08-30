package pl.olczku;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TicketCommand extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TicketCommand.class);
    private static final String LOG_CHANNEL_ID = "1392863674506678314";
    private static final String TICKET_CATEGORY_NAME = "Tickety";
    private static final String EMBED_IMAGE_URL = "https://cdn.discordapp.com/attachments/1367810525937008643/1370791310524551209/miniaturka_jakas_4fun.png";
    private final ConcurrentHashMap<String, ScheduledExecutorService> closingTickets = new ConcurrentHashMap<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "ticket-setup":
                handleTicketSetupCommand(event);
                break;
            case "close":
                handleCloseCommand(event);
                break;
            case "closestop":
                handleCloseStopCommand(event);
                break;
            case "dodaj":
                handleAddUserCommand(event);
                break;
        }
    }

    private void handleTicketSetupCommand(SlashCommandInteractionEvent event) {
        if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("❌ Nie posiadasz uprawnień administratora, aby użyć tej komendy!").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setDescription("**```🎫 • Tickety - Informacje ```**\n" +
                        "> **Pomoc Ogólna** - Wszelkie pytania i problemy\n" +
                        "> **Backup** - Prośby o przywrócenie danych\n" +
                        "> **Partnerstwo** - Współpraca z innymi serwerami\n" +
                        "> **Media** - Zgłoszenia dotyczące social mediów\n" +
                        "> **Zgłoszenie Gracza** - Zgłoszenia na graczy\n" +
                        "> **Sprawa Discord** - Problemy związane z Discordem\n" +
                        "> **Do Zarządu** - Prywatne sprawy widoczne tylko dla właściciela\n\n" +
                        "**Uwaga:** Nie nadużywaj systemu ticketów!")
                .setColor(new Color(97, 0, 194))
                .setImage(EMBED_IMAGE_URL);

        Button button = Button.success("create_ticket", "Otwórz Ticket").withEmoji(Emoji.fromUnicode("🎫"));
        event.replyEmbeds(embed.build()).addActionRow(button).queue();
    }

    private void handleCloseCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel() instanceof TextChannel channel)) {
            event.reply("Ta komenda może być użyta tylko w kanale ticketa!").setEphemeral(true).queue();
            return;
        }

        if (!isTicketChannel(channel)) {
            event.reply("To nie jest kanał ticketa!").setEphemeral(true).queue();
            return;
        }

        if (closingTickets.containsKey(channel.getId())) {
            event.reply("Ten ticket jest już zaplanowany do zamknięcia!").setEphemeral(true).queue();
            return;
        }

        sendClosingEmbed(event, channel);
    }

    private boolean isTicketChannel(TextChannel channel) {
        return channel.getTopic() != null && channel.getTopic().contains("Ticket otworzony przez");
    }

    private void sendClosingEmbed(SlashCommandInteractionEvent event, TextChannel channel) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🔒 Zamknięcie Ticketa")
                .setDescription("Ten ticket zostanie zamknięty za 10 sekund.")
                .setColor(Color.RED)
                .setImage(EMBED_IMAGE_URL);

        event.replyEmbeds(embed.build()).queue();

        scheduleChannelDeletion(channel, event.getMember());
    }

    private void scheduleChannelDeletion(TextChannel channel, Member member) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        closingTickets.put(channel.getId(), scheduler);

        scheduler.schedule(() -> {
            if (closingTickets.remove(channel.getId()) != null) {
                sendTicketLog(channel.getGuild(), member.getUser(), "Ticket Zamknięty", channel.getName(), Instant.now(), getCategoryFromChannelName(channel.getName()), null);
                channel.delete().queue(null, throwable -> logger.error("Nie udało się usunąć kanału: {}", throwable.getMessage()));
            }
        }, 10, TimeUnit.SECONDS);
    }

    private String getCategoryFromChannelName(String channelName) {
        try {
            return channelName.split("-")[1];
        } catch (Exception e) {
            return "Nieznana";
        }
    }

    private void handleCloseStopCommand(SlashCommandInteractionEvent event) {
        if (!(event.getChannel() instanceof TextChannel channel)) {
            event.reply("Ta komenda może być użyta tylko w kanale ticketa!").setEphemeral(true).queue();
            return;
        }

        ScheduledExecutorService scheduler = closingTickets.remove(channel.getId());
        if (scheduler != null) {
            scheduler.shutdownNow();
            event.reply("Zamknięcie ticketa zostało anulowane!").queue();
        } else {
            event.reply("Nie zaplanowano zamknięcia tego ticketa!").setEphemeral(true).queue();
        }
    }

    private void handleAddUserCommand(SlashCommandInteractionEvent event) {
        Member userToAdd = event.getOption("user").getAsMember();
        if (userToAdd == null || !(event.getChannel() instanceof TextChannel channel)) {
            event.reply("Nie można dodać użytkownika.").setEphemeral(true).queue();
            return;
        }

        if (isTicketChannel(channel)) {
            channel.upsertPermissionOverride(userToAdd)
                    .setAllowed(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS)
                    .queue();
            event.reply("Dodano " + userToAdd.getAsMention() + " do ticketa.").setEphemeral(true).queue();
        } else {
            event.reply("To nie jest kanał ticketa!").setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("create_ticket")) {
            showCategoryMenu(event);
        } else if (event.getComponentId().equals("close_ticket")) {
            handleCloseButton(event);
        }
    }

    private void showCategoryMenu(ButtonInteractionEvent event) {
        StringSelectMenu menu = StringSelectMenu.create("ticket_category")
                .addOption("Pomoc Ogólna", "help", Emoji.fromUnicode("❓"))
                .addOption("Backup", "backup", Emoji.fromUnicode("💾"))
                .addOption("Partnerstwo", "partnership", Emoji.fromUnicode("🤝"))
                .addOption("Media", "media", Emoji.fromUnicode("📷"))
                .addOption("Zgłoszenie Gracza", "player_report", Emoji.fromUnicode("⚠️"))
                .addOption("Sprawa Discord", "discord", Emoji.fromUnicode("📱"))
                .addOption("Do Zarządu", "management", Emoji.fromUnicode("👑"))
                .build();
        event.reply("Wybierz Kategorię:").addActionRow(menu).setEphemeral(true).queue();
    }

    private void handleCloseButton(ButtonInteractionEvent event) {
        if (!(event.getChannel() instanceof TextChannel channel)) {
            event.reply("Ta akcja może być wykonana tylko w kanale ticketa!").setEphemeral(true).queue();
            return;
        }

        if (closingTickets.containsKey(channel.getId())) {
            event.reply("Ten ticket jest już zaplanowany do zamknięcia!").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🔒 Zamknięcie Ticketa")
                .setDescription("Ten ticket zostanie zamknięty za 10 sekund.")
                .setColor(Color.RED)
                .setImage(EMBED_IMAGE_URL);

        event.replyEmbeds(embed.build()).queue();
        scheduleChannelDeletion(channel, event.getMember());
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("ticket_category")) {
            String category = event.getValues().get(0);

            if (category.equals("player_report")) {
                createTicketChannel(event, category, null);
            } else {
                showProblemDescriptionModal(event, category);
            }
        }
    }

    private void showProblemDescriptionModal(StringSelectInteractionEvent event, String category) {
        TextInput problemDescription = TextInput.create("problem_description", "Opisz swój problem", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Opisz dokładnie swój problem...")
                .setRequired(true)
                .build();

        String modalTitle = switch (category) {
            case "help" -> "Pomoc Ogólna";
            case "backup" -> "Backup";
            case "partnership" -> "Partnerstwo";
            case "media" -> "Media";
            case "discord" -> "Sprawa Discord";
            case "management" -> "Do Zarządu";
            default -> "Ticket";
        };

        Modal modal = Modal.create("ticket_modal_" + category, modalTitle)
                .addActionRow(problemDescription)
                .build();

        event.replyModal(modal).queue();
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().startsWith("ticket_modal_")) {
            String category = event.getModalId().replace("ticket_modal_", "");
            String problemDescription = event.getValue("problem_description").getAsString();
            createTicketChannel(event, category, problemDescription);
        }
    }

    private void createTicketChannel(Interaction interaction, String category, String problemDescription) {
        String categoryName = getCategoryName(category);
        Guild guild = interaction.getGuild();
        Member member = interaction.getMember();
        User user = interaction.getUser();

        Category ticketCategory = guild.getCategoriesByName(TICKET_CATEGORY_NAME, true).stream()
                .findFirst()
                .orElseGet(() -> guild.createCategory(TICKET_CATEGORY_NAME).complete());

        String channelName = user.getName() + "-" + categoryName.replace(" ", "-").toLowerCase();
        TextChannel ticketChannel = ticketCategory.createTextChannel(channelName)
                .setTopic("Ticket otworzony przez " + user.getId())
                .complete();

        setupChannelPermissions(ticketChannel, member, guild);
        sendWelcomeMessage(ticketChannel, user, categoryName, problemDescription);
        sendCreationResponse(interaction, ticketChannel);
    }

    private String getCategoryName(String category) {
        return switch (category) {
            case "help" -> "Pomoc Ogólna";
            case "backup" -> "Backup";
            case "partnership" -> "Partnerstwo";
            case "media" -> "Media";
            case "player_report" -> "Zgłoszenie Gracza";
            case "discord" -> "Sprawa Discord";
            case "management" -> "Do Zarządu";
            default -> "Inne";
        };
    }

    private void setupChannelPermissions(TextChannel channel, Member member, Guild guild) {
        channel.upsertPermissionOverride(member)
                .setAllowed(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS)
                .queue();

        channel.upsertPermissionOverride(guild.getPublicRole())
                .setDenied(Permission.VIEW_CHANNEL)
                .queue();
    }


    private void sendWelcomeMessage(TextChannel channel, User user, String categoryName, String problemDescription) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🎫 • Tickety - Informacje")
                .setDescription("Zgłoszenie Utworzył: " + user.getAsMention() + "\n" +
                        "Kategoria: " + categoryName + "\n\n" +
                        (problemDescription != null ? "Opis problemu: " + problemDescription + "\n\n" : "") +
                        "Poczekaj na reakcję administracji. Możesz ich dodać do ticketa komendą `/dodaj`.\n")
                .setColor(new Color(97, 0, 194))
                .setImage(EMBED_IMAGE_URL);

        Button closeButton = Button.danger("close_ticket", "Zamknij Ticket").withEmoji(Emoji.fromUnicode("🔒"));
        channel.sendMessageEmbeds(embed.build()).setActionRow(closeButton).queue();

        sendTicketLog(channel.getGuild(), user, "Ticket Otwarty", channel.getName(), Instant.now(), categoryName, problemDescription);
    }

    private void sendCreationResponse(Interaction interaction, TextChannel channel) {
        String successMessage = "Ticket został utworzony: " + channel.getAsMention();

        if (interaction instanceof ModalInteractionEvent event) {
            event.reply(successMessage).setEphemeral(true).queue();
        } else if (interaction instanceof StringSelectInteractionEvent event) {
            // Edytuje wiadomość z menu, zastępując ją potwierdzeniem. Lepsze UX.
            event.editMessage(successMessage).setComponents().queue();
        }
    }

    private void sendTicketLog(Guild guild, User user, String title, String channelName,
                               Instant timestamp, String category, String description) {
        TextChannel logChannel = guild.getTextChannelById(LOG_CHANNEL_ID);
        if (logChannel == null) {
            logger.warn("Log channel with ID {} not found.", LOG_CHANNEL_ID);
            return;
        }

        EmbedBuilder logEmbed = new EmbedBuilder()
                .setTitle("📩 • " + title)
                .setDescription("**Użytkownik:** " + user.getAsMention() + "\n" +
                        "**Kanał:** #" + channelName + "\n" +
                        "**Kategoria:** " + category + "\n" +
                        (description != null ? "**Opis:** " + description + "\n" : "") +
                        "**Data:** <t:" + timestamp.getEpochSecond() + ":F>")
                .setColor(title.contains("Zamknięty") ? Color.RED : new Color(97, 0, 194))
                .setTimestamp(timestamp)
                .setImage(EMBED_IMAGE_URL);

        logChannel.sendMessageEmbeds(logEmbed.build()).queue(
                null, throwable -> logger.error("Nie udało się wysłać logu: {}", throwable.getMessage()));
    }
}