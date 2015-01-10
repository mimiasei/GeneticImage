package com.msg.geneticimage.gfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Renderer {
	
	public static BufferedImage paintImage(int w, int h, Polygon[] polygons, int shiftAmount, Color bgColour) {
		BufferedImage offImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D)offImg.createGraphics();
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(bgColour);
		g2.fillRect(0, 0, w, h);
		if(polygons != null)
			for (Polygon poly : polygons) {		
				g2.setColor(poly.getColour());
				if(shiftAmount > 0)
					g2.fillPolygon(scaleCoords(poly.getPolygonX(), shiftAmount), 
							scaleCoords(poly.getPolygonY(), shiftAmount), poly.getVertexCount());
				else
					g2.fillPolygon(poly.getPolygonX(), poly.getPolygonY(), poly.getVertexCount());
			}
		g2.dispose();
		return offImg;
	}
	
	public static BufferedImage paintImage(int w, int h, Polygon[] polygons, int shiftAmount) {
		return paintImage(w, h, polygons, shiftAmount, Color.BLACK);
	}
	
	public static int[] scaleCoords(int[] coordsArray, int shiftAmount) {
		for (int i = 0; i < coordsArray.length; i++)
			coordsArray[i] = coordsArray[i] >> shiftAmount;
		return coordsArray;
	}
	
	public static int[] getPixelsArray(BufferedImage image) {
		return ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}
}
