package me.ubermc.Protection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import me.ubermc.Main.PSettings;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

//Should not need to create any maps linking player to this file, this file links the player...
public class ProtectionZone {
    private String owneruuid;
    private HashSet<String> whitelistuuid = new HashSet<String>();
    //we care about ordering, so that we can display nicely in gui, also for small set arraylist outperforms barely
    private ArrayList<ProtectionFlag> flags = new ArrayList<ProtectionFlag>();
    private String fileName;
    private Location ploc;
    ProtectionManager pman;

    // cunstructor
    public ProtectionZone(ProtectionManager pman, Location islandloc) {
        this.pman = pman;
        ploc = islandloc;
        flags.add(new ProtectionFlag("anvil", true, "Anvil Use", Material.ANVIL));
        flags.add(new ProtectionFlag("armorstand", true, "Armor Stand Use", Material.ARMOR_STAND));
        flags.add(new ProtectionFlag("beacon", true, "Beacon Use", Material.BEACON));
        flags.add(new ProtectionFlag("bed", true, "Bed Use", Material.BED));
        flags.add(new ProtectionFlag("breaking", true, "Block Breaking", Material.IRON_PICKAXE));
        flags.add(new ProtectionFlag("breeding", true, "Breeding", Material.CARROT));
        flags.add(new ProtectionFlag("brewing", true, "Brewstand Use", Material.BREWING_STAND_ITEM));
        flags.add(new ProtectionFlag("expbottle", true, "EXP Bottle Use", Material.EXP_BOTTLE));
        flags.add(new ProtectionFlag("bucket", true, "Bucket Use", Material.BUCKET));
        flags.add(new ProtectionFlag("chest", true, "Chest Use", Material.CHEST));
        flags.add(new ProtectionFlag("dropitems", true, "Drop Items", Material.BONE));
        flags.add(new ProtectionFlag("dispenser", true, "Dispenser/Dropper/Hopper", Material.DISPENSER));
        flags.add(new ProtectionFlag("workbench", true, "Crafting Table Use", Material.WORKBENCH));
        flags.add(new ProtectionFlag("trampling", true, "Trample Crops", Material.WHEAT));
        flags.add(new ProtectionFlag("door", true, "Door Use", Material.WOOD_DOOR));
        flags.add(new ProtectionFlag("enchantmenttable", true, "Enchantment Table Use", Material.ENCHANTMENT_TABLE));
        flags.add(new ProtectionFlag("enderpearl", true, "Ender Pearl Use", Material.ENDER_PEARL));
        flags.add(new ProtectionFlag("furnace", true, "Furnace Use", Material.FURNACE));
        flags.add(new ProtectionFlag("gate", true, "Gate Use", Material.FENCE_GATE));
        flags.add(new ProtectionFlag("horse", true, "Horse Interacting", Material.SADDLE));
        flags.add(new ProtectionFlag("leash", true, "Leash Use", Material.LEASH));
        flags.add(new ProtectionFlag("lever", true, "Lever Use", Material.LEVER));
        flags.add(new ProtectionFlag("jukebox", true, "JukeBox Use", Material.JUKEBOX));
        flags.add(new ProtectionFlag("place", true, "Block Placing", Material.DIRT));
        flags.add(new ProtectionFlag("pressure", true, "Pressure Plate Use", Material.STONE_PLATE));
        flags.add(new ProtectionFlag("tripwire", true, "TripWire Use", Material.TRIPWIRE_HOOK));
        flags.add(new ProtectionFlag("pvp", true, "PvP", Material.DIAMOND_SWORD));
        flags.add(new ProtectionFlag("redstone", true, "Redstone Adjustment", Material.DIODE));
        flags.add(new ProtectionFlag("shears", true, "Shears Use", Material.SHEARS));
        flags.add(new ProtectionFlag("walk", true, "Can Players be here at all", Material.IRON_BOOTS));
        flags.add(new ProtectionFlag("mobdmg", true, "Damage Mobs/Animals", Material.EGG));

        File dir = new File(PSettings.dir_pzones + File.separator);
        if (!dir.exists()) {
            System.out.println("create dir:" + dir.toString());
            dir.mkdir();
        }

        fileName = PSettings.dir_pzones + File.separator + islandloc.getBlockX() + "," + islandloc.getBlockZ() + ".yml";
        File file = new File(fileName);

        if (file.exists()) {
            YamlConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
            for (ProtectionFlag pflag : flags) {
                pflag.setFlagValue(fileConfig.getBoolean(pflag.getFlagName()));

            }
            owneruuid = fileConfig.getString("owner");
            whitelistuuid = new HashSet<String>(Arrays.asList(fileConfig.getString("whitelist").split(",")));
        }
        // createnewdefault
        else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            changetoUnclaimed();
        }

