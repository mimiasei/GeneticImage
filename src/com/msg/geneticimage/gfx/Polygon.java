package com.msg.geneticimage.gfx;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.interfaces.Gene;
import com.msg.geneticimage.tools.Tools;

public class Polygon implements Gene {
	
	private ArrayList<Vertex> vertices;
	private Colour colour;
	public int w = Tools.imgWidth;
	public int h = Tools.imgHeight;
	public int x, y;
	
	public Polygon(int w, int h) {
		this.w = w;
		this.h = h;
		x = 0;
		y = 0;
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
		this.w = clone.w;
		this.h = clone.h;
		this.x = clone.x;
		this.y = clone.y;
	}
	
	public void setWidth(int w) {
		this.w = w;
	}
	
	public void setHeight(int h) {
		this.h = h;
	}
	
	private int[] getPolygonC(boolean isX) {
		int[] array = new int[vertices.size()];
		for (int i = 0; i < vertices.size(); i++)
			array[i] = (isX ? vertices.get(i).x : vertices.get(i).y);
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
		return new Point(x, y);
	}
	
	public void move(int mx, int my) {
		for (Vertex v : vertices) {
			v.x += mx;
			v.y += my;
		}
	}
	
	public int getVertexCount() {
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
		/* Change colour of polygon if passing CHANCE_COLOUR_RATIO test. */
		if(Tools.mutatable(Cons.CHANCE_COLOUR_RATIO)) {
			if(Tools.mutatable(Cons.CHANCE_RANDOM_NEW_RATIO))
				colour = new Colour();
			else
				colour.mutate();
			return;
		}
		
		/* If passing test, randomly add or remove a random vertex. */
		if(Tools.mutatable(Cons.CHANCE_VERTICES_COUNT_RATIO)) {
			mutateVerticesCount();
			return;
		}
		
		/* Mutate position of vertices. */
		for (Vertex v : vertices)
			if(Tools.mutatable(Cons.CHANCE_VERTICES_RATIO))
				v.mutate();
	}

	@Override
	public void generateRandom() {
		byte vertsCount = (byte)(Tools.rndInt(3, 3 + Cons.NBR_POLYGON_VERTICES));
		Vertex origo = new Vertex(w, h);
		x = origo.x;
		y = origo.y;
		for (byte i = 0; i < vertsCount; i++)
			vertices.add(origo);		
	}
	
	/**
	 * Mutate vertices count by randomly adding or removing a vertex.
	 */
	public void mutateVerticesCount() {
		byte vtxPos = (byte)Tools.rndInt(0, vertices.size()-1);
		/* Either remove vertex... */
		if(Tools.rndBool() && vertices.size() > 3) {
			vertices.remove(vtxPos);
		} else if(vertices.size() < Byte.MAX_VALUE-1) {
			/* ...or add vertex. */
			vertices.add(vtxPos, new Vertex(vertices.get(vtxPos)));
		}
	}
}
