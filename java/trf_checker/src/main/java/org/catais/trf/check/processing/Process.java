package org.catais.trf.check.processing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

public abstract class Process {
	static final Logger logger = LogManager.getLogger(Process.class.getName());

	String dbhost = null;
	String dbport = null;
	String dbdatabase = null;
	String dbusr = null;
	String dbpwd = null;
	String dbschema = null;
	String dburl = null; 
	
	String fosnr = null;
	
	String dbschemaNf = null;
	String dbschemaIg = null;
	String dbschemaAgi = null;
	
	Map dbparamsNf = null;
	Map dbparamsIg = null;
	Map dbparamsAgi = null;


	public Process(HashMap<String, String> params) {
		dbhost = params.get("dbhost");		
		dbport = params.get("dbport");		
		dbdatabase = params.get("dbdatabase");		
		dbusr = params.get("dbusr");		
		dbpwd = params.get("dbpwd");		
		
		dburl = "jdbc:postgresql://"+dbhost+":"+dbport+"/"+dbdatabase+"?user="+dbusr+"&password="+dbpwd;
		
		fosnr = params.get("fosnr");	
		
		dbschemaNf = "so_" + fosnr + "_nf";
		dbschemaIg = "so_" + fosnr + "_ig";
		dbschemaAgi = "so_" + fosnr + "_agi";
		
		dbparamsNf = new HashMap();
		dbparamsNf.put("dbtype", "postgis");        
		dbparamsNf.put("host", dbhost);        
		dbparamsNf.put("port", dbport);  
		dbparamsNf.put("database", dbdatabase); 
		dbparamsNf.put("schema", dbschemaNf);
		dbparamsNf.put("user", dbusr);        
		dbparamsNf.put("passwd", dbpwd); 
		dbparamsNf.put(PostgisNGDataStoreFactory.VALIDATECONN, true);
		dbparamsNf.put(PostgisNGDataStoreFactory.MAX_OPEN_PREPARED_STATEMENTS, 20);
		dbparamsNf.put(PostgisNGDataStoreFactory.LOOSEBBOX, true);
		dbparamsNf.put(PostgisNGDataStoreFactory.PREPARED_STATEMENTS, true);
		
		dbparamsIg = new HashMap();
		dbparamsIg.put("dbtype", "postgis");        
		dbparamsIg.put("host", dbhost);        
		dbparamsIg.put("port", dbport);  
		dbparamsIg.put("database", dbdatabase); 
		dbparamsIg.put("schema", dbschemaIg);
		dbparamsIg.put("user", dbusr);        
		dbparamsIg.put("passwd", dbpwd); 
		dbparamsIg.put(PostgisNGDataStoreFactory.VALIDATECONN, true);
		dbparamsIg.put(PostgisNGDataStoreFactory.MAX_OPEN_PREPARED_STATEMENTS, 20);
		dbparamsIg.put(PostgisNGDataStoreFactory.LOOSEBBOX, true);
		dbparamsIg.put(PostgisNGDataStoreFactory.PREPARED_STATEMENTS, true);
		
		dbparamsAgi = new HashMap();
		dbparamsAgi.put("dbtype", "postgis");        
		dbparamsAgi.put("host", dbhost);        
		dbparamsAgi.put("port", dbport);  
		dbparamsAgi.put("database", dbdatabase); 
		dbparamsAgi.put("schema", dbschemaAgi);
		dbparamsAgi.put("user", dbusr);        
		dbparamsAgi.put("passwd", dbpwd); 
		dbparamsAgi.put(PostgisNGDataStoreFactory.VALIDATECONN, true);
		dbparamsAgi.put(PostgisNGDataStoreFactory.MAX_OPEN_PREPARED_STATEMENTS, 20);
		dbparamsAgi.put(PostgisNGDataStoreFactory.LOOSEBBOX, true);
		dbparamsAgi.put(PostgisNGDataStoreFactory.PREPARED_STATEMENTS, true);
	}

	public abstract void run() throws Exception;
	
	protected void writeFeatures(DefaultFeatureCollection featureCollection, 
			FeatureStore<SimpleFeatureType, SimpleFeature> featureStore) throws Exception {
        Transaction t = new DefaultTransaction();
        featureStore.setTransaction(t);

        try {
        	Filter filter = Filter.INCLUDE;
        	featureStore.removeFeatures(filter);
	        featureStore.addFeatures(featureCollection);
	        t.commit();
	        logger.debug("Gefundene Fehler gespeichert.");
        } catch (Exception e) {
        	logger.error(e.getMessage());
        	t.rollback();
        	throw new Exception("Fehler beim Speichern der Daten.");
        } finally {
        	t.close();
        }

	}
	
	protected void executeQuery(String sql) throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        try {
            con = DriverManager.getConnection(dburl);
            st = con.createStatement();
            rs = st.executeQuery("SELECT VERSION()");

            if (rs.next()) {
                System.out.println(rs.getString(1));
            }

        } catch (SQLException e) {
        	logger.error(e.getMessage());
        	
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException e) {
            	logger.error(e.getMessage());
            }
        }
	}
	
	protected void executeUpdate(String sql) throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        try {
            con = DriverManager.getConnection(dburl);
            st = con.createStatement();
            int ret = st.executeUpdate(sql);

            logger.debug(ret);

        } catch (SQLException e) {
        	logger.error(e.getMessage());
        	
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException e) {
            	logger.error(e.getMessage());
            }
        }
	}
}
