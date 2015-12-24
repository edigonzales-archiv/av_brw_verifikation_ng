package org.catais.brw.verifikation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

public class App {

	static final Logger logger = LogManager.getLogger(App.class.getName());
	
	public static void main(String[] args) {
		
		logger.info("Starting at: "+ new Date());
		
		HashMap<String,String> params = new HashMap<String,String>();
		
		try {
			// Read parameters from properties file.
			String propFileName = "config.properties";
			InputStream input = new FileInputStream(propFileName);
			
			Properties prop = new Properties();
			prop.load(input);
			
			params.put("dbhost", prop.getProperty("dbhost","localhost"));
			params.put("dbport", prop.getProperty("dbport","5432"));
			params.put("dbdatabase", prop.getProperty("dbdatabase","sogis_brw_verifikation"));
			params.put("dbusr", prop.getProperty("dbpwd","stefan"));
			params.put("dbpwd", prop.getProperty("dbpwd","ziegler12"));
			params.put("defaultSrsAuth", prop.getProperty("defaultSrsAuth","EPSG"));
			params.put("defaultSrsCode", prop.getProperty("defaultSrsCode","2056"));
			params.put("models", prop.getProperty("models","DM01AVSO24LV95"));
			params.put("modeldir", prop.getProperty("modeldir","http://www.catais.org/models/"));
			
			logger.debug(params);
			
			// Handle the cli options.
			Options options = new Options();	    	
			options.addOption(null, "itf_nf", true, "Interlis file from Nachführungsgeometer.");
			options.addOption(null, "itf_ig", true, "Interlis file from Infogrips.");
			options.addOption(null, "fosnr", true, " Swiss Federal Statistical Office Number of Community.");
			
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);

			String fosnr = null;
			String itf_nf = null;
			String itf_ig = null;
			
			// Do some basic checking for fosnr.
			// Is it an integer value?
			// What about the range?
			if (!cmd.hasOption("fosnr")) {
				throw new MissingOptionException("fosnr");
			} else {
				int fosnrInt;
				
				try {
					fosnrInt = Integer.parseInt(cmd.getOptionValue("fosnr"));	
					
				} catch (NumberFormatException e) {
					throw new NumberFormatException("Option 'fosnr' is not an integer value.");
				}
				
				if (fosnrInt < 2401)  {
					throw new NumberFormatException("Option 'fosnr' is too small.");
				}
				
				if (fosnrInt > 2700)  {
					throw new NumberFormatException("Option 'fosnr' is too big.");
				}
				
				params.put("fosnr", fosnr);
			}
			
			
			
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (MissingOptionException e) {
			//e.printStackTrace();
			logger.error("Missing option: " + e.getMessage());
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
		}
		

		logger.info("Stopping at: "+ new Date());
	}
}
