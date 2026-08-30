package pl.olczku.skyluckCore.Przekierowanie;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class ShutdownListener implements Listener {

    private final OlczkuDannyPrzekKom plugin;

    public ShutdownListener(OlczkuDannyPrzekKom plugin) {
        this.plugin = plugin;
    }

    private void kickAllPlayers() {
        String kickMessage = "§cSerwer jest restartowany! Wróć za chwilę!";
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(kickMessage);
        }
        plugin.getLogger().info("Wykryto zatrzymanie serwera, wszyscy gracze zostali wyrzuceni.");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerCommand(ServerCommandEvent event) {
        String command = event.getCommand().toLowerCase();
        if (command.equals("stop") || command.equals("restart")) {
            kickAllPlayers();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();
        if (command.equals("/stop") || command.equals("/restart")) {
            if (event.getPlayer().hasPermission("bukkit.command.stop") || event.getPlayer().hasPermission("bukkit.command.restart")) {
                kickAllPlayers();
            }
        }
    }
}