package com.msg.geneticimage.gfx;

import java.awt.Color;
import java.util.Random;

import com.msg.geneticimage.interfaces.Constants;
import com.msg.geneticimage.interfaces.Gene;

public class Colour implements Gene, Constants {
	
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
		if (MUTATION_RATIO > 0.0f)
			if(random.nextFloat() < MUTATION_RATIO) {
				for (int ch = 0; ch < 4; ch++)
					rgbaCh[ch] = (int)((rgba.getRGB() & (0x000000FF << ch)) * 
							(random.nextFloat() + 0.5f));
				rgba = new Color(rgbaCh[0], rgbaCh[1], rgbaCh[2], rgbaCh[3]);
			}
	}
	
	@Override
	public void generateRandom() {
		Random random = new Random(System.nanoTime());
		/* Roll random rgba channels. */
		rgba = new Color(random.nextInt(255),random.nextInt(255), 
				random.nextInt(255), random.nextInt(255));
	}

	public Color getRGBA() {
		return rgba;
	}

	public void setRGBA(Color colour) {
		this.rgba = colour;
	}
}
