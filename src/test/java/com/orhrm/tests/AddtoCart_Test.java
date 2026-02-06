package com.orhrm.tests;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class AddtoCart_Test {

  private WebDriver driver;
  private WebDriverWait wait;
  private Map<String, Object> vars;
  private JavascriptExecutor js;

  // Extent reporting
  private ExtentReports extent;
  private ExtentTest test;

  private String projectPath = System.getProperty("user.dir");

  @BeforeSuite
  public void startReport() {
    ExtentSparkReporter spark = new ExtentSparkReporter(
        projectPath + File.separator + "AddToCart_Report.html"
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
    driver = new FirefoxDriver();
    js = (JavascriptExecutor) driver;
    wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    vars = new HashMap<>();
  }

  @Test
  public void t7() throws IOException {
    test = extent.createTest("Add to Cart Flow - t7");

    driver.get("https://automationexercise.com/");
    test.info("Opened site");

    driver.manage().window().setSize(new Dimension(1295, 687));
    test.info("Set window size");

    safeClick(By.cssSelector("a[href='/view_cart']"));
    test.info("Clicked View Cart");

    By hereLink = By.xpath("//u[normalize-space()='here']/parent::a");
    safeClick(hereLink);
    test.info("Clicked 'here' link");

    safeClick(By.cssSelector(".panel:nth-child(3) .badge"));
    test.info("Opened Kids category");

    safeClick(By.cssSelector("#Kids li:nth-child(2) > a"));
    test.info("Clicked Kids subcategory item 2");

    safeClick(By.cssSelector(".panel:nth-child(3) .fa"));
    test.info("Expanded Kids category");

    safeClick(By.cssSelector("#Kids li:nth-child(1) > a"));
    test.info("Clicked Kids subcategory item 1");

    safeClick(By.cssSelector(".col-sm-4:nth-child(4) .choose a"));
    test.info("Opened a product");

    setQuantity(3);
    test.info("Set quantity to 3");

    safeClick(By.cssSelector(".cart"));
    test.info("Clicked Add to cart");

    By viewCart = By.cssSelector(".modal-content a[href='/view_cart']");
    safeClick(viewCart);
    test.info("Clicked View Cart in modal");

    By proceedToCheckout = By.cssSelector("a.check_out");
    safeClick(proceedToCheckout);
    test.info("Clicked Proceed to Checkout");

  

    
  }

  @AfterMethod
  public void tearDown(ITestResult result) throws IOException {
    if (test != null) {
      if (result.getStatus() == ITestResult.FAILURE) {
        String path = takeScreenshot(result.getName());
        test.fail(result.getThrowable());
        test.fail("Test failed - screenshot attached")
            .addScreenCaptureFromPath(path);
      } else if (result.getStatus() == ITestResult.SUCCESS) {
        test.pass("Test passed");
      } else if (result.getStatus() == ITestResult.SKIP) {
        test.skip("Test skipped: " + result.getThrowable());
      }
    }

    if (driver != null) {
      driver.quit();
    }
  }

  private String takeScreenshot(String name) throws IOException {
    File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

    String screenshotsDir = projectPath + File.separator + "Screenshots";
    new File(screenshotsDir).mkdirs();

    String destPath = screenshotsDir + File.separator + name + "_" + timestamp + ".png";
    FileUtils.copyFile(src, new File(destPath));
    return destPath;
  }

  private void setQuantity(int qty) {
    By qtyBox = By.id("quantity");
    WebElement q = wait.until(ExpectedConditions.visibilityOfElementLocated(qtyBox));
    js.executeScript("arguments[0].scrollIntoView({block:'center'});", q);

    q.click();
    q.sendKeys(Keys.chord(Keys.CONTROL, "a"));
    q.sendKeys(Keys.BACK_SPACE);
    q.sendKeys(String.valueOf(qty));
  }

  private void safeClick(By locator) {
    WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
    wait.until(ExpectedConditions.elementToBeClickable(locator));

    try {
      el.click();
    } catch (ElementNotInteractableException e) {
      js.executeScript("arguments[0].click();", el);
    }
  }
}