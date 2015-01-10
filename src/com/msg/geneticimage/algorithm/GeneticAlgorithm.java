package com.msg.geneticimage.algorithm;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import com.msg.geneticimage.gfx.PolygonImage;
import com.msg.geneticimage.gfx.Polygon;
import com.msg.geneticimage.gfx.Renderer;
import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.main.FitnessCalc;
import com.msg.geneticimage.main.GeneticImage;
import com.msg.geneticimage.main.NanoTimer;
import com.msg.geneticimage.main.FileHandler;
import com.msg.geneticimage.main.Tools;

public class GeneticAlgorithm extends Algorithm<PolygonImage[]> {
	
	private long previousBestFitness;
	private PolygonImage currentBestImage, currentImage;
	private GeneticImage geneticImage;
	private ArrayList<PolygonImage> population;
	private ArrayList<PolygonImage> geneCan;
	private int stagnating;
	private double mutationRatio;
	private boolean[] usedPop = new boolean[Cons.POPULATION_SIZE << 1];
	private double fitPercent = 1.0;
	private int iterations = 0;
	private int pickedFromCan = 0;
	
	public GeneticAlgorithm(GeneticImage geneticImage) {
		this.geneticImage = geneticImage;
		currentBestImage = new PolygonImage(this.geneticImage, maxIterations);
		currentImage = currentBestImage;
		population = new ArrayList<PolygonImage>();
		geneCan = new ArrayList<PolygonImage>();
		mutationRatio = Cons.MUTATION_RATIO;
	}

