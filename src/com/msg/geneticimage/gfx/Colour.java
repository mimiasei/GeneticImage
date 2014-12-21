package com.msg.geneticimage.gfx;

import java.awt.Color;
import java.util.Random;

import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.interfaces.Gene;

public class Colour implements Gene {
	
	private Color rgba;
	
	public Colour(Color colour) {
		this.rgba = colour;
	}
	
	public Colour() {
		generateRandom();
	}
	
	public Colour(Colour clone) {
		this.rgba = clone.rgba;
	}

	@Override
	public void mutate() {
		Random random = new Random(System.nanoTime());
		double mut = Cons.COLOUR_FUZZINESS_SCALE * 2.0;
		double init = 1.0 - mut;
		int[] rgbaCh = new int[4];
		rgbaCh[0] = Math.max(0, Math.min(255, (int)(rgba.getRed() * (init + random.nextFloat()*mut))));
		rgbaCh[1] = Math.max(0, Math.min(255, (int)(rgba.getGreen() * (init + random.nextFloat()*mut))));
		rgbaCh[2] = Math.max(0, Math.min(255, (int)(rgba.getBlue() * (init + random.nextFloat()*mut))));
		/* Alpha value. */
//		rgbaCh[3] = Math.max(0, Math.min(255, (int)(rgba.getAlpha() * (init + random.nextFloat()*mut))));
		rgba = new Color(rgbaCh[0], rgbaCh[1], rgbaCh[2], rgba.getAlpha());
	}
	
	@Override
	public void generateRandom() {
		Random random = new Random(System.nanoTime());
		/* Alpha value. */
		int alpha = (int)((Math.min(random.nextFloat() + Cons.MIN_ALPHA, Cons.MAX_ALPHA)) * 255);
		/* Roll random RGB channels. */
		rgba = new Color(random.nextInt(255),random.nextInt(255), 
				random.nextInt(255), alpha);

	}

	public Color getRGBA() {
		return rgba;
	}

	public void setRGBA(Color colour) {
		this.rgba = colour;
	}
}
