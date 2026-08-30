package pl.olczku.skyluck_cooldown_skrzyn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class Skyluck_cooldown_skrzyn extends JavaPlugin implements Listener {

    private int delaySeconds;
    private String openingSubtitle;
    private String alreadyOpeningMessage;
    private String reloadSuccessMessage;

    private final Set<UUID> playersInDelay = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("skyluck_cooldown_skrzyn został włączony!");
    }

    @Override
    public void onDisable() {
        getLogger().info("skyluck_cooldown_skrzyn został wyłączony.");
    }

    private void loadConfigValues() {
        reloadConfig();
        this.delaySeconds = getConfig().getInt("delay-seconds", 15);
        this.openingSubtitle = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.opening-subtitle", "&eOtwieranie za &c%seconds%s..."));
        this.alreadyOpeningMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.already-opening", "&cJuż otwierasz jeden pojemnik!"));
        this.reloadSuccessMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.reload-success", "&aKonfiguracja przeładowana!"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("skyluckcooldown")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("skyluck.cooldown.reload")) {
                    sender.sendMessage(ChatColor.RED + "Nie masz uprawnień do wykonania tej komendy.");
                    return true;
                }
                loadConfigValues();
                sender.sendMessage(this.reloadSuccessMessage);
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Poprawne użycie: /" + label + " reload");
            return true;
        }
        return false;
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || !(clickedBlock.getState() instanceof Container)) {
            return;
        }

        Player player = event.getPlayer();

        if (player.hasPermission("skyluck.cooldown.bypass")) {
            return;
        }

        if (playersInDelay.contains(player.getUniqueId())) {
            player.setNoActionTicks(Integer.parseInt(this.alreadyOpeningMessage));
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        playersInDelay.add(player.getUniqueId());

        AtomicInteger countdown = new AtomicInteger(this.delaySeconds);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    playersInDelay.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                int currentSeconds = countdown.getAndDecrement();

                if (currentSeconds > 0) {
                    String subtitle = openingSubtitle.replace("%seconds%", String.valueOf(currentSeconds));
                    player.sendTitle("", subtitle, 0, 25, 0);
                } else {
                    Bukkit.getScheduler().runTask(Skyluck_cooldown_skrzyn.this, () -> {
                        if (clickedBlock.getState() instanceof Container) {
                            Container container = (Container) clickedBlock.getState();
                            player.openInventory(container.getInventory());
                        }
                    });
                    playersInDelay.remove(player.getUniqueId());
                    cancel();
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }
}