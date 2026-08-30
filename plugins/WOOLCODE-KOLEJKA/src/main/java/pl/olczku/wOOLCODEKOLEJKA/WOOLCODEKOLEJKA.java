package pl.olczku.wOOLCODEKOLEJKA;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class WOOLCODEKOLEJKA extends JavaPlugin implements Listener {

    private int maxPlayers;
    private final Queue<Player> queue = new LinkedList<>();
    private final Map<UUID, Location> previousLocations = new HashMap<>();
    private Location limboLocation;

    @Override
    public void onEnable() {
        String expectedAuthor = "WOOLCODE | OLCZKU";
        String actualAuthor = String.join(", ", getDescription().getAuthors());

        if (!actualAuthor.equals(expectedAuthor)) {
            getLogger().severe("PLUGIN ZOSTAŁ ZMODYFIKOWANY! OCZEKIWANY AUTOR: " + expectedAuthor);
            getLogger().severe("ZNALEZIONO: " + actualAuthor);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        FileConfiguration config = getConfig();
        maxPlayers = config.getInt("limit");

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MoveBlockerListener(this), this);

        limboLocation = new Location(Bukkit.getWorlds().get(0), 0, -100, 0);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!queue.isEmpty() && Bukkit.getOnlinePlayers().size() < maxPlayers) {
                    Player player = queue.poll();
                    if (player != null && player.isOnline()) {
                        Location loc = previousLocations.get(player.getUniqueId());
                        if (loc != null) {
                            player.teleport(loc);
                        }
                        player.sendTitle("", ChatColor.GREEN + "Zostałeś połączony z serwerem!", 10, 60, 10);
                        previousLocations.remove(player.getUniqueId());
                    }
                }

                int i = 1;
                for (Player p : queue) {
                    if (p.isOnline()) {
                        p.sendTitle("", ChatColor.GREEN + "Jesteś w kolejce. Miejsce: #" + i, 10, 60, 10);
                        i++;
                    }
                }
            }
        }.runTaskTimer(this, 0L, 60L);
    }

    public boolean isFull() {
        return Bukkit.getOnlinePlayers().size() >= maxPlayers;
    }

    public Queue<Player> getQueue() {
        return queue;
    }

    public Location getLimboLocation() {
        return limboLocation;
    }

    public void savePlayerLocation(Player player) {
        previousLocations.put(player.getUniqueId(), player.getLocation());
    }

    public boolean isInQueue(Player player) {
        return queue.contains(player);
    }
}
