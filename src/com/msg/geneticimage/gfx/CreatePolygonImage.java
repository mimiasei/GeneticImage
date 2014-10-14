package com.msg.geneticimage.gfx;

import java.awt.Color;
import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JComponent;

@SuppressWarnings("serial")
public class CreatePolygonImage extends JComponent implements Comparable<CreatePolygonImage> {
	
	private ArrayList<Polygon> polygons = new ArrayList<Polygon>();
	private int width, height;
	private boolean isNew = true;
	private long fitness = Long.MAX_VALUE;
	
	public CreatePolygonImage(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public CreatePolygonImage(CreatePolygonImage clone) {
		this.width = clone.width;
		this.height = clone.height;
		this.fitness = clone.fitness;
		this.isNew = clone.isNew;
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
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}

	public long getFitness() {
		return fitness;
	}

	public void setFitness(long fitness) {
		this.fitness = fitness;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	@Override
	public int compareTo(CreatePolygonImage o) {
        if(this.fitness > o.getFitness()) return 1;
        if(this.fitness < o.getFitness()) return -1;
        return 0;
	}
}

