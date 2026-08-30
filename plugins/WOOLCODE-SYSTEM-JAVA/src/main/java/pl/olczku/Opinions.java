package pl.olczku;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Opinions extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        Channel channel = e.getChannel();
        if (channel.getId().equals("1304177398387966084")) {
           e.getMessage().delete();
            EmbedBuilder opinionEmbed = new EmbedBuilder();
            opinionEmbed.setAuthor("", e.getAuthor().getAvatarUrl());
            opinionEmbed.setDescription("```WOOLCODE - OPINIE``` \n" +
                    "Autor: <@" + e.getAuthor().getId() + ">\n" +
                    "Opinia: " + e.getMessage().getContentDisplay()
            );

        }
    }
}
