package com.msg.geneticimage.gfx;

import java.awt.Point;

import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.interfaces.Gene;
import com.msg.geneticimage.main.Tools;

public class Vertex implements Gene {
	
	public int x = 0, y = 0;
	
	public Vertex() {
		generateRandom();
	}
	
	public Vertex(Point coords) {
		this.x = coords.x;
		this.y = coords.y;
	}
	
	public Vertex(Vertex clone) {
		this.x = clone.x;
		this.y = clone.y;
	}
	
	public void setXY(Point coords) {
		this.x = coords.x;
		this.y = coords.y;
	}
	
	public Point getCoords() {
		return new Point(x, y);
	}

	@Override
	public void mutate() {
		if(Tools.mutatable(Cons.CHANGE_VERTICES_MAX_RATIO)) {
			x = Tools.rndInt(0, Tools.imgWidth);
			y = Tools.rndInt(0, Tools.imgHeight);
		}
		
		if(Tools.mutatable(Cons.CHANGE_VERTICES_RATIO)) {
			int xRate = (int)(Tools.imgWidth * Cons.VERTICES_FUZZINESS_SCALE);
			int yRate = (int)(Tools.imgWidth * Cons.VERTICES_FUZZINESS_SCALE);
			x += Tools.rndInt(-xRate, xRate);
			y += Tools.rndInt(-yRate, yRate);
		}
	}

	@Override
	public void generateRandom() {
		x = Tools.rndInt(0, Tools.imgWidth);
		y = Tools.rndInt(0, Tools.imgHeight);
	}

}
