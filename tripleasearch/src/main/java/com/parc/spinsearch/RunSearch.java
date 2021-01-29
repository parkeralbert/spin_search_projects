package com.parc.spinsearch;

import java.util.ArrayList;

public class RunSearch {

	public static void main(String[] args) {
		//System.setProperty("webdriver.chrome.driver", "/opt/WebDriver/bin/chromedriver");
		String inputPath = "artist_and_id_input.txt";
		String outputPath = "AAA_spin_data.txt";
		JbeSearch jbe = new JbeSearch();
		SmSearch sm = new SmSearch();
		
		ArrayList <ArrayList<String>> artistInfo = jbe.getArtistList(inputPath);
		try {
			jbe.spinSearch("https://triplea.jackbartonentertainment.com/", artistInfo, outputPath, inputPath);
			sm.spinSearch("http://submodern.fmqb.com/Default.aspx", artistInfo, outputPath, inputPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		WebDriver driver = new ChromeDriver();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		try {
			driver.get("https://triplea.jackbartonentertainment.com/");
			driver.findElement(By.id("txtUser")).sendKeys("terrorbird2");
			driver.findElement(By.id("txtPass")).sendKeys("caragliano");
			driver.findElement(By.id("btnLogin")).click();
			driver.findElement(By.xpath("//a[@href='listFullTAnonComAlbums.aspx?fid=8']")).click();
			driver.findElement(By.id("cphMain_ucSearch_txtSearch")).sendKeys("Arlo Parks");
			driver.findElement(By.id("cphMain_ucSearch_btnSearch")).click();
			driver.findElement(By.xpath("//a[@href='artists.aspx?rid=20482']")).click();
			WebElement rawData = wait.until(presenceOfElementLocated(By.id("cphMain_gvArtists")));
			List<WebElement> spinData = rawData.findElements(By.xpath("./child::*"));
			for(WebElement spin : spinData) {
				List<WebElement> tableData = spin.findElements(By.xpath("./child::*"));
				for(WebElement td : tableData) {
					List<WebElement> eachCell = td.findElements(By.xpath("./child::*"));
					for (WebElement cell : eachCell) {
						System.out.println("Spin is: " + cell.getText());
					}
				}
			}
			System.out.println("JBE results:" + driver.getCurrentUrl() + " results: " + rawData.getText());
			
			driver.get("http://submodern.fmqb.com/Default.aspx");
			driver.findElement(By.id("txtUser")).sendKeys("terrorbirdsub");
			driver.findElement(By.id("txtPass")).sendKeys("birdofTerror321");
			driver.findElement(By.id("btnLogin")).click();
			driver.findElement(By.xpath("//a[@href='sub_listFull.aspx']")).click();
			driver.findElement(By.id("ctl00_cphMain_ctl00_txtSearch")).sendKeys("Arlo Parks");
			driver.findElement(By.id("ctl00_cphMain_ctl00_btnSearch")).click();
			driver.findElement(By.xpath("//a[@href='sub_artists.aspx?rid=27172']")).click();
			WebElement subModernResults = wait.until(presenceOfElementLocated(By.id("ctl00_cphMain_ctl00_hlReporting")));
			System.out.println("SubModern results:" + driver.getCurrentUrl() + " results: " + subModernResults.getText());
		} finally {
			driver.quit();
		}
		*/
	}
}
