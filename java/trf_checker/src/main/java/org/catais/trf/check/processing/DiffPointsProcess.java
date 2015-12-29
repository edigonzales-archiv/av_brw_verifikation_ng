package org.catais.trf.check.processing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.data.FeatureStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import com.vividsolutions.jts.geom.*;

public class DiffPointsProcess extends GeoToolsProcess {
	static final Logger logger = LogManager.getLogger(DiffSegmentsProcess.class.getName());

	String outputTable = null;
	String inputTable = null;

	public DiffPointsProcess(HashMap<String, String> params) throws IOException {
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

		// Points from 'Nachfuehrungsgeometer'
		ArrayList<Point> pointsNf = new ArrayList();
		SimpleFeatureIterator iteratorNf = (SimpleFeatureIterator) collectionNf.features();
		try {
			while (iteratorNf.hasNext()) {
				SimpleFeature f = iteratorNf.next();
				pointsNf.add((Point) f.getDefaultGeometry());
			}
		}
		finally {
			iteratorNf.close();
		}

		// Points from 'Infogrips'
		ArrayList<Point> pointsIg = new ArrayList();
		SimpleFeatureIterator iteratorIg = (SimpleFeatureIterator) collectionIg.features();
		try {
			while (iteratorIg.hasNext()) {
				SimpleFeature f = iteratorIg.next();
				pointsIg.add((Point) f.getDefaultGeometry());
			}
		}
		finally {
			iteratorIg.close();
		}

		// Get the difference.
		// Points only in 'Nachfuehrungsgeometer'.
		ArrayList<Point> pointsNfClone = (ArrayList<Point>) pointsNf.clone();
		pointsNf.removeAll(pointsIg);
		
		for (int i = 0; i < pointsNf.size(); i++) {
        	SimpleFeatureBuilder featBuilder = new SimpleFeatureBuilder(featureType);
        	featBuilder.set("t_index", 0);
        	featBuilder.set("geometrie", pointsNf.get(i));
        	
        	SimpleFeature checkFeature = featBuilder.buildFeature(null);
        	featureCollection.add(checkFeature);
		}

		// Points only in 'Infogrips'.
		pointsIg.removeAll(pointsNfClone);

		for (int i = 0; i < pointsIg.size(); i++) {
        	SimpleFeatureBuilder featBuilder = new SimpleFeatureBuilder(featureType);
        	featBuilder.set("t_index", 1);
        	featBuilder.set("geometrie", pointsIg.get(i));
        	
        	SimpleFeature checkFeature = featBuilder.buildFeature(null);
        	featureCollection.add(checkFeature);
		}

        // Write features to database.
        writeFeatures(featureCollection, featureStore);
		
        // Dispose the data stores.
        disposeDataStores();
	}
}
