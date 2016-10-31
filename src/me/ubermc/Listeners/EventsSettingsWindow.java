package me.ubermc.Listeners;

import me.ubermc.Protection.ProtectionManager;
import me.ubermc.Protection.ProtectionZone;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class EventsSettingsWindow implements Listener {

    ProtectionManager pman;

    public EventsSettingsWindow(ProtectionManager pman) {
        this.pman = pman;
    }

    @EventHandler
    public void guiClick(InventoryClickEvent event) {

        if (event.getInventory().getName().toLowerCase().contains("visitor settings")) {
            if (event.getWhoClicked() instanceof Player) {
                ItemStack clickeditem = event.getCurrentItem();
                if (clickeditem.getType() != null) {
                    Player player = (Player) event.getWhoClicked();
                    ProtectionZone pzone = pman.getPZoneByPlayerLoc(player);
                    if (clickeditem.getType() != Material.AIR) {
                        pzone.getFlag(clickeditem.getType()).toggleFlagValue();
                        player.closeInventory();
                        player.chat("/protect");
                    }
                }
            }
        }
    }
}