	/**
	 * Genetic algorithm. Optimizes a given population array of PolygonImages
	 * of size POPULATION_SIZE and returns the optimized array.
	 * @param PolygonImage[]
	 * @return new PolygonImage[]
	 */
	@Override
	public PolygonImage[] process(PolygonImage[] inputPopulation) {
		
		BufferedImage compareImage = geneticImage.getCompareImage();
		/* Empty population list. */
		population.clear();
		/* Recalculate fitness of all PolygonImages in input population. */
		inputPopulation = recalculatePopulationFitness(inputPopulation);
		/* Create PolygonImage list from population array parameter. */
		Collections.addAll(population, inputPopulation);
		
		/* If plotData then create fileHandler for saving data for plotting
		 * in GnuPlot. */
		FileHandler plotter;
		if(Cons.PLOT_DATA)
			plotter = new FileHandler(population.get(0));
		
		long startFitness;
		PolygonImage[] parent = new PolygonImage[Cons.NUMBER_OF_PARENTS];
		PolygonImage[] children;
		
		NanoTimer nanoTimer = new NanoTimer();
		NanoTimer nanoTimerAll = new NanoTimer();
		
		int[] comparePixels = Renderer.getPixelsArray(compareImage);
		long maxFitness = FitnessCalc.getFitness(comparePixels, FitnessCalc.getInversePixels(comparePixels));
		
		/* Create a final initial chromosome with max fitness score, for initiation purposes. */
		final PolygonImage initialChromosome = new PolygonImage(geneticImage, maxIterations);
		/* Initialize current best chromosome. */
		currentImage = currentBestImage = new PolygonImage(initialChromosome);
		
		startFitness = population.get(0).getFitness();
		previousBestFitness = startFitness;
		stagnating = 0;
		int minFitnessDiff = (int)Math.max(1, startFitness * Cons.MIN_FITNESS_DIFF_RATIO);
		int wasted = 0;
						
		System.out.println("\nGenetic algorithm. Pop. size: " + Cons.POPULATION_SIZE + ". Poly count: " +
				maxIterations + ". Image dims: " + compareImage.getWidth() + " x " + compareImage.getHeight() +
				". BitShift: " + geneticImage.getBitShift() + ". Min fitnessDiff: " + minFitnessDiff);
		
		nanoTimerAll.startTimer();
		
		/* 
		 * - -----------~~~=====<< Main algorithm loop. >>=====~~~----------- - 
		 */
		while (fitPercent > 0.005 || stagnating < Cons.NUMBER_OF_GENERATIONS) {
			
			nanoTimer.startTimer();
			
			iterations++;
			
			/* Update the gene can state. */
			updateGeneCan();
			
			/* Reset usedPop array. */
			Arrays.fill(usedPop, false);
			
//			boolean better = false;
			
			/* Go through population picking two parents at a time. */
			for (int p = 0; p < Cons.POPULATION_SIZE; p += 2) {
			
				/* Get random NUMBER_OF_PARENTS parents from combined
				 * pool of population and gene can. */
				parent = getParents();
				
				children = new PolygonImage[parent.length];
				
				/* Create the two children. */
				for (byte c = 0; c < children.length; c++)	{			
					/* Create child as copy of its parent. */
					children[c] = new PolygonImage(parent[c]);
				}
				
				/* Use the smallest poly count of the two children. */
				int polyCount = Math.min(children[0].getPolyCount(), 
						children[1].getPolyCount());
	
				/* >>> CROSS-OVER PHASE
				 * Do two-point cross-over if ratio permits it. */
				children = doCrossOver(children, polyCount);
					
				/* >>> MUTATION PHASE 
				 * Mutate children if ratio permits it. */
				mutationRatio = getMutationRatio();
				children = doMutation(children, polyCount, true);
				
				/* Add children to population. */
				for (byte c = 0; c < 2; c++) {
					if(children[c].isDirty())
						children[c].calculateFitness();
					population.add(children[c]);
//					if(children[c].getFitness() < currentBestImage.getFitness())
//						better = true;
				}	
			}
			
			previousBestFitness = currentBestImage.getFitness();
			
			/* Only bother with sorting population if a child's fitness
			 * actually is an improvement. */
//			if(better) {
			/* Sort newPopulation by fitness. */
			Collections.sort(population);
			
			/* Check if current fitness is the better one. */
			if(population.get(0).getFitness() < currentBestImage.getFitness())
				setCurrentBestImage(population.get(0));
			else
				wasted++;
			
			/* Remove worst half and put that half in gene can. */
			removeWorstHalf();
			
			/* If current best fitness is not more than MIN_FITNESS_DIFF_RATIO better 
			 * than previous best, increment stagnating. Else, reset stagnating. */
			minFitnessDiff = (int)(currentBestImage.getFitness() * Cons.MIN_FITNESS_DIFF_RATIO);
			if((previousBestFitness - currentBestImage.getFitness()) < minFitnessDiff)
				stagnating++;
			else
				stagnating = 0;
			
			/* If stagnating NUMBER_OF_STAGNATIONS times, inject new blood. */
			if(isStagnating())
				injectBlood();
			
			nanoTimer.stopTimer();
			
			/* Plot best and worst graph. */
			if(Cons.PLOT_DATA)
				plotter.saveData(population, iterations);
			
			/* Calculate current fitness percentage compared to maxFitness. */
			fitPercent = (double)(currentBestImage.getFitness() / (double)maxFitness);
			
			/* Print every PRINT_FREQUENCY generations. */
			if(iterations % Cons.PRINT_FREQUENCY == 0) {
				System.out.println("Gen.time: " + nanoTimer + 
						". Total: " + nanoTimerAll.getElapsedTime() + 
						". Gen#: " + iterations + 
						". Stag: " + stagnating +
						". Waste: " + wasted +
						". PFGC: " + pickedFromCan +
						". Mut.ratio: " + Tools.nDecimals(mutationRatio, 4) +
						". geneCan: " + geneCan.size() +
						". Fit%: " + Tools.nDecimals((1.0 - fitPercent) * 100.0, 5) + 
						". Polys: " + currentBestImage.getPolyCount());
			}
		}
		
		nanoTimerAll.stopTimer();
		
		if(Cons.PLOT_DATA)
			plotter.closeFile();
		
		PolygonImage[] array = new PolygonImage[population.size()];
		return (PolygonImage[])population.toArray(array);
	}

	/* 
	 * ------------------------<<<<< End main algorithm >>>>>------------------------- 
	 */
	
	public void debugPrintPopulation() {
		System.out.println("Population:");
		for (PolygonImage chromo : population)
			System.out.println(chromo.getFitness() + " age: " + chromo.getAge());
	}
	
	public void debugPrintGeneCan() {
		System.out.println("Gene Can:");
		for (PolygonImage chromo : geneCan)
			System.out.println(chromo.getFitness() + " age: " + chromo.getAge());
	}
	
