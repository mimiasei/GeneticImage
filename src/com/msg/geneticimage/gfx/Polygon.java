package com.msg.geneticimage.gfx;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import com.msg.geneticimage.interfaces.Constants;
import com.msg.geneticimage.interfaces.Gene;

public class Polygon implements Gene, Constants {
	
	private ArrayList<Vertex> vertices;
	private Colour colour;
	private PolygonImage polyImage;
	private Point origo;
	private float rMin, rMax;
	
	public Polygon(PolygonImage polyImage) {
		this.polyImage = polyImage;
		origo = new Point(0, 0);
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
		float factor;
		if(random.nextFloat() < CHANGE_VERTICES_RATIO) {
			
			/* Mutate position of origo. */
			factor = random.nextFloat() * VERTICES_FUZZINESS_SCALE;
			factor = ((1.0f - VERTICES_FUZZINESS_SCALE) - (-(VERTICES_FUZZINESS_SCALE / 2.0f) + factor));		
			origo = new Point((int)(origo.x * factor), (int)(origo.y * factor));
			
			if(random.nextBoolean()) {
				/* Mutate position of vertices if passing MUTATION_RATIO test. */
				for (Vertex v : vertices) {
					factor = (1.0f - VERTICES_FUZZINESS_SCALE) + 
							(random.nextFloat() * (VERTICES_FUZZINESS_SCALE * 2));
					v.setXY(new Point((int)(v.getCoords().x * factor), 
							(int)(v.getCoords().y * factor)));			
				}
			} else {		
				/* Mutate scale of local coordinates of all vertices, 
				 * changing the scale of the entire polygon.
				 */
				factor = (1.0f - VERTICES_FUZZINESS_SCALE) + 
						(random.nextFloat() * (VERTICES_FUZZINESS_SCALE * 2));
				for (Vertex v : vertices)
					v.setXY(new Point((int)(v.getCoords().x * factor), 
							(int)(v.getCoords().y * factor)));			
			}
		}
		/* Change colour of polugon if passing CHANGE_COLOUR_RATIO test. */
		if(random.nextFloat() < CHANGE_COLOUR_RATIO)
			this.colour = new Colour();
	}

	@Override
	public void generateRandom() {
		Random random = new Random(System.nanoTime());
		int w = polyImage.getGeneticImage().getImage().getWidth();
		int h = polyImage.getGeneticImage().getImage().getHeight();
		byte numberOfVertices = (byte)(random.nextInt(POLYGON_VERTICES) + 3);
		float radius = getRandomRadius(w, h);	
		this.origo = new Point(random.nextInt(w), random.nextInt(h));
		convertPolarToEuclidean(numberOfVertices, radius);
	}
	
	public float getRandomRadius(int w, int h) {
		Random random = new Random(System.nanoTime());
		/* Make initial radius half the diagonal divided by polyCount. */
		float fuzziness = random.nextFloat() * POLYGON_FUZZINESS_SCALE;
		fuzziness = (0.5f - (-(POLYGON_FUZZINESS_SCALE / 2.0f) + fuzziness));
//		System.out.println("fuzziness: " + fuzziness);
//		float radius = (float)(Math.sqrt((w * h) / 
//				(polyImage.getNumberOfPolygons() * Math.PI))) * fuzziness;
		float radius = random.nextFloat() * (float)(w / 2.0f) * fuzziness;
		return radius;
	}
	
	public Point convertPolarToEuclidean(byte numberOfVertices, float radius) {
		Random random = new Random(System.nanoTime());
		float theta = 0.0f;
		int x = 0, y = 0;
		Vertex newVtx;
		for (int i = 0; i < numberOfVertices; i++) {
			newVtx = new Vertex(this);			
			/* Calculate theta from random 0 - PI/2. */
			theta = random.nextFloat() * ((float)Math.PI / 2.0f) + theta;
			theta = theta > ((float)Math.PI * 2.0f) ? ((float)Math.PI * 2.0f) : theta;
			/* Convert polar to euclidean space. */
			x = (int)(radius * Math.cos(theta));
			y = (int)(radius * Math.sin(theta));
			newVtx.setXY(new Point(x, y));
			vertices.add(newVtx);
		}
		return new Point(x, y);
	}
}
