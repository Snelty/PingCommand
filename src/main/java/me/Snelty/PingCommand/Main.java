package me.Snelty.PingCommand;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getCommand("ping").setExecutor(new PingCommand());
    }
}
