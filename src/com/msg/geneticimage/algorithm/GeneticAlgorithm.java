package com.msg.geneticimage.algorithm;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;

import com.msg.geneticimage.gfx.CreatePolygonImage;
import com.msg.geneticimage.gfx.Polygon;
import com.msg.geneticimage.interfaces.Constants;
import com.msg.geneticimage.main.NanoTimer;

/* TODO:
 * 
 * Switch from using Chromosome class for sorting by fitness, to using
 * the actual CreatePolygonImage class itself and its fitness field.
 * The fitness field and compareTo are already implemented.
 * In other words, make the population array a CreatePolygonImage array,
 * and remove all usage of Chromosome (e.g. the newPopulation array).
 * 
 * Perhaps let the population array be twice the length of popSize?
 * It'd probably make everything easier.
 * 
 */

public class GeneticAlgorithm extends Algorithm<CreatePolygonImage> implements Constants {
	
	private int popSize;
	private BufferedImage compareImage;
	private CreatePolygonImage processingImage;
	private NanoTimer nanoTimer = new NanoTimer();
	
	public GeneticAlgorithm(BufferedImage compareImage) {
		popSize = POPULATION_SIZE;
		this.compareImage = compareImage;
		processingImage = new CreatePolygonImage(compareImage.getWidth(), compareImage.getHeight());
	}

