package com.msg.geneticimage.main;

import java.util.ArrayList;

import com.msg.geneticimage.gfx.PolygonImage;
import com.msg.geneticimage.interfaces.Cons;

public class ParentPicker {
	
	ArrayList<PolygonImage> population;
	
	public ParentPicker(ArrayList<PolygonImage> population) {
		this.population = population;
	}
	
	/**
	 * Returns NUMBER_OF_PARENTS indexes of random parents from population.
	 * NOT USED.
	 * @return indexes
	 */
	public int[] selectParents() {
		int[] parents = new int[Cons.NUMBER_OF_PARENTS];
		int choice = Tools.rndInt(0, 4);
		int range = 0;
		boolean rnd = true;
		boolean bothBestAndWorst = false;
		boolean justWorst = false;
		switch(choice) {
		/* Random two */
		case 0:
			range = population.size();
			break;
		/* Random from best and worst each */
		case 1:
			range = population.size() >> 1;
			bothBestAndWorst = true;
			break;
		/* Random two from best */
		case 2:
			justWorst = true;
		/* Random two from worst */
		case 3:
			range = population.size() >> 1;
			break;
		/* Top two best */
		case 4:
			range = parents.length;
			rnd = false;
		}
		
		for (int p = 0; p < parents.length; p++)
			if(rnd) {
				parents[p] = Tools.rndInt(0, range-1);
				if(justWorst)
					parents[p] += range;
				if(p > 0 && bothBestAndWorst)
					parents[p] = Tools.rndInt(0, range-1) + range;
			} else
				parents[p] = p;
		
		return parents;
	}
}
