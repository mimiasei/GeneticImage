package com.msg.geneticimage.algorithm;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import com.msg.geneticimage.gfx.PolygonImage;
import com.msg.geneticimage.gfx.Polygon;
import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.main.GeneticImage;
import com.msg.geneticimage.main.NanoTimer;
import com.msg.geneticimage.main.FileHandler;

public class GeneticAlgorithm extends Algorithm<PolygonImage[]> {
	
	private long previousBestFitness;
	private PolygonImage currentBestImage, currentImage;
	private GeneticImage geneticImage;
	private ArrayList<PolygonImage> population;
	private int stagnating;
	private double mutationRatio;
	
	public GeneticAlgorithm(GeneticImage geneticImage) {
		this.geneticImage = geneticImage;
		currentBestImage = new PolygonImage(this.geneticImage, maxIterations);
		currentImage = currentBestImage;
		population = new ArrayList<PolygonImage>();
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
		
		/* If plotData then create fileHandler for saving data for plotting
		 * in GnuPlot. */
		FileHandler plotter;
		if(Cons.PLOT_DATA)
			plotter = new FileHandler(inputPopulation[0]);
		
		BufferedImage compareImage = geneticImage.getCompareImage();
		/* Empty population list. */
		population.clear();
		/* Recalculate fitness of all PolygonImages in input population. */
		inputPopulation = recalculatePopulationFitness(inputPopulation);
		/* Create PolygonImage list from population array parameter. */
		Collections.addAll(population, inputPopulation);		
		boolean[] usedPopulation = new boolean[Cons.POPULATION_SIZE << 1];
		int iterations = 0, bonusPolys = 0;
		long startFitness;
		Random random = new Random(System.nanoTime());
		Cons.ParentChoice parentChoice;
		/* Create a final initial chromosome with max fitness score, for initiation purposes. */
		final PolygonImage initialChromosome = new PolygonImage(geneticImage, maxIterations);
		
		PolygonImage[] parent = new PolygonImage[2];
		PolygonImage[] children;
		
		NanoTimer nanoTimer = new NanoTimer();
		NanoTimer nanoTimerAll = new NanoTimer();
				
		/* Initialize current best chromosome. */
		currentImage = currentBestImage = new PolygonImage(initialChromosome);
		
		startFitness = population.get(0).getFitness();
		previousBestFitness = startFitness;
		stagnating = 0;
		int minFitnessDiff = (int)(startFitness * Cons.MIN_FITNESS_DIFF_RATIO);
						
		System.out.println("\nGenetic algorithm. Pop. size: " + Cons.POPULATION_SIZE + ". Poly count: " +
				maxIterations + ". Image dims: " + compareImage.getWidth() + " x " + compareImage.getHeight() +
				". BitShift: " + geneticImage.getBitShift() + ". Min fitnessDiff: " + minFitnessDiff);
		
		nanoTimerAll.startTimer();
		
		/* 
		 * - -----------~~~=====<< Main algorithm loop. >>=====~~~----------- - 
		 */
		while (stagnating < Cons.NUMBER_OF_GENERATIONS) {
			
			random = new Random(System.nanoTime());		
			nanoTimer.startTimer();
			
			/* Select mates by fitness. 
			 * Cross them and get two children using cross-over.
			 * Mutate children.
			 * Remove half of chromosomes by lowest fitness.
			 * Go through chromosomes via loop of half the size of POPULATION_SIZE,
			 * since two parents are crossed each loop.
			 * Randomly choose one of three types of parent selection.
			 */
			
			/* Initialize usedPop array. */
			Arrays.fill(usedPopulation, false);
			
			byte better = 0;
			parentChoice = Cons.ParentChoice.values()[random.nextInt(Cons.ParentChoice.values().length)];
			for (byte i = 0; i < Cons.POPULATION_SIZE; i += 2) {			
				switch (parentChoice) {
					case RND_TWO:			for (byte b = 0; b < 2; b++) {
												do
													better = (byte)random.nextInt(Cons.POPULATION_SIZE);
												while (usedPopulation[better]);
												parent[b] = new PolygonImage(population.get(better));
												usedPopulation[better] = true;
											}
											break;
					case RND_BEST_WORST:	for (byte b = 0; b < 2; b++) {
												do
													better = (byte)(random.nextInt((Cons.POPULATION_SIZE >> 1)) + 
															b * (Cons.POPULATION_SIZE >> 1));
												while (usedPopulation[better]);
												parent[b] = new PolygonImage(population.get(better));
												usedPopulation[better] = true;
											}
											break;
					case TOP_TWO_BEST:		byte sel = 0;
											boolean foundParent;
											for (byte b = 0; b < 2; b++) {
												foundParent = false;
												do {
													better = sel;
													if(!usedPopulation[sel])
														foundParent = true;
													else
														sel++;
												} while (!foundParent && sel < Cons.POPULATION_SIZE);
												if(sel < Cons.POPULATION_SIZE) {
													parent[b] = new PolygonImage(population.get(better));
													usedPopulation[sel] = true;
												}
											}	
											break;
					case TOP_BEST_WORST:	for (byte b = 0; b < 2; b++)	
												parent[b] = new PolygonImage(population.get(b * (Cons.POPULATION_SIZE-1)));
											break;
				}
				
				children = new PolygonImage[2];
				
				/* Create the two children. */
				for (byte c = 0; c < 2; c++)	{			
					/* Create child as copy of its parent. */
					children[c] = new PolygonImage(parent[c]);
				}
				
				/* Use the smallest poly count of the two children. */
				int polyCount = Math.min(children[0].getNumberOfPolygons(), children[1].getNumberOfPolygons());

				/* Do two-point cross-over if ratio permits it. */
				if(random.nextFloat() < Cons.CROSSOVER_RATIO) {
					int pos1 = random.nextInt(polyCount);
					int pos2 = Math.min((random.nextInt(Math.max(polyCount>>1, 1)) + pos1), polyCount);
					for (int b = pos1; b < pos2; b++) {
						Polygon tmpPoly = new Polygon(children[0].getPolygon(b));
						children[0].setPolygon(b, children[1].getPolygon(b));
						children[1].setPolygon(b, tmpPoly);
					}
					children[0].calculateFitness();
					children[1].calculateFitness();
				}
					
				/* Mutate children if ratio permits it.
				 * Random number of mutations per child up to MAX_MUTATIONS percentage
				 * of POLYGON_COUNT. */
				Polygon polygon;
				if(random.nextFloat() < getMutationRatio()) {
					for (byte c = 0; c < 2; c++) {
						int nbr = (int)((random.nextFloat() * Cons.MAX_MUTATIONS) * polyCount) + 1;
						for (int n = 0; n < nbr; n++) {
							int pos = random.nextInt(polyCount);
							if(random.nextFloat() < Cons.RANDOM_NEW_RATIO) {
								/* Generate random polygon. */
								polygon = new Polygon(inputPopulation[0]);
								children[c].setPolygon(pos, polygon);
							} else {
								/* Mutate polygon. */
								if(children[c].getPolygon(pos) != null) {
									polygon = new Polygon(children[c].getPolygon(pos));
									polygon.mutate();
									children[c].setPolygon(pos, polygon);
								}
							}
						}
						/* Mutate number of polygons. */
						children[c].mutatePolyCount();		
						/* Swap order of two random polygons. */
						children[c].swapRandom();
						
						children[c].calculateFitness();
					}
					
				}
				
				/* Copy the children to bottom half of population array. */
				for (byte c = 0; c < 2; c++)
					population.add(children[c]);
			}
			
			previousBestFitness = this.currentBestImage.getFitness();
						
			/* Sort newPopulation by fitness. */
			Collections.sort(population);
			
//			System.out.println("Sorted ------------------");
//			for (PolygonImage polyImg : population)
//				System.out.println("polyimage: " + polyImg.getFitness());
			
			/* Set currentimage as first image in sorted population. */
			currentImage = population.get(0);
			
			/* Check if current fitness is the better one. */
			if(population.get(0).getFitness() < currentBestImage.getFitness())
				setCurrentBestImage(population.get(0));
			
			/* Remove the worse half of population. */
			for (int p = population.size() - 1; p > Cons.POPULATION_SIZE - 1; p--)
				population.remove(p);
			
			/* If current best fitness is not more than 0.1 percent better than previous best,
			 * increment iterations. */
			iterations++;
			minFitnessDiff = (int)(currentBestImage.getFitness() * Cons.MIN_FITNESS_DIFF_RATIO);
			if((previousBestFitness - currentBestImage.getFitness()) < minFitnessDiff)
				stagnating++;
			else {
				stagnating = 0;
				mutationRatio = Cons.MUTATION_RATIO;
				bonusPolys--;
			}
			
			/* If stagnating NUMBER_OF_STAGNATIONS times, inject new blood. */
			if(stagnating > 0 && stagnating % Cons.NUMBER_OF_STAGNATIONS == 0) {
				
				/* Burst mutation ratio. */
				mutationRatio = Math.min(0.8, mutationRatio + (random.nextFloat()*0.2) + 0.1);
				
				System.out.println("Stagnating: " + stagnating +
						". Bursting mutation ratio to: " + mutationRatio + 
						". Removing worst half, replacing by injecting new blood.");
				
				/* Remove the worse half of population. */
				population = getTopHalfPopulation(population);

				/* Replace removed worse half with new blood. */
				int addNumber = Cons.POPULATION_SIZE - population.size();
				for (int blood = 0; blood < addNumber; blood++) {
					PolygonImage polyImage = new PolygonImage(population.get(blood));
					polyImage.generateRandom();
					population.add(new PolygonImage(polyImage));
				}
			}
			
			nanoTimer.stopTimer();
			
			/* Plot best and worst graph. */
			if(Cons.PLOT_DATA)
				plotter.saveData(population, iterations);
			
			/* Print every PRINT_FREQUENCY generations. */
			double fitPercent = (double)(currentBestImage.getFitness() / (double)startFitness) * 100;
			if(iterations % Cons.PRINT_FREQUENCY == 0)
				System.out.println("Gen. time: " + nanoTimer + 
						". Total time: " + nanoTimerAll.getElapsedTime() + 
						". Gen #: " + iterations + 
						". Stagnating: " + stagnating +
						". Fitness %: " + Math.round((100.0 - fitPercent) * 100000) / 100000.0 + 
						". Poly count: " + currentBestImage.getNumberOfPolygons());
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
	
	/**
	 * Calculates chance of mutation with Cons.MUTATION_RATIO as base.
	 * The chance increases exponentially with number of stagnations,
	 * making a mutation more likely to occur when current best fitness
	 * does not improve (stagnates).
	 * @return mutationRatio
	 */
	public double getMutationRatio() {
		double ratio = stagnating / (double)Cons.NUMBER_OF_GENERATIONS;
		return mutationRatio + (ratio * stagnating);
	}
	
	public PolygonImage getCurrentBestImage() {
		return currentBestImage;
	}
	
	public PolygonImage getCurrentImage() {
		return currentImage;
	}

	public void setCurrentBestImage(PolygonImage currentBestImage) {
		this.currentBestImage = new PolygonImage(currentBestImage);
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
		for (PolygonImage polyImage : population)
			list.add(new PolygonImage(polyImage));
		/* Remove the worse half of population. */
		for (int p = size - 1; p > (size >> 1) - 1; p--)
			list.remove(p);

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
