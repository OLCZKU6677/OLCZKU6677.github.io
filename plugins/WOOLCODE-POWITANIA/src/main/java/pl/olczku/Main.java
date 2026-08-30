package pl.olczku;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
    public static void main(String[] args) {
        try {
            String token = "OTk5OTk5OTk5OTk5OTk5OTk5.XXXXXX.XXXXXXXXXXXXXXXXXXXXXXXXXXX";

            JDABuilder jda = JDABuilder.createDefault(token);

            jda.setActivity(Activity.playing("Wbij na dc.woolcode.pl"));


            jda.addEventListeners(new WelcomeListener());
            jda.enableIntents(GatewayIntent.GUILD_MEMBERS);

            jda.build().awaitReady();


            System.out.println("Bot działa");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Wystąpił błąd przy uruchamianiu bota.");
        }
    }
}
