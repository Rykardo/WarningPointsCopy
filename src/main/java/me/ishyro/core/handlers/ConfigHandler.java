package me.ishyro.core.handlers;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import me.ishyro.core.WarningPoints;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

@Getter
public class ConfigHandler {
    private static ConfigHandler instance;
    private YamlConfiguration lang;

    public static synchronized ConfigHandler getInstance() {
        if (instance == null) {
            instance = new ConfigHandler();
        }

        return instance;
    }

    private ConfigHandler() {
        WarningPoints instance = WarningPoints.getInstance();
        instance.saveDefaultConfig();
        this.loadLang();
    }

    public void loadLang() {
        this.lang = new YamlConfiguration();
        WarningPoints plugin = WarningPoints.getInstance();
        File langFile = new File(plugin.getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            plugin.saveResource("lang.yml", false);
        }

        try {
            this.lang.load(langFile);
        } catch (InvalidConfigurationException | IOException var4) {
            throw new RuntimeException(var4);
        }
    }

    public void reload() {
        this.loadLang();
        WarningPoints warningPoints = WarningPoints.getInstance();
        warningPoints.reloadConfig();
    }

    public String translateColorCodes(String str) {
        if (str.contains("&#")) {
            Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
            Matcher matcher = hexPattern.matcher(str);
            StringBuffer buffer = new StringBuffer(str.length() + 32);

            while(matcher.find()) {
                String group = matcher.group(1);
                char var10002 = group.charAt(0);
                matcher.appendReplacement(buffer, "§x§" + var10002 + "§" + group.charAt(1) + "§" + group.charAt(2) + "§" + group.charAt(3) + "§" + group.charAt(4) + "§" + group.charAt(5));
            }

            str = matcher.appendTail(buffer).toString();
        }

        return str.replace("&", "§");
    }

    public String getPrefix() {
        String prefix = this.lang.getString("Prefix", "ERROR PREFIX");
        return this.translateColorCodes(prefix);
    }

    public String getLangStringWithPrefix(String path) {
        String langStr = this.lang.getString(path, "ERROR: " + path);
        langStr = this.translateColorCodes(langStr);
        String prefix = this.getPrefix();
        return langStr.contains("ERROR: ") ? "" : prefix + langStr;
    }

    public String getLangString(String path) {
        String langStr = this.lang.getString(path, "ERROR: " + path);
        langStr = this.translateColorCodes(langStr);
        return langStr;
    }

}