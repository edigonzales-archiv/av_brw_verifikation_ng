package org.catais.trf.check.utils;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;

public class DiffSegments {

	private SimpleFeatureCollection[] fc = new SimpleFeatureCollection[2];
//	private UnmatchedEdgeExtracter[] uee = new UnmatchedEdgeExtracter[2];
	  
	public DiffSegments() {
		
	}

	public void setSegments(int index, FeatureCollection fc)
	{
		this.fc[index] = (SimpleFeatureCollection) fc;
		
//		uee[index] = new UnmatchedEdgeExtracter();
//		for (Iterator it = fc.getFeatures().iterator(); it.hasNext(); ) {
//			Feature f = (Feature) it.next();
//			uee[index].add(f.getGeometry());
//		}
	}
	

}
