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

import app.komunumo.data.db.tables.records.EventRecord;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.UUID;

public record EventDto(
        @Nullable UUID id,
        @Nullable UUID communityId,
        @Nullable ZonedDateTime created,
        @Nullable ZonedDateTime updated,
        @NotNull String title,
        @NotNull String description,
        @NotNull String location,
        @Nullable ZonedDateTime begin,
        @Nullable ZonedDateTime end,
        @Nullable UUID imageId,
        @NotNull String visibility,
        @NotNull String status
        ) {
}
