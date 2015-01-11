package com.msg.geneticimage.gfx;

import java.awt.Color;

import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.interfaces.Gene;
import com.msg.geneticimage.main.Tools;

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
	
	public Color getRGBA() {
		return rgba;
	}

	public void setRGBA(Color colour) {
		this.rgba = colour;
	}

	@Override
	public void mutate() {
		double mut = Cons.COLOUR_FUZZINESS_SCALE; // * 2.0
		double init = 1.0 - mut;
		int[] rgbaCh = new int[4];
		/* Either mutate the RGB colour channels.. */
		if(Tools.rndBool()) {
			rgbaCh[0] = Math.max(0, Math.min(255, (int)(rgba.getRed() * (init + Tools.rndDouble(0, mut)))));
			rgbaCh[1] = Math.max(0, Math.min(255, (int)(rgba.getGreen() * (init + Tools.rndDouble(0, mut)))));
			rgbaCh[2] = Math.max(0, Math.min(255, (int)(rgba.getBlue() * (init + Tools.rndDouble(0, mut)))));
			rgba = new Color(rgbaCh[0], rgbaCh[1], rgbaCh[2], rgba.getAlpha());
			return;
		/* ..or the alpha channel. */
		} else {
			rgbaCh[3] = Math.max(0, Math.min(255, (int)(rgba.getAlpha() * (init + Tools.rndDouble(0, mut)))));
			rgba = new Color(rgba.getRed(), rgba.getGreen(), rgba.getBlue(), rgbaCh[3]);
		}
	}
	
	@Override
	public void generateRandom() {
		/* Alpha value. */
		int alpha = (int)((Math.min(Tools.rndDouble(0, 1.0) + Cons.MIN_ALPHA, Cons.MAX_ALPHA)) * 255);
		/* Roll random RGB channels. */
		rgba = new Color(Tools.randomInt(0, 255),Tools.randomInt(0, 255), Tools.randomInt(0, 255), alpha);
	}
}
