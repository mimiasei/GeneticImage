package com.msg.geneticimage.gfx;

import java.awt.Point;

import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.interfaces.Gene;
import com.msg.geneticimage.main.Tools;

public class Vertex implements Gene {
	
	public int x = 0, y = 0;
	public int w = Tools.imgWidth, h = Tools.imgHeight;
	
	public Vertex(int w, int h) {
		this.w = w;
		this.h = h;
		generateRandom();
	}
	
	public Vertex(Vertex clone) {
		this.x = clone.x;
		this.y = clone.y;
		this.w = clone.w;
		this.h = clone.h;
	}
	
	public Point getCoords() {
		return new Point(x, y);
	}
	
	public void setWidth(int w) {
		this.w = w;
	}
	
	public void setHeight(int h) {
		this.h = h;
	}

	@Override
	public void mutate() {	
		if(Tools.mutatable(Cons.CHANGE_VERTICES_RATIO)) {
			x += Tools.gaussianInt(w, Cons.VERTICES_FUZZINESS_SCALE);
			y += Tools.gaussianInt(h, Cons.VERTICES_FUZZINESS_SCALE);
//			x += (Tools.rndInt(0, (int)(w * Cons.VERTICES_FUZZINESS_SCALE)) * 
//					(Tools.rndBool() ? 1 : -1));
//			y += (Tools.rndInt(0, (int)(h * Cons.VERTICES_FUZZINESS_SCALE)) *
//					(Tools.rndBool() ? 1 : -1));
		}
	}

	@Override
	public void generateRandom() {
		x = Tools.randomInt(0, w-1);
		y = Tools.randomInt(0, h-1);
	}
	
	@Override
	public String toString() {
		return (x + ", " + y);
	}

}
