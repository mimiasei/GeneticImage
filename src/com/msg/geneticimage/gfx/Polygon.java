package com.msg.geneticimage.gfx;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.interfaces.Gene;

public class Polygon implements Gene {
	
	final static double dblPI = (double)Math.PI * 2;
	final static double halfPI = (double)Math.PI / 2.0f;
	
	private ArrayList<Vertex> vertices;
	private Colour colour;
	private PolygonImage polyImage;
	private Point origo;
	private double radius = 0.0, height = 0.0, theta = 0.0;
	private boolean splineBased;
	
	public Polygon(PolygonImage polyImage, boolean splineBased) {
		this.polyImage = polyImage;
		this.splineBased = splineBased;
		origo = new Point(0, 0);
		vertices = new ArrayList<Vertex>();
		/* Generate random colour. */
		colour = new Colour();
		/* Generate random polygon using polar coordinates. */
		generateRandom();
	}
	
	public Polygon(PolygonImage polyImage) {
		this(polyImage, false);
	}
	
	public Polygon(Polygon clone) {
		this.vertices = new ArrayList<Vertex>(clone.vertices.size());
		for (Vertex v : clone.vertices)
			this.vertices.add(new Vertex(v));
		this.colour = new Colour(clone.colour);
		this.polyImage = new PolygonImage(clone.polyImage);
		this.origo = new Point(clone.origo);
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

	public Point getOrigo() {
		return origo;
	}

	public void setOrigo(Point origo) {
		this.origo = origo;
	}
	
	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}
	
	public double getTheta() {
		return theta;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}

	public static int getDistance(Point src, Point dst) {
		int dist = 0;
		int dX = dst.x - src.x;
		int dY = dst.y - src.y;
		dist = (int)(Math.sqrt(dX * dX + dY * dY));
		return dist;
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
		
		/* Mutate scale of local coordinates of all vertices, 
		 * changing the scale of the entire polygon.
		 */
		if(random.nextDouble() < Cons.RANDOM_SCALE_RATIO) {
			factor = getFuzziness(Cons.POLYGON_FUZZINESS_SCALE);
			for (Vertex v : vertices)
				v.setXY(mutatePosition(v.getCoords(), factor));	
		}
		
		/* Mutate position of origo. */
		if(random.nextDouble() < Cons.RANDOM_ORIGO_RATIO)
			origo = mutatePosition(origo, 0);
		
		/* Mutate rotation of origo. */
		if(random.nextDouble() < Cons.RANDOM_ROTATION_RATIO)
			mutateRotation(this, Cons.POLYGON_ROTATION_SCALE);
		
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
	 * Mutate rotation of given polygon by given factor.
	 * 
	 * @param polygon
	 * @return polygon
	 */
	public static Polygon mutateRotation(Polygon polygon, double factor) {
		Random random = new Random(System.nanoTime());
		double addTheta = random.nextDouble() * factor;
		addTheta *= (random.nextBoolean() ? 1.0 : -1.0);
		double theta;
		for (Vertex v : polygon.vertices) {
			theta = convertPointToTheta(v.getXY()) + addTheta;
			v.setXY(convertThetaToPoint(theta, polygon.origo.distance(v.getXY())));
		}
		return polygon;
	}
	
	/**
	 * Mutate vertices count by randomly adding or removing a vertex.
	 */
	public void mutateVerticesCount() {
		Random random = new Random(System.nanoTime());
		int vtxPos = random.nextInt(vertices.size() - 1);
		if(random.nextBoolean()) {
//			double radius = getDistance(origo, vertices.get(vtxPos).getXY());
//			double theta = getRandomTheta(convertPointToTheta(vertices.get(vtxPos).getXY()));
			int x1 = vertices.get(vtxPos).getXY().x;
			int x2 = vertices.get(vtxPos + 1).getXY().x;
			int y1 = vertices.get(vtxPos).getXY().y;
			int y2 = vertices.get(vtxPos + 1).getXY().y;
			/* Make new point in the middle between vtxPos and the next pos. */
			Point point = new Point(x1 + ((x2 - x1) >> 1), y1 + ((y2 - y1) >> 1));
			double fuzziness = getFuzziness(Cons.VERTICES_FUZZINESS_SCALE);
			point = new Point((int)(point.x * fuzziness), (int)(point.y * fuzziness));
			Vertex vertex = new Vertex(this);
//			vertex.setXY(convertThetaToPoint(theta, radius));
			vertex.setXY(point);		
			vertices.add(vtxPos, vertex);
		} else
			if(vertices.size() > 3)
				vertices.remove(vtxPos);
	}

	/**
	 * Get random radius using POLYGON_FUZZINESS_SCALE.
	 * @param w
	 * @param h
	 * @return radius
	 */
	public double getRandomRadius(int w, int h) {	
		/* Make initial radius 1/4 of width times fuzziness. */
		double radius = (w >> 1) * getFuzziness(Cons.POLYGON_FUZZINESS_SCALE);
		/* Make initial radius the diagonal divided by polyCount times PI. */
//		double radius = (double)(Math.sqrt((w * h) / 
//				(polyImage.getNumberOfPolygons() * Math.PI))) * getFuzziness(POLYGON_FUZZINESS_SCALE);
		return Math.max(radius, 1.0);
	}
	
	public static double getFuzziness(double factor) {
		Random random = new Random(System.nanoTime());
		double fuzziness = random.nextDouble() * factor;
		final double halfPolyFuzzy = factor / 2.0f;
		double startVal = 1.0f - halfPolyFuzzy;
		fuzziness = (startVal - (-halfPolyFuzzy + fuzziness));
		return fuzziness;
	}
	
	/**
	 * Get random theta (0 - PI/2) from previous theta value.
	 * @param previousTheta
	 * @return theta
	 */
	public static double getRandomTheta(double previousTheta) {
		Random random = new Random(System.nanoTime());
		double theta = 0.0f;
		/* Calculate theta from random 0 - PI/2. */
		theta = (random.nextDouble() * halfPI) + previousTheta;
		theta = absTheta(theta > dblPI ? dblPI : theta);
		return theta;
	}
	
	/**
	 * Convert euclidean to polar space.
	 * @param point
	 * @return theta
	 */
	public static double convertPointToTheta(Point point) {
		double theta = 0.0f;
		theta = (double)Math.atan(point.y / (double)point.x);
		if(point.x < 0 && point.y < 0)
			theta = -theta;
		theta = absTheta(theta);
		return theta;
	}

	/**
	 * Convert polar to euclidean space.
	 * @param theta
	 * @param radius
	 * @return Point
	 */
	public static Point convertThetaToPoint(double theta, double radius) {
		int x = (int)(radius * Math.cos(theta));
		int y = (int)(radius * Math.sin(theta));
		return new Point(x, y);
	}
	
	public static double absTheta(double theta) {
		if(theta < 0.0f)
			theta = dblPI + theta;
		return theta;
	}
	
	public void convertPolarToEuclidean(byte numberOfVertices, double radius) {
		Random random = new Random(System.nanoTime());
		double theta = 0.0f;
		Vertex newVtx;
		for (byte i = 0; i < numberOfVertices; i++) {
			double fuzziness = 1.0f;
			if(random.nextDouble() < Cons.RANDOM_RADIUS_RATIO)
				fuzziness = getFuzziness(Cons.POLYGON_FUZZINESS_SCALE);
			newVtx = new Vertex(this);			
			theta = getRandomTheta(theta);
			newVtx.setXY(convertThetaToPoint(theta, radius * fuzziness));
			vertices.add(newVtx);
		}
	}
}
