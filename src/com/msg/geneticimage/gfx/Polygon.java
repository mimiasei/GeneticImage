package com.msg.geneticimage.gfx;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.interfaces.Gene;
import com.msg.geneticimage.main.Tools;

public class Polygon implements Gene {
	
	private ArrayList<Vertex> vertices;
	private Colour colour;
	
	public Polygon(PolygonImage polyImage) {
		vertices = new ArrayList<Vertex>();
		/* Generate random colour. */
		colour = new Colour();
		/* Generate random polygon using polar coordinates. */
		generateRandom();
	}
	
	public Polygon(Polygon clone) {
		this.vertices = new ArrayList<Vertex>(clone.vertices.size());
		for (Vertex v : clone.vertices)
			this.vertices.add(new Vertex(v));
		this.colour = new Colour(clone.colour);
	}
	
	private int[] getPolygonC(boolean isX) {
		int[] array = new int[vertices.size()];
		for (int i = 0; i < vertices.size(); i++)
			array[i] = (isX ? vertices.get(i).getCoords().x : vertices.get(i).getCoords().y);
		return array;
	}
	
	public int[] getPolygonX() {
		return getPolygonC(true);
	}
	
	public int[] getPolygonY() {
		return getPolygonC(false);
	}
	
	/**
	 * Returns the mean of the coords of all the vertices.
	 * @return origo
	 */
	public Point getOrigo() {
		int meanX = 0, meanY = 0;
		if(vertices != null && getVertexLength() > 0) {
			for (Vertex v : vertices) {
				meanX += v.x;
				meanY += v.y;
			}
			meanX /= getVertexLength();
			meanY /= getVertexLength();
		}
		return new Point(meanX, meanY);
	}
	
	public void move(int mx, int my) {
		for (Vertex v : vertices) {
			v.x += mx;
			v.y += my;
		}
	}
	
	public int getVertexLength() {
		return vertices.size();
	}
	
	public Vertex getVertex(int index) {
		return vertices.get(index);
	}

	public Color getColour() {
		return colour.getRGBA();
	}

	public void setColour(Color colour) {
		this.colour = new Colour(colour);
	}

	@Override
	public void mutate() {
		/* Change colour of polugon if passing CHANGE_COLOUR_RATIO test. */
		if(Tools.mutatable(Cons.CHANGE_COLOUR_RATIO))
			if(Tools.mutatable(Cons.RANDOM_NEW_RATIO))
				colour = new Colour();
			else
				colour.mutate();
		
		/* If passing test, randomly add or remove a random vertex. */
		if(Tools.mutatable(Cons.CHANGE_VERTICES_COUNT_RATIO))
			mutateVerticesCount();
		
		/* Mutate position of vertices. */
		for (Vertex v : vertices)
				v.setXY(mutatePosition(v.getCoords(), 0));			
	}

	@Override
	public void generateRandom() {
		byte vertsCount = (byte)(Tools.rndInt(3, Cons.POLYGON_VERTICES + 3));
		Vertex origo = new Vertex();
		int xRate = Tools.maxPolyWidth >> 1;
		int yRate = Tools.maxPolyHeight >> 1;
		for (int i = 0; i < vertsCount; i++) {
			Point newPnt = new Point();
			newPnt.x = Math.min(Tools.imgWidth, Math.max(0, origo.x + Tools.rndInt(-xRate, xRate)));
			newPnt.y = Math.min(Tools.imgHeight, Math.max(0, origo.y + Tools.rndInt(-yRate, yRate)));
			vertices.add(new Vertex(newPnt));
		}		
	}
	
	/**
	 * Mutate position of given Point by given factor. If factor = 0 then
	 * it is chosen randomly using VERTICES_FUZZINESS_SCALE.
	 * @param point
	 * @param factor
	 * @return Point
	 */
	public Point mutatePosition(Point point, double factor) {
		Random random = new Random(System.nanoTime());
		if(factor == 0) {
			factor = random.nextDouble() * Cons.VERTICES_FUZZINESS_SCALE;
			factor = ((1.0f - Cons.VERTICES_FUZZINESS_SCALE) - 
					(-(Cons.VERTICES_FUZZINESS_SCALE / 2.0f) + factor));
		}
		return new Point((int)(point.x * factor), (int)(point.y * factor));
	}
	
	/**
	 * Mutate vertices count by randomly adding or removing a vertex.
	 */
	public void mutateVerticesCount() {
		Random random = new Random(System.nanoTime());
		int vtxPos = random.nextInt(vertices.size() - 1);
		/* Either add vertex... */
		if(random.nextBoolean()) {
			int x1 = vertices.get(vtxPos).x;
			int x2 = vertices.get(vtxPos + 1).x;
			int y1 = vertices.get(vtxPos).y;
			int y2 = vertices.get(vtxPos + 1).y;
			/* Make new point in the middle between vtxPos and the next pos. */
			Point point = new Point(x1 + ((x2 - x1) >> 1), y1 + ((y2 - y1) >> 1));
			Vertex vertex = new Vertex();
			vertex.setXY(point);		
			vertices.add(vtxPos, vertex);
		} else
			/* ...or remove vertex. */
			if(vertices.size() > 3)
				vertices.remove(vtxPos);
	}
	
	public static double getFuzziness(double factor) {
		Random random = new Random(System.nanoTime());
		double fuzziness = random.nextDouble() * factor;
		final double halfPolyFuzzy = factor / 2.0f;
		double startVal = 1.0f - halfPolyFuzzy;
		fuzziness = (startVal - (-halfPolyFuzzy + fuzziness));
		return fuzziness;
	}
}
