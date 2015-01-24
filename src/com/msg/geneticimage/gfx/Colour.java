package com.msg.geneticimage.gfx;

import java.awt.Color;

import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.interfaces.Gene;
import com.msg.geneticimage.tools.Tools;

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
		double mut = Cons.FACTOR_COLOUR_FUZZINESS;
//		double init = 1.0 - mut;
		int[] rgbaCh = new int[4];
		/* Either mutate the RGB colour channels.. */
		if(Tools.rndBool()) {
//			rgbaCh[0] = Math.max(0, Math.min(255, (int)(rgba.getRed() * (init + Tools.rndDouble(0, mut)))));
//			rgbaCh[1] = Math.max(0, Math.min(255, (int)(rgba.getGreen() * (init + Tools.rndDouble(0, mut)))));
//			rgbaCh[2] = Math.max(0, Math.min(255, (int)(rgba.getBlue() * (init + Tools.rndDouble(0, mut)))));
			rgbaCh[0] = Math.max(0, Math.min(255, rgba.getRed() + Tools.gaussianInt(127, mut)));
			rgbaCh[1] = Math.max(0, Math.min(255, rgba.getGreen() + Tools.gaussianInt(127, mut)));
			rgbaCh[2] = Math.max(0, Math.min(255, rgba.getBlue() + Tools.gaussianInt(127, mut)));
			rgba = new Color(rgbaCh[0], rgbaCh[1], rgbaCh[2], rgba.getAlpha());
		/* ..or the alpha channel. */
		} else {
			rgbaCh[3] = Math.max(0, Math.min(255, (int)(rgba.getAlpha()  + Tools.gaussianInt(127, mut))));
			rgba = new Color(rgba.getRed(), rgba.getGreen(), rgba.getBlue(), rgbaCh[3]);
		}
	}
	
	@Override
	public void generateRandom() {
		/* Alpha dblValue. */
		int alpha = (int)((Math.min(Tools.rndDouble(0, 1.0) + Cons.FACTOR_MIN_ALPHA, Cons.FACTOR_MAX_ALPHA)) * 255);
		/* Roll random RGB channels. */
		rgba = new Color(Tools.randomInt(0, 255),Tools.randomInt(0, 255), Tools.randomInt(0, 255), alpha);
	}
}
