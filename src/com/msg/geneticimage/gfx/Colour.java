package com.msg.geneticimage.gfx;

import java.awt.Color;
import java.util.Random;
import com.msg.geneticimage.interfaces.Gene;

public class Colour implements Gene {
	
	private Color rgba;
	
	public Colour(Color colour) {
		this.rgba = colour;
	}

	@Override
	public void mutate() {

	}
	
	@Override
	public void generateRandom(int value) {
		Random random = new Random();
		/* Roll random rgba channels. */
		int[] ch = new int[4];
		for (int c = 0; c < ch.length; c++)
			ch[c] = random.nextInt(255);
		rgba = new Color(ch[0], ch[1], ch[2], ch[3]);
	}

	public Color getRGBA() {
		return rgba;
	}

	public void setRGBA(Color colour) {
		this.rgba = colour;
	}
}
