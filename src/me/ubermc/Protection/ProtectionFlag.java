package me.ubermc.Protection;

import org.bukkit.Material;

/**
 * Creates a new protection flag, Will be listed in Minecraft inventoryGUI,
 * Player will be able to toggle the flag, true/false. and necessary Bukkit events will be handled.
 */
public class ProtectionFlag {
    private String flag;
    private String displayname;
    private Material maticon;
    private boolean flagvalue;

    /** 
     * Class constructor.
     * 
     * @param flag  flag name used internally to handle bukkit events and saving to file.
     * @param flagvalue default value of the flag, false=Prevent Use, true=Allow Use.
     * @param displayname Name of the flag displayed in the Minecraft inventoryGUI viewed by the player.
     * @param maticon Material of the item displayed as an icon in the Minecraft inventoryGUI.
     */
    public ProtectionFlag(String flag, boolean flagvalue, String displayname, Material maticon) {
        this.flag = flag;
        this.flagvalue = flagvalue;
        this.displayname = displayname;
        this.maticon = maticon;
    }

    /**
     * Gets the flag name used internally for handling Bukkit Events
     */
    public String getFlagName() {
        return flag;
    }

    /**
     * Gets the name of the flag displayed in the Minecraft inventoryGUI viewed by the player.
     */
    public String getDisplayName() {
        return displayname;
    }

    /**
     * Gets the Material of the item displayed as an icon in the Minecraft inventoryGUI viewed by the player.
     */
    public Material getMaterialIcon() {
        return maticon;
    }

    /**
     * Gets the value of the flag, false=Prevent Use, true=Allow Use.
     */
    public boolean getFlagValue() {
        return flagvalue;
    }

    /**
     * Sets the value of the flag, false=Prevent Use, true=Allow Use.
     * @param flagvalue false = Prevent Use, true=Allow Use.
     */
    public boolean setFlagValue(boolean flagvalue) {
        return (this.flagvalue = flagvalue);
    }

    /**
     * Toggle the value of the flag.
     */
    public boolean toggleFlagValue() {
        return (this.flagvalue = !this.flagvalue);
    }
}
