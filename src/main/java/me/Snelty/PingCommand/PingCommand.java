package me.Snelty.PingCommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PingCommand implements CommandExecutor {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_INPUT = LegacyComponentSerializer.legacyAmpersand();
    private static final LegacyComponentSerializer LEGACY_OUTPUT = LegacyComponentSerializer.legacySection();
    private final JavaPlugin plugin;

    public PingCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sendConfiguredMessage(sender, "only-player-message", 0);
            return true;
        }
        Player player = (Player) sender;
        int ping = getPing(player);
        sendConfiguredMessage(player, "ping-message", ping);
        return true;
    }

    private void sendConfiguredMessage(CommandSender sender, String path, int ping) {
        String template = plugin.getConfig().getString(path, "<red>Mensaje no configurado.");
        String parsed = template
            .replace("{ping}", String.valueOf(ping))
            .replace("{player}", sender.getName());
        Component component = parseComponent(parsed);
        sender.sendMessage(LEGACY_OUTPUT.serialize(component));
    }

    private Component parseComponent(String message) {
        String configuredFormat = plugin.getConfig().getString("format", "auto");
        Format format = Format.from(configuredFormat);
        switch (format) {
            case JSON:
                return parseJsonOrFallback(message);
            case MINIMESSAGE:
                return MINI_MESSAGE.deserialize(message);
            case LEGACY:
                return LEGACY_INPUT.deserialize(message);
            case PLAIN:
                return Component.text(message);
            case AUTO:
            default:
                return autoParse(message);
        }
    }

    private Component autoParse(String message) {
        String trimmed = message.trim();
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            return parseJsonOrFallback(message);
        }
        return MINI_MESSAGE.deserialize(message);
    }

    private Component parseJsonOrFallback(String message) {
        try {
            return GsonComponentSerializer.gson().deserialize(message);
        } catch (Exception ignored) {
            return MINI_MESSAGE.deserialize(message);
        }
    }

    private int getPing(Player player) {
        try {
            Method getPingMethod = player.getClass().getMethod("getPing");
            Object value = getPingMethod.invoke(player);
            if (value instanceof Integer) {
                return (Integer) value;
            }
        } catch (Exception ignored) {
        }

        try {
            Method getHandleMethod = player.getClass().getMethod("getHandle");
            Object handle = getHandleMethod.invoke(player);
            Field pingField = handle.getClass().getDeclaredField("ping");
            pingField.setAccessible(true);
            return pingField.getInt(handle);
        } catch (Exception ignored) {
            return -1;
        }
    }

    private enum Format {
        AUTO,
        MINIMESSAGE,
        JSON,
        LEGACY,
        PLAIN;

        static Format from(String raw) {
            if (raw == null) {
                return AUTO;
            }
            try {
                return Format.valueOf(raw.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                return AUTO;
            }
        }
    }
}
