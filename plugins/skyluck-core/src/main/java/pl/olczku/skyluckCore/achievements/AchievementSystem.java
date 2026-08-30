package pl.olczku.skyluckCore.achievements;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AchievementSystem implements CommandExecutor, Listener {

    private final OlczkuDannyPrzekKom plugin;
    private final Map<String, Achievement> achievements = new HashMap<>();
    private final Map<UUID, Set<String>> completedAchievements = new HashMap<>();

    public AchievementSystem(OlczkuDannyPrzekKom plugin) {
        this.plugin = plugin;
        loadAchievements();
        loadPlayerData();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cTylko gracze mogą używać tej komendy!");
            return true;
        }

        Player player = (Player) sender;
        openAchievementsGUI(player);
        return true;
    }

    private void openAchievementsGUI(Player player) {
        player.sendMessage("§6=== OSIĄGNIĘCIA ===");

        for (Achievement achievement : achievements.values()) {
            if (hasCompleted(player, achievement.getId())) {
                player.sendMessage("§a✓ " + achievement.getName() + " - Wykonane przez " + achievement.getCompletedBy());
            } else if (isCompleted(achievement.getId())) {
                player.sendMessage("§c✗ " + achievement.getName() + " - Zajęte przez " + achievement.getCompletedBy());
            } else {
                player.sendMessage("§7○ " + achievement.getName() + " - Dostępne");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material blockType = event.getBlock().getType();

        checkAchievement(player, "block-break", blockType.name(), 1);
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            String entityType = event.getEntityType().name();

            checkAchievement(player, "entity-kill", entityType, 1);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        checkAchievement(event.getPlayer(), "join", "join", 1);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo().getBlockY() >= 100) {
            checkAchievement(event.getPlayer(), "location", "height_100", 1);
        }
    }

    private void checkAchievement(Player player, String type, String target, int amount) {
        for (Achievement achievement : achievements.values()) {
            if (!achievement.getType().equals(type) || !achievement.getTarget().equals(target)) {
                continue;
            }

            if (isCompleted(achievement.getId())) {
                continue;
            }

            if (achievement.checkProgress(player, amount)) {
                completeAchievement(player, achievement);
            }
        }
    }

    private void completeAchievement(Player player, Achievement achievement) {
        achievement.setCompletedBy(player.getName());
        completedAchievements.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(achievement.getId());

        // Ogłoszenie na czacie
        if (plugin.getAchievementsConfig().getBoolean("settings.broadcast_achievements", true)) {
            Bukkit.broadcastMessage("§6§lOSIĄGNIĘCIE! §e" + player.getName() +
                    " §7wykonał(a) osiągnięcie: §a" + achievement.getName());
        }

        // Nagroda
        if (plugin.getAchievementsConfig().getBoolean("settings.give_rewards", true) &&
                achievement.getRewardCommand() != null) {
            String command = achievement.getRewardCommand().replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        savePlayerData();
        saveAchievements();
    }

    public boolean hasCompleted(Player player, String achievementId) {
        return completedAchievements.getOrDefault(player.getUniqueId(), Collections.emptySet())
                .contains(achievementId);
    }

    public boolean isCompleted(String achievementId) {
        Achievement achievement = achievements.get(achievementId);
        return achievement != null && achievement.getCompletedBy() != null;
    }

    private void loadAchievements() {
        FileConfiguration config = plugin.getAchievementsConfig();
        ConfigurationSection section = config.getConfigurationSection("achievements");

        if (section == null) {
            plugin.getLogger().warning("Brak sekcji 'achievements' w konfiguracji!");
            return;
        }

        for (String key : section.getKeys(false)) {
            String name = section.getString(key + ".name");
            String type = section.getString(key + ".type");
            String target = section.getString(key + ".target");
            int requiredAmount = section.getInt(key + ".required_amount");
            String rewardCommand = section.getString(key + ".reward_command");

            if (name == null || type == null || target == null) {
                plugin.getLogger().warning("Nieprawidłowa konfiguracja osiągnięcia: " + key);
                continue;
            }

            Achievement achievement = new Achievement(key, name, type, target, requiredAmount, rewardCommand);

            if (section.contains(key + ".completed_by")) {
                achievement.setCompletedBy(section.getString(key + ".completed_by"));
            }

            achievements.put(key, achievement);
        }
    }

    private void saveAchievements() {
        FileConfiguration config = plugin.getAchievementsConfig();

        for (Achievement achievement : achievements.values()) {
            String path = "achievements." + achievement.getId();
            config.set(path + ".completed_by", achievement.getCompletedBy());
        }

        try {
            config.save(new File(plugin.getDataFolder(), "achievements.yml"));
        } catch (IOException e) {
            plugin.getLogger().warning("Nie udało się zapisać achievements.yml: " + e.getMessage());
        }
    }

    private void loadPlayerData() {
        FileConfiguration config = plugin.getAchievementsDataConfig();

        for (String uuidString : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                Set<String> completed = new HashSet<>(config.getStringList(uuidString));
                completedAchievements.put(uuid, completed);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Nieprawidłowy UUID w achievements_data.yml: " + uuidString);
            }
        }
    }

    private void savePlayerData() {
        FileConfiguration config = plugin.getAchievementsDataConfig();

        // Wyczyść stary config
        for (String key : config.getKeys(false)) {
            config.set(key, null);
        }

        // Zapisz nowe dane
        for (Map.Entry<UUID, Set<String>> entry : completedAchievements.entrySet()) {
            config.set(entry.getKey().toString(), new ArrayList<>(entry.getValue()));
        }

        try {
            config.save(new File(plugin.getDataFolder(), "achievements_data.yml"));
        } catch (IOException e) {
            plugin.getLogger().warning("Nie udało się zapisać achievements_data.yml: " + e.getMessage());
        }
    }

    public ItemStack getAchievementHead(String achievementId, Player player) {
        Achievement achievement = achievements.get(achievementId);
        if (achievement == null) return null;

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6" + achievement.getName());

            List<String> lore = new ArrayList<>();
            if (achievement.getCompletedBy() != null) {
                lore.add("§7Wykonane przez: §a" + achievement.getCompletedBy());
                try {
                    meta.setOwningPlayer(Bukkit.getOfflinePlayer(achievement.getCompletedBy()));
                } catch (Exception e) {
                    plugin.getLogger().warning("Nie udało się ustawić właściciela głowy: " + e.getMessage());
                }
            } else {
                lore.add("§7Jeszcze nie wykonane");
            }
            lore.add("§7Typ: §e" + achievement.getType());
            lore.add("§7Cel: §e" + achievement.getTarget());
            lore.add("§7Wymagana ilość: §e" + achievement.getRequiredAmount());

            meta.setLore(lore);
            head.setItemMeta(meta);
        }

        return head;
    }
}