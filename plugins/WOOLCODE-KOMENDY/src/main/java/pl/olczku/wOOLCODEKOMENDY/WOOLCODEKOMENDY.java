package pl.olczku.wOOLCODEKOMENDY;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class WOOLCODEKOMENDY extends JavaPlugin {

    private Map<String, CustomCommand> customCommands = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig(); // Tworzy domyślny plik config.yml, jeśli nie istnieje
        loadCommands(); // Ładuje komendy z konfiguracji

        // Rejestracja komendy do przeładowania konfiguracji
        this.getCommand("woolcode-commands").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                    if (!sender.hasPermission("woolcode.commands.reload")) {
                        sender.sendMessage(ChatColor.RED + "Nie masz uprawnień do użycia tej komendy!");
                        return true;
                    }
                    reloadConfig(); // Przeładowanie config.yml
                    loadCommands(); // Ponowne załadowanie komend
                    sender.sendMessage(ChatColor.GREEN + "Konfiguracja komend została przeładowana!");
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "Użycie: /woolcode-commands reload");
                return true;
            }
        });
    }

    private void loadCommands() {
        customCommands.clear(); // Czyści stare komendy
        FileConfiguration config = getConfig();
        if (config.contains("commands")) {
            for (String commandName : config.getConfigurationSection("commands").getKeys(false)) {
                String description = config.getString("commands." + commandName + ".description", "No description provided");
                String permission = config.getString("commands." + commandName + ".permission", "");
                String action = config.getString("commands." + commandName + ".action", "");

                CustomCommand customCommand = new CustomCommand(description, permission, action);
                customCommands.put(commandName, customCommand);

                // Rejestracja komendy dynamicznie
                Bukkit.getPluginCommand(commandName).setExecutor(new CommandExecutor() {
                    @Override
                    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                        if (!customCommand.getPermission().isEmpty() && !sender.hasPermission(customCommand.getPermission())) {
                            sender.sendMessage(ChatColor.RED + "Nie masz uprawnień do użycia tej komendy!");
                            return true;
                        }
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), customCommand.getAction());
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private static class CustomCommand {
        private final String description;
        private final String permission;
        private final String action;

        public CustomCommand(String description, String permission, String action) {
            this.description = description;
            this.permission = permission;
            this.action = action;
        }

        public String getDescription() {
            return description;
        }

        public String getPermission() {
            return permission;
        }

        public String getAction() {
            return action;
        }
    }
}
