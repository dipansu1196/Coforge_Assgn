package com.orhrm.tests;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

public class AddtoCart_Test {

  private WebDriver driver;
  private WebDriverWait wait;
  private Map<String, Object> vars;
  private JavascriptExecutor js;

  @BeforeMethod
  public void setUp() {
    driver = new FirefoxDriver();
    js = (JavascriptExecutor) driver;
    wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    vars = new HashMap<>();
  }

  @AfterMethod
  public void tearDown() {
    driver.quit();
  }

  @Test
  public void t7() {
    driver.get("https://automationexercise.com/");
    driver.manage().window().setSize(new Dimension(1295, 687));

    
    safeClick(By.cssSelector("a[href='/view_cart']"));


    By hereLink = By.xpath("//u[normalize-space()='here']/parent::a");
    safeClick(hereLink);

   
    safeClick(By.cssSelector(".panel:nth-child(3) .badge"));   // Kids expand
    safeClick(By.cssSelector("#Kids li:nth-child(2) > a"));    // e.g. Dress
    safeClick(By.cssSelector(".panel:nth-child(3) .fa"));      // collapse/expand
    safeClick(By.cssSelector("#Kids li:nth-child(1) > a"));    // e.g. Tops

 
    safeClick(By.cssSelector(".col-sm-4:nth-child(4) .choose a"));

  
    setQuantity(3);

   
    safeClick(By.cssSelector(".cart"));

    
    By viewCart = By.cssSelector(".modal-content a[href='/view_cart']");
    safeClick(viewCart);


    By proceedToCheckout = By.cssSelector("a.check_out"); // often used on this site
  
    safeClick(proceedToCheckout);

   
    By registerLogin = By.cssSelector(".modal-content a.btn.btn-success");
    safeClick(registerLogin);
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

  private void safeClick(By locator) throws ElementClickInterceptedException {
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
