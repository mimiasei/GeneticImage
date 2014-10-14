package com.msg.geneticimage.gfx;

import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;
import java.util.Random;

import com.msg.geneticimage.interfaces.Constants;

public class Polygon implements Constants {
	
	private Point[] vtx;
	private Color colour;
	private int width, height;
	
	public Polygon() {
		vtx = new Point[POLYGON_VERTICES];
		Arrays.fill(vtx, new Point(0, 0));
		colour = Color.WHITE;
		width = height = 0;
	}
	
	/* Create new random polygon using polar coordinates. */
	public void createRandomPolar(int width, int height) {
		Random random = new Random();
		this.width = width;
		this.height = height;
		int radius, radRnd, radRndMin;
		int diagonal = (int)Math.sqrt(width*width + height*height);
		radRnd = (int)(diagonal * POLYGON_MAX_SCALE);
		radRndMin = (int)(diagonal * POLYGON_MIN_SCALE);
		radRnd = (radRnd - radRndMin) > 3 ? radRnd : radRndMin + 4;
		/* If POLYGON_MAX_SCALE == POLYGON_MIN_SCALE only use POLYGON_MAX_SCALE. */
		if((POLYGON_MAX_SCALE - POLYGON_MIN_SCALE) > 0.0f)
			radius = random.nextInt(radRnd - radRndMin) + radRndMin;
		else
			radius = random.nextInt(radRnd) + 4;
		int origoX = random.nextInt(width - radius) + radius;
		int origoY = random.nextInt(height - radius) + radius;
		int x, y;
		double theta = 0.0;
		for (int i = 0; i < POLYGON_VERTICES; i += 2) {
			for (int j = 0; j < 2; j++) {
				theta = random.nextFloat() * (Math.PI / 2.0f) + theta;
				theta = theta > (Math.PI * 2.0f) ? (Math.PI * 2.0f) : theta;
				x = (int)(radius * Math.cos(theta)) + origoX;
				y = (int)(radius * Math.sin(theta)) + origoY;
				vtx[i + j] = new Point(x, y);		
			}
		}		
	}
	
	private int[] getPolygonC(boolean isX) {
		int[] array = new int[vtx.length];
		for (int i = 0; i < vtx.length; i++)
			array[i] = (isX ? vtx[i].x : vtx[i].y);
		return array;
	}
	
	public int[] getPolygonX() {
		return getPolygonC(true);
	}
	
	public int[] getPolygonY() {
		return getPolygonC(false);
	}
	
	public Point getVertex(int vertex) {
		if(vertex < 0)
			vertex = 0;
		else if(vertex > 2)
			vertex = 2;
		return vtx[vertex];
	}
	
	public void setVertex(int vertex, Point p) {
		if(vertex < 0)
			vertex = 0;
		else if(vertex > 2)
			vertex = 2;
		vtx[vertex] = p;
	}
	
	public int getDistance(Point p1, Point p2) {
		int deltaX = p2.x - p1.x;
		int deltaY = p2.y - p1.y;
		return (int)Math.sqrt(deltaX*deltaX + deltaY*deltaY);
	}
	
	public int getVertexLength() {
		return vtx.length;
	}

	public Color getColour() {
		return colour;
	}

	public void setColour(Color colour) {
		this.colour = colour;
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

	@Override
	public String toString() {
		String text = "";
		for (byte i = 0; i < vtx.length; i++)
			text += ("Vtx" + i + " = (" + vtx[i].x + ", " + vtx[i].y + ") ");
		return text;
	}
}
