package SeleniumPack;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Login_POM {

	WebDriver driver;
	
	By username= By.name("username");
	By password= By.name("password");
	By submitbutton= By.xpath("//h6[button(@type='submit']");
	By dashboard= By.xpath("//h6[text('Dashboard']");
	


	public Login_POM(WebDriver driver2) {
		this.driver=driver2;
		// TODO Auto-generated constructor stub
	}

	public void enterusername(String uname) {
		driver.findElement(username).sendKeys(uname);
	}
	
	public void enterpassword(String pword) {
		driver.findElement(password)
.sendKeys(pword);	}
	
	public void clickonsubmit() {
		driver.findElement(submitbutton).click();
	}
	
	public boolean dashboarddisplayed() {
		return driver.findElement(dashboard).isDisplayed();
	}
}
