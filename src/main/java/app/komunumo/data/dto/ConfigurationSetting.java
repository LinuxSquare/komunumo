/*
 * Komunumo - Open Source Community Manager
 * Copyright (C) Marcus Fihlon and the individual contributors to Komunumo.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package app.komunumo.data.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>Defines all configuration settings available in Komunumo.</p>
 *
 * <p>Each constant maps to a configuration entry stored in the {@code config} table and can be accessed
 * through the {@link app.komunumo.data.service.ConfigurationService}. Settings can be language independent or
 * language dependent, as indicated by {@link #isLanguageDependent()}.</p>
 */
public enum ConfigurationSetting {

    /**
     * <p>Base URL where the instance is accessible.</p>
     */
    INSTANCE_URL("instance.url", false, "http://localhost:8080"),

    /**
     * <p>Official name of the instance.</p>
     */
    INSTANCE_NAME("instance.name", false, "Your Instance Name"),

    /**
     * <p>Slogan or short description of the instance purpose.</p>
     */
    INSTANCE_SLOGAN("instance.slogan", true, "Your Instance Slogan"),

    /**
     * <p>Link to a custom CSS style file applied to all pages.</p>
     */
    INSTANCE_CUSTOM_STYLES("instance.custom.styles", false, ""),

    /**
     * <p>Whether to hide the community list on the home page.</p>
     */
    INSTANCE_HIDE_COMMUNITIES("instance.hideCommunities", false, "false"),

    /**
     * <p>Whether to allow local users to create communities or not.</p>
     */
    INSTANCE_ADD_COMMUNITY_ALLOWED("instance.addCommunityAllowed", false, "true"),

    /**
     * <p>Whether to allow account registration or not.</p>
     */
    INSTANCE_REGISTRATION_ALLOWED("instance.registrationAllowed", false, "true");

    private final String setting;
    private final boolean languageDependent;
    private final String defaultValue;

    /**
     * <p>Creates a new configuration setting definition.</p>
     *
     * @param setting the unique key of the setting
     * @param languageDependent true if this setting is language specific
     * @param defaultValue the default value if no custom value is stored
     */
    ConfigurationSetting(final @NotNull String setting,
                         final boolean languageDependent,
                         final @NotNull String defaultValue) {
        this.setting = setting;
        this.languageDependent = languageDependent;
        this.defaultValue = defaultValue;
    }

    /**
     * <p>Returns the enum constant for the given key.</p>
     *
     * <p>Use this method to resolve a stored string key into its corresponding enum constant.</p>
     *
     * @param settingString the key of the setting
     * @return the matching enum constant, never {@code null}
     * @throws IllegalArgumentException if the key is unknown
     */
    public static @NotNull ConfigurationSetting fromString(final @Nullable String settingString) {
        for (ConfigurationSetting setting : values()) {
            if (setting.setting.equals(settingString)) {
                return setting;
            }
        }
        throw new IllegalArgumentException("Unknown setting: " + settingString);
    }

    /**
     * <p>Returns the unique key of this setting.</p>
     *
     * @return the configuration key, never {@code null}
     */
    public @NotNull String setting() {
        return setting;
    }

    /**
     * <p>Indicates whether this setting is language dependent.</p>
     *
     * @return {@code true} if the setting is language specific, otherwise {@code false}
     */
    public boolean isLanguageDependent() {
        return languageDependent;
    }


    /**
     * <p>Returns the default value of this setting.</p>
     *
     * @return the default value, never {@code null}
     */
    public @NotNull String defaultValue() {
        return defaultValue;
    }
}
