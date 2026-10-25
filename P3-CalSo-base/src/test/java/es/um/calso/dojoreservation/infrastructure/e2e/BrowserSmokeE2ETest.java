package es.um.calso.dojoreservation.infrastructure.e2e;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

class BrowserSmokeE2ETest extends BrowserE2ESupport {

    @Test
    void testGivenRunningWebApplicationWhenOpeningDojoSearchThenSearchFormIsVisible() {
        open("/dojos");
        assertTrue(browser.findElement(By.id("search")).isDisplayed());
    }
}
