package org.catais.trf.check.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;

public class DiffSegments {
	static final Logger logger = LogManager.getLogger(DiffSegments.class.getName());

	private SimpleFeatureCollection[] fc = new SimpleFeatureCollection[2];
	private UnmatchedEdgeExtracter[] uee = new UnmatchedEdgeExtracter[2];
	  
	public DiffSegments() {
		
	}

	public void setSegments(int index, FeatureCollection fc)
	{
		this.fc[index] = (SimpleFeatureCollection) fc;
		uee[index] = new UnmatchedEdgeExtracter();
		
	    SimpleFeatureIterator iterator = (SimpleFeatureIterator) fc.features();
	    try {
	        while(iterator.hasNext()){
	            SimpleFeature f = iterator.next();	            
	            uee[index].add((Geometry) f.getDefaultGeometry());
	        }
	    }
	    finally {
	        iterator.close();
	    }
	}
	
	/**
	 * Returns all the subedges from fc which are unmatched.
	 */
	public List computeDiffEdges(int index)
	{
		List diffEdges = new ArrayList();
		UnmatchedEdgeExtracter otherUee = uee[1 - index];
		
	    SimpleFeatureIterator iterator = (SimpleFeatureIterator) fc[index].features();
	    try {
	        while(iterator.hasNext()){
	            SimpleFeature f = iterator.next();	            
	            otherUee.getDiffEdges((Geometry) f.getDefaultGeometry(), diffEdges);
	        }
	    }
	    finally {
	        iterator.close();
	    }
	    
	    return diffEdges;	    
	}
}
