package de.themoep.utils.lang.velocity;

/*
 * lang - lang-velocity
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

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import de.themoep.utils.lang.LanguageManagerCore;
import ninja.leaping.configurate.ConfigurationNode;

import java.io.File;

public class LanguageManager extends LanguageManagerCore<CommandSource, ConfigurationNode> {
    private final LangAwarePlugin plugin;

    public LanguageManager(LangAwarePlugin plugin, String defaultLocale, VelocityLanguageConfig... configs) {
        this(plugin, "languages", defaultLocale, configs);
    }

    public LanguageManager(LangAwarePlugin plugin, String defaultLocale, boolean saveFiles, VelocityLanguageConfig... configs) {
        this(plugin, "languages", "languages", defaultLocale, saveFiles, configs);
    }

    public LanguageManager(LangAwarePlugin plugin, String folder, String defaultLocale, VelocityLanguageConfig... configs) {
        this(plugin, folder, folder, defaultLocale, configs);
    }

    public LanguageManager(LangAwarePlugin plugin, String resourceFolder, String folder, String defaultLocale, VelocityLanguageConfig... configs) {
        this(plugin, resourceFolder, folder, defaultLocale, true, configs);
    }

    public LanguageManager(LangAwarePlugin plugin, String resourceFolder, String folder, String defaultLocale, boolean saveFiles, VelocityLanguageConfig... configs) {
        super(defaultLocale, resourceFolder, new File(String.valueOf(plugin.getDataFolder()), folder), sender -> {
            if (sender instanceof Player) {
                return ((Player) sender).getPlayerSettings().getLocale().getLanguage().replace('-', '_');
            }
            return null;
        }, "lang.", ".yml", saveFiles, configs);
        this.plugin = plugin;
        loadConfigs();
    }

    @Override
    public void loadConfigs() {
        loadConfigs(plugin.getClass(), plugin.getLogger(), locale -> new VelocityLanguageConfig(plugin, getResourceFolder(),
                new File(getFolder(), filePrefix + locale + fileSuffix), locale, saveFiles));
    }
}
