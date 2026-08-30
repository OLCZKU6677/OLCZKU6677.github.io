package pl.olczku.skyluckCore.gamma;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.olczku.skyluckCore.util.Messages;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GammaCommand implements CommandExecutor {
    private final Messages messages;
    private final Map<UUID, Boolean> gammaStates = new HashMap<>();

    public GammaCommand(Messages messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cTylko gracze mogą używać tej komendy!");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        // Sprawdzanie czy gracz ma night vision
        boolean hasNightVision = player.hasPotionEffect(PotionEffectType.NIGHT_VISION);
        boolean gammaEnabled = gammaStates.getOrDefault(playerId, false);

        if (hasNightVision || gammaEnabled) {
            // Wyłączanie gamma
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            gammaStates.put(playerId, false);
            messages.send(player, "gamma_disabled");
        } else {
            // Włączanie gamma
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.NIGHT_VISION,
                    PotionEffect.INFINITE_DURATION,
                    0,
                    false,
                    false,
                    false
            ));
            gammaStates.put(playerId, true);
            messages.send(player, "gamma_enabled");
        }

        return true;
    }

    public void removeGammaEffect(Player player) {
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        gammaStates.remove(player.getUniqueId());
    }
}