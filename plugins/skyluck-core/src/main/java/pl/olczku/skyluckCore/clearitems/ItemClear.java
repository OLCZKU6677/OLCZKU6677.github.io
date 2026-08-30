package pl.olczku.skyluckCore.clearitems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ItemClear {
    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private int taskId;
    private int countdownTaskId;
    private long nextClearTime;
    private final Map<String, String> placeholders = new HashMap<>();

    public ItemClear(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        startClearingTask();
        startCountdownTask();
    }

    private void startClearingTask() {
        int intervalMinutes = config.getInt("clear-items.interval", 15);
        long intervalTicks = intervalMinutes * 60 * 20L; // minuty -> ticki

        // Ustawienie czasu następnego czyszczenia
        nextClearTime = System.currentTimeMillis() + (intervalMinutes * 60 * 1000L);

        taskId = new BukkitRunnable() {
            @Override
            public void run() {
                clearItems();
                // Reset czasu następnego czyszczenia
                nextClearTime = System.currentTimeMillis() + (intervalMinutes * 60 * 1000L);
            }
        }.runTaskTimer(plugin, intervalTicks, intervalTicks).getTaskId();
    }

    private void startCountdownTask() {
        countdownTaskId = new BukkitRunnable() {
            @Override
            public void run() {
                updatePlaceholders();
                sendCountdownMessages();
            }
        }.runTaskTimer(plugin, 0L, 20L).getTaskId(); // Co sekundę
    }

    private void updatePlaceholders() {
        long timeLeft = nextClearTime - System.currentTimeMillis();
        long secondsLeft = timeLeft / 1000L;

        long minutes = secondsLeft / 60;
        long seconds = secondsLeft % 60;

        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        placeholders.put("skyluck-czas", timeFormatted);

        // Aktualizacja liczby przedmiotów (można dodać liczenie w czasie rzeczywistym)
        int itemCount = countItemsOnGround();
        placeholders.put("skyluck-przedmioty", String.valueOf(itemCount));
    }

    private int countItemsOnGround() {
        int count = 0;
        List<String> enabledWorlds = config.getStringList("clear-items.enabled-worlds");

        for (World world : Bukkit.getWorlds()) {
            if (enabledWorlds.contains(world.getName())) {
                count += world.getEntitiesByClass(Item.class).size();
            }
        }
        return count;
    }

    private void sendCountdownMessages() {
        long timeLeft = nextClearTime - System.currentTimeMillis();
        long minutesLeft = timeLeft / (60 * 1000L);
        long secondsLeft = (timeLeft % (60 * 1000L)) / 1000L;

        // 5 minut przed
        if (minutesLeft == 5 && secondsLeft == 0) {
            String message = getMessageWithPlaceholders("clear-items.message_5min");
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        // 1 minuta przed
        if (minutesLeft == 1 && secondsLeft == 0) {
            String message = getMessageWithPlaceholders("clear-items.message_1min");
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        // 30 sekund przed
        if (minutesLeft == 0 && secondsLeft == 30) {
            String message = getMessageWithPlaceholders("clear-items.message_30sec");
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        // 10 sekund przed
        if (minutesLeft == 0 && secondsLeft == 10) {
            String message = getMessageWithPlaceholders("clear-items.message_10sec");
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    private String getMessageWithPlaceholders(String configPath) {
        String message = config.getString(configPath, "");
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return message;
    }

    public void clearItems() {
        List<String> enabledWorlds = config.getStringList("clear-items.enabled-worlds");
        List<Item> removedItems = new ArrayList<>();

        for (World world : Bukkit.getWorlds()) {
            if (enabledWorlds.contains(world.getName())) {
                for (Item item : world.getEntitiesByClass(Item.class)) {
                    removedItems.add(item);
                    item.remove();
                }
            }
        }

        // Wysyłanie komunikatu po czyszczeniu
        if (!removedItems.isEmpty()) {
            String message = getMessageWithPlaceholders("clear-items.message")
                    .replace("{count}", String.valueOf(removedItems.size()));

            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public void reload() {
        Bukkit.getScheduler().cancelTask(taskId);
        Bukkit.getScheduler().cancelTask(countdownTaskId);
        startClearingTask();
        startCountdownTask();
    }

    public Map<String, String> getPlaceholders() {
        return placeholders;
    }
}