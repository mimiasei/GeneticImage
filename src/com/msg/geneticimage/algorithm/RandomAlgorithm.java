package com.msg.geneticimage.algorithm;

import java.awt.Color;
import java.util.Random;

import com.msg.geneticimage.gfx.CreatePolygonImage;
import com.msg.geneticimage.gfx.Polygon;

public class RandomAlgorithm extends Algorithm<CreatePolygonImage> {
		
	public RandomAlgorithm() {
		// empty constructor
	}
	
	/**
	 * Random construction algorithm. Generates a BufferedImage
	 * from a randomly created polygons.
	 * 
	 * @return new BufferedImage
	 */
	@Override
	public CreatePolygonImage process(CreatePolygonImage image) {
		/* If parameter image has any polygons at all then roll vs ratio
		 * and if permitted, return it thus exit algorithm. 
		 */
		if(image.getNumberOfPolygons() > 0)
			return image;
		
		CreatePolygonImage createImage = new CreatePolygonImage(image.getWidth(), image.getHeight());
		Polygon polygon = null;
		Random random = new Random();
		int[] ch = new int[4];
		
		/* Create and add maxIterations number of polygons 
		 * to CreatePolygonImage object.
		 */
		for (int i = 0; i < maxIterations; i++) {			
			polygon = new Polygon();
			/* Roll random colour channels. */
			for (int c = 0; c < ch.length; c++)
				ch[c] = random.nextInt(255);
			polygon.setColour(new Color(ch[0], ch[1], ch[2], ch[3]));
			
			/* Create random polygon. */
			polygon.createRandomPolar(image.getWidth(), image.getHeight());
			
			/* Add the new polygon to list in CreatePolygonImage. */
			createImage.addPolygon(polygon);
		}

		return createImage;
	}
}
