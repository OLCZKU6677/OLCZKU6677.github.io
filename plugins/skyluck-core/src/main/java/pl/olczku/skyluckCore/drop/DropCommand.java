package pl.olczku.skyluckCore.drop;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;
import java.util.Map;

public class DropCommand implements CommandExecutor {
    private final OlczkuDannyPrzekKom plugin;
    public DropCommand(OlczkuDannyPrzekKom plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) { plugin.messages().send(sender, "player_only"); return true; }
        if (!p.hasPermission("skyluck.core.drop")) { plugin.messages().send(p, "no_permission"); return true; }
        if (args.length != 1) { plugin.messages().send(p, "drop_usage"); return true; }
        double chance;
        try { chance = Double.parseDouble(args[0].replace(",", ".")); }
        catch (NumberFormatException e) { plugin.messages().send(p, "number_required"); return true; }
        if (chance < 0) chance = 0; if (chance > 100) chance = 100;
        ItemStack inHand = p.getInventory().getItemInMainHand();
        if (inHand == null || inHand.getType() == Material.AIR) { plugin.messages().send(p, "hand_empty"); return true; }
        Block target = p.getTargetBlockExact(5);
        if (target == null || target.getType() != Material.CHEST) { plugin.messages().send(p, "look_at_chest"); return true; }
        if (!target.getWorld().getName().equalsIgnoreCase(plugin.allowedWorld())) {
            plugin.messages().send(p, "wrong_world", Map.of("world", plugin.allowedWorld()));
            return true;
        }
        Location chestLoc = target.getLocation();
        String id = plugin.addDropToChest(chestLoc, inHand.clone(), chance);

        if (target.getState() instanceof org.bukkit.block.Chest) {
            org.bukkit.block.Chest chest = (org.bukkit.block.Chest) target.getState();
            Inventory chestInventory = chest.getInventory();

            if (chestInventory.getItem(12) == null) {
                chestInventory.setItem(12, inHand.clone());
            } else {

                int firstEmpty = chestInventory.firstEmpty();
                if (firstEmpty != -1) {
                    chestInventory.setItem(firstEmpty, inHand.clone());
                } else {
                    plugin.messages().send(p, "chest_full");
                    return true;
                }
            }
        }

        plugin.messages().send(p, "drop_added", Map.of(
                "item", inHand.getType().name(),
                "amount", String.valueOf(inHand.getAmount()),
                "key", OlczkuDannyPrzekKom.chestKey(chestLoc),
                "chance", String.valueOf(chance),
                "id", id
        ));
        return true;
    }
}