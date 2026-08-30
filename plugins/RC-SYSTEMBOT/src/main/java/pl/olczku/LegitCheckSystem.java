package pl.olczku;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.*;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LegitCheckSystem extends ListenerAdapter {
    private final Logger logger = LoggerFactory.getLogger(LegitCheckSystem.class);
    private final long targetChannelId;

    // Wzorzec: łapie prefix (G1), opcjonalny myślnik i numer (G2) na końcu
    private static final Pattern CHANNEL_NAME_PATTERN = Pattern.compile("(.+?)-?(\\d+)$");

    public LegitCheckSystem(Properties config) {
        String idString = config.getProperty("legitcheck.channel.id");
        if (idString != null && !idString.isBlank()) {
            this.targetChannelId = Long.parseLong(idString);
        } else {
            this.targetChannelId = 0;
            logger.error("Brak konfiguracji 'legitcheck.channel.id'. System LegitCheck nie będzie działał.");
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        // Sprawdzenie, czy wiadomość jest na kanale docelowym
        if (event.getChannel().getIdLong() == targetChannelId) {
            TextChannel channel = event.getChannel().asTextChannel();

            // 1. WYSYŁKA NOWEJ WIADOMOŚCI (Akcja niezależna od zmiany nazwy)
            sendWelcomeEmbed(channel);

            // 2. ZMIANA NAZWY KANAŁU (Akcja niezależna, podlega Rate Limit)
            changeChannelName(channel);
        }
    }

    private void changeChannelName(TextChannel channel) {
        String currentName = channel.getName();
        Matcher matcher = CHANNEL_NAME_PATTERN.matcher(currentName);

        if (matcher.find()) {
            try {
                String prefix = matcher.group(1);
                int currentNumber = Integer.parseInt(matcher.group(2));

                int nextNumber = currentNumber + 1;
                String newName = prefix.endsWith("-") ? prefix + nextNumber : prefix + "-" + nextNumber;

                logger.info("Zmiana nazwy: {} -> {}", currentName, newName);

                // Edycja nazwy kanału. Operacja wymaga uprawnienia "Zarządzanie kanałami".
                channel.getManager().setName(newName).queue(
                        (v) -> logger.info("Zmieniono nazwę kanału na {}.", newName),
                        (error) -> logger.error("Nie udało się zmienić nazwy kanału {} (Sprawdź uprawnienia!): {}", currentName, error.getMessage())
                );
            } catch (NumberFormatException e) {
                logger.error("Błąd parsowania numeru z nazwy kanału: {}", currentName);
            }
        } else {
            logger.warn("Nazwa kanału {} nie pasuje do wzorca 'prefix-liczba' i nie zostanie zmieniona.", currentName);
        }
    }

    private void sendWelcomeEmbed(TextChannel channel) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(107,219,161));


        String description =
                "```Realm Code × LegitCheck``` \n" +
                "**Wzór:**\n" +
                        "`+rep @administrator sprzedający 1x [produkt] [cena] PLN [metoda płatności]`\n\n" +
                        "**Przykład:**\n" +
                        "`+rep @olczku_ 1x Plugin Minecraft 20 PLN [BLIK]`\n";

        embed.setImage("https://cdn.discordapp.com/attachments/1407053232458694760/1424436219747631254/bannerLegit.jpg?ex=68e3f11b&is=68e29f9b&hm=cd9ee62e6fbdfe97e0a4611c036a8a57f8680f8f3ffc3e0559e9ab9a4698babc&");
        embed.setDescription(description);

        // Wysłanie wiadomości Embed
        channel.sendMessageEmbeds(embed.build()).queue(
                null,
                (error) -> logger.error("Błąd wysyłania powitalnego Embed na kanale {}: {}", channel.getName(), error.getMessage())
        );
    }
}