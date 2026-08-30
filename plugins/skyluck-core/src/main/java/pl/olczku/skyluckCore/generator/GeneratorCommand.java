package pl.olczku.skyluckCore.generator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;

public class GeneratorCommand implements CommandExecutor {

    private final OlczkuDannyPrzekKom plugin;
    private final GeneratorManager generatorManager;

    public GeneratorCommand(OlczkuDannyPrzekKom plugin, GeneratorManager generatorManager) {
        this.plugin = plugin;
        this.generatorManager = generatorManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("skyluck.core.generator.admin")) {
            plugin.messages().send(sender, "no_permission");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadGeneratoryConfig();
            generatorManager.registerCraftingRecipe();
            sender.sendMessage("§aPomyślnie przeładowano konfigurację generatorów.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("daj")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cTa komenda jest dostępna tylko dla graczy.");
                return true;
            }
            Player player = (Player) sender;
            player.getInventory().addItem(generatorManager.getGeneratorItem());
            player.sendMessage("§aOtrzymano generator.");
            return true;
        }

        sender.sendMessage("§cPoprawne użycie: /generator <daj|reload>");
        return true;
    }
}