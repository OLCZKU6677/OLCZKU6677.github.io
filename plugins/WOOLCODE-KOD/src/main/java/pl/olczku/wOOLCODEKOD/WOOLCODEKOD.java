package pl.olczku.wOOLCODEKOD;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class WOOLCODEKOD extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getCommand("kod").setExecutor(new KodCommandExecutor());
    }

    @Override
    public void onDisable() {}

    public class KodCommandExecutor implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(color("&cTylko gracze mogą używać tej komendy!"));
                return true;
            }

            Player player = (Player) sender;

            if (!player.hasPermission("woolcode.kod")) {
                player.sendMessage(color("&cNie masz uprawnień do użycia tej komendy!"));
                return true;
            }

            String kod = getConfig().getString("kod");
            if (kod == null || kod.isEmpty()) {
                player.sendMessage(color("&cKod nie został ustawiony w konfiguracji!"));
                return true;
            }

            if (args.length == 0 || !args[0].equals(kod)) {
                player.sendMessage(color("&cNieprawidłowy kod!"));
                return true;
            }

            List<String> komendy = getConfig().getStringList("komendy");
            if (komendy == null || komendy.isEmpty()) {
                player.sendMessage(color("&cBrak komend do wykonania!"));
                return true;
            }

            for (String cmd : komendy) {
                cmd = cmd.replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }

            player.sendMessage(color("&aGratulacje! Kod został zaakceptowany, a nagrody przyznane."));

            return true;
        }

        private String color(String message) {
            return ChatColor.translateAlternateColorCodes('&', message);
        }
    }
}
