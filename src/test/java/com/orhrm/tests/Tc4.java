package com.orhrm.tests;
 
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
 
import java.time.Duration;
 
public class Tc4 {
 
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;   // ✅ FIX: declare js here
 
    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        js = (JavascriptExecutor) driver; // ✅ now valid
        wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        driver.manage().window().maximize();
    }
 
    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }
 
    @Test
    public void tc02() {
        try {
            driver.get("https://automationexercise.com/");
            driver.manage().window().setSize(new Dimension(1296, 688));
 
            // Click Products
            WebElement products = wait.until(
                    ExpectedConditions.elementToBeClickable(By.linkText(" Products"))
            );
            products.click();
 
            // Search jeans
            WebElement searchBox = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("search_product"))
            );
            searchBox.clear();
            searchBox.sendKeys("shirt");
 
            WebElement submit = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("submit_search"))
            );
            submit.click();
 
            // Wait for products grid to load
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".features_items")));
 
            // Scroll to product
            WebElement product = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector(".col-sm-4:nth-child(3) .single-products")
                    )
            );
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", product);
 
            // Click product using JS to avoid intercepted click
            js.executeScript("arguments[0].click();", product);
 
            // Click Add to cart (overlay button)
            WebElement addToCart = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector(".col-sm-4:nth-child(3) .product-overlay .btn")
                    )
            );
            js.executeScript("arguments[0].click();", addToCart);
 
            // Click View Cart (more stable than cssSelector("u"))
            WebElement viewCart = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//u[normalize-space()='View Cart']"))
            );
            viewCart.click();
 
            System.out.println("✅ TEST PASSED: tc02 completed successfully!");
 
        } catch (Exception e) {
            System.out.println("❌ TEST FAILED: " + e);
            throw e;
        }
    }
}
