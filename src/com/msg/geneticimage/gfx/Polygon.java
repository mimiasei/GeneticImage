package com.msg.geneticimage.gfx;

import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;
import java.util.Random;

import com.msg.geneticimage.interfaces.Constants;
import com.msg.geneticimage.interfaces.Gene;

public class Polygon implements Gene, Constants {
	
	private Point[] vtx;
	private Colour colour;
	private int area = 0;
	
	public Polygon() {
		Random random = new Random();
		int polyVertices = random.nextInt(POLYGON_VERTICES) + 3;
		polyVertices = polyVertices % 2 == 0 ? polyVertices : polyVertices - 1;
		vtx = new Point[polyVertices];
		Arrays.fill(vtx, new Point(0, 0));
		colour = new Colour(Color.WHITE);
	}
	
	/* Create new random polygon using polar coordinates. */
	public void createRandomPolar(int area, int polyCount) {
		this.area = area;
		generateRandom(polyCount);
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
	
	public int getDistance(Point p1, Point p2) {
		int deltaX = p2.x - p1.x;
		int deltaY = p2.y - p1.y;
		return (int)Math.sqrt(deltaX*deltaX + deltaY*deltaY);
	}
	
	public int getVertexLength() {
		return vtx.length;
	}

	public Color getColour() {
		return colour.getRGBA();
	}

	public void setColour(Color colour) {
		this.colour = new Colour(colour);
	}
	
	public int getArea() {
		return area;
	}
	
	public void setArea(int area) {
		this.area = area;
	}

	@Override
	public void mutate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateRandom(int polyCount) {
		Random random = new Random();
		float radius, fuzziness;
		/* Make initial radius half the diagonal divided by polyCount. */
		radius = (float)Math.sqrt(area / (polyCount * Math.PI));
				
		fuzziness = random.nextFloat() * POLYGON_FUZZINESS_SCALE;
		radius = ((1.0f - fuzziness) + (fuzziness * 2.0f)) * radius;
		fuzziness = random.nextFloat() * RANDOM_RADIUS;
		radius = ((1.0f - fuzziness) + (fuzziness * 2.0f)) * radius;
					
		int origoX = random.nextInt(width - (int)radius) + (int)radius;
		int origoY = random.nextInt(height -(int)radius) + (int)radius;
		int x, y;
		double theta = 0.0;
		for (int i = 0; i < vtx.length; i += 2) {
			for (int j = 0; j < 2; j++) {
				theta = random.nextFloat() * (Math.PI / 2.0f) + theta;
				theta = theta > (Math.PI * 2.0f) ? (Math.PI * 2.0f) : theta;
				x = (int)(radius * Math.cos(theta)) + origoX;
				y = (int)(radius * Math.sin(theta)) + origoY;
				vtx[i + j] = new Point(x, y);		
			}
		}
		colour.generateRandom(0);
	}
}
