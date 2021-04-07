package com.parc.spinsearch;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
public class SpinSearch {
public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";

	
	public static Date parseDate(String date) {
	     try {
	         return new SimpleDateFormat("MM/dd/yy").parse(date);
	     } catch (ParseException e) {
	         return null;
	     }
	  }
	
	public static Date parseDateAndTime(String date) {
	     try {
	         return new SimpleDateFormat("MM/dd/yy h:mm a z").parse(date);
	     } catch (ParseException e) {
	         return null;
	     }
	}

	public Date getFirstDayOfWeek(String filePath) {
		Date firstDayOfWeek = null;
		String line = null;
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			while ((line = reader.readLine()) != null && firstDayOfWeek == null)
			{
				firstDayOfWeek = parseFirstDayOfWeek(line);
			}
			reader.close();
		}
		catch (Exception e)
		{
			System.err.println("Error: " + e);
		}

		return firstDayOfWeek;
	}
	
	public Date getLastDayOfWeek(String filePath) {
		Date lastDayOfWeek = null;
		String line = null;
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			while ((line = reader.readLine()) != null && lastDayOfWeek == null)
			{
				lastDayOfWeek = parseLastDayOfWeek(line);
			}
			reader.close();
		}
		catch (Exception e)
		{
			System.err.println("Error: " + e);
		}
		return lastDayOfWeek;
	}

	
	public static Date parseFirstDayOfWeek(String line) {
		Date firstDayOfWeek = null;
		if(line.indexOf("Date:") != -1) {
			if (line.contains("\"")) {
				line = line.replaceAll("\"", "");
			}
			String[] segments = line.trim().substring(6).split(" - ");
			firstDayOfWeek = parseDateAndTime(segments[0]);
		}
		return firstDayOfWeek;
	}
	
	public static Date parseLastDayOfWeek(String line) {
		Date lastDayOfWeek = null;
		if(line.indexOf("Date:") != -1) {
			if (line.contains("\"")) {
				line = line.replaceAll("\"", "");
			}
			String[] segments = line.trim().substring(6).split(" - ");
			lastDayOfWeek = parseDateAndTime(segments[1]);
		}
		return lastDayOfWeek;
	}
	
	
	

	
	public boolean isDayOnly(Date firstDayOfWeek, Date lastDayOfWeek){
	    Date date = new Date();  
		double difference_In_Time = date.getTime() - firstDayOfWeek.getTime();
		double difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24));
		
		if (difference_In_Days <1.0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public ArrayList<String> getArtistList(String artistInputPath){
		String line = null;
		ArrayList<String> artistNames = new ArrayList<String>(); 
		
		try
		{
			BufferedReader artistReader = new BufferedReader(new FileReader(artistInputPath));
			
			while ((line = artistReader.readLine()) != null )
			{
					addArtistNames(line, artistNames);
				
			}
			artistReader.close();
			
		}
		catch (Exception e)
		{
			System.err.println("Error: " + e);
			e.printStackTrace();
		}
		
		return artistNames;
        
	}
	
	public void addArtistNames(String line, ArrayList<String> artistInfo) {
		
		if(line.trim().length() > 0 && !line.contains("Last Day of Week:") && !line.contains("Date:") && !line.contains("https://")) {
			if (line.indexOf("*") == 0) {
				line = line.replace("*", "");
			}
			artistInfo.add(line.trim());
		}
		
	}
	
	
	public void spinSearch(String url, ArrayList<String> artistInfo, String outputPath, String inputPath, boolean append) throws Exception {
		Map<String, ArrayList <String>> spinsByArtist = getSpins(url, artistInfo, outputPath, inputPath);
		outputSpinsByArtist(outputPath, spinsByArtist, append);
	}
	

	public static String replaceSmartQuotes(String input) {
		StringBuilder output = new StringBuilder();
		
		for (char c : input.toCharArray()) {
			if (c == 0x2018 || c == 0x2019) {
				output.append('\'');
			}
			else {
				output.append(c);
			}
		}
		return output.toString();
	}
	
	public Map<String, ArrayList <String>> getSpins(String url,  ArrayList<String> artistInfo, String outputPath, String inputPath) throws Exception {
		Map<String, ArrayList <String>> spinsToPrint = new HashMap<>();
		
	    for (String currentArtist : artistInfo) {
					ArrayList<ArrayList <String>> spinData = getSpinData(currentArtist, url, inputPath);
					addSpin(spinData, currentArtist, spinsToPrint);
	    }
		
		return spinsToPrint;

	}

	public void outputSpinsByArtist(String filePath, Map<String, ArrayList <String>> spinsByArtist, boolean append) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
		writer.write("WXPN");
		writer.newLine();
		writer.close();
		
		for (String currentArtist : spinsByArtist.keySet()) {
			writeSpinsToFile(currentArtist, spinsByArtist.get(currentArtist), filePath);
		}
	}
	
	public static String insertString(String originalString, String stringToBeInserted, int index) 
    { 
        StringBuffer newString = new StringBuffer(originalString); 
        
        newString.insert(index + 1, stringToBeInserted); 
   
        return newString.toString(); 
    } 

	public ArrayList<ArrayList<String>> getSpinData(String currentArtist, String url, String inputPath) {
		System.setProperty("webdriver.chrome.driver", "/opt/WebDriver/bin/chromedriver");

		WebDriver driver = new ChromeDriver();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		ArrayList<ArrayList<String>> allSpinData = new ArrayList<>();
		
		String artistUrl = insertString("//a[@href='artists.aspx?rid=']", currentArtist, 26);
		
		try {
			driver.get("https://triplea.jackbartonentertainment.com/");
			driver.findElement(By.id("txtUser")).sendKeys("terrorbird2");
			driver.findElement(By.id("txtPass")).sendKeys("caragliano");
			driver.findElement(By.id("btnLogin")).click();
			driver.findElement(By.xpath("//a[@href='listFullTAnonComAlbums.aspx?fid=8']")).click();
			driver.findElement(By.id("cphMain_ucSearch_txtSearch")).sendKeys(currentArtist);
			driver.findElement(By.id("cphMain_ucSearch_btnSearch")).click();
			driver.findElement(By.xpath(artistUrl)).click();
			
			WebElement rawData = wait.until(presenceOfElementLocated(By.id("cphMain_gvArtists")));
			List<WebElement> pageData = rawData.findElements(By.xpath("./child::*"));
			
			for(WebElement spin : pageData) {
				
				List<WebElement> tableData = spin.findElements(By.xpath("./child::*"));
				
				for(WebElement td : tableData) {
					List<WebElement> eachCell = td.findElements(By.xpath("./child::*"));
					ArrayList<String> spinData = new ArrayList<>();
					
					for (WebElement cell : eachCell) {
						System.out.println("Spin is: " + cell.getText());
						spinData.add(cell.getText());
					}
					
					allSpinData.add(spinData);
				}
			}
		} 
		finally {
			driver.quit();
		}
		return allSpinData;
	}

