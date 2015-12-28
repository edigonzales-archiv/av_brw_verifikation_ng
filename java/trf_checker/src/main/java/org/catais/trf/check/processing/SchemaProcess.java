package org.catais.trf.check.processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.catais.trf.check.ItfReader;

public class SchemaProcess extends Process {
	static final Logger logger = LogManager.getLogger(SchemaProcess.class.getName());
	
	public SchemaProcess(HashMap<String, String> params) {
		super(params);
	}

	@Override
	public void run() throws ClassNotFoundException, SQLException {
		String dbschema = "so_" + fosnr + "_agi";
		
		String sql = new StringBuilder()
				.append("CREATE SCHEMA " + dbschema + "\n")
				.append("AUTHORIZATION " + dbusr + ";\n")
				.append("GRANT USAGE ON SCHEMA " + dbschema + " TO mspublic;\n")
				.toString();
		
		executeUpdate(sql);
	}
}
