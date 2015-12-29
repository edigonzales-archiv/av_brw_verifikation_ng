package org.catais.trf.check.processing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureStore;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class ControlPointOutsideProcess extends GeoToolsProcess {
	static final Logger logger = LogManager.getLogger(ControlPointOutsideProcess.class.getName());

	public ControlPointOutsideProcess(HashMap<String, String> params) throws IOException {
		super(params);
	}

	@Override
	public void run() throws Exception {
		// Output
		SimpleFeatureType featureType = dataStoreAgi.getSchema("t_trf_lfp3ausserhalb");
		SimpleFeatureSource featureSource = dataStoreAgi.getFeatureSource("t_trf_lfp3ausserhalb");
		FeatureStore<SimpleFeatureType, SimpleFeature> featureStore = (FeatureStore<SimpleFeatureType, SimpleFeature>) featureSource;
		DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();

		// Input
		SimpleFeatureSource featureSourceGemeindegrenze = dataStoreNf.getFeatureSource("gemeindegrenzen_gemeindegrenze");
		SimpleFeatureCollection collectionGemeindegrenze = featureSourceGemeindegrenze.getFeatures();
		
		logger.debug("Anzahl Gemeindegrenzen-Polygone: " + collectionGemeindegrenze.size());
		
		// Create a multipolygon first. After that we only have to deal with
		// one multipolygon.
		MultiPolygon multiPoly = null;
		ArrayList<Polygon> polys = new ArrayList();
	    SimpleFeatureIterator iterator = (SimpleFeatureIterator) collectionGemeindegrenze.features();
	    try {
	        while(iterator.hasNext()){
	            SimpleFeature feat = iterator.next();
	            Polygon poly = (Polygon) feat.getDefaultGeometry();
	            
	            polys.add(poly);
	        }
	        
	        GeometryFactory geometryFactory = new GeometryFactory();
	        multiPoly = geometryFactory.createMultiPolygon(polys.toArray(new Polygon[polys.size()]));
	        logger.debug("MultiPolygon: " + multiPoly.toString());
	    }
	    finally {
	        iterator.close();
	    }

	    // Now iterate through all control points and check if they are inside 
	    // the multipolygon (= Gemeindegrenze).
		SimpleFeatureSource featureSourceLfp3 = dataStoreNf.getFeatureSource("fixpunktekategorie3_lfp3");
		SimpleFeatureCollection collectionLfp3 = featureSourceLfp3.getFeatures();

	    iterator = (SimpleFeatureIterator) collectionLfp3.features();
	    try {
	        while(iterator.hasNext()){
	            SimpleFeature feat = iterator.next();
	            Point point = (Point) feat.getDefaultGeometry();
	            
	            if (!point.intersects(multiPoly)) {
	            	SimpleFeatureBuilder featBuilder = new SimpleFeatureBuilder(featureType);
	            	featBuilder.set("t_ili_tid", feat.getAttribute("t_ili_tid"));
	            	featBuilder.set("nbident", feat.getAttribute("nbident"));
	            	featBuilder.set("nummer", feat.getAttribute("nummer"));
	            	featBuilder.set("geometrie", feat.getDefaultGeometry());
	            	
	            	SimpleFeature errorFeature = featBuilder.buildFeature(null);
	            	featureCollection.add(errorFeature);
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
