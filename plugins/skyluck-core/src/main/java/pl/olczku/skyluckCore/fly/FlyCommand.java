package pl.olczku.skyluckCore.fly;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;
import pl.olczku.skyluckCore.util.Messages;

import java.util.ArrayList;
import java.util.List;

public class FlyCommand implements CommandExecutor, TabCompleter {
    private final OlczkuDannyPrzekKom plugin;
    private final Messages messages;

    public FlyCommand(OlczkuDannyPrzekKom plugin, Messages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;

        if (args.length > 0 && sender.hasPermission("fly.others")) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cGracz nie jest online!");
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage("§cTylko gracze mogą używać tej komendy!");
            return true;
        }

        // Sprawdzanie uprawnień
        if (!target.hasPermission("fly.use") && !target.hasPermission("fly.bypass")) {
            messages.send(target, "no_permission");
            return true;
        }

        // Sprawdzanie świata
        if (!isWorldAllowed(target.getWorld().getName())) {
            messages.send(target, "fly_disabled_world");
            return true;
        }

        // Przełączanie fly
        boolean newState = !target.getAllowFlight();
        target.setAllowFlight(newState);

        if (!newState) {
            target.setFlying(false);
        }

        String messageKey = newState ? "fly_enabled" : "fly_disabled";
        messages.send(target, messageKey);

        if (!target.equals(sender)) {
            String otherMessageKey = newState ? "fly_enabled_other" : "fly_disabled_other";
            messages.send(sender, otherMessageKey, java.util.Map.of("player", target.getName()));
        }

        return true;
    }

    private boolean isWorldAllowed(String worldName) {
        List<String> allowedWorlds = plugin.getConfig().getStringList("fly.allowed-worlds");

        // Jeśli lista jest pusta, fly działa na wszystkich światach
        if (allowedWorlds.isEmpty()) {
            return true;
        }

        return allowedWorlds.contains(worldName);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("fly.others")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    suggestions.add(player.getName());
                }
            }
        }

        return suggestions;
    }
}