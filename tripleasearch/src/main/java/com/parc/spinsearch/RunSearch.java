package com.parc.spinsearch;

import java.util.ArrayList;

import org.openqa.selenium.WebDriver;


public class RunSearch {

	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "/opt/WebDriver/bin/chromedriver");
		//System.setProperty("webdriver.gecko.driver", "/opt/WebDriver/bin/geckodriver");
		String inputPath = "artist_input.txt";
		String outputPath;
		
		JbeSearch jbe = new JbeSearch();
		SmSearch sm = new SmSearch();
		StSearch st = new StSearch();
		MbSearch mb = new MbSearch();
		ArrayList<String> artistInfo = jbe.getArtistList(inputPath);

		try {
			
			if (args.length == 1 && args[0].equalsIgnoreCase("jbe")){
				outputPath = "jbe_spin_data.txt";
				jbe.spinSearch("https://triplea.jackbartonentertainment.com/", artistInfo, outputPath, inputPath, false);
			}
			
			if (args.length == 1 && args[0].equalsIgnoreCase("submodern")){
				outputPath = "submodern_spin_data.txt";
				sm.spinSearch("http://submodern.fmqb.com/Default.aspx", artistInfo, outputPath, inputPath, false);
			}
			
			if (args.length == 1 &&  args[0].equalsIgnoreCase("spinitron")){
				outputPath = "spinitron_spin_data.txt";
				String date = st.getDateForUrl(inputPath);
				String url = st.addDateToUrl(date, "https://spinitron.com/m/search?range=week&date=%20%2C%20&q=");
				st.spinSearch(url, artistInfo, outputPath, inputPath, false);
			}
			
			if (args.length == 1 && args[0].equalsIgnoreCase("mediabase")){
				WebDriver driver = mb.login("https://www2.mediabase.com");
				//mb.testButton("https://www2.mediabase.com");
			}
			
			if (args.length == 1 &&  args[0].equalsIgnoreCase("all")){
				outputPath = "spinitron_spin_data.txt";
				String date = st.getDateForUrl(inputPath);
				String url = st.addDateToUrl(date, "https://spinitron.com/m/search?range=week&date=%20%2C%20&q=");
				st.addArtistToUrl("Arlo Parks", url);
				st.spinSearch(url, artistInfo, outputPath, inputPath, false);
				
				outputPath = "jbe_spin_data.txt";
				jbe.spinSearch("https://triplea.jackbartonentertainment.com/", artistInfo, outputPath, inputPath, false);
				
				outputPath = "submodern_spin_data.txt";
				sm.spinSearch("http://submodern.fmqb.com/Default.aspx", artistInfo, outputPath, inputPath, false);
			}
			
			if (args.length == 2){
				outputPath = "jbe_spin_data.txt";
				jbe.spinSearch("https://triplea.jackbartonentertainment.com/", artistInfo, outputPath, inputPath, false);
				
				outputPath = "submodern_spin_data.txt";
				sm.spinSearch("http://submodern.fmqb.com/Default.aspx", artistInfo, outputPath, inputPath, false);
			}

			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
}
