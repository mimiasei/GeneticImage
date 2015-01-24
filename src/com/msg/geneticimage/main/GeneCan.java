package com.msg.geneticimage.main;

import java.util.Comparator;
import java.util.Iterator;

import com.msg.geneticimage.gfx.PolygonImage;
import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.tools.SortedList;
import com.msg.geneticimage.tools.Tools;

public class GeneCan {
	
	private SortedList<PolygonImage> genes;
	
	/* Comparator for comparing PolygonImages. */
	Comparator<PolygonImage> comp = new Comparator<PolygonImage>() {
		public int compare(PolygonImage one, PolygonImage two){
			return one.compareTo(two);
		}
	};
	
	public GeneCan() {
		genes = new SortedList<PolygonImage>(comp);
	}
	
	public SortedList<PolygonImage> getGenes() {
		return genes;
	}
	
	public PolygonImage get(int index) {
		return genes.get(index);
	}
	
	public void add(PolygonImage chromosome) {
		genes.add(chromosome);
	}
	
	public void add(SortedList<PolygonImage> chromosomes) {
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
