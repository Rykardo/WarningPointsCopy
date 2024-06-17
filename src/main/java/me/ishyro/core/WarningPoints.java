package me.ishyro.core;

import lombok.Getter;
import me.ishyro.core.commands.Commands;
import me.ishyro.core.database.DatabaseManager;
import me.ishyro.core.events.PlayerDatabase;
import me.ishyro.core.factory.WarningFactory;
import me.ishyro.core.handlers.ConfigHandler;
import me.ishyro.core.listeners.PlayerJoinListener;
import me.ishyro.core.utils.ConfigUtil;
import org.bukkit.plugin.java.JavaPlugin;
@Getter
public class WarningPoints extends JavaPlugin {

    private static WarningPoints INSTANCE;
    private DatabaseManager databaseManager;
    private final ConfigUtil warnings= new ConfigUtil(this, "warnings.yml");
    private WarningFactory warningFactory;

    @Override
    public void onEnable() {
        INSTANCE = this;
        ConfigHandler.getInstance();

        databaseManager = new DatabaseManager(this);

        warnings.load();
        warningFactory = new WarningFactory(this);

        registerCommands();
        registerEvents();

        this.getServer().getConsoleSender().sendMessage("§7[§4WarningPoints§7] §fHas been initialized sucessfully!");
    }

    @Override
    public void onDisable() {
        databaseManager.disconnect();
        this.getServer().getConsoleSender().sendMessage("§7[§4WarningPoints§7] §fHas closed the database connections and stopped.");
    }

    public void registerCommands(){
        getCommand("warnpoints").setExecutor(new Commands(this, new PlayerDatabase()));
    }
    public void registerEvents(){
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(new PlayerDatabase()), this);
    }
    public static WarningPoints getInstance() {
        return INSTANCE;
    }
}
