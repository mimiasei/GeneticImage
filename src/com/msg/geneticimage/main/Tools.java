package com.msg.geneticimage.main;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.msg.geneticimage.gfx.Renderer;
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
	
	public static double rndGaussian() {
		random.setSeed(System.nanoTime());
		return Math.min(1.0, Math.abs((random.nextGaussian() * Cons.ONE_THIRD) + 0.49999));
	}
	
	public static double gaussian(double ratio, double normDist) {
		random.setSeed(System.nanoTime());
		return (random.nextGaussian() * normDist) + ratio;
	}
	
	public static int gaussianInt(int number, double factor) {
		double normDist = 1000.0;
		double ratio = 0.01;
		double value =  Tools.gaussian(ratio / 2.0, normDist) * number * factor;
		value /= normDist;
		value = (Math.abs(value) > 0.1 && Math.abs(value) <= 1.0) ? (Math.signum(value) * 1.0) : value;
		return (int)value;
	}
	
	/**
	 * Returns random double value between input min and max,
	 * using rndGaussian().
	 * @param min
	 * @param max
	 * @return double
	 */
	public static double rndDouble(double min, double max) {
		if(max < min) max = min;
		return (rndGaussian() * (max - min)) + min;
	}
	
	/**
	 * Returns random integer value between input min and max,
	 * using rndGaussian().
	 * @param min
	 * @param max
	 * @return int
	 */
	public static int rndInt(int min, int max) {
		if(max < min) max = min;
	    return (int)(rndGaussian() * (max - min)) + min;
	}
	
	/**
	 * Returns random integer value between input min and max,
	 * using Random.nextInt().
	 * @param min
	 * @param max
	 * @return int
	 */
	public static int randomInt(int min, int max) {
		random.setSeed(System.nanoTime());
		if(max < min) max = min;
	    return random.nextInt((max - min)+1) + min;
	}
	
	public static boolean mutatable(double chance) {
		random.setSeed(System.nanoTime());
		return random.nextDouble() < chance;
	}
	
	public static boolean rndBool() {
		random.setSeed(System.nanoTime());
		return random.nextBoolean();
	}
	
	public static double nDecimals(double number, int decimals) {
		double[] factors = {1.0, 10.0, 100.0, 1000.0, 10000.0, 100000.0,
							1000000.0, 10000000.0, 100000000.0, 1000000000.0};
		decimals = Math.min(10, decimals);	
		return Math.round(number * (int)factors[decimals]) / factors[decimals];
	}
}
