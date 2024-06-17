package me.ishyro.core.commands;

import lombok.Getter;
import me.ishyro.core.WarningPoints;
import me.ishyro.core.api.sql.SQLTable;
import me.ishyro.core.events.PlayerDatabase;
import me.ishyro.core.factory.WarningFactory;
import me.ishyro.core.handlers.WarningsHandler;
import me.ishyro.core.logging.LogSeverity;
import me.ishyro.core.logging.Logging;
import me.ishyro.core.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;


@Getter
public class Commands implements CommandExecutor {
    private final WarningPoints plugin;
    private final PlayerDatabase database;
    private final WarningFactory warningFactory;
    private final WarningsHandler warningsHandler;

    public Commands(WarningPoints plugin, PlayerDatabase database) {
        this.plugin = plugin;
        this.database = database;
        this.warningFactory = plugin.getWarningFactory();
        this.warningsHandler = new WarningsHandler(warningFactory);
        Bukkit.getServer().getConsoleSender().sendMessage("§7[§4WarningPoints§7] §fCommand 'warnpoints' have been initialized sucessfully!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("&cThis command can only be executed from the console.");
            return false;
        }
        ConsoleCommandSender console = (ConsoleCommandSender) sender;

        if (args.length < 1) {
            TextUtil.sendPrefixedMessage(console, "&cCommand usage: /warnpoints set|add|get <player> <value>");
            return true;
        }
        Player targetPlayer;

        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.onEnable();
                console.sendMessage("§7[§4WarningPoints§7] §aWarningPoints reloaded.");
                return true;
            case "add":
                if (args.length != 3) {
                    TextUtil.sendPrefixedMessage(console, "&cCommand usage: /warnpoints add <player> <value>");
                    return false;
                }

                targetPlayer = console.getServer().getPlayer(args[1]);
                if (targetPlayer == null) {
                    TextUtil.sendPrefixedMessage(console, "&cPlayer is not valid or is disconnected.");
                    return false;
                }

                int value;
                try {
                    value = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    TextUtil.sendPrefixedMessage(console, "&cInvalid integer, please enter a numeric value");
                    return false;
                }
                database.addWarningsToPlayer(targetPlayer.getUniqueId(), value);

                int newWarnings = database.getWarningsForPlayer(targetPlayer.getUniqueId());
                warningsHandler.handleWarnings(newWarnings, targetPlayer);
                return true;
            case "remove":
                if (args.length != 3) {
                    TextUtil.sendPrefixedMessage(console, "&cCommand usage: /warnpoints remove <player> <value>");
                    return false;
                }

                targetPlayer = console.getServer().getPlayer(args[1]);
                if (targetPlayer == null) {
                    TextUtil.sendPrefixedMessage(console, "&cPlayer is not valid or is disconnected.");
                    return false;
                }

                int removeValue;
                try {
                    removeValue = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    TextUtil.sendPrefixedMessage(console, "&cInvalid integer, please enter a numeric value");
                    return false;
                }
                database.removeWarningsFromPlayer(targetPlayer.getUniqueId(), removeValue);
                return true;

            case "set":
                if (args.length != 3) {
                    TextUtil.sendPrefixedMessage(console, "&cCommand usage: /warnpoints set <player> <value>");
                    return false;
                }

                targetPlayer = console.getServer().getPlayer(args[1]);
                if (targetPlayer == null) {
                    TextUtil.sendPrefixedMessage(console, "&cPlayer is not valid or is disconnected.");
                    return false;
                }

                int setValue;
                try {
                    setValue = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    TextUtil.sendPrefixedMessage(console, "&cInvalid integer, please enter a numeric value");
                    return false;
                }
                database.setWarningsForPlayer(targetPlayer.getUniqueId(), setValue);
                int newWarnings2 = database.getWarningsForPlayer(targetPlayer.getUniqueId());
                warningsHandler.handleWarnings(newWarnings2, targetPlayer);
                return true;

            case "get":
                if (args.length != 2) {
                    TextUtil.sendPrefixedMessage(console, "&cCommand usage: /warnpoints get <player>");
                    return false;
                }

                targetPlayer = console.getServer().getPlayer(args[1]);
                if (targetPlayer == null) {
                    TextUtil.sendPrefixedMessage(console, "&cPlayer is not valid or is disconnected.");
                    return false;
                }

                if (!isPlayerInDatabase(targetPlayer.getUniqueId())) {
                    Logging.log("An error occurred trying to get this player, it was not found in the database, registering the user...", LogSeverity.WARN);
                    database.addPlayerToDatabase(targetPlayer.getUniqueId(), targetPlayer.getName());
                    return false;
                }

                int warnings = database.getWarningsForPlayer(targetPlayer.getUniqueId());
                Logging.log("The player " + targetPlayer.getName() + " currently has " + warnings + " warnings", LogSeverity.NORMAL);
                return true;
        }
        return false;
    }

    public boolean isPlayerInDatabase(UUID uuid) {
        SQLTable playersTable = new SQLTable("players", "uuid");
        return playersTable.exist(uuid.toString());
    }
}
