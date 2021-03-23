package com.parc.spinsearch;

import java.util.Date;
import java.util.ArrayList;



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
		XmSearch xm = new XmSearch();
		ArrayList<String> artistInfo = jbe.getArtistList(inputPath);

		try {
			
			if (args.length == 1 && args[0].equalsIgnoreCase("jbe")){
				outputPath = "data_jbe.txt";
				jbe.spinSearch("https://triplea.jackbartonentertainment.com/", artistInfo, outputPath, inputPath, false);
			}
			
			if (args.length == 1 && args[0].equalsIgnoreCase("submodern")){
				outputPath = "data_submodern.txt";
				sm.spinSearch("http://etracking.deanemediasolutions.com/", artistInfo, outputPath, inputPath, false);
			}
			
			if (args.length == 1 &&  args[0].equalsIgnoreCase("spinitron")){
				outputPath = "data_spinitron.txt";
				String date = st.getDateForUrl(inputPath);
				String url = st.addDateToUrl(date, "https://spinitron.com/m/search?range=week&date=%20%2C%20&q=");
				st.spinSearch(url, artistInfo, outputPath, inputPath, false);
			}
			
			if (args.length == 1 && args[0].equalsIgnoreCase("mediabase")){
				ArrayList<String> mbArtistInfo = mb.getArtistList(inputPath);
				outputPath = "data_mediabase.txt";
				mb.spinSearch("https://www2.mediabase.com/mbapp/Account/Login?ReturnUrl=%2Fmbapp%2F", mbArtistInfo, outputPath, inputPath, false);
				//mb.testButton("https://www2.mediabase.com");
			}
			if (args.length == 1 && args[0].equalsIgnoreCase("sirius")){
				outputPath = "data_xm.txt";
				Date firstDayOfWeek = xm.getFirstDayOfWeek(inputPath);
				Date lastDayOfWeek = xm.getLastDayOfWeek(inputPath);
				xm.getSpins("https://xmplaylist.com/", artistInfo, outputPath, inputPath, firstDayOfWeek, lastDayOfWeek);
				//mb.testButton("https://www2.mediabase.com");
			}
			if (args.length == 1 &&  args[0].equalsIgnoreCase("all")){
				outputPath = "data_spinitron.txt";
				String date = st.getDateForUrl(inputPath);
				String url = st.addDateToUrl(date, "https://spinitron.com/m/search?range=week&date=%20%2C%20&q=");
				st.addArtistToUrl("Arlo Parks", url);
				st.spinSearch(url, artistInfo, outputPath, inputPath, false);
				
				outputPath = "data_jbe.txt";
				jbe.spinSearch("https://triplea.jackbartonentertainment.com/", artistInfo, outputPath, inputPath, false);
				
				outputPath = "data_submodern.txt";
				sm.spinSearch("http://submodern.fmqb.com/Default.aspx", artistInfo, outputPath, inputPath, false);
				
				ArrayList<String> mbArtistInfo = mb.getArtistList(inputPath);
				outputPath = "data_mediabase.txt";
				mb.spinSearch("https://www2.mediabase.com/mbapp/Account/Login?ReturnUrl=%2Fmbapp%2F", mbArtistInfo, outputPath, inputPath, false);
			}
			
			if (args.length == 2){
				outputPath = "data_jbe.txt";
				jbe.spinSearch("https://triplea.jackbartonentertainment.com/", artistInfo, outputPath, inputPath, false);
				
				outputPath = "data_submodern.txt";
				sm.spinSearch("http://submodern.fmqb.com/Default.aspx", artistInfo, outputPath, inputPath, false);
			}

			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
}
