package com.parc.spinsearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

public class XmSearch extends SpinSearch{
	public void spinSearch(String url, ArrayList<String> artistInfo, String outputPath, String inputPath, boolean append, Date firstDayOfWeek, Date lastDayOfWeek, String allOutput, Boolean exact) throws Exception {
		Map<String, ArrayList <String>> spinsByArtist = getSpins(url, artistInfo, outputPath, inputPath, firstDayOfWeek, lastDayOfWeek, exact);
		outputSpinsByArtist(outputPath, spinsByArtist, true);
		outputSpinsByArtist(allOutput, spinsByArtist, true);
	}
	
	public Map<String, ArrayList <String>> getSpins(String url, ArrayList <String> artistInfo, String outputPath, String inputPath, Date firstDayOfWeek, Date lastDayOfWeek, Boolean exact) throws Exception {
		Map<String, ArrayList <String>> spinsToPrint = new HashMap<>();
		
		WebDriver driver = login(url);
		
	    for (String currentArtist : artistInfo) {
	    			url = createUrl(currentArtist, firstDayOfWeek, lastDayOfWeek);
					ArrayList<ArrayList <String>> spinData = getSpinData(currentArtist, driver, url, exact);
					addSpin(spinData, currentArtist, spinsToPrint, firstDayOfWeek, lastDayOfWeek);
	    } 
		driver.quit();
	    
		return spinsToPrint; 

	}
	
	public String createUrl (String artistName, Date firstDayOfWeek, Date lastDayOfWeek){
		String line = null;
		String urlArtist = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = formatter.format(firstDayOfWeek);
		String endDate = formatter.format(lastDayOfWeek);
		
		ArrayList <String> urls = new ArrayList<>();
		String searchUrl = "https://xmplaylist.com/search?artistName=input&startDate=input&endDate=input&currentPage=1";
		String[] segments = searchUrl.split("input");
		urlArtist = artistName.replaceAll(" ", "+" );
		if (isDayOnly(firstDayOfWeek, lastDayOfWeek)) {
			Date oneDayBefore = new Date(firstDayOfWeek.getTime() - 2);
			startDate = formatter.format(oneDayBefore);
		}
		return (segments[0] + urlArtist + segments[1] + startDate + segments[2] + endDate + segments[3]);		
	}
	
