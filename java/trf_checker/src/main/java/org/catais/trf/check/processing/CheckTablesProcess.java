package org.catais.trf.check.processing;

import java.util.HashMap;

public class CheckTablesProcess extends Process {

	public CheckTablesProcess(HashMap<String, String> params) {
		super(params);
	}

	@Override
	public void run() throws Exception {
		String dbschema = "so_" + fosnr + "_agi";
		String dbtable = "t_trf_nbgeometrie";
		
		String sql = new StringBuilder()
				.append("CREATE TABLE " + dbschema + "." + dbtable + "\n")
				.append("(\n")
				.append(" ogc_fid serial,\n")
				.append(" t_ili_id varchar,\n")
				.append(" bemerkung varchar,\n")
				.append(" nbnummer varchar,\n")
				.append(" geometrie geometry(Polygon,2056),\n")
				.append(" CONSTRAINT t_trf_nbgeometrie_pkey PRIMARY KEY (ogc_fid) \n")
				.append(");\n\n")
				.append("GRANT SELECT ON " + dbschema + "." + dbtable + " TO mspublic;\n")
				.toString();

		System.out.println(sql);
		executeUpdate(sql);

	}

}
