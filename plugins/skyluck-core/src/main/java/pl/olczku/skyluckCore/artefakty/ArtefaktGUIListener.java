package pl.olczku.skyluckCore.artefakty;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import pl.olczku.skyluckCore.Przekierowanie.OlczkuDannyPrzekKom;

public class ArtefaktGUIListener implements Listener {
    private final ArtefaktManager manager;
    private final OlczkuDannyPrzekKom plugin;

    public ArtefaktGUIListener(ArtefaktManager manager, OlczkuDannyPrzekKom plugin) {
        this.manager = manager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        InventoryView view = event.getView();
        String guiTitle = plugin.getArtefaktyConfig().getString("gui.tytul", "§6System Artefaktów");

        // Sprawdź czy to GUI artefaktów
        if (ChatColor.stripColor(view.getTitle()).equals(ChatColor.stripColor(guiTitle))) {

            // Pozwól na wyciąganie przedmiotów z inventory gracza (dolna część)
            if (event.getRawSlot() >= event.getView().getTopInventory().getSize()) {
                return; // Pozwól na normalne działanie w inventory gracza
            }

            // Sprawdź czy kliknięto w slot z artefaktem
            boolean isArtefaktSlot = false;
            for (ArtefaktType typ : ArtefaktType.values()) {
                int slot = typ.getGUISlot(plugin);
                if (event.getSlot() == slot) {
                    isArtefaktSlot = true;
                    break;
                }
            }

            // Jeśli kliknięto w pusty slot lub poza artefaktami, pozwól wyciągać przedmioty
            if (!isArtefaktSlot && event.getCurrentItem() != null) {
                return; // Pozwól na normalne wyciąganie przedmiotów
            }

            // Jeśli kliknięto w slot z artefaktem, obsłuż to
            event.setCancelled(true);

            if (event.getCurrentItem() == null) return;

            for (ArtefaktType typ : ArtefaktType.values()) {
                int slot = typ.getGUISlot(plugin);
                if (event.getSlot() == slot) {
                    if (event.isLeftClick()) {
                        // Lewy przycisk - wyciąganie artefaktu
                        manager.dajFizycznyArtefakt(player, typ);
                    } else if (event.isRightClick()) {
                        // Prawy przycisk - przełączanie artefaktu
                        manager.przelaczArtefakt(player, typ);
                        // Odśwież GUI
                        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                            manager.otworzGUI(player);
                        }, 1L);
                    }
                    break;
                }
            }
        }
    }
}