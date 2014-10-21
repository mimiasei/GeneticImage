package com.msg.geneticimage.gfx;

import java.awt.Point;

import com.msg.geneticimage.interfaces.Gene;

public class Vertex implements Gene {
	
	private Point xy;
	private Polygon polygon;
	
	public Vertex(Polygon polygon) {
		this.xy = new Point(0, 0);
		this.polygon = polygon;
	}
	
	public Vertex(Vertex clone) {
		this.xy = new Point(clone.xy);
		this.polygon = clone.polygon;
	}

	public Point getCoords() {
		return new Point(polygon.getOrigo().x + xy.x, polygon.getOrigo().y + xy.y);
	}
	
	public Point getXY() {
		return xy;
	}

	public void setXY(Point xy) {
		this.xy = xy;
	}

	@Override
	public void mutate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateRandom() {
		// TODO Auto-generated method stub

	}

}