/*
	private static void removeQuotes(ArtistInfo artistInfo, ArrayList <String> songs) {
		for (int i = 0; i < artistInfo.getSongs().size(); i++) {
			System.out.println("Parsing song: " + songs.get(i));
			songs.set(i, songs.get(i).substring(1, songs.get(i).length()-1));
		}
	}

	private static void setSongs(String songs, ArtistInfo artistInfo) {
		if(songs.indexOf(" + ") != -1) {
			for (String song : songs.split("\s\\+\s")) {
				artistInfo.addSong(song);
			}
		}
		
		else {
			artistInfo.addSong(songs);
		}
	}
	
	private static String[] splitArtistNameAndProjectName(String line, String delim, ArtistInfo artistInfo) {
		String[] segments = line.split(" " + delim + " ");
		return segments;
	}
	
	*/

	public void addSpin(ArrayList <ArrayList<String>> spinData, String currentArtist, Map<String, ArrayList <String>> spinsToPrint) throws Exception   {
		if(spinData != null) {
			String stationName = null;
			String song = null;
			String location = null;
			String spinCount = null;
			
			ArrayList <String> spins = new ArrayList<>();
			for (ArrayList<String> spin : spinData) {
				
				for (int i = 0; i< spin.size(); i++) {
					if (i==0) {
						stationName = spin.get(i);
					}
					if (i==1) {
						location = spin.get(i);
					}
					if (i==2) {
						song = spin.get(i);
					}
					if (i==3) {
						spinCount = spin.get(i);
					}
				}
				
				location = removeZip(location);
				spins.add(currentArtist + "|" + stationName + "|" + location + "|" + song + "|" + spinCount);
				System.out.println("Raw Spin Data: " + spin);
			}
				spinsToPrint.put(currentArtist, spins);
		}
	}
	
	public static String removeZip(final String location) {                
	    
	    StringBuilder sb = new StringBuilder();
	    for(char c : location.toCharArray()){
	        if(!Character.isDigit(c)){
	            sb.append(c);
	        }
	    }
	    return sb.toString();
	}
	
	public static boolean isDateInRange(Date firstDayOfWeek, Date lastDayOfWeek, Date spinDate) {
		if (spinDate != null && (spinDate.after(firstDayOfWeek) || spinDate.equals(firstDayOfWeek)) && (spinDate.before(lastDayOfWeek) || spinDate.equals(lastDayOfWeek))){
			return true;
		}
		else {
			return false;
		}
	}

	public static Map<String, List<Spin>> getSpinsByArtist(Collection<Spin> values) {
		
		Map<String, List<Spin>> spins = new HashMap<>();
		
		for(Spin processedSpin : values) {
			List<Spin> artistSpins = spins.get(processedSpin.getArtist());
			if(artistSpins == null){
				artistSpins = new ArrayList <Spin>();
				spins.put(processedSpin.getArtist(), artistSpins);
			}
			artistSpins.add(processedSpin);
		}

		
		return spins;
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
	
/*
	private String formatWrittenDate(Spin processedSpin){
		String formattedDate;

		if (processedSpin.getFirstPlayDate() != processedSpin.getLastPlayDate()) {
			String firstDate = 	removeZerosFromDate(processedSpin.getFirstPlayDate());
			String lastDate = removeZerosFromDate(processedSpin.getLastPlayDate());

			formattedDate = firstDate + "-" + lastDate;
		} else {

			formattedDate = removeZerosFromDate(processedSpin.getFirstPlayDate());
		}

		return formattedDate;
	}

	private static String removeZerosFromDate(Date inputDate) {
		SimpleDateFormat secondFormatter = new SimpleDateFormat("MM/dd");
		
		String newDate = secondFormatter.format(inputDate);

		String[] segments = newDate.split("/");
		int i = 0;
		for (String segment : segments) {
			if (segment.indexOf('0') == 0) {
				segments[i] = segments[i].substring(1);
			}
			i++;
		}
		newDate = segments[0] + "/" + segments[1];
		return newDate;
	}
*/
}


