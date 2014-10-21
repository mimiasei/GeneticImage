package com.msg.geneticimage.algorithm;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import com.msg.geneticimage.gfx.PolygonImage;
import com.msg.geneticimage.gfx.Polygon;
import com.msg.geneticimage.interfaces.Constants;
import com.msg.geneticimage.main.GeneticImage;
import com.msg.geneticimage.main.NanoTimer;

/* TODO:
 * 
 */

public class GeneticAlgorithm extends Algorithm<PolygonImage[]> implements Constants {
	
	private int popSize;
	private long previousBestFitness;
	private PolygonImage currentBestImage;
	private GeneticImage geneticImage;
	private NanoTimer nanoTimer = new NanoTimer();
	private ArrayList<PolygonImage> population;
//	private float maxFitnessRatio;
	
	public GeneticAlgorithm(GeneticImage geneticImage) {
		this.geneticImage = geneticImage;
		popSize = POPULATION_SIZE;
		currentBestImage = new PolygonImage(this.geneticImage, maxIterations);
		population = new ArrayList<PolygonImage>();
//		maxFitnessRatio = 1.0f;
	}

	/**
	 * Genetic algorithm. Optimizes a given population array of PolygonImages
	 * of size POPULATION_SIZE and returns the optimized array.
	 * 
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
		boolean[] usedPopulation = new boolean[popSize << 1];
		int iterations = 0, stagnating = 0;
		long startFitness;
		Random random = new Random(System.nanoTime());
		ParentChoice parentChoice;
		/* Create a final initial chromosome with max fitness score, for initiation purposes. */
		final PolygonImage initialChromosome = new PolygonImage(geneticImage, maxIterations);
		
		PolygonImage[] parent = new PolygonImage[2];
				
		/* Initialize current best chromosome. */
		currentBestImage = new PolygonImage(initialChromosome);
		
		startFitness = population.get(0).getFitness();
		previousBestFitness = startFitness;
		int minFitnessDiff = (int)(startFitness * MIN_FITNESS_DIFF_RATIO);
						
		System.out.println("\nGenetic algorithm. Pop. size: " + popSize + ". Polygon count: " +
				maxIterations + ". Image dims: " + compareImage.getWidth() + " x " + compareImage.getHeight() +
				". BitShift: " + geneticImage.getBitShift() + ". Minimum fitnessDiff: " + minFitnessDiff);
		
		/* 
		 * - -----------~~~=====<< Main algorithm loop. >>=====~~~----------- - 
		 */
		
		while (stagnating < NUMBER_OF_GENERATIONS) {
			
//			if(iterations % 50 == 0)
//				findMinMaxOrigoDistance();
			
			random = new Random(System.nanoTime());
			
			nanoTimer.startTimer();
			
			/* Initialize usedPop array. */
			Arrays.fill(usedPopulation, false);
			
			/* Select mates by fitness. 
			 * Cross them and get two children using cross-over.
			 * Mutate children.
			 * Remove half of chromosomes by lowest fitness.
			 */
			byte better = 0;		
			PolygonImage[] createdChildren;
			
			/* Go through chromosomes via loop of half the size of popSize,
			 * since two parents are crossed each loop.
			 * Randomly choose one of three types of parent selection.
			 */			
			parentChoice = ParentChoice.values()[random.nextInt(ParentChoice.values().length)];
//			parentChoice = ParentChoice.TOP_TWO_BEST;
			for (byte i = 0; i < popSize; i += 2) {			
				switch (parentChoice) {
					case RND_TWO:			for (byte b = 0; b < 2; b++) {
												do
													better = (byte)random.nextInt(popSize);
												while (usedPopulation[better]);
												parent[b] = new PolygonImage(population.get(better));
												usedPopulation[better] = true;
											}
											break;
					case RND_BEST_WORST:	for (byte b = 0; b < 2; b++) {
												do
													better = (byte)(random.nextInt((popSize >> 1)) + b * (popSize >> 1));
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
												} while (!foundParent && sel < popSize);
												if(sel < popSize) {
													parent[b] = new PolygonImage(population.get(better));
													usedPopulation[sel] = true;
												}
											}	
											break;
				}
								
				createdChildren = new PolygonImage[2];
				
				/* Create the two children. */
				for (byte c = 0; c < 2; c++)	{			
					/* Create child as copy of its parent. */
					createdChildren[c] = new PolygonImage(parent[c]);
				}
				
				/* Do two-point cross-over if ratio permits it. */
				if(random.nextFloat() < CROSSOVER_RATIO) {
					int pos1 = random.nextInt(maxIterations);
					int pos2 = (random.nextInt(maxIterations - pos1) + pos1);
					for (byte child = 0; child < 2; child++) {
						for (int b = pos1; b < pos2; b++)
							createdChildren[child].setPolygon(b, parent[1 - child].getPolygon(b));
						createdChildren[child].calculateFitness();						
					}
				}
					
				/* Mutate children if ratio permits it.
				 * Random number of mutations per child up to MAX_MUTATIONS percentage
				 * of POLYGON_COUNT. 
				 */
				Polygon polygon;
				if(random.nextFloat() < MUTATION_RATIO) {
					for (byte c = 0; c < 2; c++) {
						int nbr = (int)((random.nextFloat() * MAX_MUTATIONS) * maxIterations) + 1;
						for (int n = 0; n < nbr; n++) {
							int pos = random.nextInt(maxIterations);
							if(random.nextBoolean()) {
								/* Generate random polygon. */
								polygon = new Polygon(inputPopulation[0]);
								createdChildren[c].setPolygon(pos, polygon);
							} else {
								/* Mutate polygon. */
								if(createdChildren[c].getPolygon(pos) != null) {
									polygon = new Polygon(createdChildren[c].getPolygon(pos));
									polygon.mutate();
									createdChildren[c].setPolygon(pos, polygon);
								}
							}
						}
						createdChildren[c].calculateFitness();
					}
					
				}
				
				/* Copy the children to bottom half of population array. */
				for (byte c = 0; c < 2; c++)
					population.add(new PolygonImage(createdChildren[c]));
			}
			
