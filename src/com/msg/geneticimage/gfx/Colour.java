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
		// TODO: change this to subtly adjust hue, saturation, brightness and opacity.
		Random random = new Random(System.nanoTime());
		int[] rgbaCh = new int[4];
		rgbaCh[0] = Math.max(0, Math.min(255, (int)(rgba.getRed() * (0.9 + random.nextFloat()*0.2))));
		rgbaCh[1] = Math.max(0, Math.min(255, (int)(rgba.getGreen() * (0.9 + random.nextFloat()*0.2))));
		rgbaCh[2] = Math.max(0, Math.min(255, (int)(rgba.getBlue() * (0.9 + random.nextFloat()*0.2))));
		/* Alpha value. */
		rgbaCh[3] = Math.max(0, Math.min(255, (int)(rgba.getAlpha() * (0.9 + random.nextFloat()*0.2))));
		rgba = new Color(rgbaCh[0], rgbaCh[1], rgbaCh[2], rgbaCh[3]);
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
