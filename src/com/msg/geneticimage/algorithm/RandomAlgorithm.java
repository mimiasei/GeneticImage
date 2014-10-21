package com.msg.geneticimage.algorithm;

import com.msg.geneticimage.gfx.PolygonImage;
import com.msg.geneticimage.gfx.Polygon;

public class RandomAlgorithm extends Algorithm<PolygonImage> {
		
	public RandomAlgorithm() {
		// empty constructor
	}
	
	/**
	 * Random construction algorithm. Generates a BufferedImage
	 * from maxIterations number of randomly created polygons.
	 * 
	 * @param PolygonImage
	 * @return new PolygonImage
	 */
	@Override
	public PolygonImage process(PolygonImage polygonImage) {
		PolygonImage randomPolygonImage = new PolygonImage(polygonImage.getGeneticImage(), maxIterations);
		
		/* Create and add maxIterations number of polygons 
		 * to PolygonImage object.
		 */
		for (int i = 0; i < maxIterations; i++) {		
			/* Add the new polygon to list in PolygonImage. */
			randomPolygonImage.addPolygon(new Polygon(polygonImage));
		}

		return randomPolygonImage;
	}
}
