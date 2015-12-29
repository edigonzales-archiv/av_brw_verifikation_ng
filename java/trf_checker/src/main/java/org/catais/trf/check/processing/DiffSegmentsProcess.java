package org.catais.trf.check.processing;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.catais.trf.check.utils.DiffSegments;
import org.geotools.data.FeatureStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class DiffSegmentsProcess extends GeoToolsProcess {
	static final Logger logger = LogManager.getLogger(DiffSegmentsProcess.class.getName());

	String outputTable = null;
	String inputTable = null;
	
	public DiffSegmentsProcess(HashMap<String, String> params) throws IOException {
		super(params);
		
		outputTable = params.get("outputTable");
		inputTable = params.get("inputTable");
	}

	@Override
	public void run() throws Exception {
		// Output
		SimpleFeatureType featureType = dataStoreAgi.getSchema(outputTable);
		SimpleFeatureSource featureSource = dataStoreAgi.getFeatureSource(outputTable);
		FeatureStore<SimpleFeatureType, SimpleFeature> featureStore = (FeatureStore<SimpleFeatureType, SimpleFeature>) featureSource;
		DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();

		// Input 'Nachfuehrungsgeometer'.
		SimpleFeatureSource featureSourceNf = dataStoreNf.getFeatureSource(inputTable);
		SimpleFeatureCollection collectionNf = featureSourceNf.getFeatures();
		logger.debug("Anzahl Features NF-Geometer: " + collectionNf.size());

		// Input 'Infogrips'.
		SimpleFeatureSource featureSourceIg = dataStoreIg.getFeatureSource(inputTable);
		SimpleFeatureCollection collectionIg = featureSourceIg.getFeatures();
		logger.debug("Anzahl Features Infogrips: " + collectionIg.size());

		// Calculate the difference of the line segments.
		DiffSegments diffSegments = new DiffSegments();
		diffSegments.setSegments(0, collectionNf);
		diffSegments.setSegments(1, collectionIg);
		
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

        // Dispose the data stores.
        disposeDataStores();
	}
}
