package com.msg.geneticimage.gfx;

import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;
import java.util.Random;

import com.msg.geneticimage.interfaces.Constants;

public class Polygon implements Constants {
	
	private Point[] vtx;
	private Color colour;
	
	public Polygon() {
		vtx = new Point[POLYGON_VERTICES];
		Arrays.fill(vtx, new Point(0, 0));
		colour = Color.WHITE;
	}
	
	/* Create new random polygon using polar coordinates. */
	public void createRandomPolar(int width, int height) {
		Random random = new Random();
		int radius = random.nextInt((int)(width * POLYGON_MAX_SCALE));
		/* If POLYGON_MAX_SCALE == POLYGON_MIN_SCALE only use POLYGON_MAX_SCALE. */
		if(POLYGON_MAX_SCALE - POLYGON_MIN_SCALE > 0)
			radius += POLYGON_MIN_SCALE;
		/* Make sure radius is minimum 2. */
		radius = (radius < 2 ? 2 : radius);
		int origoX = random.nextInt(width - radius) + radius;
		int origoY = random.nextInt(height - radius) + radius;
		int x, y;
		double theta = 0.0;
		for (int i = 0; i < POLYGON_VERTICES; i += 2) {
			for (int j = 0; j < 2; j++) {
				theta = random.nextDouble() * (Math.PI / 2.0) + theta;
				theta = theta > (Math.PI * 2.0) ? (Math.PI * 2.0) : theta;
				x = (int)(radius * Math.cos(theta)) + origoX;
				y = (int)(radius * Math.sin(theta)) + origoY;
				vtx[i + j] = new Point(x, y);		
			}
		}		
	}
	
	public void setPolygon(Point p0, Point p1, Point p2) {
		vtx[0] = p0;
		vtx[1] = p1;
		vtx[2] = p2;		
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
	
	/*    1
	 *    |\
	 *    | \
	 *  a |  \ c
	 *    |   \
	 *    |    \
	 *    0-----2
	 *       b
	 */
	public int getArea() {
		int a, b, c;
		a = getDistance(vtx[0], vtx[1]);
		b = getDistance(vtx[0], vtx[2]);
		c = getDistance(vtx[1], vtx[2]);
		int area = (int)(Math.sqrt((a+b+c)*(b+c-a)*(c+a-b)*(a+b-c)) / 4.0);
		return area;
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
}
