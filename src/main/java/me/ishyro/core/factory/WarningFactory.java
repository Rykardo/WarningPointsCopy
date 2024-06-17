package me.ishyro.core.factory;

import lombok.Getter;
import me.ishyro.core.WarningPoints;
import org.bukkit.Bukkit;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class WarningFactory {
    private HashMap<Integer, List<String>> warnings;
    private final WarningPoints plugin;

    public WarningFactory(WarningPoints plugin) {
        this.plugin = plugin;
        loadWarnings();
        Bukkit.getServer().getConsoleSender().sendMessage("§7[§4WarningPoints§7] §fWarningFactory have been initialized sucessfully!");
    }

    private void loadWarnings() {
        Yaml yaml = new Yaml();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(plugin.getDataFolder() + "/warnings.yml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Map<String, List<String>> obj = yaml.load(inputStream);

        warnings = new HashMap<>();
        for(Map.Entry<String, List<String>> entry : obj.entrySet()) {
            System.out.println("- " + entry.getKey() + " values: " + entry.getValue());
            warnings.put(Integer.parseInt(entry.getKey()), entry.getValue());
        }
    }
}
