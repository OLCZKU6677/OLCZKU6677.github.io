package pl.olczku.skyluckCore.skrzynkapoziom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SkrzynkaCommand implements CommandExecutor {
    private final OlczkuDannyPrzekKom plugin;

    public SkrzynkaCommand(OlczkuDannyPrzekKom plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("dodaj")) {
            handleDodaj(sender, args);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("daj")) {
            handleGiveToPlayer(sender, args);
            return true;
        }

        handleGive(sender, args);
        return true;
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("skyluck.core.skrzynka.admin")) {
            plugin.messages().send(sender, "no_permission");
            return;
        }

        if (args.length != 1) {
            plugin.messages().send(sender, "skrzynka_usage");
            return;
        }

        if (!(sender instanceof Player)) {
            plugin.messages().send(sender, "player_only");
            return;
        }

        int poziom;
        try {
            poziom = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            plugin.messages().send(sender, "number_required");
            return;
        }

        if (poziom <= 0) {
            plugin.messages().send(sender, "invalid_level");
            return;
        }

        Player player = (Player) sender;
        giveSkrzynka(player, poziom);
        plugin.messages().send(player, "skrzynka_given", Map.of("level", String.valueOf(poziom)));
    }

    private void handleGiveToPlayer(CommandSender sender, String[] args) {
        if (!sender.hasPermission("skyluck.core.skrzynka.admin")) {
            plugin.messages().send(sender, "no_permission");
            return;
        }

        if (args.length != 3) {
            sender.sendMessage("§cPoprawne użycie: /skrzynka daj <gracz> <poziom>");
            return;
        }

        String playerName = args[1];
        Player target = Bukkit.getPlayer(playerName);

        if (target == null) {
            plugin.messages().send(sender, "player_not_found", Map.of("player", playerName));
            return;
        }

        int poziom;
        try {
            poziom = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            plugin.messages().send(sender, "number_required");
            return;
        }

        if (poziom <= 0) {
            plugin.messages().send(sender, "invalid_level");
            return;
        }

        giveSkrzynka(target, poziom);
        plugin.messages().send(sender, "skrzynka_given_to_player",
                Map.of("level", String.valueOf(poziom), "player", target.getName()));
        plugin.messages().send(target, "skrzynka_received",
                Map.of("level", String.valueOf(poziom), "sender", sender.getName()));
    }

    private void giveSkrzynka(Player player, int poziom) {
        ConfigurationSection skrzynkaConfig = plugin.getSkrzynkiConfig().getConfigurationSection("skrzynki." + poziom);

        String nazwaSkrzynki = "§6Skrzynka obrońców (Poziom " + poziom + ")";
        if (skrzynkaConfig != null && skrzynkaConfig.contains("nazwa")) {
            nazwaSkrzynki = skrzynkaConfig.getString("nazwa");
        }

        List<String> lore = Collections.singletonList("§7Postaw, aby otworzyć!");
        if (skrzynkaConfig != null && skrzynkaConfig.contains("lore")) {
            lore = skrzynkaConfig.getStringList("lore");
        }

        ItemStack skrzynka = new ItemStack(Material.CHEST);
        ItemMeta meta = skrzynka.getItemMeta();
        meta.setDisplayName(nazwaSkrzynki);
        meta.setLore(lore);
        meta.setCustomModelData(poziom);
        skrzynka.setItemMeta(meta);

        player.getInventory().addItem(skrzynka);
    }

    private void handleDodaj(CommandSender sender, String[] args) {
        if (!sender.hasPermission("skyluck.core.skrzynka.dodaj")) {
            plugin.messages().send(sender, "no_permission");
            return;
        }

        if (!(sender instanceof Player)) {
            plugin.messages().send(sender, "player_only");
            return;
        }

        if (args.length != 3) {
            sender.sendMessage("§cPoprawne użycie: /skrzynka dodaj <poziom> <szansa>");
            return;
        }

        Player player = (Player) sender;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            plugin.messages().send(sender, "hand_empty");
            return;
        }

        int poziom;
        double szansa;

        try {
            poziom = Integer.parseInt(args[1]);
            szansa = Double.parseDouble(args[2].replace(",", "."));
        } catch (NumberFormatException e) {
            plugin.messages().send(sender, "number_required");
            return;
        }

        String path = "skrzynki." + poziom + ".items";
        ConfigurationSection itemsSection = plugin.getSkrzynkiConfig().getConfigurationSection(path);
        if (itemsSection == null) {
            itemsSection = plugin.getSkrzynkiConfig().createSection(path);
        }

        String key = UUID.randomUUID().toString().substring(0, 8);
        ConfigurationSection newItemSection = itemsSection.createSection(key);
        newItemSection.set("item", itemInHand);
        newItemSection.set("chance", szansa);

        plugin.saveSkrzynkiConfig();
        sender.sendMessage("§aPomyślnie dodano przedmiot do skrzynki poziomu " + poziom + " z szansą " + szansa + "%!");
    }
}