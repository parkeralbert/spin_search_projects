package com.parc.spinsearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MbSearch{
	
	public void addArtistNames(String line, ArrayList<String> artistInfo) {
		
		if(line.trim().length() > 0 && !line.contains("Last Day of Week:") && !line.contains("Date:") && !line.contains("https://") && (!line.equalsIgnoreCase("rolling") || !line.equalsIgnoreCase("published"))) {
			if (!(line.indexOf("*") == 0)) {
				artistInfo.add(line.trim());
			}

		}
		
	}
	
	public void spinSearch(String url, ArrayList<String> artistInfo, String outputPath, String inputPath, boolean append, String allOutput, boolean published) throws Exception {
		published = getSpinType(inputPath);
		WebDriver driver = login(url);
		
		Map<String, ArrayList <String>> spinsByArtist = getSpins(artistInfo, outputPath, inputPath, driver, published);
		
		outputSpinsByArtist(outputPath, spinsByArtist, true);
		outputSpinsByArtist(allOutput, spinsByArtist, true);
	}
	
	public boolean getSpinType(String filePath) {
		boolean spintype = true;
		String line = null;
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			while ((line = reader.readLine()) != null)
			{
				if (line.equalsIgnoreCase("rolling")) {
					spintype = false;
					break;
				}
			}
			reader.close();
		}
		catch (Exception e)
		{
			System.err.println("Error: " + e);
		}

		return spintype;
	}
	
	public WebDriver login(String url) {
		
		WebDriver driver = new ChromeDriver();
		try {
			driver.get(url);
			//driver.findElement(By.id("onetrust-accept-btn-handler")).click();	
			driver.findElement(By.id("app-login-menu")).click();
			
			WebDriverWait wait = new WebDriverWait(driver, 1000);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("app-login-username")));
			
			driver.findElement(By.id("app-login-username")).sendKeys("ARTISTCOOP19");
			driver.findElement(By.id("app-login-password")).sendKeys("terrorbird2020");
			driver.findElement(By.id("app-login-submit")).click();
			
			wait.until(ExpectedConditions.presenceOfElementLocated(By.className("grid-body")));
			wait.until(ExpectedConditions.elementToBeClickable(By.className("list-item")));
			WebElement selector = driver.findElement(By.xpath("//td[contains(text(), '7-Day Song Analysis')]"));
			selector.click();
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			e.printStackTrace();
	    }
		return driver;
	}
	
	public Map<String, ArrayList <String>> getSpins(ArrayList<String> artistInfo, String filePath, String inputPath, WebDriver driver, boolean published) throws Exception {
		Map<String, ArrayList <String>> spinsToPrint = new HashMap<>();
		int iterator = 0;
		try {
		    for (String currentArtist : artistInfo) {
					ArrayList<String[]> spinData = getSpinData(currentArtist, driver, iterator, published);
					addSpin(spinData, currentArtist, spinsToPrint);
					iterator++;
		    }
		}
		finally {
		    driver.quit();
		}

	    
		return spinsToPrint;

	}
	
	
	public ArrayList <String[]> getSpinData(String currentArtist, WebDriver driver, int iterator, boolean published){
		ArrayList <String[]> allSpinData = new ArrayList<>();
		WebDriverWait wait = new WebDriverWait(driver, 1000);
		try {
		driver.get("https://www2.mediabase.com/mbapp/SongAnalysisReport/Index");
		Thread.sleep(2000);
		if (iterator==0) {
			if (published) {
				wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@title='Published']")));
				WebElement cycleSelection= driver.findElement(By.xpath("//select[@title='Published']"));
				cycleSelection.click();
				wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//option[@title='Published']")));
				WebElement cycle= driver.findElement(By.xpath("//option[@title='Published']"));
				cycle.click();
			}
			else {
				wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@title='Published']")));
				WebElement cycleSelection= driver.findElement(By.xpath("//select[@title='Published']"));
				cycleSelection.click();
				wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//option[@title='Rolling']")));
				WebElement cycle= driver.findElement(By.xpath("//option[@title='Rolling']"));
				cycle.click();
			}
			WebElement panelSelection= driver.findElement(By.xpath("//select[@title='Mediabase - Published Panel']"));
			panelSelection.click();
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//option[@title='Mediabase - All Stations (U.S)']")));
			WebElement panel= driver.findElement(By.xpath("//option[@title='Mediabase - All Stations (U.S)']"));
			panel.click();
		}
		WebElement selection = driver.findElement(By.xpath("//div[@class='mb-txt-selection mb-input-selector']")); 
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selection);
		//Thread.sleep(2000);
		//wait.until(ExpectedConditions.visibilityOf(button));

		while (driver.findElements((By.xpath("//input[@class = 'mb-form-control']"))).size() == 0) {
			/*for (WebElement button : buttons) {
				button.click();
			}
			button.click();
			//driver.findElement(By.xpath("//div[@class='mb-txt-selection mb-input-selector']")).click();
			//driver.findElement(By.xpath("//button[@class='ui-button ui-widget btn-action']")).click();
			Thread.sleep(2000);*/
			List<WebElement> selectChildren = selection.findElements(By.xpath("./child::*"));
			WebElement button = null;
			for (WebElement child : selectChildren) {
				List<WebElement> finalChildren = child.findElements(By.xpath("./child::*"));
				for(int i = 0; i<finalChildren.size(); i++) {
					if (i==1) {
						button = finalChildren.get(i);
						break;
					}
				}
				if (button != null) {
					break;
				}
			}
			JavascriptExecutor executor = (JavascriptExecutor)driver;
			//
			executor.executeScript("arguments[0].click();", button);
			Thread.sleep(1000);
		}
		wait.until(ExpectedConditions.presenceOfElementLocated((By.xpath("//input[@class='all-row-selector']"))));
		
		for (int i = 0; i<200; i++) {
			try {
				List <WebElement> checkBoxes= driver.findElements(By.xpath("//input[@class='all-row-selector']"));
				checkBoxes.get(1).click();
				break;
			}
			catch(org.openqa.selenium.StaleElementReferenceException  | ElementNotInteractableException v){
				Thread.sleep(200);
			}
		}

		driver.findElement(By.xpath("//button[@class='mb-btn-remove']")).click();
		driver.findElement(By.xpath("//input[@class='mb-chk-airplay']")).click();
		driver.findElement(By.xpath("//input[@class='mb-form-control']")).sendKeys(currentArtist);

		driver.findElement(By.xpath("//div[@class='mb-rbtn-type mb-radio-button-picker']")).click();
		driver.findElement(By.xpath("//button[@class='btn btn-default']")).click();
		
		WebElement eb = driver.findElement(By.xpath("//div[@class='mb-search-list mb-data-list']"));
		List<WebElement> songFinder = eb.findElements(By.xpath("./child::*"));
		List<WebElement> songTable = songFinder.get(0).findElements(By.xpath("./child::*"));
		List<WebElement> tableList = songTable.get(0).findElements(By.xpath("./child::*"));
		WebElement listEmpty = tableList.get(0);
		
		wait.until(ExpectedConditions.or(
				ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[@class = 'list-item selectable']")),
				ExpectedConditions.textToBePresentInElement(listEmpty, "The list is empty.")
			));
		
			
		int numSelected = selectSongs(currentArtist, driver);
		
		if (numSelected == 0) {
			driver.findElement(By.xpath("//button[@class='ui-button ui-corner-all ui-widget ui-button-icon-only ui-dialog-titlebar-close']")).click();
			return null;		
		}
		
		driver.findElement(By.xpath("//button[@class='mb-btn-add']")).click();
		driver.findElement(By.xpath("//button[@class='dlg-btn-ok ui-button ui-corner-all ui-widget']")).click();

		wait.until(ExpectedConditions.elementToBeClickable(By.id("app-btn-run-report")));
		driver.findElement(By.id("app-btn-run-report")).click();


		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ul[contains(@id,'mb-tab-nav')]")));
		WebElement navBar= driver.findElement(By.xpath("//ul[contains(@id,'mb-tab-nav')]"));
		List<WebElement> tabs = navBar.findElements(By.xpath("./child::*"));

		String station = null;
		String location = null;
		String spinCount = null;
		String song = null;
		List<WebElement> nextTab = null;

		for(int i = 0;  i<tabs.size(); i++) {
			
			
			if(i != tabs.size()-1) {
				 nextTab =  tabs.get(i+1).findElements(By.xpath("./child::*"));
			}

			
			Thread.sleep(3000);
			wait.until(ExpectedConditions.or(
				    ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(text(), 'No records found')]")),
				    ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@class='ui-jqgrid-btable']"))
				)); 
			
			//wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(text(),'status-box-text']")));
			/*WebElement statusBox= driver.findElement(By.xpath("//div[@class='status-box-text']"));
				if(statusBox.getText().equalsIgnoreCase("No records found")) {
					if(i != tabs.size()-1) {
						try {
							nextTab.get(0).click();
						}
						catch (org.openqa.selenium.NoSuchElementException  | ElementNotInteractableException e) {
							driver.findElement(By.xpath("//i[@class='glyphicon glyphicon-chevron-right']")).click();
							Thread.sleep(1000);
							nextTab.get(0).click();
						}
					}

				}*/
					List<WebElement> thisTab =  tabs.get(i).findElements(By.xpath("./child::*"));
					List<WebElement> tabText = thisTab.get(0).findElements(By.xpath("./child::*"));
					List<WebElement> songDiv = tabText.get(0).findElements(By.xpath("./child::*"));
					String[] songAndArtist = songDiv.get(0).getText().split("-");
					song = songAndArtist[1];
					
					WebElement table = driver.findElement(By.xpath("//table[@class='ui-jqgrid-btable']"));
					List <WebElement> tbody = table.findElements(By.xpath("./child::*"));
					List <WebElement> rows = tbody.get(0).findElements(By.xpath("./child::*"));
					//List <WebElement> data = rows.get(1).findElements(By.xpath("./child::*"));
					//System.out.println("table is " + driver.findElement(By.xpath("//table[@class='ui-jqgrid-btable']")).getText());
					
					if (rows.size() > 1) {
						for (WebElement row : rows) {
							if (row == rows.get(0)) {
								continue;
							}
							
							List <WebElement> tds = row.findElements(By.xpath("./child::*"));
							if (published) {
								if(tds.get(7).getText().equalsIgnoreCase("Triple A") || tds.get(6).getText().equalsIgnoreCase(">SiriusXM")) {
									station = tds.get(2).getText();
									location = tds.get(6).getText();
									spinCount = tds.get(12).getText();
									String [] spin = {station, location, currentArtist, song, spinCount};
									System.out.println("Spin is: " + spin[0] + spin[1] + spin[3] + spin[4]);
									allSpinData.add(spin);
								}
							}
							else {
								if(tds.get(7).getText().equalsIgnoreCase("Triple A") || tds.get(6).getText().equalsIgnoreCase(">SiriusXM")) {
									station = tds.get(2).getText();
									location = tds.get(6).getText();
									spinCount = tds.get(12).getText();
									String [] spin = {station, location, currentArtist, song, spinCount};
									System.out.println("Spin is: " + spin[0] + spin[1] + spin[3] + spin[4]);
									allSpinData.add(spin);
								}
							}
						}
					}
					if(i != tabs.size()-1) {
							try {
								nextTab.get(0).click();
							}
							catch (org.openqa.selenium.NoSuchElementException  | ElementNotInteractableException e) {
									driver.findElement(By.xpath("//i[@class='glyphicon glyphicon-chevron-right']")).click();
									Thread.sleep(2000);
									try {
										nextTab.get(0).click();
									}
									catch (ElementNotInteractableException f) {
										try {
											driver.findElement(By.xpath("//i[@class='glyphicon glyphicon-chevron-right']")).click();
											Thread.sleep(2000);
											nextTab.get(0).click();
										}
										
										catch (ElementNotInteractableException g) {
											break;
										}
									}
						    }
					}
				
		}
	}

		catch (org.openqa.selenium.NoSuchElementException | InterruptedException e) {
			e.printStackTrace();
		}
	
		return allSpinData;
	}

	public int selectSongs(String currentArtist, WebDriver driver) {
		int numSelected = 0;
		try {
			WebElement searchResults= driver.findElement(By.xpath("//div[@class='mb-count']"));
			String numResults = searchResults.getText().replaceAll(" items", "");
			int resultNumber = Integer.parseInt(numResults);  
			
			if(resultNumber > 300) {
				return numSelected;
			}
			
			List <WebElement> sortButtons = driver.findElements(By.xpath("//span[@class='ui-icon-asc ui-sort-ltr ui-icon ui-icon-triangle-1-n']"));
			sortButtons.get(3).click();
			WebDriverWait wait = new WebDriverWait(driver, 1000);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class = 'ui-icon-asc ui-sort-ltr ui-icon ui-icon-triangle-1-n ui-state-disabled']")));
			WebElement firstResult= driver.findElement(By.xpath("//tr[@class='list-item selectable']"));
			List<WebElement> resultData = firstResult.findElements(By.xpath("./child::*"));
			if(resultData.get(1).getText().equalsIgnoreCase(currentArtist) && (resultData.get(4).getText().equalsIgnoreCase("2022") || resultData.get(4).getText().equalsIgnoreCase("2021"))) {
				
			}
			else {
				return numSelected;
			}
			
			List <WebElement> selectables= driver.findElements(By.xpath("//tr[@class='list-item selectable'] | //tr[@class='list-item selectable hover']"));
/*			
			selectables.get(0).click();
			selectables.get(1).click();
			selectables.get(2).click();
			numSelected = 3;
*/			
			List<WebElement> tableData = null;
			//WebElement selectable = null;
			Actions actions = new Actions(driver);
			//actions.moveToElement(menuOption).perform();
			for (WebElement selectable : selectables) {
				tableData = selectable.findElements(By.xpath("./child::*"));
				
				if(tableData.get(1).getText().equalsIgnoreCase(currentArtist)   && (tableData.get(4).getText().equalsIgnoreCase("2022") || tableData.get(4).getText().equalsIgnoreCase("2021"))) {
					actions.moveToElement(selectable).perform();
					selectable = driver.findElement(By.xpath("//tr[@class='list-item selectable hover']"));
					selectable.click();
					numSelected++;
				}
				
				if (numSelected == 0) {
					break;
				}
			}
			
			
			
			/*for (int i = 0; i < selectables.size(); i++) {
				if (numSelected == 0) {
					selectable = selectables.get(0);
					tableData = selectable.findElements(By.xpath("./child::*"));
				}
				if (numSelected > 0) {
					List <WebElement> newSelectables= driver.findElements(By.xpath("//tr[@class='list-item selectable'] | //tr[@class='list-item selectable hover']"));
					selectable = newSelectables.get(0);
					tableData = newSelectables.get(0).findElements(By.xpath("./child::*"));
				}
				if(tableData.get(1).getText().equalsIgnoreCase(currentArtist)   && (tableData.get(4).getText().equalsIgnoreCase("2021") || tableData.get(4).getText().equalsIgnoreCase("2020"))) {
					selectable.click();
					numSelected++;
					Thread.sleep(2000);
				}
				if (numSelected == 0) {
					break;
				}
			}*/
		
		}
		catch(org.openqa.selenium.NoSuchElementException  e){
			
		}
		return numSelected;
	}
	
	
	public void addSpin(ArrayList <String[]> spinData, String currentArtist, Map<String, ArrayList <String>> spinsToPrint) throws Exception   {
		if(spinData != null) {
			
			ArrayList <String> spins = new ArrayList<>();
			
			for (String[] spin : spinData) {
					//{station, location, currentArtist, song, spinCount};
					String station = spin[0];
					String location = spin[1];
					String artist = spin[2];
					String song = spin[3];
					String spinCount = spin[4];
					
					spins.add("Mediabase" + "|" + currentArtist + "|" + "-" + "|" + song + "|" + station + "|" + location + "|" + "-" + "|" + "-" + "|" + "-" + "|" + spinCount);
			}
			
			spinsToPrint.put(currentArtist, spins);
			
		}
	}

	public void outputSpinsByArtist(String filePath, Map<String, ArrayList <String>> spinsByArtist, boolean append) throws Exception {
		BufferedWriter writer;
		if (append) {
			 writer = new BufferedWriter(new FileWriter(filePath, true));
		}
		else {
			 writer = new BufferedWriter(new FileWriter(filePath));
		}
		
		writer.newLine();
		writer.close();
		for (String currentArtist : spinsByArtist.keySet()) {
			writeSpinsToFile(currentArtist, spinsByArtist.get(currentArtist), filePath);
		}
	}
	
	public void writeSpinsToFile(String currentArtist, ArrayList <String> rawSpins, String filePath) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
				if(rawSpins.size() > 0) {
				for (String rawSpin : rawSpins) {
					writer.write(rawSpin);
					writer.newLine();
				}
				writer.close();
			}
	}
	
}
