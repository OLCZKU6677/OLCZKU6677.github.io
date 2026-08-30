package pl.olczku.wOOLCODERTP;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.Random;

public class WOOLCODERTP extends JavaPlugin implements Listener {

    private FileConfiguration config;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        config = this.getConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("rtp")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 0) {
                    if (player.hasPermission("woolcodertp.use")) {
                        teleportPlayerRandomly(player);
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "Nie masz uprawnień do używania tej komendy!");
                        return false;
                    }
                } else if (args.length == 1 && args[0].equalsIgnoreCase("ustaw")) {
                    if (player.hasPermission("woolcodertp.ustaw")) {
                        RayTraceResult rayTraceResult = player.rayTraceBlocks(5); // Sprawdź, na co gracz patrzy (zasięg 5 bloków)
                        if (rayTraceResult != null && rayTraceResult.getHitBlock() != null) {
                            Material buttonType = rayTraceResult.getHitBlock().getType();

                            // Sprawdź, czy gracz patrzy na przycisk kamienny lub drewniany
                            if (buttonType == Material.STONE_BUTTON ||
                                    buttonType == Material.OAK_BUTTON ||
                                    buttonType == Material.SPRUCE_BUTTON ||
                                    buttonType == Material.BIRCH_BUTTON ||
                                    buttonType == Material.JUNGLE_BUTTON ||
                                    buttonType == Material.ACACIA_BUTTON ||
                                    buttonType == Material.DARK_OAK_BUTTON ||
                                    buttonType == Material.CRIMSON_BUTTON ||
                                    buttonType == Material.WARPED_BUTTON) {
                                rayTraceResult.getHitBlock().setMetadata("RTPButton", new FixedMetadataValue(this, true));
                                player.sendMessage(ChatColor.GREEN + "Przycisk został ustawiony jako RTP!");
                            } else {
                                player.sendMessage(ChatColor.RED + "Musisz patrzeć na przycisk, aby go ustawić jako RTP!");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Nie patrzysz na żaden blok!");
                        }
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "Nie masz uprawnień do ustawiania przycisków RTP!");
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        RayTraceResult rayTraceResult = player.rayTraceBlocks(5); // Sprawdź, na co gracz patrzy (zasięg 5 bloków)

        if (rayTraceResult != null && rayTraceResult.getHitBlock() != null) {
            Material buttonType = rayTraceResult.getHitBlock().getType();

            // Sprawdź, czy gracz patrzy na przycisk kamienny lub drewniany
            if (buttonType == Material.STONE_BUTTON ||
                    buttonType == Material.OAK_BUTTON ||
                    buttonType == Material.SPRUCE_BUTTON ||
                    buttonType == Material.BIRCH_BUTTON ||
                    buttonType == Material.JUNGLE_BUTTON ||
                    buttonType == Material.ACACIA_BUTTON ||
                    buttonType == Material.DARK_OAK_BUTTON ||
                    buttonType == Material.CRIMSON_BUTTON ||
                    buttonType == Material.WARPED_BUTTON) {

                if (rayTraceResult.getHitBlock().hasMetadata("RTPButton") && player.hasPermission("woolcodertp.use")) {
                    teleportPlayerRandomly(player);
                }
            }
        }
    }

    private void teleportPlayerRandomly(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Random random = new Random();
                int radius = config.getInt("radius", 5000);
                int x = random.nextInt(radius * 2) - radius;
                int z = random.nextInt(radius * 2) - radius;
                int y = player.getWorld().getHighestBlockYAt(x, z) + 1;

                Location randomLocation = new Location(player.getWorld(), x, y, z);
                player.teleport(randomLocation);

                String title = config.getString("messages.title", "&aTeleportacja!");
                String subtitle = config.getString("messages.subtitle", "&7Zostałeś przeteleportowany w losowe miejsce!");

                player.sendTitle(ChatColor.translateAlternateColorCodes('&', title), ChatColor.translateAlternateColorCodes('&', subtitle), 10, 20, 10);
            }
        }.runTask(this);
    }
}
