package org.catais.trf.check.processing;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CheckTablesProcess extends Process {
	static final Logger logger = LogManager.getLogger(CheckTablesProcess.class.getName());

	public CheckTablesProcess(HashMap<String, String> params) {
		super(params);
	}

	@Override
	public void run() throws Exception {
		String dbschema = "so_" + fosnr + "_agi";
		
		String identidTable = "CREATE TABLE " + dbschema + ".t_trf_nbgeometrie\n"
								+ "(\n"
								+ " ogc_fid serial,\n"
								+ " t_ili_tid varchar,\n"
								+ " bemerkung varchar,\n"
								+ " nbnummer varchar,\n"
								+ " geometrie geometry(Polygon,2056),\n"
								+ " CONSTRAINT t_trf_nbgeometrie_pkey PRIMARY KEY (ogc_fid) \n"
								+ ");\n\n"
								+ "GRANT SELECT ON " + dbschema + ".t_trf_nbgeometrie TO mspublic;\n\n\n";
		
		String controlPointTable = "CREATE TABLE " + dbschema + ".t_trf_lfp3ausserhalb\n"
									+ "(\n"
									+ " ogc_fid serial,\n"
									+ " t_ili_tid varchar,\n"
									+ " nbident varchar,\n"
									+ " nummer varchar,\n"
									+ " bemerkung varchar,\n"
									+ " geometrie geometry(Point,2056),\n"
									+ " CONSTRAINT t_trf_lfp3ausserhalb_pkey PRIMARY KEY (ogc_fid) \n"
									+ ");\n\n"
									+ "GRANT SELECT ON " + dbschema + ".t_trf_lfp3ausserhalb TO mspublic;\n\n\n";
		
		String sql = new StringBuilder()
					.append(identidTable)
					.append(controlPointTable)
					.toString();
				

		logger.debug("Leere Check-Tabellen: " + sql);
		executeUpdate(sql);

	}

}
