package com.msg.geneticimage.gfx;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.interfaces.Gene;

public class Polygon implements Gene {
	
	private ArrayList<Vertex> vertices;
	private Colour colour;
	private PolygonImage polyImage;
	
	public Polygon(PolygonImage polyImage) {
		this.polyImage = polyImage;
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
		this.polyImage = new PolygonImage(clone.polyImage);
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

	public PolygonImage getPolyImage() {
		return polyImage;
	}

	public void setPolyImage(PolygonImage polyImage) {
		this.polyImage = polyImage;
	}

	@Override
	public void mutate() {
		Random random = new Random(System.nanoTime());		
		double factor;
		
		/* Change colour of polugon if passing CHANGE_COLOUR_RATIO test. */
		if(random.nextDouble() < Cons.CHANGE_COLOUR_RATIO)
			if(random.nextDouble() < Cons.RANDOM_NEW_RATIO)
				colour = new Colour();
			else
				colour.mutate();
		
		/* If passing test, randomly add or remove a random vertex. */
		if(random.nextDouble() < Cons.CHANGE_VERTICES_COUNT_RATIO)
			mutateVerticesCount();
		
		/* Mutate position of vertices if passing MUTATION_RATIO test. */
		if(random.nextDouble() < Cons.CHANGE_VERTICES_RATIO) {
			for (Vertex v : vertices)
				v.setXY(mutatePosition(v.getCoords(), 0));			
		}
	}

	@Override
	public void generateRandom() {
		Random random = new Random(System.nanoTime());
		int w = polyImage.getGeneticImage().getImage().getWidth();
		int h = polyImage.getGeneticImage().getImage().getHeight();
		radius = getRandomRadius(w, h);	
		origo = new Point(random.nextInt(w), random.nextInt(h));
		if(splineBased) {
			height = getRandomRadius(h, w);
			/* Get random theta from 0 to 2*PI. */
			theta = getRandomTheta(0) * 4.0; 
		} else {
			byte numberOfVertices = (byte)(random.nextInt(Cons.POLYGON_VERTICES) + 3);
			convertPolarToEuclidean(numberOfVertices, radius);
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
		if(random.nextBoolean()) {
			int x1 = vertices.get(vtxPos).getXY().x;
			int x2 = vertices.get(vtxPos + 1).getXY().x;
			int y1 = vertices.get(vtxPos).getXY().y;
			int y2 = vertices.get(vtxPos + 1).getXY().y;
			/* Make new point in the middle between vtxPos and the next pos. */
			Point point = new Point(x1 + ((x2 - x1) >> 1), y1 + ((y2 - y1) >> 1));
			Vertex vertex = new Vertex(this);
			vertex.setXY(point);		
			vertices.add(vtxPos, vertex);
		} else
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
