package me.ubermc.Listeners;

import me.ubermc.Main.PSettings;
import me.ubermc.Protection.ProtectionManager;
import me.ubermc.Protection.ProtectionZone;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class EventsCommands implements Listener {

    private ProtectionManager pman;

    public EventsCommands(ProtectionManager pman) {
        System.out.println("Construct Commands Event, reference ProtectionUtil");
        this.pman = pman;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        if ((event.getMessage().equalsIgnoreCase("/protect"))) {
            if (event.getPlayer().getLocation().distance(Bukkit.getWorld(PSettings.worldName).getSpawnLocation()) <= 100) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + "You're too close to spawn!");
                return;
            }

            if (!pman.getPZone(pman.getPZoneLocationbyBlock(event.getPlayer().getLocation()).toString()).getOwnerUUID().equals("none")) {
                event.getPlayer().sendMessage("Cannot claim, this is already protected");
                // event.getPlayer().sendMessage("Cannot claim, this is already aprotected by:" + playerislandPSettings.get(getIslandLocationbyBlock(event.getPlayer().getLocation())).owner);
                event.setCancelled(true);
                return;
            }

            pman.getPZone(event.getPlayer().getLocation().toString()).changetoDefaultPlayerOwned(event.getPlayer().getUniqueId().toString());
            event.setCancelled(true);

            event.getPlayer().sendMessage(ChatColor.GREEN + "You have succesfully claimed this 30x30 chunk, type /protect preview to view");

            event.getPlayer().chat("/protect preview");

        }

        if ((event.getMessage().toLowerCase().contains("/protect setting"))) {
            pman.openSettingsMenu(event.getPlayer());

            event.setCancelled(true);
        }

        if ((event.getMessage().toLowerCase().contains("/protect preview"))) {

            pman.PreviewProtectionbyBlock(event.getPlayer(), event.getPlayer().getLocation());

            event.setCancelled(true);
        }

        if ((event.getMessage().toLowerCase().contains("/protect add"))) {
            ProtectionZone pzone = pman.getPZoneByPlayerLoc(event.getPlayer());
            if (pzone != null) {

                String playername = event.getMessage().substring(event.getMessage().indexOf(" ", 11) + 1);
                event.getPlayer().sendMessage("Adding player to whitelist:" + playername);
                pzone.addPlayertoWhiteList(playername);

            }
            event.setCancelled(true);
        }

        if ((event.getMessage().toLowerCase().contains("/protect remove"))) {
            ProtectionZone pzone = pman.getPZoneByPlayerLoc(event.getPlayer());
            if (pzone != null) {

                String playername = event.getMessage().substring(event.getMessage().indexOf(" ", 11) + 1);
                event.getPlayer().sendMessage("remove player to whitelist:" + playername);
                pzone.removePlayerfromWhiteList(playername);
            }
            event.setCancelled(true);
        }

        if ((event.getMessage().toLowerCase().contains("/protect delete"))) {
            ProtectionZone pzone = pman.getPZoneByPlayerLoc(event.getPlayer());
            if (pzone != null) {
                pzone.changetoUnclaimed();

                pman.PlayersZonesLoc.remove(event.getPlayer().getName());
                event.getPlayer().sendMessage("You've deleted your protection zone where you're standing");

            } else {
                event.getPlayer().sendMessage("You don't have a protection zone here");
            }
            event.setCancelled(true);
        }

    }

    /*
    if ((event.getMessage().toLowerCase().contains("/protect delete"))) {
        if (PlayersProtectedZone.containsKey(event.getPlayer().getName())) {
            Location protectlocation = PlayersProtectedZone.get(event.getPlayer().getName()).protectionlocation;
            playerislandPSettings.get(protectlocation.toString()).ChangePSettingstoUnclaimed(protectlocation);
            PlayersProtectedZone.get(event.getPlayer().getName()).Delete(event.getPlayer().getName());
            PlayersProtectedZone.remove(event.getPlayer().getName());
            event.getPlayer().sendMessage("You've deleted your protection zone");

        } else {
            event.getPlayer().sendMessage("You don't have a protection zone");
        }
        event.setCancelled(true);
    }
    */

}
