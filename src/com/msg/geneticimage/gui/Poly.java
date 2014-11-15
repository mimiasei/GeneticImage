package com.msg.geneticimage.gui;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;

public class Poly {
	
	private Point coords;
	
	public Poly(int x, int y) {
		coords = new Point(x, y);
	}
	
	public static Polygon generate(int sides, int outsideRadius, int insideRadius) {
		AffineTransform trans = new AffineTransform();
		Polygon poly = new Polygon();
		for (int i = 0; i < sides; i++) {
			trans.rotate(Math.PI * 2 / (float) sides / 2);
			Point2D out = trans.transform(new Point2D.Float(0, outsideRadius), null);
			poly.addPoint((int) out.getX(), (int) out.getY());
			trans.rotate(Math.PI * 2 / (float) sides / 2);
			
			if (insideRadius > 0) {
				Point2D in = trans.transform(new Point2D.Float(0, insideRadius), null);
				poly.addPoint((int) in.getX(), (int) in.getY());
			}
		}
		return poly;
	}
}