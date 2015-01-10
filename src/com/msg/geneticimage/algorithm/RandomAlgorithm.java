package com.msg.geneticimage.algorithm;

import com.msg.geneticimage.gfx.PolygonImage;

public class RandomAlgorithm extends Algorithm<PolygonImage> {
		
	public RandomAlgorithm(int maxIterations) {
		this.maxIterations = maxIterations;
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
		PolygonImage randomPolygonImage = 
				new PolygonImage(polygonImage.getGeneticImage(), maxIterations);	
		randomPolygonImage.generateRandom();

		return randomPolygonImage;
	}
}
