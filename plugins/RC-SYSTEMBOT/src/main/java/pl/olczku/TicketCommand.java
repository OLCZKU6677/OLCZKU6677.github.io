package pl.olczku;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class TicketCommand extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TicketCommand.class);
    private static final String STAFF_ROLE_ID = "1411720334620495882";
    private static final String LOG_CHANNEL_ID = "1411696517466685501";

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().startsWith("ticket_modal_")) {
            String category = event.getModalId().replace("ticket_modal_", "");
            String categoryName = switch (category) {
                case "pomoc-ogolna" -> "Pomoc Ogólna";
                case "problem-z-usluga" -> "Problem z usługą";
                case "wspolpraca" -> "Współpraca";
                case "inne-sprawy" -> "Inne sprawy";
                default -> "Inne";
            };

            String problemDescription = event.getValue("problem_description").getAsString();

            String channelName = "ticket-" + event.getUser().getName().toLowerCase() + "-" + category.toLowerCase();
            Guild guild = event.getGuild();
            Category categoryChannel = guild.getCategoriesByName(categoryName, true).stream().findFirst().orElse(null);
            if (categoryChannel == null) {
                categoryChannel = guild.createCategory(categoryName).complete();
            }
            TextChannel channel = categoryChannel.createTextChannel(channelName).complete();

            channel.upsertPermissionOverride(event.getMember())
                    .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS)
                    .queue();

            Role staffRole = guild.getRoleById(STAFF_ROLE_ID);
            if (staffRole != null) {
                channel.upsertPermissionOverride(staffRole)
                        .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS)
                        .queue();
            }

            channel.upsertPermissionOverride(guild.getPublicRole())
                    .deny(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS)
                    .queue();

            String rolePing = "<@&" + STAFF_ROLE_ID + ">";
            channel.sendMessage(rolePing).queue();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(107,219,161));
            embed.setTitle("```💎 REALMCODE ✖️ TICKET```");

            long currentUnixTime = Instant.now().getEpochSecond();

            String description =
                    "Zgłoszenie utworzył: " + event.getUser().getAsMention() + "\n" +
                            "Kategoria: **" + categoryName + "**\n\n" +
                            "Utworzono: <t:" + currentUnixTime + ":R>\n\n" +
                            "Jaki jest powód otwarcia zgłoszenia?\n" +
                            "```" + problemDescription + "```";

            embed.setDescription(description);
            embed.setImage("https://cdn.discordapp.com/attachments/1422639879548043317/1423367126768418826/bannerTicket.jpg?ex=68e00d6f&is=68debbef&hm=d773ef2c8bbc943fa47e324e29c16a3d5834f324bb9053657710df755f2286c3&");
            embed.setFooter("© 2021 - 2025 • RealmCode.CC");

            Button closeButton = Button.secondary("close_ticket", "🔒 Zamknij zgłoszenie!");

            channel.sendMessageEmbeds(embed.build()).setActionRow(closeButton).queue();


            TextChannel logChannel = guild.getTextChannelById(LOG_CHANNEL_ID);
            if (logChannel != null) {
                EmbedBuilder logEmbed = new EmbedBuilder();
                logEmbed.setTitle("🎫 Ticket Otwarty");
                logEmbed.setDescription("Ticket otwarty przez: " + event.getUser().getAsMention() + "\n" +
                        "Kategoria: " + categoryName + "\n" +
                        "Kanał: " + channel.getAsMention() + "\n" +
                        "Opis problemu: `" + problemDescription + "`\n" +
                        "Data: " + Instant.now());
                logEmbed.setColor(new Color(107,219,161));
                logEmbed.setFooter("RealmCode", "https://i.imgur.com/dKx9sXb.png");
                logChannel.sendMessageEmbeds(logEmbed.build()).queue();
            }

            event.reply("✅ Twój ticket został utworzony: " + channel.getAsMention()).setEphemeral(true).queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("ticket-setup")) {
            event.deferReply(true).queue();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("```💎 REALMCODE ✖️ TICKETY```");

            String description =
                    " > Jeżeli potrzebujesz pomocy, wsparcia lub masz pytania, skorzystaj z opcji **Pomoc Ogólna**.\n\n" +
                            " > Jeżeli chcesz złożyć zamówienie bądź dowiedzieć się o przewidywanych kosztach skorzystaj z poprawnej kategorii w **poniższym menu**.\n\n" +
                            " > Jeżeli jesteś **kupcem** pamiętaj, że pieniądze wysyłasz tylko na dane podane **przez bota**.\n\n" +
                            " > ~ Jako Administracja oraz Zespół prosimy o nie otwieranie zgłoszeń **dla zabawy** oraz o nie pingowanie nas, odpiszemy w wolnej chwili.";

            embed.setDescription(description);
            embed.setColor(new Color(107,219,161));
            embed.setFooter("© 2021 - 2025 • RealmCode.CC ", null);
            embed.setImage("https://cdn.discordapp.com/attachments/1422639879548043317/1423367126768418826/bannerTicket.jpg?ex=68e00d6f&is=68debbef&hm=d773ef2c8bbc943fa47e324e29c16a3d5834f324bb9053657710df755f2286c3&");

            StringSelectMenu menu = StringSelectMenu.create("ticket")
                    .setPlaceholder("Wybierz jedną z opcji którą jesteś zainteresowany...")
                    .addOptions(
                            net.dv8tion.jda.api.interactions.components.selections.SelectOption.of("Pomoc ogólna", "pomoc-ogolna").withDescription("Kliknij, aby wybrać temat ticketa").withEmoji(Emoji.fromCustom("support", 1411702199263821885L, false)),
                            net.dv8tion.jda.api.interactions.components.selections.SelectOption.of("Problem z usługą", "problem-z-usluga").withDescription("Kliknij, aby wybrać temat ticketa").withEmoji(Emoji.fromCustom("developer", 1411702197120667668L, false)),
                            net.dv8tion.jda.api.interactions.components.selections.SelectOption.of("Współpraca", "wspolpraca").withDescription("Kliknij, aby wybrać temat ticketa").withEmoji(Emoji.fromCustom("booster", 1411702225008328735L, false)),
                            net.dv8tion.jda.api.interactions.components.selections.SelectOption.of("Inne sprawy", "inne-sprawy").withDescription("Kliknij, aby wybrać temat ticketa").withEmoji(Emoji.fromCustom("designer2", 1411702217068515420L, false))
                    )
                    .build();

            event.getChannel().sendMessageEmbeds(embed.build()).addActionRow(menu).queue();
        } else if (event.getName().equals("dodaj")) {
            Member userToAdd = event.getOption("user").getAsMember();
            if (userToAdd != null && event.getChannel() instanceof TextChannel) {
                TextChannel channel = (TextChannel) event.getChannel();
                if (channel.getName().startsWith("ticket-")) {
                    channel.upsertPermissionOverride(userToAdd)
                            .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS)
                            .queue();

                    event.reply("✅ Dodano " + userToAdd.getAsMention() + " do ticketa.").setEphemeral(true).queue();
                } else {
                    event.reply("❌ To nie jest kanał ticketa!").setEphemeral(true).queue();
                }
            } else {
                event.reply("❌ Nie można dodać użytkownika.").setEphemeral(true).queue();
            }
        } else if (event.getName().equals("ticketclose")) {
            if (event.getChannel() instanceof TextChannel) {
                TextChannel channel = (TextChannel) event.getChannel();
                if (!channel.getName().startsWith("ticket-")) {
                    event.reply("❌ To nie jest kanał ticketa!").setEphemeral(true).queue();
                    return;
                }
                event.deferReply().queue();
                logTicket(channel, event.getUser());

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("🔒 Ticket Zamknięty");
                embed.setDescription("Ten ticket został zamknięty przez " + event.getUser().getAsMention() + ".\n" +
                        "Kanał zostanie usunięty za `5` sekund.");
                embed.setColor(new Color(244, 67, 54));
                channel.sendMessageEmbeds(embed.build()).queue(message -> {
                    message.getChannel().delete().queueAfter(5, TimeUnit.SECONDS);
                });

                TextChannel logChannel = event.getGuild().getTextChannelById(LOG_CHANNEL_ID);
                if (logChannel != null) {
                    EmbedBuilder logEmbed = new EmbedBuilder();
                    logEmbed.setTitle("🔒 Ticket Zamknięty");
                    logEmbed.setDescription("Ticket zamknięty przez: " + event.getUser().getAsMention() + "\n" +
                            "Kanał: #" + channel.getName() + "\n" +
                            "Data zamknięcia: " + Instant.now());
                    logEmbed.setColor(new Color(244, 67, 54));
                    logEmbed.setFooter("RealmCode", "https://i.imgur.com/dKx9sXb.png");
                    logChannel.sendMessageEmbeds(logEmbed.build()).queue();
                }
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("close_ticket")) {
            if (event.getChannel() instanceof TextChannel) {
                TextChannel channel = (TextChannel) event.getChannel();
                if (!channel.getName().startsWith("ticket-")) {
                    event.reply("❌ To nie jest kanał ticketa!").setEphemeral(true).queue();
                    return;
                }
                event.deferReply().queue();
                logTicket(channel, event.getUser());

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("🔒 Ticket Zamknięty");
                embed.setDescription("Ten ticket został zamknięty przez " + event.getUser().getAsMention() + ".\n" +
                        "Kanał zostanie usunięty za `5` sekund.");
                embed.setColor(new Color(244, 67, 54));
                channel.sendMessageEmbeds(embed.build()).queue(message -> {
                    message.getChannel().delete().queueAfter(5, TimeUnit.SECONDS);
                });

                TextChannel logChannel = event.getGuild().getTextChannelById(LOG_CHANNEL_ID);
                if (logChannel != null) {
                    EmbedBuilder logEmbed = new EmbedBuilder();
                    logEmbed.setTitle("🔒 Ticket Zamknięty");
                    logEmbed.setDescription("Ticket zamknięty przez: " + event.getUser().getAsMention() + "\n" +
                            "Kanał: #" + channel.getName() + "\n" +
                            "Data zamknięcia: " + Instant.now());
                    logEmbed.setColor(new Color(244, 67, 54));
                    logEmbed.setFooter("RealmCode", "https://i.imgur.com/dKx9sXb.png");
                    logChannel.sendMessageEmbeds(logEmbed.build()).queue();
                }
            }
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("ticket")) {
            String category = event.getValues().get(0);
            String categoryName = switch (category) {
                case "pomoc-ogolna" -> "Pomoc Ogólna";
                case "problem-z-usluga" -> "Problem z usługą";
                case "wspolpraca" -> "Współpraca";
                case "inne-sprawy" -> "Inne sprawy";
                default -> "Inne";
            };

            String placeholderText = switch (category) {
                case "pomoc-ogolna" -> "Opisz dokładnie swój problem lub pytanie...";
                case "problem-z-usluga" -> "Opisz problem z usługą: jaka usługa, ID zamówienia, szczegóły, co się stało.";
                case "wspolpraca" -> "Opisz szczegółowo cel współpracy, swoją ofertę i oczekiwania...";
                case "inne-sprawy" -> "Opisz szczegółowo o jaką inną sprawę chodzi...";
                default -> "Opisz dokładnie swój problem...";
            };

            TextInput problemDescription = TextInput.create("problem_description", "Opisz swój problem", TextInputStyle.PARAGRAPH)
                    .setPlaceholder(placeholderText)
                    .setRequired(true)
                    .build();

            Modal modal = Modal.create("ticket_modal_" + category, "Nowy Ticket: " + categoryName)
                    .addActionRow(problemDescription)
                    .build();

            event.replyModal(modal).queue();
        }
    }

    private void logTicket(TextChannel channel, User user) {
        Path logFile = Path.of("ticket_logs.txt");
        String logEntry = "Ticket zamknięty przez: " + user.getAsTag() + " na kanale: " + channel.getName() + " w dniu: " + Instant.now() + "\n";
        try {
            Files.writeString(logFile, logEntry, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error("Błąd logowania ticketa: ", e);
        }
    }
}