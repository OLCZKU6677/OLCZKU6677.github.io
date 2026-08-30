package pl.olczku.skyluckCore.drop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ResetChestsCommand implements CommandExecutor {
    private final OlczkuDannyPrzekKom plugin;
    public ResetChestsCommand(OlczkuDannyPrzekKom plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("skyluck.core.reset")) {
            plugin.messages().send(sender, "no_permission");
            return true;
        }

        String world = plugin.allowedWorld();
        for (Map.Entry<UUID, Map<String, Long>> e : plugin.cooldowns().entrySet()) {
            Iterator<Map.Entry<String, Long>> it = e.getValue().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Long> ce = it.next();
                if (ce.getKey().startsWith(world + ":")) it.remove();
            }
        }

        plugin.saveData();
        plugin.messages().send(sender, "reset_done_clear");
        return true;
    }
}