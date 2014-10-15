package com.msg.geneticimage.gfx;

import java.awt.Color;
import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
//import javax.swing.JComponent;

public class PolygonImage implements Comparable<PolygonImage> { // extends JComponent
	
	private ArrayList<Polygon> polygons = new ArrayList<Polygon>();
	private int width, height;
	private long fitness = Long.MAX_VALUE;
	
	public PolygonImage(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public PolygonImage(PolygonImage clone) {
		this.width = clone.width;
		this.height = clone.height;
		this.fitness = clone.fitness;
		this.polygons.addAll(clone.polygons);
	}
	
	public void addPolygon(Polygon polygon) {
		polygons.add(polygon);
	}
	
	public Polygon getPolygon(int index) {
		return polygons.get(index);
	}
	
	public void setPolygon(int index, Polygon polygon) {
		if(index < polygons.size())
			polygons.set(index, polygon);
	}
	
	public int getNumberOfPolygons() {
		return polygons.size();
	}

	public BufferedImage paintImage() {
		BufferedImage offImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g2 = offImg.createGraphics();
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.clearRect(0, 0, width, height);
				
		for (Polygon poly : polygons) {
			g2.setColor(poly.getColour());
			g2.fillPolygon(scaleCoords(poly, poly.getPolygonX()), 
					scaleCoords(poly, poly.getPolygonY()), poly.getVertexLength());
		}
		g2.setColor(Color.LIGHT_GRAY);
		g2.dispose();
		return offImg;
	}
	
	public int[] scaleCoords(Polygon poly, int[] coordsArray) {
		float ratio = width / (float)poly.getWidth();
		for (int i = 0; i < coordsArray.length; i++)
			coordsArray[i] = (int)(coordsArray[i] * ratio);
		return coordsArray;
	}
 	
	public BufferedImage getImage() {
		return paintImage();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public long getFitness() {
		return fitness;
	}

	public void setFitness(long fitness) {
		this.fitness = fitness;
	}
	
	public static long getFitness(BufferedImage image, BufferedImage compareImage) {
		long fitness = 0;
		for (int y = 0; y < image.getHeight(); y++)
			for (int x = 0; x < image.getWidth(); x++) {
				Color c1 = new Color(image.getRGB(x, y));
				Color c2 = new Color(compareImage.getRGB(x, y));
		
				/* Get delta per colour. */
				int deltaRed = c1.getRed() - c2.getRed();
				int deltaGreen = c1.getGreen() - c2.getGreen();
				int deltaBlue = c1.getBlue() - c2.getBlue();
		
				/* Measure the distance between the colours in 3D space. */
				long pixelFitness = 
						deltaRed*deltaRed + deltaGreen*deltaGreen + deltaBlue*deltaBlue;
		 
		        /* Add the pixel fitness to the total fitness (lower is better). */
				fitness += pixelFitness;
			}	 
		return fitness;
	}
	
	@Override
	public int compareTo(PolygonImage o) {
        if(this.fitness > o.getFitness()) return 1;
        if(this.fitness < o.getFitness()) return -1;
        return 0;
	}
}

