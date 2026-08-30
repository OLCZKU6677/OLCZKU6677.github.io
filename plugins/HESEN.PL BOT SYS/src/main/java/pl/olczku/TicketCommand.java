package pl.olczku;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TicketCommand extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TicketCommand.class);
    private static final String LOG_CHANNEL_ID = "1396860347398357103";
    private static final String EMBED_IMAGE_URL = "https://cdn.discordapp.com/attachments/1325878026759573597/1396849880407740486/image.png?ex=687f9553&is=687e43d3&hm=142c64033904f3e208c165a63eaacc97d0251d3667d56214f7c46cdd28ba9553&";
    private static final String SUPPORT_ROLE_ID = "1371581040073904179";
    private static final String DEFAULT_TICKET_CATEGORY = "Tickety";

    private final Map<String, String> categoryMappings = new HashMap<>();
    private final ConcurrentHashMap<String, ScheduledExecutorService> closingTickets = new ConcurrentHashMap<>();

    public TicketCommand() {
        categoryMappings.put("bug_report", "HESEN.PL » ZNALAZŁEM BŁĄD");
        categoryMappings.put("web_issue", "HESEN.PL » PROBLEM WWW");
        categoryMappings.put("backup", "HESEN.PL » BACKUP");
        categoryMappings.put("cheater_report", "HESEN.PL » ZGŁOŚ CHEATERA");
        categoryMappings.put("unban_appeal", "HESEN.PL » NIESŁUSZNY BAN");
        categoryMappings.put("discord_issue", "HESEN.PL » DISCORD");
        categoryMappings.put("account_issue", "HESEN.PL » PROBLEM Z KONTEM");
        categoryMappings.put("other", "HESEN.PL » INNE");
        categoryMappings.put("partnership", "HESEN.PL » PARTNERSTWO");
    }

    private boolean hasTicketPermissions(Member member) {
        if (member == null) return false;
        if (member.hasPermission(Permission.ADMINISTRATOR)) return true;
        return member.getRoles().stream().anyMatch(role -> role.getId().equals(SUPPORT_ROLE_ID));
    }

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
                .setTitle("` HESEN.PL x CENTRUM POMOCY `")
                .setColor(new Color(255, 255, 255))
                .setDescription(
                        "> Aby stworzyć ticket, by móc szybko skontaktować się z Administracją naciśnij przycisk poniżej.\n\n" +
                                "> Czas oczekiwania na odpowiedź, może trwać do godziny.\n" +
                                "> Wysyłanie bezsensownych ticketów, bedzię karane mutem.\n\n" +
                                "> Stwórz zgłoszenie z odpowiednią kategorią i z cierpliwością poczekaj na odpowiedź administracji.\n"
                )
                .setImage(EMBED_IMAGE_URL);

        StringSelectMenu menu = StringSelectMenu.create("ticket_category")
                .setPlaceholder("Wybierz rodzaj ticketu")
                .addOption("Znalazłem Błąd", "bug_report", Emoji.fromUnicode("🐞"))
                .addOption("Problem WWW", "web_issue", Emoji.fromUnicode("🌐"))
                .addOption("Backup", "backup", Emoji.fromUnicode("📦"))
                .addOption("Zgłoś Cheatera", "cheater_report", Emoji.fromUnicode("🛡️"))
                .addOption("Niesłuszny Ban", "unban_appeal", Emoji.fromUnicode("⚖️"))
                .addOption("Sprawa Discord", "discord_issue", Emoji.fromUnicode("💬"))
                .addOption("Problem z Kontem", "account_issue", Emoji.fromUnicode("👤"))
                .addOption("Inne", "other", Emoji.fromUnicode("💡"))
                .addOption("Partnerstwo", "partnership", Emoji.fromUnicode("🤝"))
                .build();

        event.replyEmbeds(embed.build()).addActionRow(menu).queue();
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("ticket_category")) {
            String category = event.getValues().get(0);
            if (category.equals("cheater_report")) {
                createTicketChannel(event, category, null);
            } else {
                showProblemDescriptionModal(event, category);
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().startsWith("ticket_modal_")) {
            String category = event.getModalId().replace("ticket_modal_", "");
            String problemDescription = event.getValue("problem_description").getAsString();
            createTicketChannel(event, category, problemDescription);
        }
    }

    private void createTicketChannel(Interaction interaction, String categoryValue, String problemDescription) {
        Guild guild = interaction.getGuild();
        if (guild == null) return;
        Member member = interaction.getMember();
        if (member == null) return;
        User user = interaction.getUser();

        String targetCategoryName = categoryMappings.getOrDefault(categoryValue, DEFAULT_TICKET_CATEGORY);
        Category ticketCategory = guild.getCategoriesByName(targetCategoryName, true).stream()
                .findFirst()
                .orElseGet(() -> {
                    logger.info("Creating Discord category '{}' as it was not found.", targetCategoryName);
                    return guild.createCategory(targetCategoryName).complete();
                });

        String channelName = user.getName() + "-" + getCategoryName(categoryValue).replace(" ", "-").toLowerCase();
        TextChannel ticketChannel = ticketCategory.createTextChannel(channelName)
                .setTopic("Ticket otworzony przez " + user.getId() + " | Kategoria: " + getCategoryName(categoryValue))
                .complete();

        setupChannelPermissions(ticketChannel, member, guild);
        sendWelcomeMessage(ticketChannel, user, getCategoryName(categoryValue), problemDescription);
        sendCreationResponse(interaction, ticketChannel);
    }

    private void sendWelcomeMessage(TextChannel channel, User user, String categoryName, String problemDescription) {
        String supportRoleMention = "<@&" + SUPPORT_ROLE_ID + ">";

        StringBuilder descriptionBuilder = new StringBuilder();
        descriptionBuilder.append("Zgłoszenie Utworzył: ").append(user.getAsMention()).append(" ").append(supportRoleMention).append("\n");
        descriptionBuilder.append("Kategoria: **").append(categoryName).append("**\n\n");

        if (problemDescription != null && !problemDescription.isEmpty()) {
            descriptionBuilder.append("Opis problemu: \n```\n").append(problemDescription).append("\n```\n\n");
        }

        descriptionBuilder.append("*Administracja Niedługo Ci Odpisze.*");

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(new Color(255, 255, 255))
                .setTimestamp(Instant.now())
                .setDescription(descriptionBuilder.toString())
                .setImage(EMBED_IMAGE_URL);

        Button closeButton = Button.danger("close_ticket", "Zamknij Ticket").withEmoji(Emoji.fromUnicode("🔒"));

        channel.sendMessageEmbeds(embed.build()).setActionRow(closeButton).queue();

        sendTicketLog(channel.getGuild(), user, "Ticket Otwarty", channel.getName(), Instant.now(), categoryName, problemDescription);
    }

    private void showProblemDescriptionModal(StringSelectInteractionEvent event, String category) {
        TextInput problemDescription = TextInput.create("problem_description", "Opisz swój problem", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Opisz dokładnie swoją sprawę...")
                .setRequired(true)
                .build();
        String modalTitle = getCategoryName(category);
        Modal modal = Modal.create("ticket_modal_" + category, modalTitle)
                .addActionRow(problemDescription)
                .build();
        event.replyModal(modal).queue();
    }

    private String getCategoryName(String categoryValue) {
        return switch (categoryValue) {
            case "bug_report" -> "Znalazłem Błąd";
            case "web_issue" -> "Problem WWW";
            case "backup" -> "Backup";
            case "cheater_report" -> "Zgłoś Cheatera";
            case "unban_appeal" -> "Niesłuszny Ban";
            case "discord_issue" -> "Sprawa Discord";
            case "account_issue" -> "Problem z Kontem";
            case "other" -> "Inne";
            case "partnership" -> "Partnerstwo";
            default -> "Nieznana";
        };
    }

    private void setupChannelPermissions(TextChannel channel, Member member, Guild guild) {
        Role supportRole = guild.getRoleById(SUPPORT_ROLE_ID);
        channel.upsertPermissionOverride(member)
                .setAllowed(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS)
                .queue();
        if (supportRole != null) {
            channel.upsertPermissionOverride(supportRole)
                    .setAllowed(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY)
                    .queue();
        } else {
            logger.warn("Support role with ID {} not found. Staff will not have access to new tickets.", SUPPORT_ROLE_ID);
        }
        channel.upsertPermissionOverride(guild.getPublicRole())
                .setDenied(Permission.VIEW_CHANNEL)
                .queue();
    }

    private void sendCreationResponse(Interaction interaction, TextChannel channel) {
        String successMessage = "✅ Ticket został utworzony: " + channel.getAsMention();
        if (interaction instanceof ModalInteractionEvent event) {
            event.reply(successMessage).setEphemeral(true).queue();
        } else if (interaction instanceof StringSelectInteractionEvent event) {
            event.getHook().sendMessage(successMessage).setEphemeral(true).queue();
        }
    }

    private void handleCloseCommand(SlashCommandInteractionEvent event) {
        if (!hasTicketPermissions(event.getMember())) {
            event.reply("❌ Nie masz uprawnień, aby użyć tej komendy.").setEphemeral(true).queue();
            return;
        }
        if (!(event.getChannel() instanceof TextChannel channel)) {
            event.reply("Ta komenda może być użyta tylko w kanale ticketa!").setEphemeral(true).queue();
            return;
        }
        if (channel.getParentCategory() == null || !channel.getParentCategory().getName().startsWith("HESEN.PL »")) {
            event.reply("To nie jest kanał ticketa!").setEphemeral(true).queue();
            return;
        }
        if (closingTickets.containsKey(channel.getId())) {
            event.reply("Ten ticket jest już zaplanowany do zamknięcia!").setEphemeral(true).queue();
            return;
        }
        sendClosingEmbed(event, channel);
    }

    private void sendClosingEmbed(SlashCommandInteractionEvent event, TextChannel channel) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🔒 Zamknięcie Ticketa")
                .setDescription("Ten ticket zostanie zamknięty za 10 sekund.")
                .setColor(Color.RED);
        event.replyEmbeds(embed.build()).queue();
        scheduleChannelDeletion(channel, event.getMember());
    }

    private void scheduleChannelDeletion(TextChannel channel, Member member) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        closingTickets.put(channel.getId(), scheduler);
        scheduler.schedule(() -> {
            if (closingTickets.remove(channel.getId()) != null) {
                Category parentCategory = channel.getParentCategory();
                sendTicketLog(channel.getGuild(), member.getUser(), "Ticket Zamknięty", channel.getName(), Instant.now(), getCategoryFromTopic(channel.getTopic()), null);
                channel.delete().queue(success -> {
                    if (parentCategory != null && parentCategory.getChannels().isEmpty()) {
                        if (parentCategory.getName().startsWith("HESEN.PL »")) {
                            logger.info("Ticket category '{}' is now empty, deleting it.", parentCategory.getName());
                            parentCategory.delete().queue();
                        }
                    }
                }, throwable -> {
                    logger.error("Nie udało się usunąć kanału: {}", throwable.getMessage());
                });
            }
        }, 10, TimeUnit.SECONDS);
    }

    private String getCategoryFromTopic(String topic) {
        if (topic != null && topic.contains("Kategoria: ")) {
            try {
                return topic.substring(topic.indexOf("Kategoria: ") + 11).trim();
            } catch (Exception e) {
                logger.error("Could not parse category from topic: {}", topic, e);
                return "Nieznana";
            }
        }
        return "Nieznana";
    }

    private void handleCloseStopCommand(SlashCommandInteractionEvent event) {
        if (!hasTicketPermissions(event.getMember())) {
            event.reply("❌ Nie masz uprawnień, aby użyć tej komendy.").setEphemeral(true).queue();
            return;
        }
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
        if (!hasTicketPermissions(event.getMember())) {
            event.reply("❌ Nie masz uprawnień, aby użyć tej komendy.").setEphemeral(true).queue();
            return;
        }
        Member userToAdd = event.getOption("user").getAsMember();
        if (userToAdd == null || !(event.getChannel() instanceof TextChannel channel)) {
            event.reply("Nie można dodać użytkownika.").setEphemeral(true).queue();
            return;
        }
        if (channel.getParentCategory() != null && channel.getParentCategory().getName().startsWith("HESEN.PL »")) {
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
        if (event.getComponentId().equals("close_ticket")) {
            handleCloseButton(event);
        }
    }

    private void handleCloseButton(ButtonInteractionEvent event) {
        if (!hasTicketPermissions(event.getMember())) {
            event.reply("❌ Nie masz uprawnień, aby zamknąć ten ticket.").setEphemeral(true).queue();
            return;
        }
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
                .setColor(Color.RED);
        event.replyEmbeds(embed.build()).queue();
        scheduleChannelDeletion(channel, event.getMember());
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
                .setColor(title.contains("Zamknięty") ? Color.RED : new Color(227, 198, 6))
                .setImage(EMBED_IMAGE_URL)
                .setTimestamp(timestamp);
        logChannel.sendMessageEmbeds(logEmbed.build()).queue(
                null, throwable -> logger.error("Nie udało się wysłać logu: {}", throwable.getMessage()));
    }
}