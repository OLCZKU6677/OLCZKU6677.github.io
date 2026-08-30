package pl.olczku.wOOLCODEHELPOPDC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.io.IOException;

public class HelpopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getMessages().getString("only-player")));
            return true;
        }

        Player player = (Player) sender;

        // Sprawdź permisję
        if (!player.hasPermission("woolcodehelpop.use")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getMessages().getString("no-permission")));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getMessages().getString("usage")));
            return true;
        }

        String message = String.join(" ", args);

        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setTitle(Main.getInstance().getMessages().getString("embed.title"))
                .addField(Main.getInstance().getMessages().getString("embed.player"), player.getName(), false)
                .addField(Main.getInstance().getMessages().getString("embed.message"), message, false)
                .setColor(0x800080); // Kolor fioletowy

        Main.getWebhook().addEmbed(embed);

        try {
            Main.getWebhook().execute();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getMessages().getString("message-sent")));
        } catch (IOException e) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getMessages().getString("error")));
            e.printStackTrace();
        }

        return true;
    }
}
