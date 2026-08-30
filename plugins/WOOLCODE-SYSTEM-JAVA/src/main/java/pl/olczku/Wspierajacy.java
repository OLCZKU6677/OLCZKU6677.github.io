package pl.olczku;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Wspierajacy extends ListenerAdapter {


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {
        if (e.getName().equals("wspierajacy")) {
            Member member = e.getMember();
            if (member == null || !member.getRoles().stream().anyMatch(role -> role.getId().equals("1346518286954659890"))) {
                EmbedBuilder selfRoleEmbed = new EmbedBuilder();


            }
        }
    }
}
