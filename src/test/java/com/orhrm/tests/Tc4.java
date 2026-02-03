package com.orhrm.tests;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class Tc4 {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    private ExtentReports extent;
    private ExtentTest test;

    private String projectPath = System.getProperty("user.dir");

    @BeforeSuite
    public void startReport() {
        ExtentSparkReporter spark = new ExtentSparkReporter(
                projectPath + File.separator + "Tc4_Report.html"
        );
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    @AfterSuite
    public void endReport() {
        if (extent != null) {
            extent.flush();
        }
    }

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        js = (JavascriptExecutor) driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        driver.manage().window().maximize();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) throws IOException {
        try {
            if (test != null && driver != null) {
                if (result.getStatus() == ITestResult.SUCCESS) {
                    String path = takeScreenshot(result.getName() + "_PASS");
                    test.pass("Test passed - screenshot attached")
                        .addScreenCaptureFromPath(path);
                } else if (result.getStatus() == ITestResult.FAILURE) {
                    String path = takeScreenshot(result.getName() + "_FAIL");
                    test.fail(result.getThrowable());
                    test.fail("Test failed - screenshot attached")
                        .addScreenCaptureFromPath(path);
                } else if (result.getStatus() == ITestResult.SKIP) {
                    String path = takeScreenshot(result.getName() + "_SKIP");
                    test.skip("Test skipped: " + result.getThrowable())
                        .addScreenCaptureFromPath(path);
                }
            }
        } finally {
            if (driver != null) driver.quit();
        }
    }

    @Test
    public void tc02() {
        test = extent.createTest("Search and Add to Cart - tc02");

        try {
            driver.get("https://automationexercise.com/");
            test.info("Opened site");

            driver.manage().window().setSize(new Dimension(1296, 688));
            test.info("Set window size");

            WebElement products = wait.until(
                    ExpectedConditions.elementToBeClickable(By.linkText(" Products"))
            );
            products.click();
            test.info("Clicked Products");

            WebElement searchBox = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("search_product"))
            );
            searchBox.clear();
            searchBox.sendKeys("shirt");
            test.info("Entered search text: shirt");

            WebElement submit = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("submit_search"))
            );
            submit.click();
            test.info("Clicked Search button");

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".features_items")));
            test.info("Products grid loaded");

            WebElement product = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector(".col-sm-4:nth-child(3) .single-products")
                    )
            );
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", product);
            test.info("Scrolled to product");

            js.executeScript("arguments[0].click();", product);
            test.info("Clicked product using JS");

            WebElement addToCart = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector(".col-sm-4:nth-child(3) .product-overlay .btn")
                    )
            );
            js.executeScript("arguments[0].click();", addToCart);
            test.info("Clicked Add to Cart using JS");

            WebElement viewCart = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//u[normalize-space()='View Cart']"))
            );
            viewCart.click();
            test.info("Clicked View Cart");

            System.out.println("TEST PASSED: tc02 completed successfully!");
            test.pass("tc02 completed successfully!");

        } catch (Exception e) {
            System.out.println("TEST FAILED: " + e);
            test.fail("Exception occurred: " + e);
            throw e;
        }
    }

    private String takeScreenshot(String name) throws IOException {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        String screenshotsDir = projectPath + File.separator + "Screenshots";
        new File(screenshotsDir).mkdirs();

        String destPath = screenshotsDir + File.separator + name + "_" + timestamp + ".png";
        FileUtils.copyFile(src, new File(destPath));

        System.out.println("Screenshot saved at: " + destPath);
        return destPath;
    }
}