package de.themoep.utils.lang.velocity;

/*
 * lang - lang-bungee
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
import net.kyori.text.format.TextColor;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.*;
import java.util.List;
import java.util.logging.Level;

public class VelocityLanguageConfig extends LanguageConfig<ConfigurationNode> {
    private final LangAwarePlugin plugin;

    public VelocityLanguageConfig(LangAwarePlugin plugin, String resourceFolder, File configFile, String locale) {
        this(plugin, resourceFolder, configFile, locale, true);
    }

    public VelocityLanguageConfig(LangAwarePlugin plugin, String resourceFolder, File configFile, String locale, boolean saveFile) {
        super(resourceFolder, configFile, locale, saveFile);
        this.plugin = plugin;
        saveConfigResource();
        loadConfig();
    }

    @Override
    public void loadConfig() {
        if (saveFile && configFile.exists()) {
            try {
                config = YAMLConfigurationLoader.builder().setFile(configFile).build().load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean saveConfigResource() {
        try (InputStream in = plugin.getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                plugin.getLogger().log(Level.WARNING, "No default config '" + resourcePath + "' found in " + plugin.getName() + "!");
                return false;
            }

            @NonNull YAMLConfigurationLoader resourceLoader = YAMLConfigurationLoader.builder().setSource(
                    () -> new BufferedReader(new InputStreamReader(in))).build();

            defaultConfig = config = resourceLoader.load();
            if (saveFile && !configFile.exists()) {
                File parent = configFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                try {
                    YAMLConfigurationLoader.builder().setFile(configFile).build().save(config);
                    return true;
                } catch (IOException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Could not save " + configFile.getName() + " to " + configFile, ex);
                }
            }
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not load default config from " + resourcePath, ex);
        }
        return false;
    }

    @Override
    public boolean contains(String key) {
        return contains(key, false);
    }

    @Override
    public boolean contains(String key, boolean checkDefault) {
        return !config.getNode(key).isVirtual() || (checkDefault && defaultConfig != null && !defaultConfig.getNode(key).isVirtual());
    }

    @Override
    public String get(String key) {
        Object o = config.getNode(key).getValue();
        String string = null;
        if (o instanceof String) {
            string = (String) o;
        } else if (o instanceof List) {
            List<String> stringList = (List<String>) o;
            if (stringList != null) {
                string = String.join("\n", stringList);
            }
        }
        if (string == null) {
            return TextColor.RED + "Missing language key " + TextColor.YELLOW + key + TextColor.RED + " for locale " + TextColor.YELLOW + getLocale();
        }
        return LegacyComponentSerializer.INSTANCE.serialize(LegacyComponentSerializer.INSTANCE.deserialize(string, '&'), '&');
    }

    @Override
    public ConfigurationNode getRawConfig() {
        return config;
    }
}
