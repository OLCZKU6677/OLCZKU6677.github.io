package pl.olczku.wOOLCODEKOLEJKA;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final WOOLCODEKOLEJKA plugin;

    public PlayerJoinListener(WOOLCODEKOLEJKA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (plugin.isFull()) {
            plugin.getQueue().add(event.getPlayer());
            plugin.savePlayerLocation(event.getPlayer());

            Block block = plugin.getLimboLocation().getBlock();
            block.setType(Material.BARRIER);

            event.getPlayer().teleport(plugin.getLimboLocation());
            event.getPlayer().sendTitle("", "§aDołączono do kolejki...", 10, 60, 10);
        }
    }
}
