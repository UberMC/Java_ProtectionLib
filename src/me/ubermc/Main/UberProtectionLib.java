package me.ubermc.Main;

import java.io.File;

import me.ubermc.Listeners.EventsCommands;
import me.ubermc.Listeners.EventsProtection;
import me.ubermc.Listeners.EventsSettingsWindow;
import me.ubermc.Protection.ProtectionManager;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class UberProtectionLib extends JavaPlugin {
    PluginDescriptionFile pdf = this.getDescription();
    static UberProtectionLib plugin;
    public ProtectionManager pman;

    public void onEnable() {
        pman = new ProtectionManager();
        plugin = this;
        System.out.println("Enabling :" + pdf.getName() + " version:" + pdf.getVersion());

        PSettings.dir_pzones = getDataFolder() + File.separator + "pzones";
        PSettings.pzonesize = 30;
        PSettings.worldName = "world";

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new EventsProtection(pman), getInstance());
        pm.registerEvents(new EventsSettingsWindow(pman), getInstance());
        pm.registerEvents(new EventsCommands(pman), getInstance());
    }

    public void onDisable() {
        System.out.println("Disabling :" + pdf.getName() + " version:" + pdf.getVersion());
    }

    public UberProtectionLib getInstance() {
        return this;
    }

}
