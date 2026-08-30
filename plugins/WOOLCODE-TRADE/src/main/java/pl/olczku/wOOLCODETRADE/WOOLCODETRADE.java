package pl.olczku.wOOLCODETRADE;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class WOOLCODETRADE extends JavaPlugin implements Listener {

    private Map<UUID, TradeSession> tradeSessions = new HashMap<>();
    private Map<String, String> messages = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadMessages();

        getCommand("wymiana").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player && args.length == 1) {
                Player player = (Player) sender;
                Player target = Bukkit.getPlayer(args[0]);

                if (target != null && !target.equals(player)) {
                    startTrade(player, target);
                } else {
                    player.sendMessage(messages.get("player-not-found"));
                }
            }
            return true;
        });

        getServer().getPluginManager().registerEvents(this, this);
    }

    private void loadMessages() {
        messages.put("player-not-found", ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.player-not-found", "&cGracz nie jest dostępny!")));
        messages.put("trade-started", ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.trade-started", "&aRozpoczęto wymianę z %player%")));
        messages.put("trade-accepted", ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.trade-accepted", "&aZaakceptowano wymianę!")));
        messages.put("trade-denied", ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.trade-denied", "&cWymiana odrzucona!")));
        messages.put("trade-complete", ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.trade-complete", "&aWymiana zakończona pomyślnie!")));
        messages.put("trade-countdown", ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.trade-countdown", "&eWymiana za %seconds% sekund...")));
    }

    private void startTrade(Player player1, Player player2) {
        TradeSession session = new TradeSession(player1, player2);
        tradeSessions.put(player1.getUniqueId(), session);
        tradeSessions.put(player2.getUniqueId(), session);

        player1.sendMessage(messages.get("trade-started").replace("%player%", player2.getName()));
        player2.sendMessage(messages.get("trade-started").replace("%player%", player1.getName()));

        openTradeGUI(player1);
        openTradeGUI(player2);
    }

    private void openTradeGUI(Player player) {
        TradeSession session = tradeSessions.get(player.getUniqueId());
        if (session != null) {
            Inventory gui = Bukkit.createInventory(player, 54, "§6Wymiana");

            // Add glass panes to make the GUI look better
            for (int i = 0; i < 54; i++) {
                if (i < 45 || i >= 54) {
                    gui.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
                }
            }

            gui.setItem(49, createAcceptButton());
            gui.setItem(50, createDenyButton());
            player.openInventory(gui);
        }
    }

    private ItemStack createAcceptButton() {
        ItemStack accept = new ItemStack(Material.GREEN_WOOL);
        accept.getItemMeta().setDisplayName("§aAkceptuj");
        return accept;
    }

    private ItemStack createDenyButton() {
        ItemStack deny = new ItemStack(Material.RED_WOOL);
        deny.getItemMeta().setDisplayName("§cOdrzuć");
        return deny;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§6Wymiana")) {
            Player player = (Player) event.getWhoClicked();
            TradeSession session = tradeSessions.get(player.getUniqueId());

            if (session != null) {
                if (event.getSlot() == 49) {
                    session.accept(player);
                    event.setCancelled(true);
                } else if (event.getSlot() == 50) {
                    session.deny(player);
                    event.setCancelled(true);
                } else if (event.getSlot() < 45) {
                    session.updateItems(player, event.getCurrentItem());
                    session.updateGUI();
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        TradeSession session = tradeSessions.get(player.getUniqueId());

        if (session != null && event.getView().getTitle().equals("§6Wymiana")) {
            session.cancel();
        }
    }

    private class TradeSession {
        private Player player1;
        private Player player2;
        private boolean player1Accepted = false;
        private boolean player2Accepted = false;
        private Map<Player, ItemStack[]> items = new HashMap<>();

        public TradeSession(Player player1, Player player2) {
            this.player1 = player1;
            this.player2 = player2;
            items.put(player1, new ItemStack[45]);
            items.put(player2, new ItemStack[45]);
        }

        public void accept(Player player) {
            if (player.equals(player1)) {
                player1Accepted = true;
            } else if (player.equals(player2)) {
                player2Accepted = true;
            }

            if (player1Accepted && player2Accepted) {
                startCountdown();
            } else {
                player.sendMessage(messages.get("trade-accepted"));
            }
        }

        public void deny(Player player) {
            player.sendMessage(messages.get("trade-denied"));
            cancel();
        }

        public void cancel() {
            player1.closeInventory();
            player2.closeInventory();
            tradeSessions.remove(player1.getUniqueId());
            tradeSessions.remove(player2.getUniqueId());
        }

        public void updateItems(Player player, ItemStack item) {
            items.get(player)[player.getInventory().getHeldItemSlot()] = item;
        }

        public void updateGUI() {
            updatePlayerGUI(player1, items.get(player2));
            updatePlayerGUI(player2, items.get(player1));
        }

        private void updatePlayerGUI(Player player, ItemStack[] items) {
            Inventory gui = player.getOpenInventory().getTopInventory();
            for (int i = 0; i < 45; i++) {
                gui.setItem(i, items[i]);
            }
        }

        private void startCountdown() {
            new BukkitRunnable() {
                int countdown = 5;

                @Override
                public void run() {
                    if (countdown > 0) {
                        player1.sendMessage(messages.get("trade-countdown").replace("%seconds%", String.valueOf(countdown)));
                        player2.sendMessage(messages.get("trade-countdown").replace("%seconds%", String.valueOf(countdown)));
                        countdown--;
                    } else {
                        completeTrade();
                        cancel();
                    }
                }
            }.runTaskTimer(WOOLCODETRADE.this, 0, 20);
        }

        private void completeTrade() {
            player1.getInventory().addItem(items.get(player2));
            player2.getInventory().addItem(items.get(player1));
            player1.sendMessage(messages.get("trade-complete"));
            player2.sendMessage(messages.get("trade-complete"));
            cancel();
        }
    }
}
