package pl.olczku;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DropCommand extends ListenerAdapter {

    // ---------------- KONFIGURACJA ID RÓL I KANAŁÓW -----------------

    // ROLA UPRAWNIONA DO DROPÓW STANDARDOWYCH (lub lepsza)
    private static final String NORMAL_ROLE_ID = "1409187208824357026";

    // ROLA UPRAWNIONA DO DROPÓW PREMIUM (uprawnia też do dropu standardowego)
    private static final String PREMIUM_ROLE_ID = "1409838806286209084";

    // ID KANAŁÓW
    private static final String STANDARD_DROP_CHANNEL_ID = "1424427640156065993";
    private static final String PREMIUM_DROP_CHANNEL_ID = "1411696517466685501";

    private static final long COOLDOWN_HOURS = 6;
    private final Map<String, Long> cooldowns = new HashMap<>();

    // STANDARDOWE NAGRODY (WAGI: Suma = 100)
    private static final Map<String, Reward> STANDARD_REWARDS = Map.of(
            "0%", new Reward(null, 70, "0% Zniżki / Brak Nagrody", true),
            "5%", new Reward("1426288911688269955", 15, "5% Zniżki: Dobry Początek!"),
            "10%", new Reward("1426288972128194716", 5, "10% Zniżki: Jest już lepiej!"),
            "15%", new Reward("1426288991920984255", 3, "15% Zniżki: Naprawdę świetnie!"),
            "20%", new Reward("1426289014293401720", 2, "20% Zniżki: Mega szczęście!"),
            "30pkt", new Reward("1426289043074977843", 2, "30 Punktów"),
            "100pkt", new Reward("1426289062163120138", 2, "100 Punktów"),
            "200pkt", new Reward("1426289086876090449", 0, "200 Punktów"),
            "500pkt", new Reward("1426289125610356809", 1, "500 Punktów - Chyba Oszukałeś!")
    );

    // NAGRODY PREMIUM (WAGI: Suma = 100)
    private static final Map<String, Reward> PREMIUM_REWARDS = Map.of(
            "0%", new Reward(null, 75, "0% Zniżki / Brak Nagrody (Premium)", true), // Zwiększono do 75%
            "5%", new Reward("1426288911688269955", 10, "5% Zniżki: Dobry Początek! (Premium)"), // Zmniejszono
            "10%", new Reward("1426288972128194716", 5, "10% Zniżki: Jest już lepiej! (Premium)"), // Zmniejszono
            "15%", new Reward("1426288991920984255", 3, "15% Zniżki: Naprawdę świetnie! (Premium)"),
            "20%", new Reward("1426289014293401720", 2, "20% Zniżki: Mega szczęście! (Premium)"), // Zmniejszono
            "30pkt", new Reward("1426289043074977843", 2, "30 Punktów (Premium)"),
            "100pkt", new Reward("1426289062163120138", 1, "100 Punktów (Premium)"), // Zmniejszono
            "200pkt", new Reward("1426289086876090449", 1, "200 Punktów (Premium)"),
            "500pkt", new Reward("1426289125610356809", 1, "500 Punktów - Chyba Oszukałeś! (Premium)")
    );

    // ---------------- KOMENDY I NASŁUCHIWANIE -----------------

    public static SlashCommandData getCommandData() {
        return Commands.slash("drop", "Spróbuj swojego szczęścia w dropie (Standard)");
    }

    public static SlashCommandData getDropPremiumCommandData() {
        return Commands.slash("drop-premium", "Spróbuj szczęścia w dropie Premium (Wymagana Rola)");
    }

    public static SlashCommandData getDropInfoCommandData() {
        return Commands.slash("dropinfo", "Informacje o systemie dropów (Standard i Premium)");
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.isAcknowledged()) return;

        try {
            String commandName = event.getName();

            if (!commandName.equals("drop") && !commandName.equals("drop-premium") && !commandName.equals("dropinfo")) {
                return;
            }

            // DeferReply jest zawsze ephemeral (prywatny)
            event.deferReply(true).queue();

            boolean isPremiumCommand = commandName.equals("drop-premium");

            // Walidacja kanału
            String expectedChannelId = isPremiumCommand ? PREMIUM_DROP_CHANNEL_ID : STANDARD_DROP_CHANNEL_ID;
            if (!event.getChannel().getId().equals(expectedChannelId)) {
                sendPrivateEmbed(event, createErrorEmbed(
                        "❌ Błąd: Zły Kanał",
                        "Ta komenda działa tylko na kanale <#" + expectedChannelId + ">.",
                        "Użyj komendy w odpowiednim miejscu.",
                        Color.RED
                ));
                return;
            }

            // Walidacja Ról
            Member member = event.getMember();
            if (member == null) return;

            if (isPremiumCommand) {
                // Do /drop-premium wymaga roli PREMIUM
                if (!hasRole(member, PREMIUM_ROLE_ID)) {
                    sendPrivateEmbed(event, createErrorEmbed(
                            "❌ Błąd: Wymagana Rola Premium",
                            "Do użycia komendy `/drop-premium` wymagana jest rola Premium! (<@&" + PREMIUM_ROLE_ID + ">)",
                            "Zakup lub zdobądź rolę, aby spróbować w dropie Premium.",
                            Color.RED
                    ));
                    return;
                }
            } else {
                // Do /drop (Standard) wymaga roli NORMAL lub PREMIUM
                if (!hasRole(member, NORMAL_ROLE_ID) && !hasRole(member, PREMIUM_ROLE_ID)) {
                    sendPrivateEmbed(event, createErrorEmbed(
                            "❌ Błąd: Wymagana Rola",
                            "Do użycia komendy `/drop` wymagana jest rola Standardowa lub Premium! (<@&" + NORMAL_ROLE_ID + ">)",
                            "Zdobądź rolę, aby spróbować w dropie Standardowym.",
                            Color.RED
                    ));
                    return;
                }
            }

            if (commandName.equals("drop") || commandName.equals("drop-premium")) {
                handleDropCommand(event, isPremiumCommand);
            } else if (commandName.equals("dropinfo")) {
                handleDropInfoCommand(event);
            }
        } catch (Exception e) {
            event.getHook().sendMessage("❌ Wystąpił błąd podczas przetwarzania komendy").setEphemeral(true).queue();
            e.printStackTrace();
        }
    }

    // Sprawdza, czy użytkownik ma określoną rolę
    private boolean hasRole(Member member, String roleId) {
        if (member == null || roleId == null) return false;
        return member.getRoles().stream()
                .anyMatch(role -> role.getId().equals(roleId));
    }

    // ---------------- LOGIKA DROPÓW -----------------

    private void handleDropCommand(SlashCommandInteractionEvent event, boolean isPremium) {
        Member member = event.getMember();
        // Zabezpieczenie na wypadek użycia w DM lub braku membera/guild
        if (member == null || event.getGuild() == null || event.getChannel().getType().isThread() || !event.getChannel().getType().isMessage()) {
            sendPrivateEmbed(event, createErrorEmbed(
                    "❌ Błąd",
                    "Komendy drop działają tylko na kanałach tekstowych na serwerze!",
                    null,
                    Color.RED
            ));
            return;
        }

        String userId = member.getId();

        // Używamy tego samego cooldownu dla obu dropów
        if (cooldowns.containsKey(userId)) {
            long cooldownTime = cooldowns.get(userId);
            long remainingTime = cooldownTime - System.currentTimeMillis();

            if (remainingTime > 0) {
                long hours = TimeUnit.MILLISECONDS.toHours(remainingTime);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTime) % 60;

                sendPrivateEmbed(event, createErrorEmbed(
                        "⏳ Drop - Cooldown",
                        String.format("Musisz poczekać jeszcze **%d godzin** i **%d minut** przed kolejnym dropem!", hours, minutes),
                        null,
                        Color.ORANGE
                ));
                return;
            }
        }

        // Losowanie
        Map<String, Reward> rewardsMap = isPremium ? PREMIUM_REWARDS : STANDARD_REWARDS;
        Reward reward = getRandomReward(rewardsMap);

        // Przyznawanie roli (tylko jeśli rolaId nie jest null)
        if (reward.roleId != null) {
            Role role = event.getGuild().getRoleById(reward.roleId);
            if (role != null) {
                event.getGuild().addRoleToMember(member, role).queue(
                        success -> {},
                        error -> System.err.printf("Błąd dodawania roli %s użytkownikowi %s: %s\n", role.getName(), member.getUser().getName(), error.getMessage())
                );
            } else {
                System.err.printf("Błąd: Rola o ID %s dla nagrody %s nie została znaleziona na serwerze!\n", reward.roleId, reward.description);
            }
        }

        // Ustawienie nowego cooldownu
        long cooldownEndTime = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(COOLDOWN_HOURS);
        cooldowns.put(userId, cooldownEndTime);

        // Konwersja do sekund dla Discord timestamp
        long cooldownEndTimeSeconds = cooldownEndTime / 1000;

        // Konfiguracja embeda
        boolean isWin = !reward.isZeroReward;
        String dropType = isPremium ? "Premium" : "Standardowy";

        EmbedBuilder publicEmbed = new EmbedBuilder()
                .setTitle((isWin ? "🎉 WYNIK DROPu " : "🎲 WYNIK DROPu - ") + dropType + "!")
                .setDescription(String.format("Użytkownik %s właśnie wziął udział w losowaniu %s!",
                        member.getAsMention(), dropType))
                .addField("Otrzymana Nagroda", isWin ? "🏆 **" + reward.description + "**" : "**" + reward.description + "**", false)
                .addField("Następny Drop dla " + member.getEffectiveName(),
                        String.format("Dostępny **<t:%d:R>**", cooldownEndTimeSeconds), false)
                .setThumbnail(member.getUser().getAvatarUrl())
                .setFooter(String.format("Powodzenia następnym razem!"), event.getGuild().getIconUrl())
                .setColor(isWin ? (isPremium ? new Color(255, 215, 0) : Color.GREEN) : new Color(150, 150, 150)); // Złoty kolor dla Premium

        // --- POPRAWKA: Użycie getChannel() zamiast getTextChannel() ---
        // LINIA 214: Używamy event.getChannel() i rzutujemy na MessageChannel
        event.getChannel().sendMessage(MessageCreateData.fromEmbeds(publicEmbed.build())).queue();

        // --- Wysyłamy prywatne potwierdzenie dla użytkownika ---
        event.getHook().sendMessage("✅ Twój wynik dropa został opublikowany na kanale!").setEphemeral(true).queue();
    }

    // ---------------- LOGIKA INFORMACYJNA -----------------

    private void handleDropInfoCommand(SlashCommandInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🎁 System Dropów - Informacje")
                .setColor(Color.BLUE)
                .setDescription("Spróbuj swojego szczęścia raz na " + COOLDOWN_HOURS + " godziny w jednym z dostępnych dropów!")
                .addField("Wymagania Ról",
                        String.format("• **Standard**: Rola <@&%s> lub <@&%s>.\n• **Premium**: Rola <@&%s>.",
                                NORMAL_ROLE_ID, PREMIUM_ROLE_ID, PREMIUM_ROLE_ID), false)
                .addField("Kanały",
                        String.format("• **Standard**: <#%s>\n• **Premium**: <#%s>", STANDARD_DROP_CHANNEL_ID, PREMIUM_DROP_CHANNEL_ID), false);

        // Sekcja Standard
        embed.addField("🎁 STANDARDOWE NAGRODY", formatRewardsInfo(STANDARD_REWARDS), false);

        // Sekcja Premium
        embed.addField("💎 NAGRODY PREMIUM", formatRewardsInfo(PREMIUM_REWARDS), false);

        embed.addField("Zasady",
                        String.format("• 1 drop na %d godziny (cooldown jest wspólny).\n• Nagrody są automatycznie przypisywane. Rola zniżkowa jest ważna do pierwszego użycia.", COOLDOWN_HOURS),
                        false)
                .setFooter("Powodzenia w losowaniu!");

        sendPrivateEmbed(event, embed);
    }

    private String formatRewardsInfo(Map<String, Reward> rewardsMap) {
        StringBuilder rewardsInfo = new StringBuilder();
        int totalChance = rewardsMap.values().stream().mapToInt(r -> r.chance).sum();

        rewardsMap.values().stream()
                .filter(r -> r.chance > 0)
                .sorted(Comparator.comparing(r -> r.chance, Comparator.reverseOrder()))
                .forEach(reward -> {
                    double probability = (double) reward.chance / totalChance * 100;
                    rewardsInfo.append(String.format("• **%s** (%.1f%% szans)\n", reward.description.replace(" (Premium)", ""), probability));
                });
        return rewardsInfo.toString();
    }


    // ---------------- NARZĘDZIA -----------------

    // Zmieniona nazwa, bo to jest używane tylko do błędów i informacji (prywatnie)
    private void sendPrivateEmbed(SlashCommandInteractionEvent event, EmbedBuilder embed) {
        event.getHook().sendMessage(MessageCreateData.fromEmbeds(embed.build())).setEphemeral(true).queue();
    }

    private EmbedBuilder createErrorEmbed(String title, String description, String footer, Color color) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setColor(color);
        if (footer != null) {
            embed.setFooter(footer);
        }
        return embed;
    }

    private Reward getRandomReward(Map<String, Reward> rewardsMap) {
        int totalChance = rewardsMap.values().stream().mapToInt(r -> r.chance).sum();
        int random = new Random().nextInt(totalChance) + 1;
        int current = 0;

        for (Reward reward : rewardsMap.values()) {
            if (reward.chance > 0) {
                current += reward.chance;
                if (random <= current) {
                    return reward;
                }
            }
        }

        // Zabezpieczenie
        return rewardsMap.get("0%");
    }

    private static class Reward {
        final String roleId;
        final int chance;
        final String description;
        final boolean isZeroReward;

        Reward(String roleId, int chance, String description, boolean isZeroReward) {
            this.roleId = roleId;
            this.chance = chance;
            this.description = description;
            this.isZeroReward = isZeroReward;
        }

        Reward(String roleId, int chance, String description) {
            this(roleId, chance, description, false);
        }
    }
}
