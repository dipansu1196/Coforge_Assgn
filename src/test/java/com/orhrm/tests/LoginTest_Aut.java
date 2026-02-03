package com.orhrm.tests;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class LoginTest_Aut {

  private WebDriver driver;
  private Map<String, Object> vars;
  private JavascriptExecutor js;

  private ExtentReports extent;
  private ExtentTest test;

  private String projectPath = System.getProperty("user.dir");

  @BeforeSuite
  public void startReport() {
    ExtentSparkReporter spark = new ExtentSparkReporter(
        projectPath + File.separator + "Login_Report.html"
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
    vars = new HashMap<>();
  }

  @AfterMethod(alwaysRun = true)
  public void tearDown(ITestResult result) throws IOException {
    try {
      if (test != null) {

        if (driver != null) {
          String statusName;

          if (result.getStatus() == ITestResult.SUCCESS) {
            statusName = "PASS";
            String path = takeScreenshot(result.getName() + "_" + statusName);
            test.pass("Test passed - screenshot attached")
                .addScreenCaptureFromPath(path);

          } else if (result.getStatus() == ITestResult.FAILURE) {
            statusName = "FAIL";
            String path = takeScreenshot(result.getName() + "_" + statusName);
            test.fail(result.getThrowable());
            test.fail("Test failed - screenshot attached")
                .addScreenCaptureFromPath(path);

          } else if (result.getStatus() == ITestResult.SKIP) {
            statusName = "SKIP";
            String path = takeScreenshot(result.getName() + "_" + statusName);
            test.skip("Test skipped: " + result.getThrowable())
                .addScreenCaptureFromPath(path);
          }

        } else {
          if (result.getStatus() == ITestResult.FAILURE) {
            test.fail(result.getThrowable());
          }
          test.warning("Driver was null / closed. Screenshot not captured.");
        }
      }

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void tC1() {
    test = extent.createTest("Login Flow - tC1");

    driver.get("https://automationexercise.com/");
    test.info("Opened site");

    driver.manage().window().setSize(new Dimension(1170, 672));
    test.info("Set window size");

    driver.findElement(By.linkText("Signup / Login")).click();
    test.info("Clicked Signup / Login");

    driver.findElement(By.name("email")).click();
    driver.findElement(By.name("email")).sendKeys("dipansu125@gmail.com");
    test.info("Entered email");

    driver.findElement(By.name("password")).click();
    driver.findElement(By.name("password")).sendKeys("Dipu@123");
    test.info("Entered password");

    driver.findElement(By.cssSelector(".btn:nth-child(4)")).click();
    test.info("Clicked Login button");
  }

  private String takeScreenshot(String name) throws IOException {
    File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

    File screenshotsDir = new File(projectPath + File.separator + "Screenshots");
    if (!screenshotsDir.exists()) {
      screenshotsDir.mkdirs();
    }

    String destPath = screenshotsDir.getAbsolutePath()
        + File.separator + name + "_" + timestamp + ".png";

    FileUtils.copyFile(src, new File(destPath));
    System.out.println("Screenshot saved at: " + destPath);

    return destPath;
  }
}