package me.Snelty.PingCommand;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (getCommand("ping") != null) {
            getCommand("ping").setExecutor(new PingCommand(this));
        }
    }
}
