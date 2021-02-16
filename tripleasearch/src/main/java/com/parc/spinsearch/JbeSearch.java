package com.parc.spinsearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class JbeSearch extends SpinSearch{
	
	public void spinSearch(String url, ArrayList<String> artistInfo, String outputPath, String inputPath, boolean append) throws Exception {
		WebDriver driver = login(url);
		
		Map<String, ArrayList <String>> spinsByArtist = getSpins(url, artistInfo, outputPath, inputPath, driver);
		Map<String, ArrayList <String>> addsByArtist = getAdds(url, artistInfo, outputPath, inputPath, driver);
		
		outputSpinsByArtist(outputPath, spinsByArtist, append);
		outputAddsByArtist(outputPath, addsByArtist);
		
	}
	
	public Map<String, ArrayList <String>> getSpins(String url, ArrayList<String> artistInfo, String filePath, String inputPath, WebDriver driver) throws Exception {
		Map<String, ArrayList <String>> spinsToPrint = new HashMap<>();
	
		
		driver.findElement(By.xpath("//a[@href='listFullTAnonComAlbums.aspx?fid=8']")).click();
		
	    for (String currentArtist : artistInfo) {
				ArrayList<ArrayList <String>> spinData = getSpinData(currentArtist, url, driver, inputPath);
				addSpin(spinData, currentArtist, spinsToPrint);
	    }
	    
		return spinsToPrint;

	}
	
	public Map<String, ArrayList <String>> getAdds(String url, ArrayList<String> artistInfo, String filePath, String inputPath, WebDriver driver) throws Exception {
		
		Map<String, ArrayList <String>> addsToPrint = new HashMap<>();
	    driver.findElement(By.xpath("//a[@href='addBoard.aspx']")).click();
	    
	    for (String currentArtist : artistInfo) {
				ArrayList<ArrayList <String>> addData = getAddData(currentArtist, url, driver, inputPath);
				addSpin(addData, currentArtist, addsToPrint);
	    }
	    
		driver.quit();
	    
		return addsToPrint;

	}
	
	
	public ArrayList<ArrayList<String>> getSpinData(String currentArtist, String url, WebDriver driver, String inputPath) {

		ArrayList<ArrayList<String>> allSpinData = new ArrayList<>();
		
		String artistSearch = String.format("//a[ contains(text(), '%s')]", currentArtist);

		
		WebElement rawData = null;
		
		driver.findElement(By.id("cphMain_ucSearch_txtSearch")).sendKeys(currentArtist);
		driver.findElement(By.id("cphMain_ucSearch_btnSearch")).click();
			    	
		try {
			    List <WebElement> artistNames = driver.findElements(By.xpath(artistSearch));
			    boolean foundArtist = false;
			    for (WebElement artistName : artistNames) {
			    	
			    	String searchUrl = "https://triplea.jackbartonentertainment.com/artists.aspx?rid=";
				    		
				    if(artistName.getText().equalsIgnoreCase(currentArtist) && artistName.getAttribute("href").contains(searchUrl)) {
						    	artistName.click();
						    	foundArtist = true;
						    	break;
				    }
			    }
			    		
			    		if (!foundArtist) {
				    		driver.navigate().back();
				    		return null;
			    		}	
			}
			    	
			    	catch (org.openqa.selenium.NoSuchElementException f) {
			    		driver.navigate().back();
			    		return null;
			    	}
			    	
			
			
		    try {
		    	rawData = driver.findElement(By.id("cphMain_gvArtists"));
		    } catch (org.openqa.selenium.NoSuchElementException e) {
		    	driver.navigate().back();
		    	driver.navigate().back();
		        return null;
		    }
		    
			List<WebElement> pageData = rawData.findElements(By.xpath("./child::*"));
			
			for(WebElement spin : pageData) {
				
				List<WebElement> tableData = spin.findElements(By.xpath("./child::*"));
				
				for(WebElement td : tableData) {
					List<WebElement> eachCell = td.findElements(By.xpath("./child::*"));
					ArrayList<String> spinData = new ArrayList<>();
					
					if (!td.getText().contains("Station Location Song Spins")) {
						System.out.println("Spin for " + currentArtist + ": " + td.getText());
						
						for (WebElement cell : eachCell) {
							spinData.add(cell.getText());
						}
					}

					
					allSpinData.add(spinData);
				}
			}
	    	driver.navigate().back();
	    	driver.navigate().back();
	    	return allSpinData;
	}
	
	public ArrayList<ArrayList<String>> getAddData(String currentArtist, String url, WebDriver driver, String inputPath) {

		ArrayList<ArrayList<String>> allAddData = new ArrayList<>();
		
		String artistSearch = String.format("//a[ contains(text(), '%s')]", currentArtist);

		
		WebElement rawData = null;
		
			    	
		try {
			    List <WebElement> artistNames = driver.findElements(By.xpath(artistSearch));
			    boolean foundArtist = false;
			    for (WebElement artistName : artistNames) {

			    	String searchUrl = "https://triplea.jackbartonentertainment.com/artistsAdded.aspx?rid=";
				    		
				    if(artistName.getText().equalsIgnoreCase(currentArtist) && artistName.getAttribute("href").contains(searchUrl)) {
						    	artistName.click();
						    	foundArtist = true;
						    	break;
				    }
			    }
			    		
			    		if (!foundArtist) {
				    		return null;
			    		}	
			}
			    	
			    	catch (org.openqa.selenium.NoSuchElementException f) {
			    		return null;
			    	}
			    	
			
			
		    try {
		    	rawData = driver.findElement(By.id("cphMain_gvAlbums"));
		    } catch (org.openqa.selenium.NoSuchElementException e) {
		    	driver.navigate().back();
		        return null;
		    }
		    
			List<WebElement> pageData = rawData.findElements(By.xpath("./child::*"));
			
			for(WebElement spin : pageData) {
				
				List<WebElement> tableData = spin.findElements(By.xpath("./child::*"));
				
				for(WebElement td : tableData) {
					List<WebElement> eachCell = td.findElements(By.xpath("./child::*"));
					//+ currentArtist + ": "
					ArrayList<String> addData = new ArrayList<>();
					
					if (!td.getText().contains("Station Location Song Spins")) {
						System.out.println("Add for " + currentArtist + ": " + td.getText());
						
						for (WebElement cell : eachCell) {
							addData.add(cell.getText());
						}
					}
					
					allAddData.add(addData);
				}
			}
	    	driver.navigate().back();
	    	driver.navigate().back();
	    	return allAddData;
	}
	
	public void addSpin(ArrayList <ArrayList<String>> spinData, String currentArtist, Map<String, ArrayList <String>> spinsToPrint) throws Exception   {
		if(spinData != null) {
			String stationName = null;
			String song = null;
			String location = null;
			String spinCount = null;
			
			ArrayList <String> spins = new ArrayList<>();
			for (ArrayList<String> spin : spinData) {
				
				if (spin.size() == 4) {
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
					location = location.replace('-', ' ');
					spins.add(currentArtist + "|" + stationName + "|" + location + "|" + song + "|" + spinCount);
				}
			}
				spinsToPrint.put(currentArtist, spins);
		}
	}
	

	
	public WebDriver login(String url) {
		
		WebDriver driver = new ChromeDriver();
		try {
			driver.get(url);
			driver.findElement(By.id("txtUser")).sendKeys("terrorbird2");
			driver.findElement(By.id("txtPass")).sendKeys("caragliano");
			driver.findElement(By.id("btnLogin")).click();
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
	    }
		return driver;
	}
	
	
	public static String insertString(String originalString, String stringToBeInserted, int index) 
	    { 
	        StringBuffer newString = new StringBuffer(originalString); 
	        
	        newString.insert(index + 1, stringToBeInserted); 
	   
	        return newString.toString(); 
	    } 
	
	  public static String stringJoiner(String segments[]) {
	      StringBuffer sb = new StringBuffer();
	      for(int i = 0; i < segments.length; i++) {
	         if(i==0 || i==1) {
	        	 segments[i] += "|";
	         }
	    	  sb.append(segments[i]);
	      }
	      return sb.toString();
	   }
	
	public void changeID(String artist, String ID, String inputPath) {
		
		try {
			String line;
			BufferedReader artistReader = new BufferedReader(new FileReader(inputPath));
			StringBuffer inputBuffer = new StringBuffer();
			while ((line = artistReader.readLine()) != null && line.trim().length() > 0)
			{
		         
				if (line.contains(artist)){
					String[] segments = line.split("\\|");
					segments[0] = ID;
					String newLine = stringJoiner(segments);
					line = newLine;
					
				}
				 inputBuffer.append(line);
		         inputBuffer.append('\n');
				
			}
			artistReader.close();
			
	        FileOutputStream fileOut = new FileOutputStream(inputPath);
	        fileOut.write(inputBuffer.toString().getBytes());
	        fileOut.close();
		}
		
		catch (Exception e)
		{
			System.err.println("Error: " + e);
			e.printStackTrace();
		}
	}
	
	
	
	public static String removeZip(final String location) {                
	    
	    StringBuilder sb = new StringBuilder();
	    for(char c : location.toCharArray()){
	        if(!(Character.isDigit(c) || c == '-')){
	            sb.append(c);
	        }
	    }
	    return sb.toString().trim();
	}
	
	public void outputSpinsByArtist(String filePath, Map<String, ArrayList <String>> spinsByArtist, boolean append) throws Exception {
		BufferedWriter writer;
		if (append) {
			 writer = new BufferedWriter(new FileWriter(filePath, true));
		}
		else {
			 writer = new BufferedWriter(new FileWriter(filePath));
		}
		
		writer.write("JBE Spins:");
		writer.newLine();
		writer.close();
		for (String currentArtist : spinsByArtist.keySet()) {
			writeSpinsToFile(currentArtist, spinsByArtist.get(currentArtist), filePath);
		}
	}
	
	public void outputAddsByArtist(String filePath, Map<String, ArrayList <String>> spinsByArtist) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
		writer.write("JBE Adds:");
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
