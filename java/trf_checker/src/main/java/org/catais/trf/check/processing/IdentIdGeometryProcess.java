package org.catais.trf.check.processing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.jdbc.VirtualTable;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;

public class IdentIdGeometryProcess extends GeoToolsProcess {
	static final Logger logger = LogManager.getLogger(IdentIdGeometryProcess.class.getName());

	public IdentIdGeometryProcess(HashMap<String, String> params) throws IOException {
		super(params);
	}

	@Override
	public void run() throws Exception {		
		// Output
		SimpleFeatureType featureType = dataStoreAgi.getSchema("t_trf_nbgeometrie");
		SimpleFeatureSource featureSource = dataStoreAgi.getFeatureSource("t_trf_nbgeometrie");
		FeatureStore<SimpleFeatureType, SimpleFeature> featureStore = (FeatureStore<SimpleFeatureType, SimpleFeature>) featureSource;
		DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();
		
		// Input
		// Create a simple join with virtual tables
		String sql = new StringBuilder()
					.append("SELECT g.*, nb.kt, nb.nbnummer\n")
					.append("FROM " + dbschemaNf + ".nummerierungsbereiche_nbgeometrie as g,\n")
					.append(dbschemaNf + ".nummerierungsbereiche_nummerierungsbereich as nb\n")
					.append("WHERE nb.t_id = g.nbgeometrie_von")
					.toString();
		
		logger.debug("Virtual table sql: " + sql);
		
		String vtName = "nbgeom";
		VirtualTable vt = new VirtualTable(vtName, sql);
		vt.addGeometryMetadatata("geometrie", com.vividsolutions.jts.geom.Polygon.class, 2056);
		
		ArrayList primaryKeys = new ArrayList();
		primaryKeys.add("t_id");
		vt.setPrimaryKeyColumns(primaryKeys);
		
		logger.debug("Virtual table geometry type: " + vt.getGeometryType("geometrie"));
		logger.debug("Virtual table primary key: " + vt.getPrimaryKeyColumns());
		
		((JDBCDataStore) dataStoreAgi).createVirtualTable(vt);
		
		FeatureSource fs = dataStoreAgi.getFeatureSource(vtName);
		FeatureCollection fc = fs.getFeatures();
		
		logger.debug("Virtual table feature count: " + fc.size());
		
		// Now iterate through the features to find wrong geometries.
	    SimpleFeatureIterator iterator = (SimpleFeatureIterator) fc.features();
	    try {
	        while(iterator.hasNext()){
	            SimpleFeature feat = iterator.next();
	            
	            String canton = (String) feat.getAttribute("kt");
	            if (!canton.equalsIgnoreCase("CH")) {
	            	continue;
	            }
	            
	            String identid = (String) feat.getAttribute("nbnummer");
	            if (!identid.substring(0, 2).equalsIgnoreCase("03")) {
	            	continue;
	            }
	            
	            int mapId = (int) Integer.parseInt(identid.substring(6));
	            if (mapId > 2000) {
	            	continue;
	            }
	            
	            Polygon poly = (Polygon) feat.getDefaultGeometry();
	            Coordinate[] coords = poly.getCoordinates();
	            if (coords.length !=  5) {
	            	continue;
	            }
	            
	            for (int i = 0; i < coords.length; i++) {
	            	Coordinate coord = coords[i];
	            	
	            	if (coord.x == (int) coord.x && coord.y == (int) coord.y) {
	            		continue;
	            	}
	            	
	            	SimpleFeatureBuilder featBuilder = new SimpleFeatureBuilder(featureType);
	            	featBuilder.set("t_ili_tid", feat.getAttribute("t_ili_tid"));
	            	featBuilder.set("nbnummer", feat.getAttribute("nbnummer"));
	            	featBuilder.set("geometrie", feat.getDefaultGeometry());
	            	
	            	SimpleFeature errorFeature = featBuilder.buildFeature(null);
	            	featureCollection.add(errorFeature);
	            	
	            	logger.debug("x coord: " + coord.x);
	            	logger.debug("int(x) coord: " + (int) coord.x);

	            	// We can stop looping through the coordinates if we found
	            	// at least one wrong coordinate.
	            	break;
	            }
	        }
	        logger.debug("Errors found: " + featureCollection.size());
	        
	        // Write features to database.
	        writeFeatures(featureCollection, featureStore);
	        
	    }
	    finally {
	        iterator.close();
	    }
		
        // Dispose the data stores.
        disposeDataStores();
	}
}
