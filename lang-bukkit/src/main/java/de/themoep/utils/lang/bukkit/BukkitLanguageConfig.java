package de.themoep.utils.lang.bukkit;

/*
 * lang - lang-bukkit
 * Copyright (c) 2018 Max Lee aka Phoenix616 (mail@moep.tv)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import de.themoep.utils.lang.LanguageConfig;
import org.bukkit.ChatColor;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

public class BukkitLanguageConfig extends LanguageConfig {
    private final Plugin plugin;
    private final String resourcePath;
    private FileConfiguration config;

    public BukkitLanguageConfig(Plugin plugin, String folder, String locale) {
        this(plugin, folder, folder, locale);
    }

    public BukkitLanguageConfig(Plugin plugin, String resourceFolder, String folder, String locale) {
        super(folder.isEmpty() ? plugin.getDataFolder() : new File(plugin.getDataFolder(), folder), locale);
        this.plugin = plugin;
        this.resourcePath = resourceFolder.isEmpty() ? configFile.getName() : (resourceFolder + "/" + configFile.getName());
        saveConfigResource();
        loadConfig();
    }

    @Override
    public void loadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    @Override
    public boolean saveConfigResource() {
        if (!configFile.exists()) {
            File parent = configFile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            InputStream in = plugin.getResource(resourcePath);
            if (in == null) {
                plugin.getLogger().log(Level.SEVERE, "No resource '" + resourcePath + "' found in " + plugin.getName() + "'s jar file!");
                return false;
            }
            try {
                OutputStream out = new FileOutputStream(configFile);
                byte[] buf = new byte[in.available()];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
                return true;
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not save " + configFile.getName() + " to " + configFile, ex);
            }
        }
        return false;
    }

    @Override
    public void setDefaults(LanguageConfig defaults) {
        if (defaults == null) {
            config.setDefaults(new MemoryConfiguration());
        } else if (defaults instanceof BukkitLanguageConfig) {
            config.setDefaults(((BukkitLanguageConfig) defaults).config);
        }
    }

    @Override
    public boolean contains(String key) {
        return config.contains(key, true);
    }

    @Override
    public String get(String key) {
        String string = config.getString(key);
        if (string == null) {
            return ChatColor.RED + "Missing language key " + ChatColor.YELLOW + key + ChatColor.RED + " for locale " + ChatColor.YELLOW + getLocale();
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
