package pl.olczku.skyluckCore.clearitems;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.olczku.skyluckCore.util.Messages;

public class ItemClearCommand implements CommandExecutor {
    private final ItemClear itemClear;
    private final Messages messages;

    public ItemClearCommand(ItemClear itemClear, Messages messages) {
        this.itemClear = itemClear;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("skyluck.core.clearitems.admin")) {
            messages.send(sender, "no_permission");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            itemClear.reload();
            sender.sendMessage("§aKonfiguracja czyszczenia przedmiotów przeładowana!");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("now")) {
            itemClear.clearItems();
            sender.sendMessage("§aWymuszono czyszczenie przedmiotów!");
            return true;
        }

        sender.sendMessage("§cUżycie: /clearitems <reload|now>");
        return true;
    }
}