	/**
	 * Calculates chance of mutation with Cons.MUTATION_RATIO as base.
	 * The chance increases exponentially with number of stagnations,
	 * making a mutation more likely to occur when current best fitness
	 * does not improve (stagnates).
	 * @return mutationRatio
	 */
	public double getMutationRatio() {
		double mutRatio = Cons.MUTATION_RATIO;
//		double ratio = stagnating / (double)Cons.NUMBER_OF_STAGNATIONS / 2.0;
		if(iterations < Cons.NUMBER_OF_GENS_DEFAULT_MUT_RATIO) {		
			double f = 1.0 - (iterations / (double)Cons.NUMBER_OF_GENS_DEFAULT_MUT_RATIO);
			f *= 2.5;
			mutRatio *= (1.0 + (f * f));
		}
		mutRatio = Math.min(Cons.MAX_MUTATION_RATIO, mutRatio); // + (ratio * ratio * ratio); 
		if(Tools.mutatable(Cons.CHANCE_OF_MUT_RATIO_RESET)) {
			mutRatio = Cons.MUTATION_RATIO;
		}
		return mutRatio;
	}
	
	public boolean isStagnating() {
		return (stagnating > 0 && stagnating % Cons.NUMBER_OF_STAGNATIONS == 0);
	}
	
