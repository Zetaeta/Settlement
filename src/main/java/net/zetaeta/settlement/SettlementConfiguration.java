package net.zetaeta.settlement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import net.zetaeta.bukkit.configuration.PluginConfiguration;

public class SettlementConfiguration extends PluginConfiguration implements SettlementConstants {
    public static File configFile;
    private static PluginConfiguration config;
    
    public static PluginConfiguration loadConfiguration() {
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Could not create config file!", e);
                e.printStackTrace();
            }
        }
        if (configFile.length() == 0) {
            try {
                copyDefaults(plugin.getResource("config.yml"), new FileOutputStream(configFile));
            } catch (FileNotFoundException e) {
                log.log(Level.SEVERE, "Could not find config file!", e);
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                log.log(Level.SEVERE, "Could not read from default config file!", e);
                e.printStackTrace();
            }
        }
        configFile = new File(plugin.getDataFolder(), "config.yml");
        PluginConfiguration config = loadConfiguration(configFile);
        config.setDefaults(loadConfiguration(plugin.getResource("config.yml")));
        return config;
    }

    private static void copyDefaults(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[512];
        while (inputStream.available() > 0) {
            inputStream.read(buffer);
            outputStream.write(buffer);
        }
    }
    
}
