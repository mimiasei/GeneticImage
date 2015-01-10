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
	
	public void addToGeneCan(PolygonImage chromosome) {
//		chromosome.resetAge();
		genes.add(chromosome);
	}
	
	public void addToGeneCan(ArrayList<PolygonImage> chromosomes) {
		for (PolygonImage chromosome : chromosomes) {
//			chromosome.resetAge();
			genes.add(chromosome);
		}
	}
	
	/**
	 * Updates age of all chromosomes in genes.
	 * If age == 0, remove chromosome.
	 */
	public void updateGeneCan() {
		if(!genes.isEmpty()) {
			Iterator<PolygonImage> iter = genes.iterator();
//			PolygonImage chromosome;
			while (iter.hasNext()) {
//				chromosome = iter.next();
//				chromosome.decrAge();
//			    if(chromosome.isZeroAge())
				if(Tools.mutatable(Cons.CHANCE_OF_CAN_GENES_DYING))
			        iter.remove();
			}
		}
	}
	
	public int getSize() {
		return genes.size();
	}
	
	public boolean isGeneCanEmpty() {
		return genes.isEmpty();
	}
}
