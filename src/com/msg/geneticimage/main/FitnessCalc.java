package com.msg.geneticimage.main;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.msg.geneticimage.gfx.Renderer;

public class FitnessCalc {
	
	public static long getFitness(int[] imagePixels, int[] comparePixels) {
		long pixelFitness = 0;
//		int red, green, blue;
//		int step = Cons.FITNESS_PIXEL_STEP << 2; // <<2 = *4
		int[] compColours, imgColours, delta = new int[3];
		for (int pixel = 0; pixel < comparePixels.length; pixel += 4) {
			/* Get compareImage pixel colours per channel. */
//			blue = (int)(comparePixels[pixel + 1] & 0xff); // blue
//			green = (int)((comparePixels[pixel + 2] >> 8) & 0xff); // green
//			red = (int)((comparePixels[pixel + 3] >> 16) & 0xff); // red
			compColours = getColorValues(comparePixels, pixel);
			imgColours = getColorValues(imagePixels, pixel);
			/* Get delta per colour. */
//			int dB = (int) (imagePixels[pixel + 1] & 0xff) - blue;
//			int dG = (int) ((imagePixels[pixel + 2] >> 8) & 0xff) - green;
//			int dR = (int) ((imagePixels[pixel + 3] >> 16) & 0xff) - red;
			for (int i = 0; i < 3; i++)
				delta[i] = imgColours[i] - compColours[i];
//			int penalty = (polygons.size() > Cons.NBR_POLYGON_COUNT) ? polygons.size() - Cons.NBR_POLYGON_COUNT : 0;
//			pixelFitness += (dB * dB + dG * dG + dR * dR); //  * (step >> 2) + penalty
			pixelFitness += (delta[0] * delta[0] + delta[1] * delta[1] + delta[2] * delta[2]);				
		}
		return pixelFitness;
	}
	
	public static int[] getColorValues(int[] imagePixels, int pixel) {
		int[] colours = new int[3];
		for (int i = 0; i < 3; i++)
			colours[i] = (int)((imagePixels[pixel + (i+1)]>>(i*8)) & 0xff);
		return colours;
	}
	
	public static int[] getInversePixels(int[] imagePixels) {
		int[] invPixels = new int[imagePixels.length];
		int[] imgColours = new int[3];
		for (int pixel = 0; pixel < imagePixels.length; pixel += 4) { 
			imgColours = getColorValues(imagePixels, pixel);
			invPixels[pixel] = imagePixels[pixel]; // raw copy of alpha channel
			for (int i = 0; i < 3; i++)
				invPixels[pixel + (i+1)] = invertColour(imgColours[i]);		
		}
		return invPixels;
	}
	
	public static int invertColour(int colourValue) {
		return 0xff - colourValue;
	}
	
//	public static int addToColour(int colourValue, int addValue) {
//		int added = colourValue + addValue;
//		return (added > 255) ? added -= 255 : added;
//	}
	
	// UNFINISHED
	public static boolean checkFitnessArea(int[] imagePixels, BufferedImage compareImage, int x, int y) {
		int w = compareImage.getWidth() >> 3;
		int h = compareImage.getHeight() >> 3;
		int startX = w >> 1;
		int startY = h >> 1;
		int[] pixels = new int[w * h];
		for (int row = 0; row < h; row++)
			for (int col = 0; col < w; col++) {
				int addX = (row*h)+w;
				pixels[addX] = imagePixels[(startX + addX) + (startY + (addX-w))];
			}
				
		return true;
	}
}
