package pl.olczku.skyluckCore.skrzynkapoziom;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;

public class WymianaCommand implements CommandExecutor {

    private final OlczkuDannyPrzekKom plugin;
    private final WymianaListener wymianaListener;

    public WymianaCommand(OlczkuDannyPrzekKom plugin, WymianaListener wymianaListener) {
        this.plugin = plugin;
        this.wymianaListener = wymianaListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.messages().send(sender, "player_only");
            return true;
        }

        Player player = (Player) sender;
        wymianaListener.openWymianaGUI(player);
        return true;
    }
}