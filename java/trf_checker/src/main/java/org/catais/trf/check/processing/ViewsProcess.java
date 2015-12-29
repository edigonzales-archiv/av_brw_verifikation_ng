package org.catais.trf.check.processing;

import java.util.HashMap;

public class ViewsProcess extends Process {

	public ViewsProcess(HashMap<String, String> params) {
		super(params);
	}

	@Override
	public void run() throws Exception {
		String dbschema = "so_" + fosnr + "_nf";

		String eoflView = ""
				+ "CREATE OR REPLACE VIEW " + dbschema + ".v_einzelobjekte_flaechenelement AS\n"
				+ "SELECT fl.*, eo.art \n"
				+ "FROM "+dbschema+".einzelobjekte_einzelobjekt as eo, "
				+ dbschema+".einzelobjekte_flaechenelement as fl\n"
				+ "WHERE eo.t_id = fl.flaechenelement_von;\n"
				+ "GRANT SELECT ON " + dbschema + ".v_einzelobjekte_flaechenelement TO mspublic;\n\n\n";

		String eoliView = ""
				+ "CREATE OR REPLACE VIEW " + dbschema + ".v_einzelobjekte_linienelement AS\n"
				+ "SELECT li.*, eo.art \n"
				+ "FROM "+dbschema+".einzelobjekte_einzelobjekt as eo, "
				+ dbschema+".einzelobjekte_linienelement as li\n"
				+ "WHERE eo.t_id = li.linienelement_von;\n"
				+ "GRANT SELECT ON " + dbschema + ".v_einzelobjekte_linienelement TO mspublic;\n\n\n";

		String eoptView = ""
				+ "CREATE OR REPLACE VIEW " + dbschema + ".v_einzelobjekte_punktelement AS\n"
				+ "SELECT pt.*, eo.art \n"
				+ "FROM "+dbschema+".einzelobjekte_einzelobjekt as eo, "
				+ dbschema+".einzelobjekte_punktelement as pt\n"
				+ "WHERE eo.t_id = pt.punktelement_von;\n"
				+ "GRANT SELECT ON " + dbschema + ".v_einzelobjekte_punktelement TO mspublic;\n\n\n";
		 
		String bbTextView = ""
				+ "CREATE OR REPLACE VIEW " + dbschema + ".v_bodenbedeckung_objektnamepos AS\n"
				+ "SELECT p.*, n.name \n"
				+ "FROM "+dbschema+".bodenbedeckung_objektname as n, "+dbschema+".bodenbedeckung_objektnamepos as p\n"
				+ "WHERE n.t_id = p.objektnamepos_von;\n"
				+ "GRANT SELECT ON " + dbschema + ".v_bodenbedeckung_objektnamepos TO mspublic;\n\n\n";

		String lsView = ""
				+ "CREATE OR REPLACE VIEW " + dbschema + ".v_liegenschaften_liegenschaft AS\n"
				+ "SELECT l.*, g.nbident, g.nummer, g.egris_egrid, g.art, g.entstehung\n"
				+ "FROM "+dbschema+".liegenschaften_grundstueck as g, "+dbschema+".liegenschaften_liegenschaft as l\n"
				+ "WHERE g.t_id = l.liegenschaft_von;\n"
				+ "GRANT SELECT ON " + dbschema + ".v_liegenschaften_liegenschaft TO mspublic;\n\n\n";

		String lsProjView = ""
				+ "CREATE OR REPLACE VIEW " + dbschema + ".v_liegenschaften_projliegenschaft AS\n"
				+ "SELECT l.*, g.nbident, g.nummer, g.egris_egrid, g.art, g.entstehung\n"
				+ "FROM "+dbschema+".liegenschaften_projgrundstueck as g, "+dbschema+".liegenschaften_projliegenschaft as l\n"
				+ "WHERE g.t_id = l.projliegenschaft_von;\n"
				+ "GRANT SELECT ON " + dbschema + ".v_liegenschaften_projliegenschaft TO mspublic;\n\n\n";

		String sdrView = ""
				+ "CREATE OR REPLACE VIEW " + dbschema + ".v_liegenschaften_selbstrecht AS\n"
				+ "SELECT l.*, g.nbident, g.nummer, g.egris_egrid, g.art, g.entstehung\n"
				+ "FROM "+dbschema+".liegenschaften_grundstueck as g, "+dbschema+".liegenschaften_selbstrecht as l\n"
				+ "WHERE g.t_id = l.selbstrecht_von;\n"
				+ "GRANT SELECT ON " + dbschema + ".v_liegenschaften_selbstrecht TO mspublic;\n\n\n";

		String sdrProjView = ""
				+ "CREATE OR REPLACE VIEW " + dbschema + ".v_liegenschaften_projselbstrecht AS\n"
				+ "SELECT l.*, g.nbident, g.nummer, g.egris_egrid, g.art, g.entstehung\n"
				+ "FROM "+dbschema+".liegenschaften_projgrundstueck as g, "+dbschema+".liegenschaften_projselbstrecht as l\n"
				+ "WHERE g.t_id = l.projselbstrecht_von;\n"
				+ "GRANT SELECT ON " + dbschema + ".v_liegenschaften_projselbstrecht TO mspublic;\n\n\n";

		String gsPosView = ""
				+ "CREATE OR REPLACE VIEW " + dbschema + ".v_liegenschaften_grundstueckpos AS\n"
				+ "SELECT p.*, g.nummer, g.art\n"
				+ "FROM "+dbschema+".liegenschaften_grundstueck as g, "+dbschema+".liegenschaften_grundstueckpos as p\n"
				+ "WHERE g.t_id = p.grundstueckpos_von;\n"
				+ "GRANT SELECT ON " + dbschema + ".v_liegenschaften_grundstueckpos TO mspublic;\n\n\n";

		String gsProjPosView = ""
				+ "CREATE OR REPLACE VIEW " + dbschema + ".v_liegenschaften_projgrundstueckpos AS\n"
				+ "SELECT p.*, g.nummer, g.art\n"
				+ "FROM "+dbschema+".liegenschaften_projgrundstueck as g, "+dbschema+".liegenschaften_projgrundstueckpos as p\n"
				+ "WHERE g.t_id = p.projgrundstueckpos_von;\n"
				+ "GRANT SELECT ON " + dbschema + ".v_liegenschaften_projgrundstueckpos TO mspublic;\n\n\n";

		String hausnummerPosView = ""
				+ "CREATE OR REPLACE VIEW " + dbschema + ".v_gebaeudeadressen_hausnummerpos AS\n"
				+ "SELECT h.*, g.hausnummer\n"
				+ "FROM "+dbschema+".gebaeudeadressen_gebaeudeeingang as g, "+dbschema+".gebaeudeadressen_hausnummerpos as h\n"
				+ "WHERE g.t_id = h.hausnummerpos_von;\n"
				+ "GRANT SELECT ON " + dbschema + ".v_gebaeudeadressen_hausnummerpos TO mspublic;\n\n\n";

		String loknamePosView = ""
				+ "CREATE OR REPLACE VIEW " + dbschema + ".v_gebaeudeadressen_lokalisationsnamepos AS\n"
				+ "SELECT p.*, l.atext\n"
				+ "FROM "+dbschema+".gebaeudeadressen_lokalisationsname as l, "+dbschema+".gebaeudeadressen_lokalisationsnamepos as p\n"
				+ "WHERE l.t_id = p.lokalisationsnamepos_von;\n"
				+ "GRANT SELECT ON " + dbschema + ".v_gebaeudeadressen_lokalisationsnamepos TO mspublic;\n\n\n";

		String gpView = ""
				+ "CREATE OR REPLACE VIEW " + dbschema + ".v_liegenschaften_grenzpunkt AS\n"
				+ "SELECT gp.*, nf.gueltigkeit\n"
				+ "FROM "+dbschema+".liegenschaften_lsnachfuehrung as nf, "+dbschema+".liegenschaften_grenzpunkt as gp\n"
				+ "WHERE nf.t_id = gp.entstehung;\n"
				+ "GRANT SELECT ON " + dbschema + ".v_liegenschaften_grenzpunkt TO mspublic;\n\n\n";
	
		String hgpView = ""
				+ "CREATE OR REPLACE VIEW " + dbschema + ".v_gemeindegrenzen_hoheitsgrenzpunkt AS\n"
				+ "SELECT gp.*, nf.gueltigkeit\n"
				+ "FROM "+dbschema+".gemeindegrenzen_gemnachfuehrung as nf, "+dbschema+".gemeindegrenzen_hoheitsgrenzpunkt as gp\n"
				+ "WHERE nf.t_id = gp.entstehung;\n"
				+ "GRANT SELECT ON " + dbschema + ".v_gemeindegrenzen_hoheitsgrenzpunkt TO mspublic;\n\n\n";
		
		String sql = new StringBuilder()
				.append(eoflView)
				.append(eoliView)
				.append(eoptView)
				.append(bbTextView)
				.append(lsView)
				.append(lsProjView)
				.append(sdrView)
				.append(sdrProjView)
				.append(gsPosView)
				.append(gsProjPosView)
				.append(hausnummerPosView)
				.append(loknamePosView)
				.append(gpView)
				.append(hgpView)
				.toString();
			

		logger.debug("Views erstellen: " + sql);
		executeUpdate(sql);
	}
}
