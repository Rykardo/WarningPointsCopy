package me.ishyro.core.database;

import me.ishyro.core.logging.LogSeverity;
import me.ishyro.core.logging.Logging;
import me.ishyro.core.WarningPoints;
import me.ishyro.core.api.SQLHandler;
import me.ishyro.core.api.sql.DataType;
import me.ishyro.core.api.sql.SQLTable;
public class DatabaseManager extends SQLHandler {
    private static SQLTable table;
    public DatabaseManager(WarningPoints instance) {
        super(instance.getDataFolder().getAbsolutePath());
        connect();
    }

    public void connect() {
        try {
            super.connect("database");
            Logging.log("Plugin successfully connected to database.", LogSeverity.NORMAL);
        } catch (Exception e) {
            Logging.log("Error trying to connect with database: " + e.getMessage(), LogSeverity.ERROR);
        }
    }

    @Override
    public void onConnect() {
        createTable();
    }

    public void createTable() {
        final SQLTable sqlTable = new SQLTable("players", "uuid")

                .addField("uuid", DataType.VARCHAR36, true, true, true)
                .addField("name", DataType.VARCHAR16)
                .addField("warnings", DataType.INTEGER);

        sqlTable.create();
    }


}