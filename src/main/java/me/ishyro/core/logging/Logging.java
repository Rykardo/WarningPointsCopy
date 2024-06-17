package me.ishyro.core.logging;

import me.ishyro.core.WarningPoints;
import org.bukkit.Bukkit;

public class Logging {
    public static void log(String mes, LogSeverity severity) {
        boolean debugMessages = WarningPoints.getInstance().getConfig().getBoolean("debug", false);
        mes = mes.replace("&", "§");
        if (severity == LogSeverity.NORMAL) {
            Bukkit.getConsoleSender().sendMessage("§7[§4WarningPoints§7] §f" + mes);
        } else if (severity == LogSeverity.WARN) {
            Bukkit.getConsoleSender().sendMessage("§7[§4WarningPoints§7] §e[WARN] §f" + mes);
        } else if (severity == LogSeverity.ERROR) {
            Bukkit.getConsoleSender().sendMessage("§7[§4WarningPoints§7] §4[ERROR] §f" + mes);
        } else if (severity == LogSeverity.DEBUG && debugMessages) {
            Bukkit.getConsoleSender().sendMessage("§7[§4WarningPoints§7] §a[DEBUG] §f" + mes);
        }
    }
}