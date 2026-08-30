package pl.olczku.commands.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ReloadCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public ReloadCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("core-reload")) {
            if (sender.hasPermission("core.reload")) {
                plugin.reloadConfig(); // Przeładuj konfigurację
                sender.sendMessage("§aKonfiguracja pluginu została przeładowana.");
                return true;
            } else {
                sender.sendMessage("§cNie masz uprawnień do tej komendy!");
                return false;
            }
        }
        return false;
    }
}
