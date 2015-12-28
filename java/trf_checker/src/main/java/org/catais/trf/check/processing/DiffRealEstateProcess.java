package org.catais.trf.check.processing;

import java.util.HashMap;

import org.catais.trf.check.utils.DiffSegments;
import org.geotools.data.DataStore;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;

public class DiffRealEstateProcess extends Process {

	public DiffRealEstateProcess(HashMap<String, String> params) {
		super(params);
	}

	@Override
	public void run() throws Exception {
		System.out.println("Hallo...");
		
		// Input data store 'Nachfuehrungsgeometer'.
		DataStore dataStoreNf = new PostgisNGDataStoreFactory().createDataStore(dbparamsNf);
		SimpleFeatureSource featureSourceLiegenschaftNf = dataStoreNf.getFeatureSource("liegenschaften_liegenschaft");
		SimpleFeatureCollection collectionLiegenschaftNf = featureSourceLiegenschaftNf.getFeatures();

		// Input data store 'Infogrips'.
		DataStore dataStoreIg = new PostgisNGDataStoreFactory().createDataStore(dbparamsIg);
		SimpleFeatureSource featureSourceLiegenschaftIg = dataStoreIg.getFeatureSource("liegenschaften_liegenschaft");
		SimpleFeatureCollection collectionLiegenschaftIg = featureSourceLiegenschaftIg.getFeatures();
		
		DiffSegments diffSegments = new DiffSegments();
		diffSegments.setSegments(0, collectionLiegenschaftNf);
		

	}

}
