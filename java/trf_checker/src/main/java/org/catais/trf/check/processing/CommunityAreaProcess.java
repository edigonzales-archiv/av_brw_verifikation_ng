package org.catais.trf.check.processing;

import java.util.ArrayList;
import java.util.HashMap;

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

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class CommunityAreaProcess extends Process {

	public CommunityAreaProcess(HashMap<String, String> params) {
		super(params);
	}

	@Override
	public void run() throws Exception {
		// Output data store
		DataStore dataStoreAgi = new PostgisNGDataStoreFactory().createDataStore(dbparamsAgi);
		SimpleFeatureType featureType = dataStoreAgi.getSchema("t_trf_gemeinde");
		SimpleFeatureSource featureSource = dataStoreAgi.getFeatureSource("t_trf_gemeinde");
		FeatureStore<SimpleFeatureType, SimpleFeature> featureStore = (FeatureStore<SimpleFeatureType, SimpleFeature>) featureSource;
		
		// Input data store.
		DataStore dataStoreNf = new PostgisNGDataStoreFactory().createDataStore(dbparamsNf);
		
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
	    
	    // Now iterate through all real estates (= Liegenschaften) and sum up the attribute
	    // "flaechemass". 
		SimpleFeatureSource featureSourceLs = dataStoreNf.getFeatureSource("liegenschaften_liegenschaft");
		SimpleFeatureCollection collectionLs = featureSourceLs.getFeatures();

		DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();
	    iterator = (SimpleFeatureIterator) collectionLs.features();
	    int area_attr = 0;
	    double area_calc = 0;
	    try {
	        while(iterator.hasNext()){
	            SimpleFeature feat = iterator.next();	            
	            area_attr = area_attr + (int) feat.getAttribute("flaechenmass");
	            area_calc = area_calc + (double) ((Polygon) feat.getDefaultGeometry()).getArea();
	        }
	        logger.debug("Summe Flaechmass: " + area_attr);
	       
        	SimpleFeatureBuilder featBuilder = new SimpleFeatureBuilder(featureType);
        	featBuilder.set("flaeche_gem", multiPoly.getArea());
        	featBuilder.set("flaeche_ls_attr", area_attr);
        	featBuilder.set("flaeche_ls_calc", area_calc);
        	featBuilder.set("geometrie", multiPoly);
        	
        	SimpleFeature checkFeature = featBuilder.buildFeature(null);
        	featureCollection.add(checkFeature);

	        logger.debug("Number of check features (should be one): " + featureCollection.size());
	        
	        // Write features to database.
	        writeFeatures(featureCollection, featureStore);

	    }
	    finally {
	        iterator.close();
	    }

	    dataStoreNf.dispose();
	    dataStoreAgi.dispose();


	}

}
