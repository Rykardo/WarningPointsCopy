package me.ishyro.core.listeners;

import me.ishyro.core.api.sql.SQLTable;
import me.ishyro.core.events.PlayerDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;


public class PlayerJoinListener implements Listener {
    private final PlayerDatabase database;

    public PlayerJoinListener(PlayerDatabase database) {
        this.database = database;
        Bukkit.getServer().getConsoleSender().sendMessage("§7[§4WarningPoints§7] §fPlayerJoinListener have been initialized sucessfully!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String name = player.getName();

        if (!player.hasPlayedBefore()) {
            database.addPlayerToDatabase(uuid, name);
        }

        if(!isPlayerInDatabase(uuid)) {
            database.addPlayerToDatabase(uuid, name);
        }
    }

    public boolean isPlayerInDatabase(UUID uuid) {
        SQLTable playersTable = new SQLTable("players", "uuid");
        return playersTable.exist(uuid.toString());
    }
}