	@Override
	public CreatePolygonImage process(CreatePolygonImage createImage) {
		
		/* The createImage array for initial parents of length popSize. */
		CreatePolygonImage[] createImageInitPop = new CreatePolygonImage[popSize];
		/* The createImage array for both parents and children of length popSize * 2. */
		CreatePolygonImage[] createImagePop = new CreatePolygonImage[popSize << 1];
		
		Chromosome[] population = new Chromosome[popSize];
		boolean[] usedPop = new boolean[popSize];
		int iterations = 0;
		Random random = new Random();
		Chromosome Chromosome, bestChromosome;
		Chromosome[] parent = new Chromosome[2];
		
		bestChromosome = new Chromosome(0);
		bestChromosome.fitness = Long.MAX_VALUE;
		
		Algorithm<CreatePolygonImage> randomAlg = new RandomAlgorithm();		
		/* Create images based on POLYGON_COUNT random polygons. */
		randomAlg.setMaxIterations(POLYGON_COUNT);
		
		/* Create popSize number of random images as chromosomes. */
		for (int i = 0; i < popSize; i++) {
			createImage = randomAlg.process(createImage);
			createImageInitPop[i] = createImage;
			Chromosome = new Chromosome(i);
			Chromosome.fitness = getFitness(createImage.getImage());
			population[i] = Chromosome;
		}
		
		System.out.println("\nGenetic algorithm. Population size: " + popSize + 
				". Image dimensions: " + compareImage.getWidth() + " x " + compareImage.getHeight());
		
		System.out.print("\nGeneration: ");
		
		/* 
		 * --------==<< Main algorithm loop. >>==-------- 
		 * 
		 */
		while (iterations < maxIterations) {	

			System.out.print(iterations + " ");
			
			nanoTimer.startTimer();
			
			/* Initialize usedPop array. */
			Arrays.fill(usedPop, false);
			
			/* Sort by fitness. */
			Arrays.sort(population);
			
			/* Select mates by fitness. 
			 * Cross them and get two children using cross-over.
			 * Mutate children.
			 * Remove half of chromosomes by lowest fitness.
			 */
			int better = 0, worse = 0, counter = 0;
			Chromosome[] newPopulation = new Chromosome[popSize << 1];		
			CreatePolygonImage[] createdChildren;
			
			/* Go through chromosomes via loop of half the size of popSize,
			 * since two parents are crossed each loop.
			 * 
			 * Selecting one parent from the top half fitness scores and the
			 * other parent from the bottom half.
			 */
			
			for (int i = 0; i < (popSize >> 1); i++) {
				
				switch (PARENT_SELECTION) {
					case RND_BEST:			for (int b = 0; b < 2; b++) {
												do
													better = random.nextInt((popSize >> 1));
												while (usedPop[better]);
												parent[b] = population[better];
												usedPop[better] = true;
											}
											break;
					case RND_BEST_WORST:	for (int b = 0; b < 2; b++) {
												do
													better = random.nextInt((popSize >> 1)) + b * (popSize >> 1);
												while (usedPop[better]);
												parent[b] = population[better];
												usedPop[better] = true;
											}
											break;
					case TOP_TWO_BEST:		int sel = 0;
											for (int b = 0; b < 2; b++) {
												doLoop1: do {
													better = sel;
													if(!usedPop[sel]) {
														usedPop[sel] = true;
														sel++;
														break doLoop1;
													} else
														sel++;
												} while (usedPop[better] && sel < (popSize >> 1));
												parent[b] = population[better];
											}	
											break;
					case TOP_BEST_WORST:	sel = (popSize >> 1);
											for (int b = 0; b < 2; b++) {
												doLoop2: do {
													worse = sel;
													if(!usedPop[sel]) {
														usedPop[sel] = true;
														sel++;
														break doLoop2;
													} else
														sel++;
												} while (usedPop[worse] && sel < popSize);	
												parent[b] = population[worse];
											}	
											break;
					case RND_WORST:			for (int b = 0; b < 2; b++) {
												do
													worse = random.nextInt((popSize >> 1)) + (popSize >> 1);
												while (usedPop[worse]);
												usedPop[worse] = true;
											}
				}
				
				createdChildren = new CreatePolygonImage[2];
				int nbrOfPolys = createImageInitPop[0].getNumberOfPolygons();
				
				/* Create the two children. */
				for (int c = 0; c < 2; c++)				
					/* Create totally random child if ratio permits it. */
					if(random.nextDouble() < RANDOMCHILD_RATIO)
					{
						System.out.print("R");
						createdChildren[c] = new CreatePolygonImage(randomAlg.process(createImage));
					}
					else
						/* Create child as copy of its parent. */
						createdChildren[c] = new CreatePolygonImage(createImageInitPop[parent[c].index]);
				
				/* Do two-point cross-over if ratio permits it. */
				if(random.nextDouble() < CROSSOVER_RATIO) {				
					int pos1 = random.nextInt(nbrOfPolys);
					int pos2 = random.nextInt(nbrOfPolys - pos1) + pos1;
					for (int child = 0; child < 2; child++) {
						for (int a = 0; a < pos1; a++)
							createdChildren[child].setPolygon(a, createImageInitPop[parent[child].index].getPolygon(a)); 
						for (int b = pos1; b < pos2; b++)
							createdChildren[child].setPolygon(b, createImageInitPop[parent[1 - child].index].getPolygon(b));
						for (int c = pos2; c < nbrOfPolys; c++)
							createdChildren[child].setPolygon(c, createImageInitPop[parent[child].index].getPolygon(c));
					}
				}
					
				/* Mutate children if ratio permits it.
				 * Random number of mutations per child up to MAX_MUTATIONS percentage
				 * of POLYGON_COUNT. */
				Polygon poly;
				if(random.nextDouble() < MUTATION_RATIO) {
					for (int c = 0; c < 2; c++) {
						int nbr = (int)((random.nextDouble() * MAX_MUTATIONS) * nbrOfPolys) + 1;
						for (int n = 0; n < nbr; n++) {
							int pos = random.nextInt(nbrOfPolys);
							poly = new Polygon();
							poly.createRandomPolar(createImage.getWidth(), createImage.getHeight());
							createdChildren[c].setPolygon(pos, poly);
						}
					}
				}
				
				createImagePop[counter + popSize] = createdChildren[0];
				createImagePop[counter + 1 + popSize] = createdChildren[1];
				
				counter += 2;				
			}
						
			int newBest;
			/* Transfer all fitness scores to newPopulation array. */
			for (int n = 0; n < popSize; n++) {
				newBest = -1;
				/* Add the parents. */
				newPopulation[n] = new Chromosome(n);
				newPopulation[n].fitness = getFitness(createImageInitPop[n].getImage());
				/* Add the children. */
				newPopulation[n + popSize] = new Chromosome(n + popSize);
				newPopulation[n + popSize].fitness = getFitness(createImagePop[n + popSize].getImage());

				
				/* Check if current fitness is the better one. */
				if(newPopulation[n].fitness < bestChromosome.fitness)
					newBest = 0;
				else if(newPopulation[n + popSize].fitness < bestChromosome.fitness)
					newBest = 1;
				if(newBest >= 0) {
					bestChromosome = new Chromosome(n);
					bestChromosome.fitness = newPopulation[n + newBest * popSize].fitness;
					setProcessingImage(createImagePop[bestChromosome.index]);
				}			
			}
			
			/* Sort newPopulation by fitness. */
			Arrays.sort(newPopulation);
			
			// DEBUG
			for (int i = 0; i < population.length; i++)
				System.out.println("Sorted newpopulation #" + i + " index: " + population[i].index +
						"   fitness: " + population[i].fitness);
			System.out.println("Current best fitness set to: " + bestChromosome.fitness); 
			
			/* Empty createImageInitPop array.*/
			Arrays.fill(createImageInitPop, null);
			
			/* Transfer the best half of newPopulation to the population array,
			 * and copy the best createImages from the createImagePop to the
			 * createImageInitPop array, becoming the population with the top
			 * popSize fitness scores.
			 */
			for (int p = 0; p < population.length; p++) {
				population[p] = newPopulation[p];
				createImageInitPop[p] = new CreatePolygonImage(createImagePop[population[p].index]);
			}
			
			nanoTimer.stopTimer();
			/* Print every 5 generations. */
			if(iterations % 5 == 0)
				nanoTimer.printElapsedTime("   Best fitness: " + bestChromosome.fitness);
			
			iterations++;
		}
				
		return createImageInitPop[bestChromosome.index];
	}
	
