package com.msg.geneticimage.main;

import java.util.ArrayList;
import java.util.Iterator;

import com.msg.geneticimage.gfx.PolygonImage;
import com.msg.geneticimage.interfaces.Cons;

public class GeneCan {
	
	private ArrayList<PolygonImage> genes;
	
	public GeneCan() {
		genes = new ArrayList<PolygonImage>();
	}
	
	public ArrayList<PolygonImage> getGenes() {
		return genes;
	}
	
	public PolygonImage get(int index) {
		return genes.get(index);
	}
	
	public void add(PolygonImage chromosome) {
		genes.add(chromosome);
	}
	
	public void add(ArrayList<PolygonImage> chromosomes) {
		for (PolygonImage chromosome : chromosomes) {
			genes.add(chromosome);
		}
	}
	
	/**
	 * Updates age of all chromosomes in genes.
	 * If age == 0, remove chromosome.
	 */
	public void update() {
		if(!genes.isEmpty()) {
			Iterator<PolygonImage> iter = genes.iterator();
			while (iter.hasNext()) {
				PolygonImage chromo = iter.next();
				if(Tools.mutatable(Cons.CHANCE_OF_CAN_GENES_DYING))
			        iter.remove();
			}
		}
	}
	
	public int getSize() {
		return genes.size();
	}
	
	public boolean isEmpty() {
		return genes.isEmpty();
	}
}
