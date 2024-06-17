package me.ishyro.core.utils;

import me.ishyro.core.handlers.ConfigHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class TextUtil {
    public static String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> translate(List<String> s) {
        return s.stream().map(TextUtil::translate).collect(Collectors.toList());
    }

    public static void sendConsole(ConsoleCommandSender sender, String args){
        sender.sendMessage(TextUtil.translate(args));
    }

    public static void sendPrefixedMessage(ConsoleCommandSender sender, String messageKey) {
        String prefix = ConfigHandler.getInstance().getPrefix();
        String message = ConfigHandler.getInstance().getLangString(messageKey);
        String fullMessage = prefix + message;
        TextUtil.sendConsole(sender, fullMessage);
    }
}