package pl.olczku.wOOLCODEPIN;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WOOLCODEPIN extends JavaPlugin implements Listener {

    private Map<UUID, Boolean> loggedInPlayers = new HashMap<>();
    private Map<UUID, BukkitRunnable> titleTasks = new HashMap<>();
    private String adminPin = "1234";
    private String loginTitle;
    private String loginSubtitle;
    private List<String> allowedCommands;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("WOOLCODE-PIN został włączony!");
    }

    @Override
    public void onDisable() {
        for (BukkitRunnable task : titleTasks.values()) {
            task.cancel();
        }
        getLogger().info("WOOLCODE-PIN został wyłączony!");
    }

    private void loadConfig() {
        adminPin = getConfig().getString("adminPin", "1234");
        loginTitle = translateColors(getConfig().getString("messages.loginTitle", "&aWpisz /kodadm <kod>"));
        loginSubtitle = translateColors(getConfig().getString("messages.loginSubtitle", "&eAby się zalogować"));
        allowedCommands = getConfig().getStringList("allowedCommands");
    }

    private String translateColors(String text) {
        return text.replace('&', '§');
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("woolcode.pin.adm")) {
            loggedInPlayers.put(player.getUniqueId(), false);
            player.sendMessage(translateColors("&aWpisz /kodadm <kod>, aby się zalogować."));

            // Uruchomienie cyklicznego taska do wyświetlania title
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (loggedInPlayers.containsKey(player.getUniqueId()) && !loggedInPlayers.get(player.getUniqueId())) {
                        player.sendTitle(loginTitle, loginSubtitle, 10, 70, 20);
                    } else {
                        this.cancel(); // Zatrzymaj task, jeśli gracz się zalogował
                    }
                }
            };
            task.runTaskTimer(this, 20L, 100L); // Start po 1 sekundzie, powtarzaj co 5 sekund
            titleTasks.put(player.getUniqueId(), task);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (loggedInPlayers.containsKey(player.getUniqueId()) && !loggedInPlayers.get(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (loggedInPlayers.containsKey(player.getUniqueId()) && !loggedInPlayers.get(player.getUniqueId())) {
            String command = event.getMessage().split(" ")[0];
            if (!command.equalsIgnoreCase("/kodadm") && !allowedCommands.contains(command.toLowerCase())) {
                event.setCancelled(true);
                player.sendMessage(translateColors("&cMusisz się najpierw zalogować!"));
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("kodadm")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (loggedInPlayers.containsKey(player.getUniqueId()) && !loggedInPlayers.get(player.getUniqueId())) {
                    if (args.length == 1 && args[0].equals(adminPin)) {
                        loggedInPlayers.put(player.getUniqueId(), true);
                        player.sendMessage(translateColors("&aZalogowano pomyślnie!"));

                        // Zatrzymaj task wyświetlający title
                        if (titleTasks.containsKey(player.getUniqueId())) {
                            titleTasks.get(player.getUniqueId()).cancel();
                            titleTasks.remove(player.getUniqueId());
                        }
                        return true;
                    } else {
                        player.sendMessage(translateColors("&cNieprawidłowy kod."));
                        return false;
                    }
                } else {
                    player.sendMessage(translateColors("&cNie masz wymaganych uprawnień lub jesteś już zalogowany."));
                    return false;
                }
            }
        } else if (command.getName().equalsIgnoreCase("pinustaw")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("woolcode.pin.pinustaw")) {
                    if (args.length == 1 && args[0].matches("\\d+")) {
                        adminPin = args[0];
                        getConfig().set("adminPin", adminPin);
                        saveConfig();
                        player.sendMessage(translateColors("&aNowy kod PIN został ustawiony."));
                        return true;
                    } else {
                        player.sendMessage(translateColors("&cNieprawidłowy format kodu. Kod musi składać się z cyfr."));
                        return false;
                    }
                } else {
                    player.sendMessage(translateColors("&cNie masz uprawnień do ustawienia kodu PIN."));
                    return false;
                }
            }
        } else if (command.getName().equalsIgnoreCase("woolcode-pin")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("woolcode.pin.reload")) {
                    reloadConfig();
                    loadConfig();
                    sender.sendMessage(translateColors("&aKonfiguracja pluginu została przeładowana."));
                    return true;
                } else {
                    sender.sendMessage(translateColors("&cNie masz uprawnień do przeładowania konfiguracji."));
                    return false;
                }
            }
        }
        return false;
    }
}
