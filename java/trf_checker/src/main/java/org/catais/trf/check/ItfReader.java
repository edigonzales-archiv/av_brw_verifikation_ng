package org.catais.trf.check;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;

public class ItfReader {
	static final Logger logger = LogManager.getLogger(ItfReader.class.getName());

	String dbhost = null;
	String dbport = null;
	String dbdatabase = null;
	String dbusr = null;
	String dbpwd = null;
	String dbschema = null;
	String dburl = null; 
	
	String defaultSrsAuth = null;
	String defaultSrsCode = null;
	
	String models = null;
	String modeldir = null;
			
	public ItfReader(HashMap<String,String> params) {
		dbhost = params.get("dbhost");		
		dbport = params.get("dbport");		
		dbdatabase = params.get("dbdatabase");		
		dbusr = params.get("dbusr");		
		dbpwd = params.get("dbpwd");		
		defaultSrsAuth = params.get("defaultSrsAuth");
		defaultSrsCode = params.get("defaultSrsCode");
		models = params.get("models");
		modeldir = params.get("modeldir");
		
		dburl = "jdbc:postgresql://"+dbhost+":"+dbport+"/"+dbdatabase+"?user="+dbusr+"&password="+dbpwd;
	}
	
	public void runImport(String fileName, String dbschema) throws Exception {
		
		Config config = getConfig();
		config.setDbschema(dbschema);
		config.setXtffile(fileName);

		System.out.println(fileName);
		System.out.println(dbschema);
		
		try {
			Ili2db.runImport(config, "");
			
		} catch (Ili2dbException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw new Ili2dbException("Upsi. Beim Import lief was schief.");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new Exception("Upsi. Beim Import lief was schief.");
		}

	}
	
	private Config getConfig() {
		Config config = new Config();

		config.setDbhost(dbhost);
		config.setDbdatabase(dbdatabase);
		config.setDbport(dbport);
		config.setDbusr(dbusr);
		config.setDbpwd(dbpwd);
		config.setDburl(dburl);
		
		config.setModels(models);
		config.setModeldir(modeldir);
		
		config.setGeometryConverter(ch.ehi.ili2pg.converter.PostgisColumnConverter.class.getName());
		config.setDdlGenerator(ch.ehi.sqlgen.generator_impl.jdbc.GeneratorPostgresql.class.getName());
		config.setJdbcDriver("org.postgresql.Driver");
		config.setIdGenerator(ch.ehi.ili2pg.PgSequenceBasedIdGen.class.getName());
		config.setUuidDefaultValue("uuid_generate_v4()");

		config.setNameOptimization("topic");
		config.setMaxSqlNameLength("60");
//		config.setStrokeArcs("enable");
						
		config.setSqlNull("enable"); // be less restrictive
		config.setValue("ch.ehi.sqlgen.createGeomIndex", "True");
//		config.setCreateEnumCols("addTxtCol");
		config.setTidHandling(config.TID_HANDLING_PROPERTY);
		config.setCreateFkIdx(config.CREATE_FKIDX_YES);
				
		config.setDefaultSrsAuthority(defaultSrsAuth);
		config.setDefaultSrsCode(defaultSrsCode);
		return config;
	}
}
