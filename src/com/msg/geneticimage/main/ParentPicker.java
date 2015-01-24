package com.msg.geneticimage.main;

import java.util.Arrays;

import com.msg.geneticimage.algorithm.GeneticAlgorithm;
import com.msg.geneticimage.gfx.PolygonImage;
import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.tools.Tools;

public class ParentPicker {
	
	private GeneticAlgorithm genAlg;
	private int pickedFromCan;
	private boolean[] usedPop;
	private int popSize;
	
	public ParentPicker(GeneticAlgorithm genAlg) {
		this.genAlg = genAlg;
		popSize = genAlg.getPopulation().length;
		pickedFromCan = 0;
		usedPop = new boolean[popSize << 1];
	}
	
	/**
	 * Return random NUMBER_OF_PARENTS parents, picked from both
	 * population and gene can. Chance of picking from gene can
	 * is random by factor CHANCE_GENECAN_PARENT_RATIO.
	 * @return parents
	 */
	public PolygonImage[] getParents() {
		PolygonImage[] parents = new PolygonImage[Cons.NUMBER_OF_PARENTS];
		GeneCan geneCan = genAlg.getGeneCan();
		PolygonImage[] population = genAlg.getPopulation();
		int pickIndex;
		for (int i = 0; i < parents.length; i++) {
			if(!geneCan.isEmpty() && Tools.mutatable(Cons.CHANCE_GENECAN_PARENT_RATIO)) {
				int picked = Tools.rndInt(0, geneCan.getSize()-1);
				parents[i] = geneCan.get(picked);
				pickedFromCan++;
				genAlg.geneCanPicked();
			} else {
				do
					pickIndex = Tools.rndInt(0, popSize - 1);
				while (usedPop[pickIndex]);
				parents[i] = population[pickIndex];
				usedPop[pickIndex] = true;
			}
		}
		/* Reset isDirty flags. */
		for (PolygonImage chromo : parents)
			chromo.setDirty(false);
		
		return parents;
	}
	
	/**
	 * Returns NUMBER_OF_PARENTS indexes of random parents from population.
	 * NOT USED.
	 * @return indexes
	 */
	public int[] selectParents() {
		PolygonImage[] population = genAlg.getPopulation();
		int[] parents = new int[Cons.NUMBER_OF_PARENTS];
		int choice = Tools.rndInt(0, 4);
		int range = 0;
		boolean rnd = true;
		boolean bothBestAndWorst = false;
		boolean justWorst = false;
		switch(choice) {
		/* Random two */
		case 0:
			range = population.length;
			break;
		/* Random from best and worst each */
		case 1:
			range = population.length >> 1;
			bothBestAndWorst = true;
			break;
		/* Random two from best */
		case 2:
			justWorst = true;
		/* Random two from worst */
		case 3:
			range = population.length >> 1;
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

	public int getPickedFromCan() {
		return pickedFromCan;
	}

	public void resetUsedPop() {
		Arrays.fill(usedPop, false);	
	}

	public int getPicked() {
		return pickedFromCan;
	}
}
