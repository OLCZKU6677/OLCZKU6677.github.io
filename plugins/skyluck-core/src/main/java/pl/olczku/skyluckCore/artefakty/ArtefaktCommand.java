package pl.olczku.skyluckCore.artefakty;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pl.olczku.skyluckCore.util.Messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArtefaktCommand implements CommandExecutor, TabCompleter {
    private final ArtefaktManager manager;
    private final Messages messages;

    public ArtefaktCommand(ArtefaktManager manager, Messages messages) {
        this.manager = manager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(messages.get("only-players"));
                return true;
            }

            Player gracz = (Player) sender;
            manager.otworzGUI(gracz);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "daj":
                return dajArtefakt(sender, args);
            case "usun":
                return usunArtefakt(sender, args);
            case "sprawdz":
                return sprawdzArtefakty(sender, args);
            case "reload":
                return reloadKonfiguracji(sender);
            case "lista":
                return listaArtefaktow(sender);
            case "przelacz":
                return przelaczArtefakt(sender, args);
            default:
                wyswietlPomoc(sender);
                return true;
        }
    }

    private boolean dajArtefakt(CommandSender sender, String[] args) {
        if (!sender.hasPermission("skyluckcore.artefakty.admin")) {
            sender.sendMessage(messages.get("no-permission"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§cUżycie: /artefakty daj <gracz> <artefakt>");
            return true;
        }

        Player cel = Bukkit.getPlayer(args[1]);
        if (cel == null) {
            sender.sendMessage("§cGracz " + args[1] + " nie jest online!");
            return true;
        }

        try {
            ArtefaktType typ = ArtefaktType.valueOf(args[2].toUpperCase());
            manager.dodajArtefakt(cel, typ);
            sender.sendMessage("§aDodano artefakt " + typ.name() + " graczowi " + cel.getName());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cNieznany typ artefaktu! Dostępne: " + Arrays.toString(ArtefaktType.values()));
        }

        return true;
    }

    private boolean przelaczArtefakt(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messages.get("only-players"));
            return true;
        }

        Player gracz = (Player) sender;

        if (args.length < 2) {
            sender.sendMessage("§cUżycie: /artefakty przelacz <artefakt>");
            return true;
        }

        try {
            ArtefaktType typ = ArtefaktType.valueOf(args[1].toUpperCase());
            manager.przelaczArtefakt(gracz, typ);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cNieznany typ artefaktu! Dostępne: " + Arrays.toString(ArtefaktType.values()));
        }

        return true;
    }

    private boolean usunArtefakt(CommandSender sender, String[] args) {
        if (!sender.hasPermission("skyluckcore.artefakty.admin")) {
            sender.sendMessage(messages.get("no-permission"));
            return true;
        }

        sender.sendMessage("§cFunkcjonalność usuwania artefaktów nie jest jeszcze zaimplementowana.");
        return true;
    }

    private boolean sprawdzArtefakty(CommandSender sender, String[] args) {
        if (!sender.hasPermission("skyluckcore.artefakty.admin")) {
            sender.sendMessage(messages.get("no-permission"));
            return true;
        }

        Player cel;
        if (args.length >= 2) {
            cel = Bukkit.getPlayer(args[1]);
            if (cel == null) {
                sender.sendMessage("§cGracz " + args[1] + " nie jest online!");
                return true;
            }
        } else if (sender instanceof Player) {
            cel = (Player) sender;
        } else {
            sender.sendMessage("§cPodaj nazwę gracza: /artefakty sprawdz <gracz>");
            return true;
        }

        sender.sendMessage("§6Artefakty gracza " + cel.getName() + ":");
        for (ArtefaktType typ : ArtefaktType.values()) {
            boolean posiada = manager.czyPosiadaArtefakt(cel, typ);
            boolean wlaczony = manager.czyArtefaktWlaczony(cel, typ);
            sender.sendMessage("§7- " + typ.name() + ": " + (posiada ? (wlaczony ? "§aTAK" : "§cNIE (wyłączony)") : "§cNIE"));
        }
        return true;
    }

    private boolean reloadKonfiguracji(CommandSender sender) {
        if (!sender.hasPermission("skyluckcore.artefakty.admin")) {
            sender.sendMessage(messages.get("no-permission"));
            return true;
        }

        manager.zaladujKonfiguracje();
        sender.sendMessage("§aKonfiguracja artefaktów została przeładowana!");
        return true;
    }

    private boolean listaArtefaktow(CommandSender sender) {
        sender.sendMessage("§6Lista dostępnych artefaktów:");
        for (ArtefaktType typ : ArtefaktType.values()) {
            sender.sendMessage("§7- " + typ.name() + " §8(" + typ.getConfigKey() + ")");
        }
        return true;
    }

    private void wyswietlPomoc(CommandSender sender) {
        if (sender.hasPermission("skyluckcore.artefakty.admin")) {
            sender.sendMessage("§6=== Pomoc - System Artefaktów ===");
            sender.sendMessage("§b/artefakty §f- Otwiera GUI artefaktów");
            sender.sendMessage("§b/artefakty daj <gracz> <artefakt> §f- Daje artefakt graczowi");
            sender.sendMessage("§b/artefakty usun <gracz> <artefakt> §f- Usuwa artefakt graczowi");
            sender.sendMessage("§b/artefakty sprawdz [gracz] §f- Sprawdza artefakty gracza");
            sender.sendMessage("§b/artefakty lista §f- Pokazuje listę artefaktów");
            sender.sendMessage("§b/artefakty przelacz <artefakt> §f- Przełącza artefakt");
            sender.sendMessage("§b/artefakty reload §f- Przeładowuje konfigurację");
        } else {
            sender.sendMessage("§6Użycie: §b/artefakty §f- Otwiera GUI artefaktów");
            sender.sendMessage("§b/artefakty przelacz <artefakt> §f- Przełącza artefakt");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("skyluckcore.artefakty.admin")) {
                suggestions.addAll(Arrays.asList("daj", "usun", "sprawdz", "lista", "reload", "przelacz"));
            } else {
                suggestions.add("przelacz");
            }
            suggestions.add("");
        } else if (args.length == 2) {
            if (sender.hasPermission("skyluckcore.artefakty.admin")) {
                switch (args[0].toLowerCase()) {
                    case "daj":
                    case "usun":
                    case "sprawdz":
                        // Sugestie graczy online
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            suggestions.add(player.getName());
                        }
                        break;
                    case "przelacz":
                        // Sugestie typów artefaktów
                        for (ArtefaktType typ : ArtefaktType.values()) {
                            suggestions.add(typ.name());
                        }
                        break;
                }
            } else if (args[0].equalsIgnoreCase("przelacz")) {

                for (ArtefaktType typ : ArtefaktType.values()) {
                    suggestions.add(typ.name());
                }
            }
        } else if (args.length == 3) {
            if (sender.hasPermission("skyluckcore.artefakty.admin")) {
                if (args[0].equalsIgnoreCase("daj") || args[0].equalsIgnoreCase("usun")) {

                    for (ArtefaktType typ : ArtefaktType.values()) {
                        suggestions.add(typ.name());
                    }
                }
            }
        }


        if (!args[args.length - 1].isEmpty()) {
            suggestions.removeIf(s -> !s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
        }

        return suggestions;
    }
}