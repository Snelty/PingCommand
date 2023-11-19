package me.Snelty.PingCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int ping = getPing(player);
            player.sendMessage("Tu ping es: " + ping + "ms");
        }
        return true;
    }

    private int getPing(Player player) {
        // Aquí deberías implementar la lógica para obtener el ping del jugador.
        // Esto puede variar dependiendo de la versión del servidor y la API de Bukkit que estés utilizando.
        return 0;
    }
}

    
