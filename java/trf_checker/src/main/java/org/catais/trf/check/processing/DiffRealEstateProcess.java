package org.catais.trf.check.processing;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.catais.trf.check.utils.DiffSegments;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureStore;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class DiffRealEstateProcess extends Process {
	static final Logger logger = LogManager.getLogger(DiffRealEstateProcess.class.getName());

	public DiffRealEstateProcess(HashMap<String, String> params) {
		super(params);
	}

	@Override
	public void run() throws Exception {		
		// Output data store
		DataStore dataStoreAgi = new PostgisNGDataStoreFactory().createDataStore(dbparamsAgi);
		SimpleFeatureType featureType = dataStoreAgi.getSchema("t_trf_diff_ls");
		SimpleFeatureSource featureSource = dataStoreAgi.getFeatureSource("t_trf_diff_ls");
		FeatureStore<SimpleFeatureType, SimpleFeature> featureStore = (FeatureStore<SimpleFeatureType, SimpleFeature>) featureSource;
		DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();

		// Input data store 'Nachfuehrungsgeometer'.
		DataStore dataStoreNf = new PostgisNGDataStoreFactory().createDataStore(dbparamsNf);
		SimpleFeatureSource featureSourceLiegenschaftNf = dataStoreNf.getFeatureSource("liegenschaften_liegenschaft");
		SimpleFeatureCollection collectionLiegenschaftNf = featureSourceLiegenschaftNf.getFeatures();
		logger.debug("Anzahl Liegenschaften NF-Geometer: " + collectionLiegenschaftNf.size());

		// Input data store 'Infogrips'.
		DataStore dataStoreIg = new PostgisNGDataStoreFactory().createDataStore(dbparamsIg);
		SimpleFeatureSource featureSourceLiegenschaftIg = dataStoreIg.getFeatureSource("liegenschaften_liegenschaft");
		SimpleFeatureCollection collectionLiegenschaftIg = featureSourceLiegenschaftIg.getFeatures();
		logger.debug("Anzahl Liegenschaften Infogrips: " + collectionLiegenschaftIg.size());

		// Calculate the difference of the line segments.
		DiffSegments diffSegments = new DiffSegments();
		diffSegments.setSegments(0, collectionLiegenschaftNf);
		diffSegments.setSegments(1, collectionLiegenschaftIg);
		
		List list0 = diffSegments.computeDiffEdges(0);;
		for (int i = 0; i < list0.size(); i++) {
        	SimpleFeatureBuilder featBuilder = new SimpleFeatureBuilder(featureType);
        	featBuilder.set("t_index", 0);
        	featBuilder.set("geometrie", list0.get(i));
        	
        	SimpleFeature checkFeature = featBuilder.buildFeature(null);
        	featureCollection.add(checkFeature);
		}

		List list1 = diffSegments.computeDiffEdges(1);
		for (int i = 0; i < list1.size(); i++) {
        	SimpleFeatureBuilder featBuilder = new SimpleFeatureBuilder(featureType);
        	featBuilder.set("t_index", 1);
        	featBuilder.set("geometrie", list1.get(i));
        	
        	SimpleFeature checkFeature = featBuilder.buildFeature(null);
        	featureCollection.add(checkFeature);
		}
		
        // Write features to database.
        writeFeatures(featureCollection, featureStore);
		
	    dataStoreNf.dispose();
	    dataStoreIg.dispose();
	    dataStoreAgi.dispose();
	}
}
