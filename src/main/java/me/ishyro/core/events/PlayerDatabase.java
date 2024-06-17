package me.ishyro.core.events;

import me.ishyro.core.api.SQLHandler;
import me.ishyro.core.api.sql.SQLTable;
import me.ishyro.core.api.sql.SQLUpdateColumn;
import me.ishyro.core.api.sql.SQLUtils;
import me.ishyro.core.logging.LogSeverity;
import me.ishyro.core.logging.Logging;
import me.ishyro.core.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerDatabase {
    public void addPlayerToDatabase(UUID uuid, String name) {
        Logging.log("Creating player data for "+name, LogSeverity.DEBUG);
        SQLUpdateColumn sqlUpdateColumn = new SQLTable("players", "uuid")
                .createUpdate(uuid)
                .putData("uuid", uuid.toString())
                .putData("name", name)
                .putData("warnings", 0);
        SQLHandler.sqlUpdate(SQLUtils.getInsertCommand("players", sqlUpdateColumn));
        Logging.log("Data for "+name+" with uuid "+uuid.toString()+" has been added successfully.", LogSeverity.DEBUG);
    }

    public void addWarningsToPlayer(UUID uuid, int amount) {
        String player = Bukkit.getOfflinePlayer(uuid).getName();
        SQLTable playersTable = new SQLTable("players", "uuid");
        if (playersTable.exist(uuid)) {
            playersTable.increaseValue(uuid.toString(), "warnings", amount);
        } else {
            SQLUpdateColumn sqlUpdateColumn = playersTable.createUpdate(uuid)
                    .putData("name", Bukkit.getOfflinePlayer(uuid).getName())
                    .putData("warnings", amount);
            playersTable.insertOrUpdate(sqlUpdateColumn);
            Logging.log("You added "+amount+" warnpoints to "+player, LogSeverity.NORMAL);
        }
    }

    public int getWarningsForPlayer(UUID uuid) {
        try {
            ResultSet rs = SQLHandler.sqlQuery("SELECT warnings FROM players WHERE uuid = '" + uuid.toString() + "'");
            if (rs.next()) {
                return rs.getInt("warnings");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void removeWarningsFromPlayer(UUID uuid, int amount) {
        String player = Bukkit.getOfflinePlayer(uuid).getName();
        int warnings = getWarningsForPlayer(uuid);

        if (amount < 0) {
            Logging.log("You cannot use negative numbers", LogSeverity.NORMAL);
            return;
        }
        if (warnings == 0) {
            Logging.log("You cannot modify this value because its 0", LogSeverity.WARN);
            return;
        }
        if((warnings - amount) < 0){
            Logging.log("You cannot set the warnings to below 0", LogSeverity.WARN);
            return;
        }

        int newValue = warnings - amount;
        SQLTable playersTable = new SQLTable("players", "uuid");
        SQLUpdateColumn sqlUpdateColumn = playersTable.createUpdate(uuid);
        sqlUpdateColumn.putData("warnings", String.valueOf(newValue));
        playersTable.insertOrUpdate(sqlUpdateColumn);
        Logging.log("You removed "+amount+" warnpoints from "+player+" now the player have "+ newValue +" warnings", LogSeverity.NORMAL);
    }


    public void setWarningsForPlayer(UUID uuid, int amount) {// Throw an exception for invalid input
        SQLTable playersTable = new SQLTable("players", "uuid");
        SQLUpdateColumn sqlUpdateColumn = playersTable.createUpdate(uuid);
        sqlUpdateColumn.putData("warnings", String.valueOf(amount));
        playersTable.insertOrUpdate(sqlUpdateColumn);
    }
}