        pman.createPZone(this);
    }

    // returns the new setting value (true/false) to update inventory...
    public void changeSetting(Material setting) {
        File file = new File(fileName);
        YamlConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
        for (ProtectionFlag pflag : flags) {
            if (pflag.getMaterialIcon() == setting) {
                pflag.toggleFlagValue();
                fileConfig.set(pflag.getFlagName(), pflag.getFlagValue());
            }
        }

        try {
            fileConfig.save(file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addPlayertoWhiteList(String playername) {
        File file = new File(fileName);
        YamlConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
        if (Bukkit.getPlayer(playername) != null) {
            String playeruuid = Bukkit.getPlayer(playername).getUniqueId().toString();
            if (Bukkit.getPlayer(UUID.fromString(playeruuid)) != null) { //ensures it's a clean string....
                if (!whitelistuuid.contains(playeruuid)) {
                    whitelistuuid.add(playeruuid);

                    String whiteliststring = "";
                    for (String whiteplayer : whitelistuuid) {
                        if (whiteliststring.equals("")) {
                            whiteliststring = whiteliststring + whiteplayer;
                        } else {
                            whiteliststring = whiteliststring + "," + whiteplayer;
                        }
                    }
                    fileConfig.set("whitelist", whiteliststring);
                    try {
                        fileConfig.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public void removePlayerfromWhiteList(String playername) {
        File file = new File(fileName);
        YamlConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
        if (Bukkit.getPlayer(playername) != null) {
            String playeruuid = Bukkit.getPlayer(playername).getUniqueId().toString();
            if (Bukkit.getPlayer(UUID.fromString(playeruuid)) != null) { //ensures it's a clean string....
                if (whitelistuuid.contains(playeruuid)) {
                    whitelistuuid.remove(playeruuid);

                    String whiteliststring = "";
                    for (String whiteplayer : whitelistuuid) {
                        if (whiteliststring.equals("")) {
                            whiteliststring = whiteliststring + whiteplayer;
                        } else {
                            whiteliststring = whiteliststring + "," + whiteplayer;
                        }
                    }
                    fileConfig.set("whitelist", whiteliststring);
                    try {
                        fileConfig.save(file);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void changetoDefaultPlayerOwned(String playeruuid) {
        File file = new File(fileName);
        YamlConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
        Player player = Bukkit.getPlayer(UUID.fromString(playeruuid));
        if (player != null) {

            for (ProtectionFlag pflag : flags) {
                fileConfig.set(pflag.getFlagName(), pflag.setFlagValue(true));
            }

            owneruuid = playeruuid;
            whitelistuuid.clear();
            fileConfig.set("owner", playeruuid);
            fileConfig.set("whitelist", "");

            try {
                fileConfig.save(file);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        pman.createPZone(this);

    }

    public void changetoUnclaimed() {
        File file = new File(fileName);
        YamlConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);

        for (ProtectionFlag pflag : flags) {
            fileConfig.set(pflag.getFlagName(), pflag.setFlagValue(true));
        }
        String oldowneruuid = owneruuid + "";

        owneruuid = "none";
        whitelistuuid.clear();
        fileConfig.set("owner", owneruuid);
        fileConfig.set("whitelist", "");

        try {
            fileConfig.save(file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        pman.PlayersZonesLoc.remove(oldowneruuid);
    }

    public boolean getFlagValue(String flagname) {
        for (ProtectionFlag pflag : flags) {
            if (pflag.getFlagName().equals(flagname)) {
                return pflag.getFlagValue();
            }
        }
        System.out.println("flagname does not exist!, problem in code report to developer!");
        return false;
    }

    public String getOwnerUUID() {
        return owneruuid;
    }

    public HashSet<String> getWhiteListUUID() {
        return whitelistuuid;
    }

    public ArrayList<ProtectionFlag> getAllFlags() {
        return flags;
    }

    public Location getLocation() {
        return ploc;
    }

    public ProtectionFlag getFlag(String flagname) {
        for (ProtectionFlag pflag : flags) {
            if (pflag.getFlagName().equals(flagname) || pflag.getDisplayName().equals(flagname)) {
                return pflag;
            }
        }
        return null;
    }

    public ProtectionFlag getFlag(Material maticon) {
        for (ProtectionFlag pflag : flags) {
            if (pflag.getMaterialIcon() == maticon) {
                return pflag;
            }
        }
        return null;
    }

}
