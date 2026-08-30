package pl.olczku.skyluckCore.cooldown;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;
import java.util.*;

public class SkyluckCooldown implements Listener {
    private final OlczkuDannyPrzekKom core;
    private final JavaPlugin plugin;
    private final Map<UUID, OpeningSession> sessions = new HashMap<>();
    private final Map<UUID, Map<String, Long>> chestCooldowns;
    private final Random rng = new Random();
    private final long delaySeconds;
    private final long chestCooldownMillis;

    public SkyluckCooldown(OlczkuDannyPrzekKom core) {
        this.core = core;
        this.plugin = core;
        this.chestCooldowns = core.cooldowns();
        this.delaySeconds = plugin.getConfig().getLong("opening_delay_seconds", 15L);
        long chestCooldownHours = plugin.getConfig().getLong("chest_cooldown_hours", 24L);
        this.chestCooldownMillis = chestCooldownHours * 60 * 60 * 1000L;
    }

    @EventHandler
    public void onChestInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.CHEST) return;
        if (!block.getWorld().getName().equalsIgnoreCase(core.allowedWorld())) return;

        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        Location chestLoc = block.getLocation().clone();
        String chestKey = OlczkuDannyPrzekKom.chestKey(chestLoc);

        long now = System.currentTimeMillis();
        long expireTime = chestCooldowns.computeIfAbsent(id, k -> new HashMap<>()).getOrDefault(chestKey, 0L);
        if (expireTime > now) {
            long left = expireTime - now;
            long hours = left / (1000 * 60 * 60);
            long minutes = (left / (1000 * 60)) % 60;
            core.messages().send(player, "chest_cooldown", Map.of("hours", String.valueOf(hours), "minutes", String.valueOf(minutes)));
            event.setCancelled(true);
            return;
        }

        if (sessions.containsKey(id)) {
            core.messages().send(player, "already_opening");
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        Location startLoc = player.getLocation().clone();

        BukkitTask task = new BukkitRunnable() {
            private long timeLeft = delaySeconds;

            @Override
            public void run() {
                if (!sessions.containsKey(id)) {
                    this.cancel();
                    return;
                }

                if (timeLeft > 0) {
                    String message = core.messages().get("start_opening", Map.of("seconds", String.valueOf(timeLeft)));
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
                    timeLeft--;
                } else {
                    this.cancel();
                    OpeningSession s = sessions.remove(id);
                    if (s == null) return;

                    Block b = chestLoc.getBlock();
                    if (b.getType() != Material.CHEST) {
                        core.messages().send(player, "chest_missing");
                        return;
                    }
                    Chest chest = (Chest) b.getState();
                    Inventory inv = chest.getInventory();
                    applySingleDropForChest(player, chestLoc, inv);
                    player.openInventory(inv);
                    core.messages().send(player, "chest_opened");
                    chestCooldowns.computeIfAbsent(id, k -> new HashMap<>()).put(chestKey, System.currentTimeMillis() + chestCooldownMillis);
                    core.saveData();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);

        sessions.put(id, new OpeningSession(startLoc, chestLoc, task));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        OpeningSession s = sessions.get(p.getUniqueId());
        if (s == null) return;
        if (changedBlockCoords(event)) {
            cancelSession(p.getUniqueId());
            core.messages().send(p, "moved_cancel");
        }
    }

    @EventHandler public void onQuit(PlayerQuitEvent e) { cancelSession(e.getPlayer().getUniqueId()); }
    @EventHandler public void onDeath(PlayerDeathEvent e) { cancelSession(e.getEntity().getUniqueId()); }

    private void applySingleDropForChest(Player player, Location chestLoc, Inventory inv) {
        String key = "drops." + OlczkuDannyPrzekKom.chestKey(chestLoc);
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection(key);
        if (sec == null) return;
        boolean hasBonus = player.hasPermission("skyluck.drop.bonus20");
        List<WeightedEntry> pool = new ArrayList<>();
        double totalWeight = 0.0;
        for (String id : sec.getKeys(false)) {
            ConfigurationSection e = sec.getConfigurationSection(id);
            if (e == null) continue;
            double baseChance = e.getDouble("chance", 0.0);
            double effective = baseChance + (hasBonus ? 20.0 : 0.0);
            if (effective <= 0.0) continue;
            if (effective > 100.0) effective = 100.0;
            ItemStack stack = e.getItemStack("item");
            if (stack == null) {
                String matName = e.getString("material", "");
                int amount = e.getInt("amount", 1);
                var mat = OlczkuDannyPrzekKom.safeMaterial(matName);
                if (mat == null || mat == Material.AIR) continue;
                if (amount <= 0) amount = 1;
                stack = new ItemStack(mat, amount);
            } else {
                stack = stack.clone();
            }
            pool.add(new WeightedEntry(stack, effective));
            totalWeight += effective;
        }
        if (pool.isEmpty() || totalWeight <= 0.0) return;
        double r = rng.nextDouble() * totalWeight;
        double acc = 0.0;
        ItemStack chosen = null;
        for (WeightedEntry we : pool) {
            acc += we.weight;
            if (r <= acc) { chosen = we.stack; break; }
        }
        if (chosen == null) chosen = pool.get(pool.size() - 1).stack;
        var leftovers = inv.addItem(chosen);
        if (!leftovers.isEmpty()) leftovers.values().forEach(item -> chestLoc.getWorld().dropItemNaturally(chestLoc.clone().add(0.5, 1.0, 0.5), item));
    }

    private boolean changedBlockCoords(PlayerMoveEvent e) {
        if (e.getTo() == null) return false;
        return e.getFrom().getBlockX() != e.getTo().getBlockX()
                || e.getFrom().getBlockY() != e.getTo().getBlockY()
                || e.getFrom().getBlockZ() != e.getTo().getBlockZ();
    }

    private void cancelSession(UUID uuid) {
        OpeningSession s = sessions.remove(uuid);
        if (s != null) s.task.cancel();
    }

    private static class OpeningSession {
        final Location startLoc;
        final Location chestLoc;
        final BukkitTask task;
        OpeningSession(Location startLoc, Location chestLoc, BukkitTask task) { this.startLoc = startLoc; this.chestLoc = chestLoc; this.task = task; }
    }

    private static class WeightedEntry {
        final ItemStack stack; final double weight;
        WeightedEntry(ItemStack stack, double weight) { this.stack = stack; this.weight = weight; }
    }
}