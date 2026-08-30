package pl.olczku.skyluckCore.achievements;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Achievement {
    private final String id;
    private final String name;
    private final String type;
    private final String target;
    private final int requiredAmount;
    private final String rewardCommand;
    private String completedBy;
    private final Map<UUID, Integer> playerProgress = new HashMap<>();

    public Achievement(String id, String name, String type, String target,
                       int requiredAmount, String rewardCommand) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.target = target;
        this.requiredAmount = requiredAmount;
        this.rewardCommand = rewardCommand;
    }

    public boolean checkProgress(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        int currentProgress = playerProgress.getOrDefault(uuid, 0) + amount;
        playerProgress.put(uuid, currentProgress);

        return currentProgress >= requiredAmount;
    }

    // Gettery i Settery
    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getTarget() { return target; }
    public int getRequiredAmount() { return requiredAmount; }
    public String getRewardCommand() { return rewardCommand; }
    public String getCompletedBy() { return completedBy; }
    public void setCompletedBy(String completedBy) { this.completedBy = completedBy; }
}