	public WebDriver login(String url) {
		
		WebDriver driver = new ChromeDriver();
		try {
			driver.get(url);
			WebDriverWait wait = new WebDriverWait(driver, 1000);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class = 'text-blue-600']")));
			driver.findElement(By.xpath("//a[@class = 'text-blue-600']")).click();
			
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-md bg-white text-sm leading-5 font-medium text-gray-500 hover:text-gray-400 focus:outline-none focus:border-blue-300 focus:shadow-outline-blue transition duration-150 ease-in-out']")));
			List <WebElement> buttons = driver.findElements(By.xpath("//button[@class = 'w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-md bg-white text-sm leading-5 font-medium text-gray-500 hover:text-gray-400 focus:outline-none focus:border-blue-300 focus:shadow-outline-blue transition duration-150 ease-in-out']"));
			buttons.get(1).click();
			
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@class = 'shadow-sm focus:ring-blue-500 focus:border-blue-500 block w-full sm:text-sm border-gray-300 rounded-md']")));
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			e.printStackTrace();
	    }
		return driver;
	}
	
	public  ArrayList<ArrayList <String>> getSpinData(String currentArtist, WebDriver driver, String url, Boolean exact) {
		ArrayList<ArrayList<String>> allSpinData = new ArrayList<>();
		String song = null;
		String artist = null;
		String date = null;
		String show = "-";
			try {
				driver.get(url);
				WebDriverWait wait = new WebDriverWait(driver, 1000);
				wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'inline-flex items-center px-6 py-2 border border-transparent text-sm leading-4 font-medium rounded-md text-white focus:outline-none focus:border-blue-700 focus:shadow-outline-blue active:bg-blue-700 transition ease-in-out duration-150 bg-blue-600 hover:bg-blue-500']")));
				try {
					WebElement resultList = driver.findElement(By.tagName("ul"));
					List<WebElement> allResults = resultList.findElements(By.xpath("./child::*"));
					List<WebElement> innerLinkChildren = allResults.get(0).findElements(By.xpath("./child::*"));
					List<WebElement> firstDivChildren = innerLinkChildren.get(0).findElements(By.xpath("./child::*"));
					int i = 0;
					while (firstDivChildren.get(0).getText().equalsIgnoreCase("Loading...") ) {
						Thread.sleep(100);
						i++;
						if (i>1000) {
							System.out.println("Url failed to load: " + url);
							return null;
						}
					}
					if (firstDivChildren.get(0).getText().equalsIgnoreCase("No Results")) {
						return null;
					}
				}
				catch (org.openqa.selenium.NoSuchElementException | org.openqa.selenium.StaleElementReferenceException e){
					
				}
				
				WebElement resultList = driver.findElement(By.tagName("ul"));
				List<WebElement> allResults = resultList.findElements(By.xpath("./child::*"));
				if (!exact) {
					for (WebElement spin:allResults) {
						
						ArrayList<String> spinData = new ArrayList<>();
						List<WebElement> innerLinkChildren = spin.findElements(By.xpath("./child::*"));
						List<WebElement> firstDivChildren = innerLinkChildren.get(0).findElements(By.xpath("./child::*"));
						System.out.println("song is: " + firstDivChildren.get(0).getText());
						String rawData = firstDivChildren.get(0).getText().replaceAll("[\\n]", "|");
						String[] segments = rawData.split("\\|");
						song = segments[0];
						artist = segments[1];
						date = segments[2];
						if (segments.length > 3) {
							show = segments[3];
						}
						System.out.println("song: " + song + "artist: " + artist + "date: " + date + "show: " + show);
						spinData.add(song);
						spinData.add(artist);
						spinData.add(date);
						spinData.add(show);
						allSpinData.add(spinData);
					}
				}
				else {
					for (WebElement spin:allResults) {
						
						ArrayList<String> spinData = new ArrayList<>();
						List<WebElement> innerLinkChildren = spin.findElements(By.xpath("./child::*"));
						List<WebElement> firstDivChildren = innerLinkChildren.get(0).findElements(By.xpath("./child::*"));
						System.out.println("song is: " + firstDivChildren.get(0).getText());
						String rawData = firstDivChildren.get(0).getText().replaceAll("[\\n]", "|");
						String[] segments = rawData.split("\\|");
						artist = segments[1];
						if (artist.equalsIgnoreCase(currentArtist)) {
							song = segments[0];
							date = segments[2];
							if (segments.length > 3) {
								show = segments[3];
							}
							System.out.println("song: " + song + "artist: " + artist + "date: " + date + "show: " + show);
							spinData.add(song);
							spinData.add(artist);
							spinData.add(date);
							spinData.add(show);
							allSpinData.add(spinData);
						}
					}
				}

				
			}
			catch (org.openqa.selenium.NoSuchElementException | InterruptedException e){
				e.printStackTrace();
			}
		return allSpinData;
	}
	
	public void addSpin(ArrayList <ArrayList<String>> spinData, String currentArtist, Map<String, ArrayList <String>> spinsToPrint, Date firstDayOfWeek, Date lastDayOfWeek) throws Exception   {
		if(spinData != null) {
			String show = "-";
			String song = "-";
			String artist = "-";
			String album = "-";
			SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy, h:mm a");
			Date spinDate = null;
			String date = "";
			String spinToAdd;
			boolean alreadyAdded;
			
			ArrayList <String> spins = new ArrayList<>();
			for (ArrayList<String> spin : spinData) {
				alreadyAdded = false;
				spinDate = parser.parse(spin.get(2));
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd|h:mm a");
				date = formatter.format(spinDate);
				song = spin.get(0);
				artist = spin.get(1);
				show = spin.get(3);
					
				if (isDateInRange(firstDayOfWeek, lastDayOfWeek, spinDate)) {
					spinToAdd = "SiriusXM" + "|" + artist + "|" + album + "|" + song + "|" +  "SiriusXM" + "|" + "-" + "|" + show + "|" + date + "|" + "-";
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
				spinsToPrint.put(currentArtist, spins);
		}
	}
	public static String insertString(String originalString, String stringToBeInserted, int index) 
    { 
        StringBuffer newString = new StringBuffer(originalString); 
        
        newString.insert(index + 1, stringToBeInserted); 
   
        return newString.toString(); 
    } 
	
	public String addDateToUrl (String date, String url) {
		String [] segments = date.split("/");
		String secondHalf = null;
		for (int i = 0; i<segments.length; i++) {
			if(i == 0) {
				url = insertString(url, segments[i], 46);
			}
			if(i == 1) {
				secondHalf = url.substring(53);
				url = url.substring(0, 53) + segments[i];
			}
			if(i == 2) {
				secondHalf = insertString(secondHalf, segments[i], 5);
				url = url + secondHalf;
			}
		}
		System.out.println("url is: " + url);
		return url;
	}
	
	public String addArtistToUrl (String currentArtist, String url) {
		currentArtist = currentArtist.replaceAll(" ", "%20");
		url = url + currentArtist;
		System.out.println("new url is: " + url);
		return url;
	}
	
	public String getDateForUrl (Date date) {
		String urlDate = null;
		urlDate = new SimpleDateFormat("MMMddyyyy").format(date);
		System.out.println("Url date is: " + urlDate);
		return urlDate;
	}
	
//		Calendar c = Calendar.getInstance();
//	c.setTime(firstDayOfWeek);
	
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