	public CreatePolygonImage getProcessingImage() {
		return processingImage;
	}

	public void setProcessingImage(CreatePolygonImage processingImage) {
		this.processingImage = new CreatePolygonImage(processingImage);
	}

	public long getFitness(BufferedImage image) {
		long fitness = 0;
		for (int y = 0; y < image.getHeight(); y++)
			for (int x = 0; x < image.getWidth(); x++) {
				Color c1 = new Color(image.getRGB(x, y));
				Color c2 = new Color(compareImage.getRGB(x, y));
		
				/* Get delta per colour. */
				int deltaRed = c1.getRed() - c2.getRed();
				int deltaGreen = c1.getGreen() - c2.getGreen();
				int deltaBlue = c1.getBlue() - c2.getBlue();
		
				/* Measure the distance between the colours in 3D space. */
				long pixelFitness = 
						deltaRed*deltaRed + deltaGreen*deltaGreen + deltaBlue*deltaBlue;
		 
		        /* Add the pixel fitness to the total fitness (lower is better). */
				fitness += pixelFitness;
			}
		 
		return fitness;
	}

	public int getPopSize() {
		return popSize;
	}

	public void setPopSize(int popSize) {
		this.popSize = popSize;
	}
	
	private class Chromosome implements Comparable<Chromosome> {
		int index;
		long fitness;
		 
		public Chromosome(int index) {
			this.index = index;
			this.fitness = Long.MAX_VALUE;
		}

		@Override
		public int compareTo(Chromosome o) {
	        if(fitness > o.fitness) return 1;
	        if(fitness < o.fitness) return -1;
			return 0;
		}
	}
}
