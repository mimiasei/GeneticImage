package com.msg.geneticimage.main;

import java.util.Random;

public class Tools {
	public static double rndDouble(double min, double max) {
		Random random = new Random(System.nanoTime());
		return Math.min(random.nextDouble() + min, max);
	}
}
