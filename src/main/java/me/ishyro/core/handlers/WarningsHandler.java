package me.ishyro.core.handlers;


import me.ishyro.core.factory.WarningFactory;
import me.ishyro.core.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;

public class WarningsHandler {
    private WarningFactory warningFactory;

    public WarningsHandler(WarningFactory warningFactory) {
        this.warningFactory = warningFactory;
    }

    public void handleWarnings(int warning, Player player) {
        List<String> warnings = warningFactory.getWarnings().get(warning);

        if (warnings == null) return;

        warnings.forEach(w -> handleWarning(w, player));
    }

    private void handleWarning(String warning, Player player){
        String[] parts = warning.split(" ", 2);
        String keyword = parts[0];
        String remaining = parts.length > 1 ? parts[1] : null;

        if(remaining == null) return;

        Player p = player;
        String name = p.getName();
        Server server = Bukkit.getServer();
        switch (keyword) {
            case "[DISPATCH]":// Handle proxy commands
                server.dispatchCommand(server.getConsoleSender(), "admin dispatch console " +
                        remaining.replace("<player>", name));
                break;
            case "[MESSAGE]":
                p.sendMessage(TextUtil.translate(remaining));
                break;
            case "[CONSOLE]":// Handle server commands
                server.dispatchCommand(server.getConsoleSender(),
                        remaining.replace("<player>", name));
                break;
            default:// Handle other cases / exceptions
                server.getConsoleSender()
                        .sendMessage("§7[§4WarningPoints§7] §fUnknown warning keyword: " + keyword);
                break;
        }
    }
}