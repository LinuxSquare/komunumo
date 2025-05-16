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
package app.komunumo.plugin;

import org.jetbrains.annotations.NotNull;
import app.komunumo.data.service.ServiceProvider;
import org.slf4j.Logger;

public interface PluginContext {

    /**
     * Return a logger named corresponding to the class passed as parameter.
     *
     * @param clazz the returned logger will be named after clazz
     * @return logger
     */
    @NotNull Logger getLogger(Class<?> clazz);

    /**
     * Provides access to the Komunumo services for data operations.
     */
    @NotNull ServiceProvider getServiceProvider();

}