	/**
	 * Return random NUMBER_OF_PARENTS parents, picked from both
	 * population and gene can. Chance of picking from gene can
	 * is random by factor PARENT_FROM_GENECAN_RATIO.
	 * @return parents
	 */
	public PolygonImage[] getParents() {
		PolygonImage[] parents = new PolygonImage[Cons.NUMBER_OF_PARENTS];
		int pickIndex;
		for (int i = 0; i < parents.length; i++) {
			if(!isGeneCanEmpty() && Tools.mutatable(Cons.PARENT_FROM_GENECAN_RATIO)) {
				int picked = Tools.rndInt(0, geneCan.size()-1);
				parents[i] = geneCan.get(picked);
				pickedFromCan++;
			} else {
				do
					pickIndex = Tools.rndInt(0, Cons.POPULATION_SIZE - 1);
				while (usedPop[pickIndex]);
				parents[i] = population.get(pickIndex);
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
	
	/**
	 * Do cross-over of input children pair if ratio allows.
	 * @param children
	 * @return new children
	 */
	public PolygonImage[] doCrossOver(PolygonImage[] children, int polyCount) {
		if(Tools.mutatable(Cons.CROSSOVER_RATIO)) {
			int count = Tools.rndInt(1, polyCount-1);
			for (int b = 0; b < count; b++) {
				Polygon tmpPoly = new Polygon(children[0].getPolygon(b));
				children[0].setPolygon(b, children[1].getPolygon(b));
				children[1].setPolygon(b, tmpPoly);
			}
			/* Set dirty flags since chromosomes have been tweaked. */
			children[0].setDirty(true);
			children[1].setDirty(true);
		}
		return children;
	}
	
	/**
	 * Mutate input PolygonImage array if ratio allows.
	 * if parameter 'standard' is false, all chromosomes in array
	 * will be mutated.
	 * @param children
	 * @param polyCount
	 * @param standard
	 * @return new children
	 */
	public PolygonImage[] doMutation(PolygonImage[] children, int polyCount, boolean standard) {
		Polygon polygon;
		double newRatio = standard ? Cons.RANDOM_NEW_RATIO : 1.0;
		for (byte c = 0; c < children.length; c++) {
			if(Tools.mutatable(getMutationRatio())) {
//				int pos = Tools.rndInt(0, polyCount-1);
				int pos = Tools.randomInt(0, polyCount-1);
				if(Tools.mutatable(newRatio)) {
					/* Generate random polygon. */
					children[c].setPolygon(pos, new Polygon(geneticImage.getCompareImage().getWidth(), 
							geneticImage.getCompareImage().getHeight()));
				} else {
					/* Mutate polygon. */
					if(children[c].getPolygon(pos) != null) {
						polygon = new Polygon(children[c].getPolygon(pos));
						polygon.mutate();
						children[c].setPolygon(pos, polygon);
					}
				}
				/* Set dirty flag since chromosome has been tweaked. */
				children[c].setDirty(true);
				/* Mutate number of polygons. */
				if(Tools.mutatable(Cons.CHANGE_POLYCOUNT_RATIO)) {
					children[c].mutatePolyCount();
					return children;
				}
				/* Swap order of two random polygons. */
				children[c].swapRandom();		
			}
		}
		return children;
	}
	
	/**
	 * Removes worst half of population, putting that half in
	 * the gene can.
	 */
	public void removeWorstHalf() {
		/* Put top half of population in gene can. */
		addToGeneCan(getBottomHalfPopulation(population));		
		/* Remove worst half of population. */
		population = getTopHalfPopulation(population);
	}
	
	/**
	 * If stagnating NUMBER_OF_STAGNATIONS times, inject new blood.
	 */
	public void injectBlood() {
		for (PolygonImage chromo : population)
			chromo.setNewPolyCount(chromo.getPolyCount() + maxIterations);
		for (PolygonImage chromo : geneCan)
			chromo.setNewPolyCount(chromo.getPolyCount() + maxIterations);
		
		/* Put top half of population in gene can. */
		addToGeneCan(getBottomHalfPopulation(population));
		
		/* Remove worst half of population. */
		population = getTopHalfPopulation(population);

		/* Replace removed worse half with new blood. */
		int addNumber = Cons.POPULATION_SIZE - population.size();
		for (int blood = 0; blood < addNumber; blood++) {
			PolygonImage polyImage = new PolygonImage(population.get(blood));
			polyImage.generateRandom();
			population.add(new PolygonImage(polyImage));
		}
		
		System.out.println("Stagnating: " + stagnating +
				". Removing worst half, replacing by injecting new blood." +
				". Adding " + maxIterations + " new polys.");
	}
	
	public void addToGeneCan(PolygonImage chromosome) {
		chromosome.resetAge();
		geneCan.add(chromosome);
	}
	
	public void addToGeneCan(ArrayList<PolygonImage> chromosomes) {
		for (PolygonImage chromosome : chromosomes) {
			chromosome.resetAge();
			geneCan.add(chromosome);
		}
	}
	
	/**
	 * Updates age of all chromosomes in geneCan.
	 * If age == 0, remove chromosome.
	 */
	public void updateGeneCan() {
		if(!geneCan.isEmpty()) {
			Iterator<PolygonImage> iter = geneCan.iterator();
			PolygonImage chromosome;
			while (iter.hasNext()) {
				chromosome = iter.next();
				chromosome.decrAge();
			    if(chromosome.isZeroAge())
			        iter.remove();
			}
		}
	}
	
	public boolean isGeneCanEmpty() {
		return geneCan.isEmpty();
	}
	
	public PolygonImage getCurrentBestImage() {
		return currentBestImage;
	}
	
	public PolygonImage getCurrentImage() {
		return currentImage;
	}

	public void setCurrentBestImage(PolygonImage currentBestImage) {
		this.currentBestImage = new PolygonImage(currentBestImage);
		/* Set currentimage as first image in sorted population. */
		currentImage = this.currentBestImage;
	}
	
	public PolygonImage[] getPopulation() {
		PolygonImage[] array = new PolygonImage[population.size()];
		return (PolygonImage[])population.toArray(array);
	}
	
	public void setPopulation(PolygonImage[] population) {
		if(!this.population.isEmpty())
			this.population.clear();
		Collections.addAll(this.population, population);
	}
	
	/**
	 * Returns the top half of the given list of PolygonImages.
	 * The list returned thus has half the size of the parameter (input) list. 
	 * @param population
	 * @return top half of population list
	 */
	public ArrayList<PolygonImage> getTopHalfPopulation(ArrayList<PolygonImage> population) {
		int size = population.size();
		ArrayList<PolygonImage> list = new ArrayList<PolygonImage>();
		/* Add the best half of population. */
		for (int p = 0; p < (size >> 1); p++)
			list.add(population.get(p));
		return list;
	}
	
	/**
	 * Returns the bottom half of the given list of PolygonImages.
	 * The list returned thus has half the size of the parameter (input) list. 
	 * @param population
	 * @return bottom half of population list
	 */
	public ArrayList<PolygonImage> getBottomHalfPopulation(ArrayList<PolygonImage> population) {
		int size = population.size();
		ArrayList<PolygonImage> list = new ArrayList<PolygonImage>();
		/* Add the worse half of population. */
		for (int p = size - 1; p > (size >> 1)-1; p--)
			list.add(population.get(p));
		return list;
	}
	
	public PolygonImage[] recalculatePopulationFitness(PolygonImage[] population) {
		PolygonImage[] newPop = new PolygonImage[population.length];
		System.arraycopy(population, 0, newPop, 0, population.length);
		for (int i = 0; i < population.length; i++)
			newPop[i].calculateFitness();
		return newPop;
	}
	
	public static ArrayList<PolygonImage> recalculatePopulationFitness(ArrayList<PolygonImage> population) {
		ArrayList<PolygonImage> newPop = new ArrayList<PolygonImage>();
		for (PolygonImage polyImage : population) {
			newPop.add(polyImage);
			newPop.get(newPop.size()-1).calculateFitness();
		}
		return newPop;
	}
}
