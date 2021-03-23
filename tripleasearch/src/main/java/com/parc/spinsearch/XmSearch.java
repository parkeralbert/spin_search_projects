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

public class XmSearch extends SpinSearch{
	public void spinSearch(String url, ArrayList<String> artistInfo, String outputPath, String inputPath, boolean append, Date firstDayOfWeek, Date lastDayOfWeek) throws Exception {
		Map<String, ArrayList <String>> spinsByArtist = getSpins(url, artistInfo, outputPath, inputPath, firstDayOfWeek, lastDayOfWeek);
		outputSpinsByArtist(outputPath, spinsByArtist, append);
	}
	
	public Map<String, ArrayList <String>> getSpins(String url, ArrayList <String> artistInfo, String outputPath, String inputPath, Date firstDayOfWeek, Date lastDayOfWeek) throws Exception {
		Map<String, ArrayList <String>> spinsToPrint = new HashMap<>();
		
		WebDriver driver = login(url);
		
	    for (String currentArtist : artistInfo) {
	    			url = createUrl(currentArtist, firstDayOfWeek, lastDayOfWeek);
					ArrayList<ArrayList <String>> spinData = getSpinData(currentArtist, driver, url);
					addSpin(spinData, currentArtist, spinsToPrint);
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
		return (segments[0] + urlArtist + segments[1] + startDate + segments[2] + endDate + segments[3]);		
	}
	
	public WebDriver login(String url) {
		
		WebDriver driver = new ChromeDriver();
		try {
			driver.get(url);
			WebDriverWait wait = new WebDriverWait(driver, 1000);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class = 'flex text-sm rounded-full focus:outline-none transition duration-150 ease-in-out']")));
			driver.findElement(By.xpath("//a[@class = 'flex text-sm rounded-full focus:outline-none transition duration-150 ease-in-out']")).click();
			
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@class = 'appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md placeholder-gray-400 focus:outline-none focus:shadow-outline-blue focus:border-blue-300 transition duration-150 ease-in-out sm:text-sm sm:leading-5']")));
			List <WebElement> buttons = driver.findElements(By.xpath("//button[@class = 'w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-md bg-white text-sm leading-5 font-medium text-gray-500 hover:text-gray-400 focus:outline-none focus:border-blue-300 focus:shadow-outline-blue transition duration-150 ease-in-out']"));
			buttons.get(1).click();
	
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'inline-flex items-center px-4 py-2 text-sm leading-5 font-medium rounded-md bg-white text-gray-500 hover:text-white hover:bg-red-500 focus:outline-none focus:border-red-700 focus:shadow-outline-red active:bg-red-700 transition ease-in-out duration-150']")));
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			e.printStackTrace();
	    }
		return driver;
	}
	
	public  ArrayList<ArrayList <String>> getSpinData(String currentArtist, WebDriver driver, String url) {
		ArrayList<ArrayList<String>> allSpinData = new ArrayList<>();
		String song = null;
		String artist = null;
		String date = null;
		String show = null;
			try {
				driver.get(url);
				WebDriverWait wait = new WebDriverWait(driver, 1000);
				wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'inline-flex items-center px-6 py-2 border border-transparent text-sm leading-4 font-medium rounded-md text-white focus:outline-none focus:border-blue-700 focus:shadow-outline-blue active:bg-blue-700 transition ease-in-out duration-150 bg-blue-600 hover:bg-blue-500']")));
				try {
					WebElement resultList = driver.findElement(By.tagName("ul"));
					List<WebElement> allResults = resultList.findElements(By.xpath("./child::*"));
					List<WebElement> innerLinkChildren = allResults.get(0).findElements(By.xpath("./child::*"));
					List<WebElement> firstDivChildren = innerLinkChildren.get(0).findElements(By.xpath("./child::*"));
					while (firstDivChildren.get(0).getText().equalsIgnoreCase("Loading...") ) {
						Thread.sleep(200);
					}
					if (firstDivChildren.get(0).getText().equalsIgnoreCase("No Results")) {
						return null;
					}
				}
				catch (org.openqa.selenium.NoSuchElementException | org.openqa.selenium.StaleElementReferenceException e){
				}
				
				WebElement resultList = driver.findElement(By.tagName("ul"));
				List<WebElement> allResults = resultList.findElements(By.xpath("./child::*"));
				for (WebElement spin:allResults) {
					
					ArrayList<String> spinData = new ArrayList<>();
					List<WebElement> innerLinkChildren = spin.findElements(By.xpath("./child::*"));
					List<WebElement> firstDivChildren = innerLinkChildren.get(0).findElements(By.xpath("./child::*"));
					List<WebElement> secondDivChildren = firstDivChildren.get(0).findElements(By.xpath("./child::*"));
					System.out.println("song is: " + firstDivChildren.get(0).getText());
					String rawData = firstDivChildren.get(0).getText().replaceAll("[\\n]", "|");
					System.out.println("rawdata is: " + rawData);
					String[] segments = rawData.split("\\|");
					song = segments[0];
					artist = segments[1];
					date = segments[2];
					show = segments[3];
					System.out.println("song: " + song + "artist: " + artist + "date: " + date + "show: " + show);
					spinData.add(song);
					spinData.add(artist);
					spinData.add(date);
					spinData.add(show);
					allSpinData.add(spinData);
				}
				
			}
			catch (org.openqa.selenium.NoSuchElementException | InterruptedException e){
				e.printStackTrace();
			}
		return allSpinData;
	}
	
	public void addSpin(ArrayList <ArrayList<String>> spinData, String currentArtist, Map<String, ArrayList <String>> spinsToPrint) throws Exception   {
		if(spinData != null) {
			String show = "-";
			String song = "-";
			String artist = "-";
			String album = "-";
			SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy, h:mm a");
			Date spinDate = null;
			String date = "";
			
			ArrayList <String> spins = new ArrayList<>();
			for (ArrayList<String> spin : spinData) {
				spinDate = parser.parse(spin.get(2));
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd|h:mm a");
				date = formatter.format(spinDate);
				song = spin.get(0);
				artist = spin.get(1);
				show = spin.get(3);
					
				if (artist.equalsIgnoreCase(currentArtist)) {
					spins.add("SiriusXM" + "|" + artist + "|" + album + "|" + song + "|" +  "-" + "|" + "-" + "|" + show + "|" + date + "|" + "-");
					System.out.println("Spin for " + currentArtist + ": "+ spin);
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
	
	//"https://spinitron.com/m/search?range=week&date=Feb%209
	//%2C%202021&q=Arlo%20Parks"
	//"https://spinitron.com/m/search?range=week&date=Feb%209%2C%202021&q=Arlo%20Parks"
	public String addArtistToUrl (String currentArtist, String url) {
		currentArtist = currentArtist.replaceAll(" ", "%20");
		url = url + currentArtist;
		System.out.println("new url is: " + url);
		return url;
	}
	
	public String getDateForUrl (String filePath) {
		String date = null;
		String line = null;
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			while ((line = reader.readLine()) != null && date == null)
			{
				date = parseUrlDate(line);
			}
			reader.close();
		}
		catch (Exception e)
		{
			System.err.println("Error: " + e);
		}

		System.out.println("URL date is: " + date);
		return date; 
	}
	
	public static String parseUrlDate(String line) {
		String urlDate = null;
		if(line.indexOf("Last Day of Week:") != -1) {
			if (line.contains("\"")) {
				line = line.replaceAll("\"", "");
			}
			urlDate = line.trim().substring(18);
		}
		System.out.println(urlDate);
		return urlDate;
	}

	
	public void outputSpinsByArtist(String filePath, Map<String, ArrayList <String>> spinsByArtist, boolean append) throws Exception {
		
		BufferedWriter writer;
		
		if(append) {
			writer = new BufferedWriter(new FileWriter(filePath, true));
		}
		else {
			writer = new BufferedWriter(new FileWriter(filePath));
		}

		writer.write("Spinitron Spins: ");
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
