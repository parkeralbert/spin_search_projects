package com.parc.spinsearch;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MbSearch {

	
	public WebDriver login(String url) {
		
		WebDriver driver = new ChromeDriver();
		try {
			driver.get(url);
			driver.findElement(By.id("app-login-menu")).click();	
			WebDriverWait wait = new WebDriverWait(driver, 1000);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("app-login-username")));
			driver.findElement(By.id("app-login-username")).sendKeys("ARTISTCOOP19");
			driver.findElement(By.id("app-login-password")).sendKeys("terrorbird2020");
			driver.findElement(By.id("app-login-submit")).click();
			//input[contains(@class, 'row-selector')]"
			//and contains(.//td, '7-Day Song Analysis')]"
			wait.until(ExpectedConditions.presenceOfElementLocated(By.className("grid-body")));
			wait.until(ExpectedConditions.elementToBeClickable(By.className("list-item")));
			List <WebElement> selectors = driver.findElements(By.className("list-item"));
			WebElement selector = driver.findElement(By.xpath("//td[contains(text(), '7-Day Song Analysis')]"));
			//((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selector);
			//Thread.sleep(500);
			selector.click();
			/*for (WebElement selector : selectors) {
				if (selector.getText().contains("7-Day Song Analysis")) {
					selector.click();
					break;
				}
			}*/
			   //sdafs   
			//driver.findElement(By.xpath("//div[@class='mb-filter-panel-side']")).click();
			//driver.findElement(By.xpath("//div[@class='mb-filter-panel-side']")).click();
			
			//driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			//Thread.sleep(2000);
			//wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class = 'ui-button ui-widget btn-action']")));
			driver.findElement(By.xpath("//span[@class='mb-pin']")).click();
			List <WebElement> buttons = driver.findElements(By.xpath("//div[@class='mb-txt-selection mb-input-selector']")); 
			//Thread.sleep(2000);
			//wait.until(ExpectedConditions.visibilityOf(button));
			while (driver.findElements((By.xpath("//input[@class = 'mb-form-control']"))).size() == 0) {
				for (WebElement button : buttons) {
					button.click();
				}

			}
			
			//wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@class = 'mb-form-control']")));
			driver.findElement(By.xpath("//input[@class='mb-form-control']")).sendKeys("Arlo Parks");
			
			driver.findElement(By.xpath("//div[@class='mb-rbtn-type mb-radio-button-picker']")).click();
			driver.findElement(By.xpath("//button[@class='btn btn-default']")).click();
			
			Thread.sleep(5000);
			
			List <WebElement> selectables= driver.findElements(By.xpath("//tr[@class='list-item selectable']"));
			for (WebElement selectable : selectables) {
				List<WebElement> tableData = selectable.findElements(By.xpath("./child::*"));
				for (WebElement data : tableData) {
					if(data.getText().equalsIgnoreCase("Arlo Parks")) {
						selectable.click();
					}
				}
			}
			Thread.sleep(3000);
			driver.findElement(By.xpath("//button[@class='mb-btn-add']")).click();
			driver.findElement(By.xpath("//button[@class='dlg-btn-ok ui-button ui-corner-all ui-widget']")).click();
			
			driver.findElement(By.id("app-btn-run-report")).click();

			

			WebElement navBar= driver.findElement(By.id("mb-tab-nav"));
			List<WebElement> tabs = navBar.findElements(By.xpath("./child::*"));
			for(int i = 0;  i<tabs.size(); i++) {
				
				List<WebElement> nextTab =  tabs.get(i+1).findElements(By.xpath("./child::*"));
				
				wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class = 'status-box-text']")));
				WebElement statusBox= driver.findElement(By.xpath("//div[@class='status-box-text']"));
					if(statusBox.getText().equalsIgnoreCase("No records found")) {
						nextTab.get(0).click();
				}
			}
		}
		catch (org.openqa.selenium.NoSuchElementException | InterruptedException e) {
			System.out.println("had an exception  " + driver.getCurrentUrl());
	    }
		return driver;
	}
	
	
	public void testButton(String url) {
		WebDriver driver = new FirefoxDriver();
		try {
			driver.get(url);
			driver.findElement(By.id("app-login-menu")).click();	
			WebDriverWait wait = new WebDriverWait(driver, 1000);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("app-login-username")));
			driver.findElement(By.id("app-login-username")).sendKeys("ARTISTCOOP19");
			driver.findElement(By.id("app-login-password")).sendKeys("terrorbird2020");
			driver.findElement(By.id("app-login-submit")).click();
			//input[contains(@class, 'row-selector')]"
			//and contains(.//td, '7-Day Song Analysis')]"
			wait.until(ExpectedConditions.presenceOfElementLocated(By.className("grid-body")));
			List <WebElement> selectors = driver.findElements(By.className("list-item"));
			
			for (WebElement selector : selectors) {
				if (selector.getText().contains("7-Day Song Analysis")) {
					selector.click();
					break;
				}
			}
			//driver.findElement(By.xpath("//div[@class='mb-filter-panel-side']")).click();
			//driver.findElement(By.xpath("//div[@class='mb-filter-panel-side']")).click();
			
			//driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			//Thread.sleep(2000);
			//wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class = 'ui-button ui-widget btn-action']")));
			driver.findElement(By.xpath("//span[@class='mb-pin']")).click();
			driver.findElement(By.id("app-btn-run-report")).click();
			List <WebElement> buttons = driver.findElements(By.xpath("//div[@class='mb-txt-selection mb-input-selector']"));
			//app-btn-run-report
			//wait.until(ExpectedConditions.visibilityOf(button));
			while (driver.findElements((By.xpath("//input[@class = 'mb-form-control']"))).size() < 1) {
				for (WebElement button : buttons) {
					button.click();
				}

			}
			
			//wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@class = 'mb-form-control']")));
			driver.findElement(By.xpath("//input[@class='mb-form-control']")).sendKeys("Arlo Parks");
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			System.out.println("had an exception  " + driver.getCurrentUrl());
	    }
	}

}
