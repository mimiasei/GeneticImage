package com.msg.geneticimage.gfx;

import java.awt.Point;

import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.interfaces.Gene;
import com.msg.geneticimage.tools.Tools;

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
		if(Tools.mutatable(Cons.CHANCE_VERTICES_RATIO)) {
			x += Tools.gaussianInt(w, Tools.sliders.get("FACTOR_VERTICES_FUZZINESS"));
			y += Tools.gaussianInt(h, Tools.sliders.get("FACTOR_VERTICES_FUZZINESS"));
//			int sign = Tools.rndBool() ? 1 : -1;
//			x += Tools.randomInt(0, (int)Math.max(w * Tools.sliders.get("FACTOR_VERTICES_FUZZINESS"), 1))*sign;
//			sign = Tools.rndBool() ? 1 : -1;
//			y += Tools.randomInt(0, (int)Math.max(h * Tools.sliders.get("FACTOR_VERTICES_FUZZINESS"), 1))*sign;
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
