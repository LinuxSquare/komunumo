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
package app.komunumo.ui.website.events;

import app.komunumo.data.service.EventService;
import app.komunumo.ui.BrowserTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EventGridViewIT extends BrowserTest {

    @Test
    void checkUpcomingEvents() {
        final var page = getPage();

        page.navigate("http://localhost:8081/events/");
        page.waitForSelector("h1:has-text('Komunumo')");
        captureScreenshot("upcoming-events-view");
        assertThat(page.title()).isEqualTo("Events – Komunumo");

        assertThat(page.locator("a[href='events']").getAttribute("highlight")).isNotNull().isBlank();

        final var eventCards = page.locator("vaadin-card");
        assertThat(eventCards.count()).isEqualTo(3);

        for (var entry : Map.of(
                0, 3,
                1, 5,
                2, 6) // position and event number
                .entrySet()) {
            final var position = entry.getKey();
            final var number = entry.getValue();
            final var eventCard = eventCards.nth(position);

            final var title = eventCard.locator("div[slot='title']");
            assertThat(title.textContent()).isEqualTo("Demo Event " + number);


            final var image = eventCard.locator("img[slot='media']");
            assertThat(image.count())
                    .as("image should be set")
                    .isEqualTo(1);

            if (number <= 5) {
                final var imageSrc = image.getAttribute("src");
                assertThat(imageSrc)
                        .as("expected to contain an image but was: " + imageSrc)
                        .startsWith("/images/")
                        .endsWith(".jpg")
                        .doesNotContain("placeholder");

                final var imageAlt = image.getAttribute("alt");
                assertThat(imageAlt)
                        .as("expected to contain an alt text but was: " + imageSrc)
                        .isEqualTo("Demo Event " + number);
            } else { // demo event 6+ has a placeholder image
                final var imageSrc = image.getAttribute("src");
                assertThat(imageSrc)
                        .as("expected to contain an image but was: " + imageSrc)
                        .startsWith("/images/placeholder")
                        .endsWith(".svg");

                final var imageAlt = image.getAttribute("alt");
                assertThat(imageAlt)
                        .as("expected to contain an alt text but was: " + imageSrc)
                        .isEqualTo("Placeholder Image");
            }
        }
    }

    @Test
    void clickOnEventCard() {
        final var eventService = getBean(EventService.class);
        final var testEvent = eventService.getUpcomingEventsWithImage()
                .stream()
                .filter(eventWithImage -> eventWithImage.event().title().equals("Demo Event 3"))
                .findAny()
                .orElseThrow()
                .event();

        final var page = getPage();

        page.navigate("http://localhost:8081/events");
        page.waitForSelector("h1:has-text('Komunumo')");
        captureScreenshot("event-grid-view");

        final var eventCards = page.locator("vaadin-card");
        assertThat(eventCards.count()).isNotZero();

        final var eventCard = eventCards.first();
        final var title = eventCard.locator("div[slot='title']");
        assertThat(title.textContent()).isEqualTo("Demo Event 3");

        eventCard.click();
        page.waitForURL("**/events/" + testEvent.id());
        captureScreenshot("event-detail-view");

        assertThat(page.url()).contains("/events/" + testEvent.id());
        page.waitForSelector("h2:has-text('Demo Event 3')");
    }

}
