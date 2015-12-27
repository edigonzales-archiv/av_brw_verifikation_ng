package org.catais.trf.check.processing;

import java.util.HashMap;

public abstract class Process {

	String dbhost = null;
	String dbport = null;
	String dbdatabase = null;
	String dbusr = null;
	String dbpwd = null;
	String dbschema = null;
	String dburl = null; 
	
	String fosnr = null;

	public Process(HashMap<String, String> params) {
		dbhost = params.get("dbhost");		
		dbport = params.get("dbport");		
		dbdatabase = params.get("dbdatabase");		
		dbusr = params.get("dbusr");		
		dbpwd = params.get("dbpwd");		
		
		dburl = "jdbc:postgresql://"+dbhost+":"+dbport+"/"+dbdatabase+"?user="+dbusr+"&password="+dbpwd;
		
		fosnr = params.get("fosnr");		
	}

	
	public abstract void run() throws Exception;
	
	protected void executeSql(String sql) throws Exception {
		
	}
}
