package pl.olczku;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class RekrutacjaCommand extends ListenerAdapter {

    private static final String RECRUITMENT_CATEGORY_NAME = "DO PODANIA";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("rekru-setup")) return;

        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("❌ Tylko administratorzy mogą użyć tej komendy!").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("📝 Rekrutacja do administracji")
                .setDescription("Kliknij przycisk poniżej, aby rozpocząć proces rekrutacyjny.")
                .setColor(0x3498db)
                .setFooter("Serwer FireNMC", event.getGuild().getIconUrl())
                .setTimestamp(Instant.now());

        event.replyEmbeds(embed.build())
                .addActionRow(Button.primary("start_recruitment", "Rozpocznij rekrutację"))
                .queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getButton().getId();

        if ("start_recruitment".equals(buttonId)) {
            // First part of the recruitment form
            Modal modal = Modal.create("recruitment_form_part1", "Formularz rekrutacyjny - Część 1")
                    .addActionRow(TextInput.create("discord_nick", "Podaj swój nick na Discordzie", TextInputStyle.SHORT)
                            .setPlaceholder("Wprowadź pełne zdanie.")
                            .setRequired(true)
                            .build())
                    .addActionRow(TextInput.create("minecraft_nick", "Podaj swój nick w Minecraft", TextInputStyle.SHORT)
                            .setPlaceholder("Wprowadź pełne zdanie.")
                            .setRequired(true)
                            .build())
                    .build();

            event.replyModal(modal).queue();
        } else if (buttonId.startsWith("accept_recruitment:")) {
            String userId = buttonId.split(":")[1];
            User user = event.getJDA().getUserById(userId);

            if (user != null) {
                user.openPrivateChannel().queue(privateChannel -> {
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("🎉 **Gratulacje!**")
                            .setDescription("Twoje podanie zostało **zaakceptowane** przez administrację serwera FireNMC. Witamy w zespole! 🎊")
                            .setColor(0x2ecc71)
                            .setFooter("Serwer FireNMC", event.getJDA().getSelfUser().getAvatarUrl())
                            .setTimestamp(Instant.now());

                    privateChannel.sendMessageEmbeds(embed.build()).queue();
                });
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("✅ Podanie zostało zaakceptowane!")
                    .setDescription("Gratulacje! Twoje podanie zostało zaakceptowane przez administrację.")
                    .setFooter("Zgłoszenie zamknie się automatycznie za 3 godziny.")
                    .setColor(0x2ecc71)
                    .setTimestamp(Instant.now());

            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            event.getChannel().delete().queueAfter(3, TimeUnit.HOURS);
        } else if (buttonId.startsWith("reject_recruitment:")) {
            String userId = buttonId.split(":")[1];

            // Create a modal for the rejection reason
            Modal modal = Modal.create("reject_reason_form", "Podaj powód odrzucenia")
                    .addActionRow(TextInput.create("reject_reason", "Powód odrzucenia", TextInputStyle.PARAGRAPH)
                            .setPlaceholder("Wprowadź powód odrzucenia podania.")
                            .setRequired(true)
                            .build())
                    .build();

            event.replyModal(modal).queue();
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if ("recruitment_form_part1".equals(event.getModalId())) {
            try {
                String discordNick = event.getValue("discord_nick").getAsString();
                String minecraftNick = event.getValue("minecraft_nick").getAsString();

                // Second part of the recruitment form
                Modal modal = Modal.create("recruitment_form_part2", "Formularz rekrutacyjny - Część 2")
                        .addActionRow(TextInput.create("wady_zalety", "Podaj swoje wady i zalety (minimum po trzy)", TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Wprowadź odpowiedź.")
                                .setRequired(true)
                                .build())
                        .addActionRow(TextInput.create("administrator_serwera", "Czy jesteś lub byłeś administratorem na innym serwerze? Jeśli tak – na jakim?", TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Wprowadź odpowiedź.")
                                .setRequired(true)
                                .build())
                        .addActionRow(TextInput.create("doswiadczenie", "Jak oceniasz swoje doświadczenie jako administrator?", TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Wprowadź odpowiedź.")
                                .setRequired(true)
                                .build())
                        .addActionRow(TextInput.create("wniesienie", "Co możesz wnieść do zespołu?", TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Wprowadź odpowiedź.")
                                .setRequired(true)
                                .build())
                        .addActionRow(TextInput.create("obowiazki", "Wymień znane Ci obowiązki administratora", TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Wprowadź odpowiedź.")
                                .setRequired(true)
                                .build())
                        .addActionRow(TextInput.create("umiejetnosci", "Na ile oceniasz swoje umiejętności sprawdzania graczy? (Skala od 1 do 10)", TextInputStyle.SHORT)
                                .setPlaceholder("Wprowadź odpowiedź.")
                                .setRequired(true)
                                .build())
                        .addActionRow(TextInput.create("backup", "Co zrobisz, jeśli gracz poprosi Cię o backup? (minimum 20 słów)", TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Wprowadź odpowiedź.")
                                .setRequired(true)
                                .build())
                        .addActionRow(TextInput.create("reach", "Opisz krok po kroku, jak przebiega sprawdzanie gracza podejrzanego o używanie REACH", TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Wprowadź odpowiedź.")
                                .setRequired(true)
                                .build())
                        .addActionRow(TextInput.create("bezstronnosc", "Czym według Ciebie jest bezstronność? (wyczerpująca odpowiedź)", TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Wprowadź odpowiedź.")
                                .setRequired(true)
                                .build())
                        .build();

                event.reply(String.valueOf(modal)).queue();

            } catch (Exception e) {
                e.printStackTrace();
                event.reply("❌ Wystąpił błąd podczas przetwarzania formularza: " + e.getMessage()).setEphemeral(true).queue();
            }
        } else if ("recruitment_form_part2".equals(event.getModalId())) {
            try {
                String wadyZalety = event.getValue("wady_zalety").getAsString();
                String administratorSerwera = event.getValue("administrator_serwera").getAsString();
                String doswiadczenie = event.getValue("doswiadczenie").getAsString();
                String wniesienie = event.getValue("wniesienie").getAsString();
                String obowiazki = event.getValue("obowiazki").getAsString();
                String umiejetnosci = event.getValue("umiejetnosci").getAsString();
                String backup = event.getValue("backup").getAsString();
                String reach = event.getValue("reach").getAsString();
                String bezstronnosc = event.getValue("bezstronnosc").getAsString();

                Category category = event.getGuild().getCategoriesByName(RECRUITMENT_CATEGORY_NAME, true).stream().findFirst().orElse(null);
                if (category == null) {
                    event.reply("❌ Wystąpił błąd: Nie można znaleźć kategorii 'DO PODANIA'.").setEphemeral(true).queue();
                    return;
                }

                String channelName = event.getUser().getName() + "-rekrutacja";
                category.createTextChannel(channelName).queue(channel -> {
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("📄 Nowe podanie rekrutacyjne")
                            .setDescription("**Użytkownik:** " + event.getUser().getAsMention())
                            .addField("Wady i zalety", wadyZalety, false)
                            .addField("Administrator na innym serwerze", administratorSerwera, false)
                            .addField("Doświadczenie jako administrator", doswiadczenie, false)
                            .addField("Co może wnieść do zespołu", wniesienie, false)
                            .addField("Obowiązki administratora", obowiazki, false)
                            .addField("Umiejętności sprawdzania graczy", umiejetnosci, false)
                            .addField("Backup", backup, false)
                            .addField("Sprawdzanie REACH", reach, false)
                            .addField("Bezstronność", bezstronnosc, false)
                            .setColor(0x3498db)
                            .setFooter("ID: " + event.getUser().getId())
                            .setTimestamp(Instant.now());

                    channel.sendMessageEmbeds(embed.build())
                            .setActionRow(
                                    Button.success("accept_recruitment:" + event.getUser().getId(), "✅ Zaakceptuj"),
                                    Button.danger("reject_recruitment:" + event.getUser().getId(), "❌ Odrzuć")
                            )
                            .queue();

                    event.reply("✅ Twój formularz rekrutacyjny został wysłany!").setEphemeral(true).queue();
                }, throwable -> {
                    throwable.printStackTrace();
                    event.reply("❌ Wystąpił błąd podczas tworzenia kanału: " + throwable.getMessage()).setEphemeral(true).queue();
                });

            } catch (Exception e) {
                e.printStackTrace();
                event.reply("❌ Wystąpił błąd podczas przetwarzania formularza: " + e.getMessage()).setEphemeral(true).queue();
            }
        } else if ("reject_reason_form".equals(event.getModalId())) {
            try {
                String reason = event.getValue("reject_reason").getAsString();
                String userId = event.getUser().getId();

                User user = event.getJDA().getUserById(userId);

                if (user != null) {
                    user.openPrivateChannel().queue(privateChannel -> {
                        EmbedBuilder embed = new EmbedBuilder()
                                .setTitle("❌ **Niestety...**")
                                .setDescription("Twoje podanie zostało **odrzucone** przez administrację serwera FireNMC.")
                                .addField("Powód:", reason, false)
                                .setColor(0xe74c3c)
                                .setFooter("Serwer FireNMC", event.getJDA().getSelfUser().getAvatarUrl())
                                .setTimestamp(Instant.now());

                        privateChannel.sendMessageEmbeds(embed.build()).queue();
                    });
                }

                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("❌ Podanie zostało odrzucone!")
                        .setDescription("Twoje podanie zostało odrzucone przez administrację.")
                        .addField("Powód:", reason, false)
                        .setFooter("Zgłoszenie zamknie się automatycznie za 3 godziny.")
                        .setColor(0xe74c3c)
                        .setTimestamp(Instant.now());

                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                event.getChannel().delete().queueAfter(3, TimeUnit.HOURS);

            } catch (Exception e) {
                e.printStackTrace();
                event.reply("❌ Wystąpił błąd podczas przetwarzania formularza: " + e.getMessage()).setEphemeral(true).queue();
            }
        }
    }

}