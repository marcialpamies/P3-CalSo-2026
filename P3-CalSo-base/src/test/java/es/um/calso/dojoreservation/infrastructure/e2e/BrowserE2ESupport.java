package es.um.calso.dojoreservation.infrastructure.e2e;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

abstract class BrowserE2ESupport {
    protected WebDriver browser;
    protected WebDriverWait wait;

    @BeforeEach
    void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1280,900");
        browser = new ChromeDriver(options);
        wait = new WebDriverWait(browser, Duration.ofSeconds(15));
    }

    @AfterEach
    void stopBrowser() {
        if (browser != null) {
            browser.quit();
        }
    }

    protected void open(String relativePath) {
        String root = System.getProperty("e2e.baseUrl", "http://localhost:8080");
        browser.get(root + relativePath);
    }

    protected void searchDojos(String teamId, String trainingDate) {
        open("/dojos");
        new Select(browser.findElement(By.id("teamId"))).selectByValue(teamId);
        ((JavascriptExecutor) browser).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change', {bubbles: true}));",
                browser.findElement(By.id("trainingDate")), trainingDate);
        browser.findElement(By.id("search")).click();
        wait.until(ExpectedConditions.urlContains("trainingDate=" + trainingDate));
    }

    protected void calculateCost(String teamId, String dojoId, String trainingDate) {
        open("/coste");
        new Select(browser.findElement(By.id("teamId"))).selectByValue(teamId);
        new Select(browser.findElement(By.id("dojoId"))).selectByValue(dojoId);
        ((JavascriptExecutor) browser).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change', {bubbles: true}));",
                browser.findElement(By.id("trainingDate")), trainingDate);
        browser.findElement(By.id("calculate")).click();
        wait.until(ExpectedConditions.urlContains("trainingDate=" + trainingDate));
    }

    protected String textAt(By locator) {
        return wait.until(ExpectedConditions.refreshed(
                ExpectedConditions.visibilityOfElementLocated(locator))).getText();
    }

    protected String textOf(String elementId) {
        return wait.until(ExpectedConditions.refreshed(
                ExpectedConditions.visibilityOfElementLocated(By.id(elementId)))).getText();
    }
}