			previousBestFitness = this.currentBestImage.getFitness();
			
			/* Check if current fitness is the better one. */
			for (byte n = 0; n < population.size(); n++)
				if(population.get(n).getFitness() < currentBestImage.getFitness()) {
					setCurrentBestImage(population.get(n));
				}
			
			/* Sort newPopulation by fitness. */
			Collections.sort(population);
			
			/* Remove the worse half of population. */
			for (byte p = (byte)(population.size() - 1); p > (byte)(popSize - 1); p--)
				population.remove(p);
			
			nanoTimer.stopTimer();
			
			/* If current best fitness is not more than 0.1 percent better than previous best,
			 * increment iterations.
			 */
			iterations++;
			if((previousBestFitness - currentBestImage.getFitness()) < minFitnessDiff)
				stagnating++;
			else
				stagnating = 0;
			
			/* Print every 200 generations. */
			if(iterations % 100 == 0)
				nanoTimer.printElapsedTime(" Gen #: " + iterations + ". Stagnating: " + stagnating +
						". Best fitness: " + currentBestImage.getFitness());
		}
		
		PolygonImage[] array = new PolygonImage[population.size()];
		return (PolygonImage[])population.toArray(array);
	}
	
	private void findMinMaxOrigoDistance() {
		Polygon poly;
		int min = Integer.MAX_VALUE;
		int max = 0;
		int dist = 0;
		int distSum = 0;
		int counter = 0;
		for (PolygonImage polyImg : population) {
			for (int i = 0; i < polyImg.getNumberOfPolygons(); i++ ) {
				poly = polyImg.getPolygon(i);
				for (int v = 0; v < poly.getVertexLength(); v++) {
					counter++;
					dist = Polygon.getDistance(poly.getOrigo(), poly.getVertex(v).getXY());
					distSum += dist;
					if(dist < min)
						min = dist;
					else if(dist > max)
						max = dist;
				}
			}
			
		}
		System.out.println("Min radius: " + min + ". Max radius: " + 
				max + ". Average: " + (int)(distSum / (float)counter));

	}

	/* 
	 * ------------------------<<<<< End main algorithm >>>>>------------------------- 
	 */
	
	public PolygonImage getCurrentBestImage() {
		return currentBestImage;
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
	
	public PolygonImage[] recalculatePopulationFitness(PolygonImage[] population) {
		PolygonImage[] newPop = new PolygonImage[population.length];
		System.arraycopy(population, 0, newPop, 0, population.length);
		for (int i = 0; i < population.length; i++)
			newPop[i].calculateFitness();
		return newPop;
	}

	public int getPopSize() {
		return popSize;
	}

	public void setPopSize(int popSize) {
		this.popSize = popSize;
	}
}
