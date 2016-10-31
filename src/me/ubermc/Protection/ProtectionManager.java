package me.ubermc.Protection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import me.ubermc.Main.PSettings;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ProtectionManager {
    boolean cycle = true;
    long iT;
    long eT;
    //Set is going to be massive, O(1) is important
    private HashMap<String, ProtectionZone> loadedPZones = new HashMap<String, ProtectionZone>();
    //It's impossible to refer to loadedPZones by player, without the algorithm being O=n^2 which is detrimential to large data

    public HashMap<String, HashSet<String>> PlayersZonesLoc = new HashMap<String, HashSet<String>>();

    public Location getPZoneLocationbyBlock(Location loc) {

        long islandx = Math.round(loc.getBlockX() / (double) PSettings.pzonesize) * PSettings.pzonesize;
        long islandz = Math.round(loc.getBlockZ() / (double) PSettings.pzonesize) * PSettings.pzonesize;

        // System.out.println("Island X:" + Long.toString(islandx) + "Island Z:" + Long.toString(islandz));
        Location islandloc = new Location(Bukkit.getWorld(PSettings.worldName), islandx, 70, islandz);
        return islandloc;
    }

    private void timings() {

        if (cycle) {
            iT = System.currentTimeMillis();
        } else {
            eT = System.currentTimeMillis();
            System.out.println("Timing:" + Long.toString(eT - iT) + " (ms)");
        }
        cycle = !cycle;
    }

    private void timings(String type) {

        if (cycle) {
            iT = System.currentTimeMillis();
        } else {
            eT = System.currentTimeMillis();
            System.out.println(type + " Timing:" + Long.toString(eT - iT) + " (ms)");
        }
        cycle = !cycle;
    }

    public void createPZone(ProtectionZone pzone) {
        String zloc = pzone.getLocation().toString();
        loadedPZones.put(zloc, pzone);
        if (!pzone.getOwnerUUID().equals("none")) {
            if (PlayersZonesLoc.containsKey(pzone.getOwnerUUID().toString())) {
                PlayersZonesLoc.get(pzone.getOwnerUUID().toString()).add(zloc);
            } else {
                PlayersZonesLoc.put(pzone.getOwnerUUID(), new HashSet<String>());
            }
        }
    }

    public ProtectionZone getPZone(String zloc) {
        if (loadedPZones.containsKey(zloc)) {
            return loadedPZones.get(zloc);
        }
        return null;
    }

    public HashSet<ProtectionZone> getPZones(Player player) {
        //This is O=(1) since HashMap.get is 0=(1)
        HashSet<ProtectionZone> playerspzones = new HashSet<ProtectionZone>();
        for (String zloc : PlayersZonesLoc.get(player.getUniqueId().toString())) {
            playerspzones.add(loadedPZones.get(zloc));
        }

        return playerspzones;
    }

    public boolean hasFullPermission(Player player, Location interactableloc) {
        Location playerloc = getPZoneLocationbyBlock(player.getLocation());
        Location islandloc = getPZoneLocationbyBlock(interactableloc);

        if (loadedPZones.get(islandloc.toString()).getOwnerUUID().equals(player.getUniqueId().toString()) || loadedPZones.get(islandloc.toString()).getWhiteListUUID().contains(player.getUniqueId().toString())) {
            if (playerloc.getBlockX() == islandloc.getBlockX() && playerloc.getBlockZ() == islandloc.getBlockZ()) {
                return true;
            }
        }
        return false;
    }

    public boolean isSettingAllowed(String settingname, Location islandloc) {

        if (islandloc.getBlockY() >= 45 || settingname.equals("chunk")) {
            if (loadedPZones.containsKey(islandloc.toString())) {
                if (settingname.equals("chunk")) {
                    return true;
                }
                if (loadedPZones.get(islandloc.toString()).getFlagValue(settingname)) {
                    return true;
                } else {
                    return false;
                }
            }

            // create and load into memory
            else {
                ProtectionZone isetting = new ProtectionZone(this, islandloc);
                if (settingname.equals("chunk")) {
                    return true;
                }
                if (isetting.getFlagValue(settingname)) {
                    return true;
                } else {
                    return false;
                }

            }
        } else {
            //Protection zone cannot be underground.
            return true;
        }
    }

    public void PreviewProtectionbyBlock(Player player, Location loc) {
        timings("[Preview Protection]");
        long pzonex = getPZoneLocationbyBlock(loc).getBlockX();
        long pzonez = getPZoneLocationbyBlock(loc).getBlockZ();
        int halfpzonesize = PSettings.pzonesize / 2;

        for (long x = (pzonex - halfpzonesize); x <= (pzonex + halfpzonesize); x += 1) {
            boolean forcexeffect = false;
            if (x == (pzonex + halfpzonesize) || x == (pzonex - halfpzonesize)) {
                forcexeffect = true;
            }
            for (long z = (pzonez - halfpzonesize); z <= (pzonez + halfpzonesize); z += 1) {
                boolean forcezeffect = false;
                if (z == (pzonez + halfpzonesize) || z == (pzonez - halfpzonesize)) {
                    forcezeffect = true;
                }
                for (long y = (loc.getBlockY() - 10); y <= (loc.getBlockY() + 10); y += 1) {
                    if (forcezeffect || forcexeffect) {
                        if ((x % 5 == 0) && (z % 5 == 0)) {
                            player.playEffect(new Location(Bukkit.getWorld(PSettings.worldName), x, y, z), Effect.HEART, 0);
                        }
                    } else {
                        if (loc.getWorld().getBlockTypeIdAt((int) x, (int) y + 1, (int) z) == 0) {

                            if (loc.getWorld().getBlockTypeIdAt((int) x, (int) y, (int) z) != 0) {

                                player.playEffect(new Location(Bukkit.getWorld(PSettings.worldName), x, y + 1, z), Effect.NOTE, 0);
                            }
                        }

                    }

                }
            }
        }
        timings("[Preview Protection]");
    }

    public void openSettingsMenu(Player player) {

        Inventory inv = Bukkit.createInventory(player, 36, "Visitor Settings");

        int count = 0;
        for (ItemStack is : BuildSettingInventory(player)) {
            inv.setItem(count, is);
            count = count + 1;

        }
        player.openInventory(inv);
    }

    public ProtectionZone getPZoneByPlayerLoc(Player player) {

        HashSet<ProtectionZone> pzones = getPZones(player);

        ProtectionZone pzone = null;

        for (ProtectionZone tmppzone : pzones) {
            if (tmppzone.getLocation().toString().equals(getPZoneLocationbyBlock(player.getLocation()).toString())) {
                pzone = tmppzone;
            }
        }
        return pzone;
    }

    //Do this based on protection area
    private ArrayList<ItemStack> BuildSettingInventory(Player player) {

        ArrayList<ItemStack> settinginv = new ArrayList<ItemStack>();
        ProtectionZone pzone = getPZoneByPlayerLoc(player);
        if (pzone != null) {
            for (ProtectionFlag pflag : pzone.getAllFlags()) {

                String displayname = pflag.getDisplayName();
                boolean toggle = pflag.getFlagValue();
                ItemStack is = new ItemStack(pflag.getMaterialIcon());
                ItemMeta im = is.getItemMeta();
                im.setDisplayName(displayname);
                List<String> lore = new ArrayList<String>();

                if (toggle) {
                    lore.add(ChatColor.GREEN + "Yes");
                } else {
                    lore.add(ChatColor.RED + "No");
                }
                im.setLore(lore);
                is.setItemMeta(im);
                settinginv.add(is);
            }

        }
        return settinginv;
    }
}
