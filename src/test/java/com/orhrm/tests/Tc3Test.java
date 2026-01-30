package com.orhrm.tests;

import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Set;

public class Tc3Test {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        EdgeOptions options = new EdgeOptions();

        // Helps with popups/overlays that block clicks
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-geolocation");
        options.addArguments("--start-maximized");

        // Optional: sometimes helps reduce automation detection & Edge UI overlays
        options.addArguments("--disable-blink-features=AutomationControlled");

        // Edge browser launch
        driver = new EdgeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    public void tc3() {
        driver.get("https://www.ajio.com/");

        // Wait for page to be ready
        wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));

        // Best-effort: dismiss common overlays (location prompts / banners)
        dismissOverlaysIfPresent();

        // --- 1) Find search box (try multiple locators, because AJIO is dynamic) ---
        WebElement search = waitForAnyVisible(Duration.ofSeconds(20),
                By.cssSelector("input[name='searchVal']"),
                By.cssSelector("input[placeholder*='Search']"),
                By.cssSelector("input[aria-label*='Search']"),
                By.cssSelector("input[type='text']")
        );

        search.click();
        search.clear();
        search.sendKeys("pen");
        search.sendKeys(Keys.ENTER);

        // --- 2) Wait for search results page to load ---
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("search"),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href*='/p/']"))
        ));

        // --- 3) Click first product (more stable than the recorded CSS class with product id) ---
        WebElement firstProduct = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href*='/p/']")   // product URLs typically contain /p/
        ));

        String originalWindow = driver.getWindowHandle();
        int windowsBefore = driver.getWindowHandles().size();

        firstProduct.click();

        // If it opens in a new tab/window, switch; otherwise continue in same tab
        wait.until(d -> d.getWindowHandles().size() >= windowsBefore);
        switchToNewWindowIfOpened(originalWindow);

        dismissOverlaysIfPresent();

        // --- 4) Add to bag ---
        WebElement addToBag = waitForAnyClickable(Duration.ofSeconds(20),
                By.cssSelector(".btn-gold"),
                By.cssSelector("button.btn-gold"),
                By.xpath("//*[self::button or self::div][contains(.,'ADD TO BAG') or contains(.,'Add to Bag')]")
        );
        addToBag.click();

        // --- 5) Go to cart (try common cart buttons/links) ---
        WebElement goToCart = waitForAnyClickable(Duration.ofSeconds(20),
                By.cssSelector(".btn-cart"),
                By.cssSelector("a[href*='cart']"),
                By.xpath("//*[contains(.,'GO TO BAG') or contains(.,'Go to Bag') or contains(.,'Cart')]")
        );
        goToCart.click();

        // --- 6) Assertion: cart page loaded (basic) ---
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("cart"),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(.,'Shopping Bag') or contains(.,'Bag')]"))
        ));
    }

    private void dismissOverlaysIfPresent() {
        // Don’t fail test if overlay is not there—just try.
        By[] possibleCloseButtons = new By[]{
                By.cssSelector("button[aria-label='Close']"),
                By.cssSelector(".close, .ic-close, .modal-close"),
                By.xpath("//button[contains(.,'Not Now') or contains(.,'NOT NOW')]"),
                By.xpath("//button[contains(.,'Allow')]/preceding::button[1]") // sometimes there’s an X near allow
        };

        for (By by : possibleCloseButtons) {
            try {
                WebElement el = new WebDriverWait(driver, Duration.ofSeconds(2))
                        .until(ExpectedConditions.elementToBeClickable(by));
                el.click();
            } catch (TimeoutException ignored) {
            } catch (ElementClickInterceptedException ignored) {
            }
        }
    }

    private void switchToNewWindowIfOpened(String originalWindow) {
        Set<String> handles = driver.getWindowHandles();
        if (handles.size() > 1) {
            for (String h : handles) {
                if (!h.equals(originalWindow)) {
                    driver.switchTo().window(h);
                    return;
                }
            }
        }
    }

    private WebElement waitForAnyVisible(Duration timeout, By... locators) {
        WebDriverWait localWait = new WebDriverWait(driver, timeout);
        for (By by : locators) {
            try {
                return localWait.until(ExpectedConditions.visibilityOfElementLocated(by));
            } catch (TimeoutException ignored) {}
        }
        throw new NoSuchElementException("None of the locators became visible: " + Arrays.toString(locators));
    }

    private WebElement waitForAnyClickable(Duration timeout, By... locators) {
        WebDriverWait localWait = new WebDriverWait(driver, timeout);
        for (By by : locators) {
            try {
                return localWait.until(ExpectedConditions.elementToBeClickable(by));
            } catch (TimeoutException ignored) {}
        }
        throw new NoSuchElementException("None of the locators became clickable: " + Arrays.toString(locators));
    }
}