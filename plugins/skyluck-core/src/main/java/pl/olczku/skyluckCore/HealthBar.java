package pl.olczku.skyluckCore;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HealthBar extends JavaPlugin implements Listener {

    private final Map<UUID, BukkitTask> activeHealthBars = new HashMap<>();
    private final String HEART_SYMBOL = "❤";

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        for (BukkitTask task : activeHealthBars.values()) {
            task.cancel();
        }
        activeHealthBars.clear();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof LivingEntity) || event.getEntity() instanceof Player) return;

        Player attacker = (Player) event.getDamager();
        LivingEntity entity = (LivingEntity) event.getEntity();
        UUID attackerId = attacker.getUniqueId();

        if (activeHealthBars.containsKey(attackerId)) {
            activeHealthBars.get(attackerId).cancel();
        }

        BukkitTask healthBarTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (entity.isDead() || !attacker.isOnline()) {
                activeHealthBars.get(attackerId).cancel();
                activeHealthBars.remove(attackerId);
                return;
            }

            String healthBarText = generateHealthBarText(entity);
            sendActionBar(attacker, healthBarText);

        }, 0L, 1L);

        activeHealthBars.put(attackerId, healthBarTask);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            BukkitTask currentTask = activeHealthBars.get(attackerId);
            if (currentTask != null && currentTask.getTaskId() == healthBarTask.getTaskId()) {
                currentTask.cancel();
                activeHealthBars.remove(attackerId);
            }
        }, 60L);
    }

    private String generateHealthBarText(LivingEntity entity) {
        double currentHealth = Math.max(0, entity.getHealth());
        double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

        ChatColor barColor;
        if (currentHealth / maxHealth > 0.5) {
            barColor = ChatColor.GREEN;
        } else if (currentHealth / maxHealth > 0.2) {
            barColor = ChatColor.YELLOW;
        } else {
            barColor = ChatColor.RED;
        }

        int totalBars = 20;
        int filledBars = (int) Math.round((currentHealth / maxHealth) * totalBars);

        StringBuilder healthBar = new StringBuilder();
        healthBar.append(barColor);
        for (int i = 0; i < filledBars; i++) {
            healthBar.append('▌');
        }

        healthBar.append(ChatColor.GRAY);
        for (int i = 0; i < totalBars - filledBars; i++) {
            healthBar.append('▌');
        }

        return String.format("%s%s %s(%.1f / %.1f) %s[%s]",
                ChatColor.WHITE, entity.getName(),
                ChatColor.RED + HEART_SYMBOL,
                currentHealth, maxHealth,
                ChatColor.WHITE, healthBar.toString() + ChatColor.WHITE + "]"
        );
    }

    private void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
}