package com.msg.geneticimage.algorithm;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import com.msg.geneticimage.gfx.PolygonImage;
import com.msg.geneticimage.gfx.Polygon;
import com.msg.geneticimage.interfaces.Constants;
import com.msg.geneticimage.main.NanoTimer;

/* TODO:
 * 
 */

public class GeneticAlgorithm extends Algorithm<PolygonImage[]> implements Constants {
	
	private int popSize;
	private BufferedImage compareImage;
	private PolygonImage processingImage;
	private NanoTimer nanoTimer = new NanoTimer();
	private float maxFitnessRatio;
	ArrayList<PolygonImage> population;
	
	public GeneticAlgorithm(BufferedImage compareImage) {
		popSize = POPULATION_SIZE;
		this.compareImage = compareImage;
		processingImage = new PolygonImage(compareImage.getWidth(), compareImage.getHeight());
		maxFitnessRatio = 1.0f;
	}

	/**
	 * Genetic algorithm. Optimizes a given population array of PolygonImages
	 * of size POPULATION_SIZE and returns the optimized array.
	 * 
	 * @param PolygonImage[]
	 * @return new PolygonImage[]
	 */
	@Override
	public PolygonImage[] process(PolygonImage[] inputPop) {
		
		/* Create PolygonImage list from population array parameter. */
		population = new ArrayList<PolygonImage>();
		Collections.addAll(population, inputPop);
		
		boolean[] usedPopulation = new boolean[popSize << 1];
		int iterations = 0;
		long startFitness;
		float diffRatio;
		Random random = new Random();
		PolygonImage bestChromosome;
		ParentChoice parentChoice;
		/* Create a final initial chromosome with max fitness score, for initiation purposes. */
		final PolygonImage initialChromosome =
				new PolygonImage(inputPop[0].getWidth(), inputPop[0].getHeight());
		
		PolygonImage[] parent = new PolygonImage[2];
//		PolygonImage newPolygonImage = new PolygonImage(inputPop[0]);
				
		/* Initialize current best chromosome. */
		bestChromosome = new PolygonImage(initialChromosome);
				
//		/* Sort population by fitness. */
//		Collections.sort(population);
		
		startFitness = population.get(0).getFitness();
				
		System.out.println("\nGenetic algorithm. Population size: " + popSize + 
				". Image dimensions: " + compareImage.getWidth() + " x " + compareImage.getHeight());
				
		diffRatio = 1.0f;
		
		/* --------==<< Main algorithm loop. >>==-------- 
		 */
		while (diffRatio > maxFitnessRatio) {

			if(iterations % 200 == 0)
				System.out.println("Ratio: " + diffRatio);
			
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
					byte pos1 = (byte)random.nextInt(maxIterations);
					byte pos2 = (byte)(random.nextInt(maxIterations - pos1) + pos1);
					for (byte child = 0; child < 2; child++) {
						for (byte b = (byte)pos1; b < pos2; b++)
							createdChildren[child].setPolygon(b, parent[1 - child].getPolygon(b));
						createdChildren[child].setFitness(PolygonImage.getFitness(createdChildren[child].getImage(), compareImage));
					}
				}
					
				/* Mutate children if ratio permits it.
				 * Random number of mutations per child up to MAX_MUTATIONS percentage
				 * of POLYGON_COUNT. 
				 */
				Polygon poly;
				if(random.nextFloat() < MUTATION_RATIO) {
					for (byte c = 0; c < 2; c++) {
						byte nbr = (byte)(((random.nextDouble() * MAX_MUTATIONS) * maxIterations) + 1);
						for (int n = 0; n < nbr; n++) {
							byte pos = (byte)random.nextInt(maxIterations);
							poly = new Polygon();
							poly.createRandomPolar(inputPop[0].getWidth() * inputPop[0].getHeight(), maxIterations);
							createdChildren[c].setPolygon(pos, poly);
						}
						createdChildren[c].setFitness(PolygonImage.getFitness(createdChildren[c].getImage(), compareImage));
					}
				}
				
				/* Copy the children to bottom half of population array. */
				for (byte c = 0; c < 2; c++)
					population.add(new PolygonImage(createdChildren[c]));
			}
						
			/* Check if current fitness is the better one. */
			for (byte n = 0; n < population.size(); n++)
				if(population.get(n).getFitness() < bestChromosome.getFitness()) {
					bestChromosome = new PolygonImage(population.get(n));
					setProcessingImage(bestChromosome);
				}
			
			/* Sort newPopulation by fitness. */
			Collections.sort(population);
			
			/* Remove the worse half of population. */
			for (byte p = (byte)(population.size() - 1); p > (byte)(popSize - 1); p--)
				population.remove(p);
			
			nanoTimer.stopTimer();
			/* Print every 5 generations. */
			if(iterations % 200 == 0)
				nanoTimer.printElapsedTime(" Gen #: " + iterations + " Best fitness: " + bestChromosome.getFitness());
			
			iterations++;
			diffRatio = bestChromosome.getFitness() / (float)startFitness;
		}
		
		PolygonImage[] array = new PolygonImage[population.size()];
		return (PolygonImage[])population.toArray(array);
	}
	
	public PolygonImage getProcessingImage() {
		return processingImage;
	}

	public void setProcessingImage(PolygonImage processingImage) {
		this.processingImage = new PolygonImage(processingImage);
	}
	
	public PolygonImage[] getPopulation() {
		PolygonImage[] array = new PolygonImage[population.size()];
		return (PolygonImage[])population.toArray(array);
	}
	
	public void setPopulation(PolygonImage[] population) {
		this.population.clear();
		Collections.addAll(this.population, population);
	}

	public int getPopSize() {
		return popSize;
	}

	public void setPopSize(int popSize) {
		this.popSize = popSize;
	}
	
	public void setMaxFitnessRatio(float maxFitnessRatio) {
		this.maxFitnessRatio = maxFitnessRatio;
	}
	
	// DEBUG
	public void printArray(ArrayList<PolygonImage> list) {
		for (byte i = 0; i < list.size(); i++)
			System.out.println(i + ": " + list.get(i).getFitness());
		System.out.println();
	}
}
