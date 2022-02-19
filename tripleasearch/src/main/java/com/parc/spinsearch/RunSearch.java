package com.parc.spinsearch;

import java.util.Date;
import java.util.ArrayList;



public class RunSearch {

	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "/opt/WebDriver/bin/chromedriver");
		//System.setProperty("webdriver.gecko.driver", "/opt/WebDriver/bin/geckodriver");
		String inputPath = "artist_input.txt";
		String allOutput = "data_all.txt";
		String outputPath;
		
		JbeSearch jbe = new JbeSearch();
		SmSearch sm = new SmSearch();
		StSearch st = new StSearch();
		MbSearch mb = new MbSearch();
		XmSearch xm = new XmSearch();
		ArrayList<String> artistInfo = jbe.getArtistList(inputPath);
		Boolean exact = jbe.setExactBoolean(inputPath);

		try {
			
			if (args.length == 1 && args[0].equalsIgnoreCase("jbe")){
				outputPath = "data_jbe.txt";
				jbe.spinSearch("https://triplea.jackbartonentertainment.com/", artistInfo, outputPath, inputPath, false, allOutput);
			}
			
			if (args.length == 1 && args[0].equalsIgnoreCase("submodern")){
				outputPath = "data_submodern.txt";
				sm.spinSearch("http://etracking.deanemediasolutions.com/", artistInfo, outputPath, inputPath, false, allOutput);
			}
			
			if (args.length == 1 &&  args[0].equalsIgnoreCase("spinitron")){
				outputPath = "data_spinitron.txt";
				String url = null;
				Date firstDayOfWeek = st.getFirstDayOfWeek(inputPath);
				Date lastDayOfWeek = st.getLastDayOfWeek(inputPath);
				String date = st.getDateForUrl(lastDayOfWeek);
				url = st.addDateToUrl(date, "https://spinitron.com/m/search?range=week&date=%20%2C%20&q=", false);
				st.spinSearch(url, artistInfo, outputPath, inputPath, false, allOutput, firstDayOfWeek, lastDayOfWeek);
			}
			
			if (args.length == 1 && args[0].equalsIgnoreCase("mediabase")){
				outputPath = "data_mediabase.txt";
				mb.spinSearch("https://www2.mediabase.com/mbapp/Account/Login?ReturnUrl=%2Fmbapp%2F", artistInfo, outputPath, inputPath, false, allOutput, true);
				//mb.testButton("https://www2.mediabase.com");
			}
			if (args.length == 1 && args[0].equalsIgnoreCase("sirius")){
				outputPath = "data_xm.txt";
				Date firstDayOfWeek = xm.getFirstDayOfWeek(inputPath);
				Date lastDayOfWeek = xm.getLastDayOfWeek(inputPath);
				xm.spinSearch("https://xmplaylist.com/search", artistInfo, outputPath, inputPath, false, firstDayOfWeek, lastDayOfWeek, allOutput, exact);
				//mb.testButton("https://www2.mediabase.com");
			}
			if (args.length == 1 &&  args[0].equalsIgnoreCase("all")){
				outputPath = "data_spinitron.txt";
				String url = null;
				Date firstDayOfWeek = st.getFirstDayOfWeek(inputPath);
				Date lastDayOfWeek = st.getLastDayOfWeek(inputPath);
				String date = st.getDateForUrl(lastDayOfWeek);
				url = st.addDateToUrl(date, "https://spinitron.com/m/search?range=week&date=%20%2C%20&q=", false);
				st.spinSearch(url, artistInfo, outputPath, inputPath, false, allOutput, firstDayOfWeek, lastDayOfWeek);
				
				outputPath = "data_jbe.txt";
				jbe.spinSearch("https://triplea.jackbartonentertainment.com/", artistInfo, outputPath, inputPath, false, allOutput);
				
				outputPath = "data_submodern.txt";
				sm.spinSearch("http://submodern.fmqb.com/Default.aspx", artistInfo, outputPath, inputPath, false, allOutput);
				
				outputPath = "data_mediabase.txt";
				mb.spinSearch("https://www2.mediabase.com/mbapp/Account/Login?ReturnUrl=%2Fmbapp%2F", artistInfo, outputPath, inputPath, false, allOutput, true);
		
				outputPath = "data_xm.txt";
				xm.spinSearch("https://xmplaylist.com/search", artistInfo, outputPath, inputPath, false, firstDayOfWeek, lastDayOfWeek, allOutput, exact);
			}
			
			if (args.length == 2){
				outputPath = "data_jbe.txt";
				jbe.spinSearch("https://triplea.jackbartonentertainment.com/", artistInfo, outputPath, inputPath, false, allOutput);
				
				outputPath = "data_submodern.txt";
				sm.spinSearch("http://submodern.fmqb.com/Default.aspx", artistInfo, outputPath, inputPath, false, allOutput);
			}

			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
}
