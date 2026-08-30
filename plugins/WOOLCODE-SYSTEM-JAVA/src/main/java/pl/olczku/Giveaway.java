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
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Giveaway extends ListenerAdapter {

    private final List<Member> participants = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("giveaway")) {
            Member member = event.getMember();
            if (member == null || !member.getRoles().stream().anyMatch(role -> role.getId().equals("1354478144811176027"))) {
                event.reply("Nie masz uprawnień do używania tej komendy.").setEphemeral(true).queue();
                return;
            }
            String prize = event.getOption("prize").getAsString();
            long winnersCount = event.getOption("winners").getAsLong();
            long duration = event.getOption("time").getAsLong();

            Instant endTime = Instant.now().plusSeconds(duration * 3600);

            EmbedBuilder giveawayEmbed = new EmbedBuilder();
            giveawayEmbed.setDescription("**Rozpoczęto giveaway**\n" +
                    "```Nagroda: " + prize + "```\n" +
                    "```Liczba zwycięzców: " + winnersCount + "```\n" +
                    "```Czas trwania: " + duration + " godzin```\n" +
                    "Aby dołączyć kliknij przycisk poniżej\n" +
                    "Jeśli masz jakieś pytania, skontaktuj się z administracją\n" +
                    "Możesz w tym celu stworzyć ticketa na kanale https://discord.com/channels/1280903359422922845/1304178876720873502");
            giveawayEmbed.setTimestamp(endTime);
            giveawayEmbed.setFooter("Pozostały czas: " + duration + " godzin");
            giveawayEmbed.setColor(Color.GREEN);

            event.replyEmbeds(giveawayEmbed.build())
                    .addActionRow(Button.primary("giveaway", "Dołącz do giveaway"))
                    .queue(message -> {
                        String messageId = message.getId();
                        scheduleCountdownUpdate(event.getGuild(), messageId, endTime);
                    });

            event.getJDA().getGatewayPool().schedule(() -> endGiveaway(event.getGuild(), prize, winnersCount), duration, TimeUnit.HOURS);
        }
    }

    private void scheduleCountdownUpdate(Guild guild, String messageId, Instant endTime) {
        scheduler.scheduleAtFixedRate(() -> {
            long remainingSeconds = Duration.between(Instant.now(), endTime).getSeconds();
            if (remainingSeconds <= 0) {
                scheduler.shutdown();
                return;
            }
            long hours = remainingSeconds / 3600;
            long minutes = (remainingSeconds % 3600) / 60;
            long seconds = remainingSeconds % 60;

            EmbedBuilder updatedEmbed = new EmbedBuilder();
            updatedEmbed.setFooter(String.format("Pozostały czas: %02d:%02d:%02d", hours, minutes, seconds));
            updatedEmbed.setTimestamp(endTime);

            guild.getTextChannelsByName("giveaway", true).get(0).retrieveMessageById(messageId).queue(message -> {
                message.editMessageEmbeds(updatedEmbed.build()).queue();
            });
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().equals("giveaway")) {
            Member member = event.getMember();
            if (member != null && !participants.contains(member)) {
                participants.add(member);
                event.reply("✅ Pomyślnie dołączono do giveawaya").setEphemeral(true).queue();
            } else {
                event.reply("❌ Już jesteś zapisany do giveawaya").setEphemeral(true).queue();
            }
        }
    }

    private void endGiveaway(Guild guild, String prize, long winnersCount) {
        if (participants.isEmpty()) {
            guild.getTextChannelsByName("giveaway", true).get(0).sendMessage("Nikt nie dołączył do giveawaya.").queue();
            return;
        }

        Random random = new Random();
        List<Member> winners = new ArrayList<>();
        for (int i = 0; i < winnersCount && !participants.isEmpty(); i++) {
            int winnerIndex = random.nextInt(participants.size());
            winners.add(participants.remove(winnerIndex));
        }

        EmbedBuilder resultEmbed = new EmbedBuilder();
        resultEmbed.setTitle("🎉 Giveaway zakończony!");
        resultEmbed.setDescription("Nagroda: " + prize + "\n" +
                "Zwycięzcy: " + winners.stream().map(Member::getAsMention).reduce((a, b) -> a + ", " + b).orElse("Brak"));

        guild.getTextChannelsByName("giveaway", true).get(0).sendMessageEmbeds(resultEmbed.build()).queue();
    }
}