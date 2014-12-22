package com.msg.geneticimage.main;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.msg.geneticimage.interfaces.Cons;

public class Tools {
	
	private static Random random = new Random();
	public static BufferedImage image = null;
	public static int imgWidth, imgHeight;
	public static int maxPolyWidth, maxPolyHeight;
	
	static {
		/* Load compare image. */
		try {
			image = ImageIO.read(Cons.class.getClassLoader().getResource(Cons.IMAGE_PATH));
		} catch (IOException e) {
			System.out.println("Cannot find image file!");
			System.exit(0);
		}
		imgWidth = image.getWidth();
		imgHeight = image.getHeight();
		maxPolyWidth = (int)(imgWidth * Cons.POLYGON_FUZZINESS_SCALE);
		maxPolyHeight = (int)(imgHeight * Cons.POLYGON_FUZZINESS_SCALE);
	}
	
	public static double rndDouble(double min, double max) {
		random.setSeed(System.nanoTime());
		return Math.min(random.nextDouble() + min, max);
	}
	
	public static int rndInt(int min, int max) {
		random.setSeed(System.nanoTime());
	    /* nextInt is normally exclusive of the top value, 
	     * so add 1 to make it inclusive. */
	    return random.nextInt((max - min) + 1) + min;
	}
	
	public static boolean mutatable(double chance) {
		random.setSeed(System.nanoTime());
		return random.nextDouble() < chance;
	}
}
