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

import app.komunumo.ui.KaribuTest;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = "komunumo.demo.json=")
class DemoModeNoJsonKT extends KaribuTest {

    @Autowired
    private DemoMode demoMode;

    @Test
    void resetDemoDataWhenDisabled() {
        try (var logCaptor = LogCaptor.forClass(DemoMode.class)) {
            demoMode.resetDemoData();
            assertThat(logCaptor.getWarnLogs()).containsExactly(
                "Disabling demo mode plugin automatically, because no JSON data URL is configured.");
        }
    }

}
