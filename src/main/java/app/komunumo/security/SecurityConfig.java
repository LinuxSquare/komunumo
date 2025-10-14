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
package app.komunumo.security;

import app.komunumo.ui.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * <p>Security configuration for the Komunumo application.</p>
 *
 * <p>This configuration uses {@link VaadinSecurityConfigurer} to integrate Spring Security
 * with Vaadin and to define application-specific security rules following the modern,
 * component-based Spring Security approach.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
public class SecurityConfig {

    /**
     * <p>The URL of the login page (relative to the application root).</p>
     */
    public static final @NotNull String LOGIN_URL = "login";

    /**
     * <p>The URL to redirect to after a successful logout.</p>
     */
    public static final @NotNull String LOGOUT_SUCCESS_URL = "/";

    /**
     * <p>Defines the Spring Security filter chain for the application.</p>
     *
     * <p>This bean applies Vaadin's {@link VaadinSecurityConfigurer} and configures
     * application-specific authorization rules for public endpoints. All remaining
     * requests are secured by Vaadin's view-based access control annotations.</p>
     *
     * @param http the {@link HttpSecurity} to modify
     * @return the built {@link SecurityFilterChain}
     * @throws Exception if an error occurs while configuring security
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        // Allow selected public endpoints first; do NOT call anyRequest() here
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/.well-known/**",
                        "/actuator/health",
                        "/images/**"
                ).permitAll()
        );

        // Apply Vaadin security defaults and set the login view and logout success URL
        http.with(VaadinSecurityConfigurer.vaadin(), configurer ->
                configurer.loginView(LoginView.class, LOGOUT_SUCCESS_URL)
        );

        // Build and return the filter chain
        return http.build();
    }
}
