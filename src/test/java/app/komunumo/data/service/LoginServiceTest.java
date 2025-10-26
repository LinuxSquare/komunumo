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
package app.komunumo.data.service;

import app.komunumo.data.dto.UserDto;
import app.komunumo.data.dto.UserRole;
import app.komunumo.data.dto.UserType;
import app.komunumo.data.service.confirmation.ConfirmationService;
import app.komunumo.security.UserPrincipal;
import app.komunumo.ui.TranslationProvider;
import app.komunumo.ui.signals.AuthenticationSignal;
import app.komunumo.util.SecurityUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class LoginServiceTest {

    private static LoginService createLoginServiceWithMocks(final @NotNull UserService userService) {
        final var confirmationService = mock(ConfirmationService.class);
        final var translationProvider = mock(TranslationProvider.class);
        final var authenticationSignal = mock(AuthenticationSignal.class);
        return new LoginService(userService, confirmationService, translationProvider, authenticationSignal);
    }

    @Test
    void userNotFound() {
        final var email = "test@example.com";
        final var userService = mock(UserService.class);
        when(userService.getUserByEmail(email)).thenReturn(Optional.empty());

        final var loginService = createLoginServiceWithMocks(userService);
        assertThat(loginService.login(email)).isFalse();
        assertThat(loginService.isUserLoggedIn()).isFalse();

        final var loggedInUser = loginService.getLoggedInUser();
        assertThat(loggedInUser).isEmpty();
    }

    @Test
    void loginNotAllowed() {
        final var email = "test@example.com";
        final var user = new UserDto(UUID.randomUUID(), null, null, "@test@example.com", email,
                "Test User", "Just for testing", null, UserRole.USER, UserType.ANONYMOUS);

        final var userService = mock(UserService.class);
        when(userService.getUserByEmail(email)).thenReturn(Optional.of(user));

        final var loginService = createLoginServiceWithMocks(userService);
        assertThat(loginService.login(email)).isFalse();
        assertThat(loginService.isUserLoggedIn()).isFalse();

        final var loggedInUser = loginService.getLoggedInUser();
        assertThat(loggedInUser).isEmpty();
    }

    @Test
    void loginAsUser() {
        final var userId = UUID.randomUUID();
        final var email = "test@example.com";
        final var user = new UserDto(userId, null, null, "@test@example.com", email,
                "Test User", "Just for testing", null, UserRole.USER, UserType.LOCAL);

        final var userService = mock(UserService.class);
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(userService.getUserByEmail(email)).thenReturn(Optional.of(user));

        final var loginService = createLoginServiceWithMocks(userService);
        assertThat(loginService.login(email)).isTrue();
        assertThat(loginService.isUserLoggedIn()).isTrue();

        final var loggedInUser = loginService.getLoggedInUser();
        assertThat(loggedInUser).isPresent();
        assertThat(loggedInUser.orElseThrow()).isEqualTo(user);
    }

    @Test
    void loginAsAdmin() {
        final var userId = UUID.randomUUID();
        final var email = "test@example.com";
        final var user = new UserDto(userId, null, null, "@test@example.com", email,
                "Test User", "Just for testing", null, UserRole.ADMIN, UserType.LOCAL);

        final var userService = mock(UserService.class);
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(userService.getUserByEmail(email)).thenReturn(Optional.of(user));

        final var loginService = createLoginServiceWithMocks(userService);
        try (final var vaadinService = mockStatic(VaadinService.class)) {
            vaadinService.when(VaadinService::getCurrentRequest).thenReturn(mock(VaadinServletRequest.class));
            assertThat(loginService.login(email)).isTrue();
        }
        assertThat(loginService.isUserLoggedIn()).isTrue();

        final var loggedInUser = loginService.getLoggedInUser();
        assertThat(loggedInUser).isPresent();
        assertThat(loggedInUser.orElseThrow()).isEqualTo(user);
    }

    @Test
    void logout() {
        final var location = "/test";

        final var page = mock(Page.class);
        final var ui = mock(UI.class);
        when(ui.getPage()).thenReturn(page);

        final var httpServletRequest = mock(HttpServletRequest.class);
        final var vaadinServletRequest = mock(VaadinServletRequest.class);
        when(vaadinServletRequest.getHttpServletRequest()).thenReturn(httpServletRequest);

        final var userService = mock(UserService.class);
        final var loginService = createLoginServiceWithMocks(userService);
        try (final var staticUI = mockStatic(UI.class);
             final var staticVaadinServletRequest = mockStatic(VaadinServletRequest.class);
             final var staticSecurityContextHolder = mockStatic(SecurityContextHolder.class);
             final var ignored = mockConstruction(SecurityContextLogoutHandler.class)) {

            // Static stubs
            staticUI.when(UI::getCurrent).thenReturn(ui);
            staticVaadinServletRequest.when(VaadinServletRequest::getCurrent).thenReturn(vaadinServletRequest);

            // Act + Assert (no exception)
            Assertions.assertThatCode(() -> loginService.logout(location)).doesNotThrowAnyException();

            // Verify: navigation target set
            verify(page).setLocation(location);

            // Verify: security context cleared (static verification)
            staticSecurityContextHolder.verify(SecurityContextHolder::clearContext);
        }
    }

    @Test
    void getLoggedInUser_returnsEmpty_whenAuthenticationIsNull() {
        try {
            SecurityContextHolder.clearContext();

            final var userService = mock(UserService.class);
            final var loginService = createLoginServiceWithMocks(userService);

            final var result = loginService.getLoggedInUser();

            assertThat(result).isEmpty();
            verifyNoInteractions(userService);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void getLoggedInUser_returnsEmpty_whenPrincipalIsNotUserPrincipal() {
        try {
            final var auth = mock(Authentication.class);
            when(auth.getPrincipal()).thenReturn("anonymousUser");
            final var ctx = mock(SecurityContext.class);
            when(ctx.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(ctx);

            final var userService = mock(UserService.class);
            final var loginService = createLoginServiceWithMocks(userService);

            final var result = loginService.getLoggedInUser();

            assertThat(result).isEmpty();
            verifyNoInteractions(userService);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void getLoggedInUser_returnsUser_whenPrincipalIsUserPrincipal_andUserExists() {
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            final var userId = UUID.randomUUID();
            final var principal = mock(UserPrincipal.class);
            when(principal.getUserId()).thenReturn(userId);

            final var userService = mock(UserService.class);
            final var loginService = createLoginServiceWithMocks(userService);

            final var user = new UserDto(userId, null, null, null, null, "", "",
                    null, UserRole.USER, UserType.LOCAL);
            when(userService.getUserById(userId)).thenReturn(Optional.of(user));

            securityUtil.when(SecurityUtil::getUserPrincipal)
                    .thenReturn(Optional.of(principal));

            final var result = loginService.getLoggedInUser();

            assertThat(result).containsSame(user);
            verify(userService).getUserById(userId);
            verifyNoMoreInteractions(userService);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void getLoggedInUser_returnsEmpty_whenPrincipalIsUserPrincipal_butUserNotFound() {
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            final var userId = UUID.randomUUID();
            final var principal = mock(UserPrincipal.class);
            when(principal.getUserId()).thenReturn(userId);

            final var userService = mock(UserService.class);
            final var loginService = createLoginServiceWithMocks(userService);

            when(userService.getUserById(userId)).thenReturn(Optional.empty());

            securityUtil.when(SecurityUtil::getUserPrincipal)
                    .thenReturn(Optional.of(principal));

            final var result = loginService.getLoggedInUser();

            assertThat(result).isEmpty();
            verify(userService).getUserById(userId);
            verifyNoMoreInteractions(userService);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void loginAsLocalUser_addsRoleUserLocal() {
        try {
            final var userId = UUID.randomUUID();
            final var email = "local@example.com";
            final var user = new UserDto(
                    userId, null, null, "@local@example.com", email,
                    "Local User", "", null, UserRole.USER, UserType.LOCAL);

            final var userService = mock(UserService.class);
            when(userService.getUserById(userId)).thenReturn(Optional.of(user));
            when(userService.getUserByEmail(email)).thenReturn(Optional.of(user));

            final var loginService = createLoginServiceWithMocks(userService);

            assertThat(loginService.login(email)).isTrue();

            final var authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority))
                    .containsExactlyInAnyOrder("ROLE_USER", "ROLE_USER_LOCAL");
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void loginAsLocalAdmin_addsRoleUserLocal() {
        try {
            final var userId = UUID.randomUUID();
            final var email = "local@example.com";
            final var user = new UserDto(
                    userId, null, null, "@local@example.com", email,
                    "Local Admin", "", null, UserRole.ADMIN, UserType.LOCAL);

            final var userService = mock(UserService.class);
            when(userService.getUserById(userId)).thenReturn(Optional.of(user));
            when(userService.getUserByEmail(email)).thenReturn(Optional.of(user));

            final var loginService = createLoginServiceWithMocks(userService);

            assertThat(loginService.login(email)).isTrue();

            final var authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority))
                    .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER", "ROLE_USER_LOCAL");
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

}
