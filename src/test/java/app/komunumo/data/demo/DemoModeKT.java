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
package app.komunumo.data.demo;

import app.komunumo.data.dto.CommunityDto;
import app.komunumo.data.dto.ContentType;
import app.komunumo.data.dto.EventDto;
import app.komunumo.data.dto.EventStatus;
import app.komunumo.data.dto.EventVisibility;
import app.komunumo.data.dto.GlobalPageDto;
import app.komunumo.data.dto.ImageDto;
import app.komunumo.data.dto.UserDto;
import app.komunumo.data.dto.UserRole;
import app.komunumo.data.dto.UserType;
import app.komunumo.data.service.CommunityService;
import app.komunumo.data.service.EventService;
import app.komunumo.data.service.GlobalPageService;
import app.komunumo.data.service.ImageService;
import app.komunumo.data.service.UserService;
import app.komunumo.ui.KaribuTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class DemoModeKT extends KaribuTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private EventService eventService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private GlobalPageService globalPageService;

    @Autowired
    private @NotNull DemoMode demoMode;

    @Test
    void resetDemoData() {
        assertThat(userService.getAdminCount()).isOne();
        assertThat(userService.getUserCount()).isEqualTo(3);
        assertThat(communityService.getCommunityCount()).isEqualTo(6);
        assertThat(eventService.getEventCount()).isEqualTo(6);
        assertThat(imageService.getImageCount()).isEqualTo(2);
        assertThat(globalPageService.getGlobalPageCount()).isEqualTo(2);

        userService.storeUser(new UserDto(null, null, null,
                "demoAdmin", "demo-admin@example.com", "Demo Admin", "", null,
                UserRole.ADMIN, UserType.LOCAL));
        assertThat(userService.getAdminCount()).isEqualTo(2);

        userService.storeUser(new UserDto(null, null, null,
                "demoUser", "demo-user@example.com", "Demo User", "", null,
                UserRole.USER, UserType.LOCAL));
        assertThat(userService.getUserCount()).isEqualTo(4);

        final var community = communityService.storeCommunity(new CommunityDto(null, "demoCommunity", null, null,
                "Demo Community", "", null));
        assertThat(communityService.getCommunityCount()).isEqualTo(7);

        eventService.storeEvent(new EventDto(null, community.id(), null, null,
                "Demo Event", "", "", null, null, null,
                EventVisibility.PUBLIC, EventStatus.DRAFT));
        assertThat(eventService.getEventCount()).isEqualTo(7);

        imageService.storeImage(new ImageDto(null, ContentType.IMAGE_PNG));
        assertThat(imageService.getImageCount()).isEqualTo(3);

        globalPageService.storeGlobalPage(new GlobalPageDto("demo", Locale.ENGLISH, null, null,
                "Demo Page", "**Demo Page**"));
        assertThat(globalPageService.getGlobalPageCount()).isEqualTo(3);

        demoMode.resetDemoData();

        assertThat(userService.getAdminCount()).isOne();
        assertThat(userService.getUserCount()).isEqualTo(3);
        assertThat(communityService.getCommunityCount()).isEqualTo(6);
        assertThat(eventService.getEventCount()).isEqualTo(6);
        assertThat(imageService.getImageCount()).isEqualTo(2);
        assertThat(globalPageService.getGlobalPageCount()).isEqualTo(2);
    }

}
