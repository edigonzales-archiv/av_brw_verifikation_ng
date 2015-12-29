package org.catais.trf.check;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.catais.trf.check.processing.CheckTablesProcess;
import org.catais.trf.check.processing.CommunityAreaProcess;
import org.catais.trf.check.processing.ControlPointOutsideProcess;
import org.catais.trf.check.processing.DiffRealEstateProcess;
import org.catais.trf.check.processing.DiffSegmentsProcess;
import org.catais.trf.check.processing.DiffSegmentsWithToleranceProcess;
import org.catais.trf.check.processing.IdentIdGeometryProcess;
import org.catais.trf.check.processing.SchemaProcess;

/**
--fosnr 2547 
--itf_nf /home/stefan/Projekte/av_brw_verifikation_ng/data/itf_lv95_nf/254700_LV95_20151002.itf
--itf_ig /home/stefan/Projekte/av_brw_verifikation_ng/data/itf_lv95_ig/254700_LV03_20150923_lv95.itf

--fosnr 2511 
--itf_nf /home/stefan/Projekte/av_brw_verifikation_ng/data/fake/251100_aeschi_lv95_ig_lfp3_ausserhalb.itf
--itf_ig /home/stefan/Projekte/av_brw_verifikation_ng/data/fake/251100_aeschi_lv95_ig.itf

*/

public class App 
{
	static final Logger logger = LogManager.getLogger(App.class.getName());

    public static void main( String[] args )
    {
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
			params.put("dbusr", prop.getProperty("dbusr","stefan"));
			params.put("dbpwd", prop.getProperty("dbpwd","ziegler12"));
			params.put("defaultSrsAuth", prop.getProperty("defaultSrsAuth","EPSG"));
			params.put("defaultSrsCode", prop.getProperty("defaultSrsCode","2056"));
			params.put("models", prop.getProperty("models","DM01AVSO24LV95"));
			params.put("modeldir", prop.getProperty("modeldir","http://www.catais.org/models/"));
			params.put("loglevel", prop.getProperty("loglevel","info"));

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
			// Correct range of number?
			if (!cmd.hasOption("fosnr")) {
				throw new MissingOptionException("BfS-Nummer nicht gesetzt ('--fosnr').");
			} else {
				int fosnrInt;
				
				try {
					fosnrInt = Integer.parseInt(cmd.getOptionValue("fosnr"));	
					
				} catch (NumberFormatException e) {
					throw new NumberFormatException("BfS-Nummer ist keine Zahl.");
				}
				
				if (fosnrInt < 2401)  {
					throw new NumberFormatException("BfS-Nummer ist zu klein.");
				}
				
				if (fosnrInt > 2700)  {
					throw new NumberFormatException("BfS-Nummer ist zu gross.");
				}
				
				params.put("fosnr", cmd.getOptionValue("fosnr"));
			}
			
			// Nachführungsgeometer file name.
			if (!cmd.hasOption("itf_nf")) {
				throw new MissingOptionException("ITF von NF-Geometer nicht ausgewählt ('--itf_nf').");
			} else {
				itf_nf = cmd.getOptionValue("itf_nf");
				params.put("itf_nf", itf_nf);
			}
			
			// Infogrips file name.
			if (!cmd.hasOption("itf_ig")) {
				throw new MissingOptionException("ITF von Infogrips nicht ausgewählt ('--itf_ig').");
			} else {
				itf_ig = cmd.getOptionValue("itf_ig");
				params.put("itf_ig", itf_ig);
			}		
			
			logger.debug(params);
			
			// Import ITF files.
			ItfReader itfReader = new ItfReader(params);
			
			// Nachführungsgeometer
			logger.info("Import ITF von NF-Geometer.");
//			itfReader.runImport(params.get("itf_nf"), "so_" + params.get("fosnr") + "_nf");
			
			// Infogrips
			logger.info("Import ITF von Infogrips.");
//			itfReader.runImport(params.get("itf_ig"), "so_" + params.get("fosnr") + "_ig");
			
			// Postprocessing
			// Create "_agi" schema
			logger.info("Erstelle '_agi' Schema.");
			SchemaProcess schemaProcess = new SchemaProcess(params);
//			schemaProcess.run();
			
			// Create empty check tables
			logger.info("Erstelle leere Check-Tabellen.");
			CheckTablesProcess checkTablesProcess = new CheckTablesProcess(params);
//			checkTablesProcess.run();
			
			// Find wrong geometries of Nummerierungsbereiche
			logger.info("Prüfe NB-Geometrien.");
			IdentIdGeometryProcess identIdGeometryProcess = new IdentIdGeometryProcess(params);
//			identIdGeometryProcess.run();
			
			// Find control points category 3 outside perimeter
			logger.info("LFP3 ausserhalb Gemeindegrenze.");
			ControlPointOutsideProcess controlPointOutsideProcess = new ControlPointOutsideProcess(params);
//			controlPointOutsideProcess.run();
			
			// Calculate sum areas real estate and compare it with
			// area of municipality.
			logger.info("Flächenvergleich Liegenschaft - Gemeindegrenze");
			CommunityAreaProcess communityAreaProcess = new CommunityAreaProcess(params);
//			communityAreaProcess.run();
			
			// Calculate the diff segments for real estates.
			logger.info("Differenz der Liegenschaften");
			
			params.put("outputTable", "t_trf_diff_ls");
			params.put("inputTable", "liegenschaften_liegenschaft");
			
			DiffSegmentsProcess diffRealEstateProcess = new DiffSegmentsProcess(params);
//			diffRealEstateProcess.run();
			
			// Calculate the diff segments for proj real estates.
			logger.info("Differenz der projektierten Liegenschaften");
			
			params.put("outputTable", "t_trf_diff_ls_proj");
			params.put("inputTable", "liegenschaften_projliegenschaft");
			
			DiffSegmentsProcess diffProjRealEstateProcess = new DiffSegmentsProcess(params);
//			diffProjRealEstateProcess.run();

			// Calculate the diff segments for land surface.
			logger.info("Differenz der Bodenbedeckung");
			
			params.put("outputTable", "t_trf_diff_bb");
			params.put("inputTable", "bodenbedeckung_boflaeche");
			
			DiffSegmentsProcess diffLandSurfaceProcess = new DiffSegmentsProcess(params);
//			diffLandSurfaceProcess.run();
			
			// Calculate the diff segments community border.
			logger.info("Differenz der Gemeindegrenze");
			
			params.put("outputTable", "t_trf_diff_gemgr");
			params.put("inputTable", "gemeindegrenzen_gemeindegrenze");
			
			DiffSegmentsProcess diffCommunityBorderProcess = new DiffSegmentsProcess(params);
//			diffCommunityBorderProcess.run();	
			
			// Calculate the diff segments for land surface with tolerance.
			// Can take a long time.
			logger.info("Differenz der Bodenbedeckung mit Toleranz.");
			
			params.put("outputTable", "t_trf_diff_bb_tol");
			params.put("inputTable", "bodenbedeckung_boflaeche");
			
			DiffSegmentsWithToleranceProcess diffToleranceLandSurfaceProcess = new DiffSegmentsWithToleranceProcess(params);
			diffToleranceLandSurfaceProcess.run();



		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (MissingOptionException e) {
			logger.error("Missing option: " + e.getMessage());
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
//		} catch (Ili2dbException e) {
//			e.printStackTrace();
//			logger.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		
		
		logger.info("Stopping at: "+ new Date());
    }
}
