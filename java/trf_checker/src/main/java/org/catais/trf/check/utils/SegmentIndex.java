/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

package org.catais.trf.check.utils;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.index.*;
import com.vividsolutions.jts.index.quadtree.Quadtree;
import java.util.*;
import org.catais.trf.check.utils.CoordinateArrays;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

public class SegmentIndex {

	private SpatialIndex segIndex = new Quadtree();
	private Envelope itemEnv = new Envelope();

	public SegmentIndex(FeatureCollection fc)
	{
		SimpleFeatureIterator iterator = (SimpleFeatureIterator) fc.features();
		try {
			while(iterator.hasNext()){
				SimpleFeature f = iterator.next();	
				add((Geometry) f.getDefaultGeometry());
			}
		}
		finally {
			iterator.close();
		}
	}

	public void add(Geometry geom)
	{
		// don't need to worry about orienting polygons
		add(CoordinateArrays.toCoordinateArrays(geom, false));
	}
	public void add(LineString line)
	{
		add(line.getCoordinates());
	}
	public void add(List coordArrays)
	{
		for (Iterator i = coordArrays.iterator(); i.hasNext(); ) {
			add((Coordinate[]) i.next());
		}
	}
	public void add(Coordinate[] coord)
	{
		for (int i = 0; i < coord.length - 1; i++) {
			LineSegment lineseg = new LineSegment(coord[i], coord[i + 1]);
			lineseg.normalize();

			itemEnv.init(lineseg.p0, lineseg.p1);
			segIndex.insert(itemEnv, lineseg);
		}
	}

	public List query(Envelope env)
	{
		return segIndex.query(env);
	}

}
