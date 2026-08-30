package pl.olczku;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClearCommand extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ClearCommand.class);

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("clear")) {
            Member member = event.getMember();
            if (member == null || !member.getRoles().stream().anyMatch(role -> role.getId().equals("1354158165574291779"))) {
                event.reply("Nie masz uprawnień do używania tej komendy.").setEphemeral(true).queue();
                return;
            }

            int amount = event.getOption("amount").getAsInt();
            if (amount < 1 || amount > 10) {
                event.reply("Proszę podać liczbę między 1 a 100.").setEphemeral(true).queue();
                return;
            }

            event.deferReply(true).queue(); // Acknowledge the command

            if (event.getChannel() instanceof TextChannel) {
                TextChannel textChannel = (TextChannel) event.getChannel();
                textChannel.getHistory().retrievePast(amount).queue(messages -> {
                    textChannel.deleteMessages(messages).queue(
                            success -> event.getHook().sendMessage("Usunięto " + messages.size() + " wiadomości.").queue(),
                            error -> event.getHook().sendMessage("Nie udało się usunąć wiadomości.").queue()
                    );
                });
            } else {
                event.getHook().sendMessage("Ta komenda może być używana tylko w kanałach tekstowych.").queue();
            }
        }
    }

    public static class Embeds extends ListenerAdapter {

        @Override
        public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
            if (e.getName().equals("embed")) {
                // Sprawdź, czy użytkownik ma wymaganą rolę
                if (e.getMember() == null || !e.getMember().getRoles().stream().anyMatch(role -> role.getId().equals("1346205264008708219"))) {
                    e.reply("Nie masz uprawnień do używania tej komendy.").setEphemeral(true).queue();
                    return;
                }

                // Pobierz opcję "embed"
                String embedType = e.getOption("embed").getAsString();

                // Tworzenie odpowiedniego embeda w zależności od wybranej opcji
                switch (embedType) {
                    case "boost":
                        EmbedBuilder boostEmbed = new EmbedBuilder();
                        boostEmbed.setDescription("``` WOOLCODE BOOST ``` \n" +
                                "Boostuj serwer, aby otrzymać dostęp do specjalnych kanałów i rang.\n" +
                                "```Wymagania``` \n" +
                                "1. Posiadanie rangi `Booster`\n" +
                                "2. Posiadanie boosta na serwerze\n");
                        boostEmbed.setImage("https://cdn.discordapp.com/attachments/134651828695465989/1346518286954659890/unknown.png");
                        e.replyEmbeds(boostEmbed.build()).queue();
                        break;

                    case "rules":
                        EmbedBuilder rulesEmbed = new EmbedBuilder();
                        rulesEmbed.setDescription("``` WOOLCODE REGULAMIN ``` \n" +
                                "Boostuj serwer, aby otrzymać dostęp do specjalnych kanałów i rang.\n" +
                                "```Wymagania``` \n" +
                                "1. Posiadanie rangi `Booster`\n" +
                                "2. Posiadanie boosta na serwerze\n");
                        rulesEmbed.setImage("https://cdn.discordapp.com/attachments/134651828695465989/1346518286954659890/unknown.png");
                        e.replyEmbeds(rulesEmbed.build()).queue();
                        break;

                    case "wymagania":
                        EmbedBuilder wymaganiaEmbed = new EmbedBuilder();
                        wymaganiaEmbed.setDescription("``` WOOLCODE WYMAGANIA ``` \n" +
                                "Boostuj serwer, aby otrzymać dostęp do specjalnych kanałów i rang.\n" +
                                "```Wymagania``` \n" +
                                "1. Zostaw subskrypcje na kanale **`[WoolCode](https://www.youtube.com/@WoolCode)`**\n" +
                                "2. Zaproszenie dwóch osób które dołączyły na serwer\n");
                        wymaganiaEmbed.setImage("https://cdn.discordapp.com/attachments/134651828695465989/1346518286954659890/unknown.png");
                        e.replyEmbeds(wymaganiaEmbed.build()).queue();
                        break;

                    case "rekrutacja":
                        EmbedBuilder rekrutacjaEmbed = new EmbedBuilder();
                        rekrutacjaEmbed.setDescription("```WoolCode - Rekrutacja```\n" +
                                "Chciałbyś dołączyć do naszego zespołu administracji? \n" +
                                "Poszukujemy osób pełnych pasji, które mają czas, będą zaangażowane i pomogą rozwijać nasz projekt. \n" +
                                "Jeśli jesteś zainteresowany, chcesz zdobyć nowe umiejętności i pomagać innym – zgłoś się do nas! \n" +
                                "Razem możemy stworzyć coś naprawdę wyjątkowego 🎉 \n" +
                                " \n" +
                                "```🦺 WoolCode - Dostępne Role:```   \n" +
                                "• SUPPORT \n" +
                                "• GRAFIK  \n" +
                                "• MONTAŻYSTA   \n" +
                                "• JAVA DEV   \n" +
                                "• DEV WWW   \n" +
                                "• BUDOWNICZY   \n" +
                                "• SCRIPT CREATOR   \n" +
                                "• CONFIG CREATOR   \n" +
                                "• PACK CREATOR   \n" +
                                "   \n" +
                                "```🔩 WoolCode - Wymagania:```\n" +
                                "• Min. 13 lat   \n" +
                                "• Być ogarniętym w rolę, na którą się rekrutujesz \n" +
                                "• Mieć przygotowane Portfolio lub CV   \n" +
                                "• Nie kłamać o swoich danych   \n" +
                                "• Posiadać dobry mikrofon   \n" +
                                "```🌴 WoolCode - Informacje```   \n" +
                                "⚠ Spełniasz wymagania i chcesz rozwijać społeczność WoolCode?   \n" +
                                "Nie czekaj, pisz podanie!   \n" +
                                "```Proces rekrutacyjny składa się z 3 etapów:```   \n" +
                                "\n" +
                                "📝 **I Etap** – Wypełnienie formularza   \n" +
                                "🎙 **II Etap** – Rozmowa na kanale głosowym   \n" +
                                "🔧 **III Etap** – Sprawdzenie umiejętności   \n" +
                                "🔔 **IV Etap (awaryjny)** – Dodatkowa ocena, jeśli nie mamy pewności \n");
                        rekrutacjaEmbed.setImage("https://cdn.discordapp.com/attachments/134651828695465989/1346518286954659890/unknown.png");
                        e.replyEmbeds(rekrutacjaEmbed.build()).queue();
                        break;

                    default:
                        e.reply("Nieznany typ embeda.").setEphemeral(true).queue();
                        break;
                }
            }
        }
    }
}