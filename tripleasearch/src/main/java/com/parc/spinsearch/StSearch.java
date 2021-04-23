package com.parc.spinsearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class StSearch extends SpinSearch{
	
	
	public void spinSearch(String url, ArrayList<String> artistInfo, String outputPath, String inputPath, boolean append, String allOutput, Date firstDayOfWeek, Date lastDayOfWeek) throws Exception {
		Map<String, ArrayList <String>> spinsByArtist = getSpins(url, artistInfo, outputPath, inputPath, firstDayOfWeek, lastDayOfWeek);
		outputSpinsByArtist(outputPath, spinsByArtist, append);
		outputSpinsByArtist(allOutput, spinsByArtist, true);
	}
	
	public Map<String, ArrayList <String>> getSpins(String url, ArrayList <String> artistInfo, String outputPath, String inputPath, Date firstDayOfWeek, Date lastDayOfWeek) throws Exception {
		Map<String, ArrayList <String>> spinsToPrint = new HashMap<>();
		
		
		WebDriver driver = login(url);
		
	    for (String currentArtist : artistInfo) {
					ArrayList<ArrayList <String>> spinData = getSpinData(currentArtist, driver, url);
					addSpin(spinData, currentArtist, spinsToPrint, firstDayOfWeek,  lastDayOfWeek);
	    } 
		driver.quit();
	    
		return spinsToPrint; 

	}
	
	
	public static String insertString(String originalString, String stringToBeInserted, int index) 
    { 
        StringBuffer newString = new StringBuffer(originalString); 
        
        newString.insert(index + 1, stringToBeInserted); 
   
        return newString.toString(); 
    } 
	
	public String addDateToUrl (String date, String url, boolean dayOnly) {
		String [] segments = date.split("/");
		String secondHalf = null;
		for (int i = 0; i<segments.length; i++) {
			if(i == 0) {
				if (dayOnly) {
					url = insertString(url, segments[i], 45);
				}
				else {
					url = insertString(url, segments[i], 46);
				}
			}
			if(i == 1) {
				if (dayOnly) {
					secondHalf = url.substring(52);
					url = url.substring(0, 52) + segments[i];
				}
				else {
					secondHalf = url.substring(53);
					url = url.substring(0, 53) + segments[i];
				}
			}
			if(i == 2) {
				if (dayOnly) {
					secondHalf = insertString(secondHalf, segments[i], 4);
					url = url + secondHalf;
				}
				else {
					secondHalf = insertString(secondHalf, segments[i], 5);
					url = url + secondHalf;
				}
			}
		}
		System.out.println("url is: " + url);
		return url;
	}
	
	//"https://spinitron.com/m/search?range=week&date=Feb%209
	//%2C%202021&q=Arlo%20Parks"
	//"https://spinitron.com/m/search?range=week&date=Feb%209%2C%202021&q=Arlo%20Parks"
	public String addArtistToUrl (String currentArtist, String url) {
		currentArtist = currentArtist.replaceAll(" ", "%20");
		url = url + currentArtist;
		System.out.println("new url is: " + url);
		return url;
	}
	
	public String getDateForUrl (Date date) {
		String urlDate = null;
		urlDate = new SimpleDateFormat("MMM/dd/yyyy").format(date);
		System.out.println("Url date is: " + urlDate);
		return urlDate;
	}
	
	
	public  ArrayList<ArrayList <String>> getSpinData(String currentArtist, WebDriver driver, String url) {
		//ArrayList <String> currentArtist, String inputPath, 
		
		ArrayList<ArrayList<String>> allSpinData = new ArrayList<>();
		WebElement rawData = null;
		
		url = addArtistToUrl(currentArtist, url);
			
		try {
			
			driver.get(url);
			rawData = driver.findElement(By.id("public-search-grid"));
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
	    }
		
		List<WebElement> pageData = rawData.findElements(By.xpath("./child::*"));
		
		for(WebElement spin : pageData) {
			
			List<WebElement> tableBody = spin.findElements(By.xpath("./child::*"));
			
			for(WebElement tr : tableBody) {
				List<WebElement> tableData = tr.findElements(By.xpath("./child::*"));
				
				for (WebElement td : tableData) {
					List<WebElement> eachCell = td.findElements(By.xpath("./child::*"));
					ArrayList<String> spinData = new ArrayList<>();
					
					for (WebElement cell : eachCell) {
						spinData.add(cell.getText());
					}
					allSpinData.add(spinData);
				}
				
			}
		}
		return allSpinData;
	}
	
	public void addSpin(ArrayList <ArrayList<String>> spinData, String currentArtist, Map<String, ArrayList <String>> spinsToPrint, Date firstDayOfWeek, Date lastDayOfWeek) throws Exception   {
		if(spinData != null) {
			String stationName = null;
			String song = null;
			String artist = null;
			String album = null;
			SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy, h:mm a");
			Date spinDate = null;
			String date = "";
			String spinToAdd;
			boolean alreadyAdded;
			
			ArrayList <String> spins = new ArrayList<>();
			for (ArrayList<String> spin : spinData) {
				if (spin.size() >= 3 && !spin.get(1).contains("Station")) {
					alreadyAdded = false;
					for (int i = 0; i< spin.size(); i++) {
						if (i==0) {
							spinDate = formatter.parse(spin.get(i).replaceAll("\\*", ""));
							SimpleDateFormat dateToString = new SimpleDateFormat("yyyy-MM-dd|h:mm a");
							date = dateToString.format(spinDate);
						}
						if (i==1) {
							stationName = spin.get(i);
						}
						if (i==2) {
							if (spin.get(i).equals("")){
								song = "n/a";
							}
							else {
								song = spin.get(i);
							}
						}
						if (i==3) {
							if (spin.get(i).equals("")){
								artist = "n/a";
							}
							else {
								artist = spin.get(i);
							}
						}
						if (i==4) {
							if (spin.get(i).equals("")){
								album = "n/a";
							}
							else {
								album = spin.get(i);
							}
						}
					}
					
					if (artist.equalsIgnoreCase(currentArtist) && isDateInRange(firstDayOfWeek, lastDayOfWeek, spinDate)) {
						spinToAdd = "Spinitron" + "|" + artist + "|" + album + "|" + song + "|" +  stationName + "|" + "-" + "|" + "-" + "|" + date + "|" + "-";
						for (String addedSpin : spins) {
							if (addedSpin.equalsIgnoreCase(spinToAdd)) {
								alreadyAdded = true;
								break;
							}
						}
						if (!alreadyAdded) {
							spins.add(spinToAdd);
							System.out.println("Spin for " + currentArtist + ": "+ spinToAdd);
						}
					}

				}
			}
				spinsToPrint.put(currentArtist, spins);
		}
	}
	
	
	public WebDriver login(String url) {
		
		WebDriver driver = new ChromeDriver();
		try {
			driver.get("https://spinitron.com/m/search/status");
			WebDriverWait wait = new WebDriverWait(driver, 1000);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(text(), 'Terrorbird')]")));
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
	    }
		return driver;
	}

	
	public void outputSpinsByArtist(String filePath, Map<String, ArrayList <String>> spinsByArtist, boolean append) throws Exception {
		
		BufferedWriter writer;
		
		if(append) {
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
	public void writeSpinsToFile(String currentArtist, ArrayList <String> spins, String filePath) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
				if(spins.size() > 0) {
				for (String spin : spins) {
					writer.write(spin);
					writer.newLine();
				}
				writer.close();
			}
	}
	
}
