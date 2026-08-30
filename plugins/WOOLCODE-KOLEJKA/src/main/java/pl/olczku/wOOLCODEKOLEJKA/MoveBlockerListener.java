package pl.olczku.wOOLCODEKOLEJKA;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveBlockerListener implements Listener {

    private final WOOLCODEKOLEJKA plugin;

    public MoveBlockerListener(WOOLCODEKOLEJKA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (plugin.isInQueue(event.getPlayer())) {
            if (event.getFrom().getX() != event.getTo().getX() ||
                    event.getFrom().getY() != event.getTo().getY() ||
                    event.getFrom().getZ() != event.getTo().getZ() ||
                    event.getFrom().getPitch() != event.getTo().getPitch() ||
                    event.getFrom().getYaw() != event.getTo().getYaw()) {

                event.setTo(event.getFrom());
            }
        }
    }
}
