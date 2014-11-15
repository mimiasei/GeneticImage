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
		int[] rgbaCh = new int[4];
		if (Cons.MUTATION_RATIO > 0.0f)
			if(random.nextFloat() < Cons.MUTATION_RATIO) {
				for (int ch = 0; ch < 3; ch++)
					rgbaCh[ch] = (int)((rgba.getRGB() & (0x000000FF << ch)) * random.nextFloat());
				/* Alpha value. */
				rgbaCh[3] = (int)((rgba.getRGB() & 0xFF) * 
						Math.min(random.nextFloat() + Cons.MIN_ALPHA, Cons.MAX_ALPHA));
				rgba = new Color(rgbaCh[0], rgbaCh[1], rgbaCh[2], rgbaCh[3]);
			}
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
