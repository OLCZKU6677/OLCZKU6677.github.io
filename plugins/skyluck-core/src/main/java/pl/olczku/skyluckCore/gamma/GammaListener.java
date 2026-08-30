package pl.olczku.skyluckCore.gamma;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class GammaListener implements Listener {
    private final GammaCommand gammaCommand;

    public GammaListener(GammaCommand gammaCommand) {
        this.gammaCommand = gammaCommand;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        gammaCommand.removeGammaEffect(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Usuwanie efektu przy wejściu na serwer
        gammaCommand.removeGammaEffect(event.getPlayer());
    }
}