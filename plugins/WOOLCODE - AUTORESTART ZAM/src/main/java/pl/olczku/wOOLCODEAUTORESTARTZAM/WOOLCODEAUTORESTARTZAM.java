package pl.olczku.wOOLCODEAUTORESTARTZAM;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WOOLCODEAUTORESTARTZAM extends JavaPlugin {

    private final List<BukkitTask> scheduledTasks = new ArrayList<>();
    private Instant reloadTimestamp;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadAndSchedule();
        PluginCommand command = getCommand("autorestart");
        if (command != null) {
            command.setExecutor(new RestartCommand(this));
        }
        getLogger().info("wOOLCODE-AUTORELOAD-ZAM został włączony i skonfigurowany.");
    }

    @Override
    public void onDisable() {
        cancelAllTasks();
        getLogger().info("wOOLCODE-AUTORELOAD-ZAM został wyłączony, wszystkie zadania anulowano.");
    }

    public void reload() {
        cancelAllTasks();
        reloadConfig();
        loadAndSchedule();
    }

    private void loadAndSchedule() {
        FileConfiguration config = getConfig();
        int minutesInterval = config.getInt("reload-interval-minutes", 180);
        if (minutesInterval <= 0) {
            getLogger().warning("Interwał przeładowania jest ustawiony na 0 lub mniej. Automatyczne przeładowanie jest wyłączone.");
            return;
        }
        long totalTicks = TimeUnit.MINUTES.toSeconds(minutesInterval) * 20L;
        this.reloadTimestamp = Instant.now().plusSeconds(totalTicks / 20L);
        getLogger().info("Serwer zostanie przeładowany za " + minutesInterval + " minut.");
        scheduleTask(() -> {
            getLogger().info("Rozpoczynanie sekwencji przeładowania serwera...");
            initiateReloadSequence();
        }, totalTicks);
        scheduleAnnouncements(totalTicks);
        schedulePluginDisable(totalTicks);
    }

    public void initiateReloadSequence() {
        cancelAllTasks();
        this.reloadTimestamp = Instant.now().plusSeconds(10);
        FileConfiguration config = getConfig();
        new BukkitRunnable() {
            int countdown = config.getInt("reload-countdown-seconds", 10);
            @Override
            public void run() {
                if (countdown > 0) {
                    broadcastMessage("reload-countdown", String.valueOf(countdown));
                    countdown--;
                } else {
                    broadcastMessage("reload-now", null);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reload confirm");
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    private void scheduleAnnouncements(long totalTicks) {
        ConfigurationSection announcements = getConfig().getConfigurationSection("announcements");
        if (announcements == null) return;
        for (String key : announcements.getKeys(false)) {
            long timeOffset = parseTime(key);
            if (timeOffset < 0) {
                getLogger().warning("Nieprawidłowy format czasu w ogłoszeniach: " + key);
                continue;
            }
            long announcementTick = totalTicks - (timeOffset * 20L);
            if (announcementTick > 0) {
                scheduleTask(() -> broadcastMessage("announcements." + key, null), announcementTick);
            }
        }
    }

    private void schedulePluginDisable(long totalTicks) {
        FileConfiguration config = getConfig();
        if (!config.getBoolean("disable-plugin.enabled", false)) return;
        String pluginToDisable = config.getString("disable-plugin.plugin-name");
        if (pluginToDisable == null || pluginToDisable.isEmpty()) {
            getLogger().warning("Chcesz wyłączyć plugin przed przeładowaniem, ale nie podałeś jego nazwy w config.yml!");
            return;
        }
        long timeOffset = parseTime(config.getString("disable-plugin.time-before-reload", "5m"));
        if (timeOffset < 0) {
            getLogger().warning("Nieprawidłowy format czasu dla wyłączenia pluginu: " + config.getString("disable-plugin.time-before-reload"));
            return;
        }
        long disableTick = totalTicks - (timeOffset * 20L);
        if (disableTick > 0) {
            scheduleTask(() -> {
                Plugin targetPlugin = Bukkit.getPluginManager().getPlugin(pluginToDisable);
                if (targetPlugin != null && targetPlugin.isEnabled()) {
                    Bukkit.getPluginManager().disablePlugin(targetPlugin);
                    getLogger().info("Plugin '" + pluginToDisable + "' został wyłączony przed przeładowaniem.");
                    broadcastMessage("disable-plugin.message", null);
                } else {
                    getLogger().warning("Nie znaleziono lub już wyłączono plugin '" + pluginToDisable + "'.");
                }
            }, disableTick);
        }
    }

    private void broadcastMessage(String configPath, String placeholder) {
        FileConfiguration config = getConfig();
        String title = ChatColor.translateAlternateColorCodes('&', config.getString(configPath + ".title", ""));
        String subtitle = ChatColor.translateAlternateColorCodes('&', config.getString(configPath + ".subtitle", ""));
        String chat = ChatColor.translateAlternateColorCodes('&', config.getString(configPath + ".chat", ""));
        if (placeholder != null) {
            title = title.replace("%time%", placeholder);
            subtitle = subtitle.replace("%time%", placeholder);
            chat = chat.replace("%time%", placeholder);
        }
        if (!chat.isEmpty()) {
            Bukkit.broadcastMessage(chat);
        }
        final String finalTitle = title;
        final String finalSubtitle = subtitle;
        Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(finalTitle, finalSubtitle, 10, 70, 20));
    }

    private long parseTime(String timeString) {
        if (timeString == null || timeString.isEmpty()) return -1;
        try {
            char unit = timeString.charAt(timeString.length() - 1);
            int value = Integer.parseInt(timeString.substring(0, timeString.length() - 1));
            switch (unit) {
                case 's': return value;
                case 'm': return value * 60L;
                case 'h': return value * 3600L;
                default: return -1;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String getTimeUntilReload() {
        if (reloadTimestamp == null) {
            return "Przeładowanie nie jest zaplanowane.";
        }
        Duration remaining = Duration.between(Instant.now(), reloadTimestamp);
        if (remaining.isNegative()) {
            return "Przeładowanie powinno już nastąpić.";
        }
        long hours = remaining.toHours();
        long minutes = remaining.toMinutesPart();
        long seconds = remaining.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void scheduleTask(Runnable task, long delayTicks) {
        scheduledTasks.add(new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTaskLater(this, delayTicks));
    }

    public void cancelAllTasks() {
        for (BukkitTask task : scheduledTasks) {
            if (!task.isCancelled()) {
                task.cancel();
            }
        }
        scheduledTasks.clear();
        Bukkit.getScheduler().cancelTasks(this);
        reloadTimestamp = null;
    }
}