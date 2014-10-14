package com.msg.geneticimage.algorithm;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import com.msg.geneticimage.gfx.CreatePolygonImage;
import com.msg.geneticimage.gfx.Polygon;
import com.msg.geneticimage.interfaces.Constants;
import com.msg.geneticimage.main.NanoTimer;

/* TODO:
 * 
 */

public class GeneticAlgorithm extends Algorithm<CreatePolygonImage> implements Constants {
	
	private int popSize;
	private BufferedImage compareImage;
	private CreatePolygonImage processingImage;
	private NanoTimer nanoTimer = new NanoTimer();
	private float maxFitnessRatio;
	ArrayList<CreatePolygonImage> populationLarge;
	
	public GeneticAlgorithm(BufferedImage compareImage) {
		popSize = POPULATION_SIZE;
		this.compareImage = compareImage;
		processingImage = new CreatePolygonImage(compareImage.getWidth(), compareImage.getHeight());
		maxFitnessRatio = 1.0f;
	}

	@Override
	public CreatePolygonImage process(CreatePolygonImage createImage) {
		
		/* The createImage array for both parents and children, of length popSize * 2. */
		populationLarge = new ArrayList<CreatePolygonImage>();
		
		boolean[] usedPop = new boolean[popSize << 1];
		int iterations = 0;
		long startFitness;
		float diffRatio;
		Random random = new Random();
		CreatePolygonImage bestChromosome;
		ParentChoice parentChoice;
		/* Create a final initial chromosome with max fitness score, for initiation purposes. */
		final CreatePolygonImage initialChromosome =
				new CreatePolygonImage(createImage.getWidth(), createImage.getHeight());
		
		CreatePolygonImage[] parent = new CreatePolygonImage[2];
		CreatePolygonImage newCreateImage = new CreatePolygonImage(createImage);
				
		/* Initialize current best chromosome. */
		bestChromosome = new CreatePolygonImage(initialChromosome);
		
		/* 
		 * Random algorithm phase. Create initial population randomly.
		 */
		
		Algorithm<CreatePolygonImage> randomAlg = new RandomAlgorithm();		
		/* Create images based on POLYGON_COUNT random polygons. */
		randomAlg.setMaxIterations(maxIterations);
		
		/* Create popSize number of random images as chromosomes. */
		for (byte i = 0; i < popSize; i++) {
			newCreateImage = randomAlg.process(createImage);
			populationLarge.add(newCreateImage);
			populationLarge.get(i).setFitness(getFitness(newCreateImage.getImage(), compareImage));
		}
		
		/* 
		 * Random phase end.
		 */
				
		/* Sort populationLarge by fitness. */
		Collections.sort(populationLarge);
		
		startFitness = populationLarge.get(0).getFitness();
				
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
			Arrays.fill(usedPop, false);
			
			/* Select mates by fitness. 
			 * Cross them and get two children using cross-over.
			 * Mutate children.
			 * Remove half of chromosomes by lowest fitness.
			 */
			byte better = 0;		
			CreatePolygonImage[] createdChildren;
			
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
												while (usedPop[better]);
												parent[b] = new CreatePolygonImage(populationLarge.get(better));
												usedPop[better] = true;
											}
											break;
					case RND_BEST_WORST:	for (byte b = 0; b < 2; b++) {
												do
													better = (byte)(random.nextInt((popSize >> 1)) + b * (popSize >> 1));
												while (usedPop[better]);
												parent[b] = new CreatePolygonImage(populationLarge.get(better));
												usedPop[better] = true;
											}
											break;
					case TOP_TWO_BEST:		byte sel = 0;
											boolean foundParent;
											for (byte b = 0; b < 2; b++) {
												foundParent = false;
												do {
													better = sel;
													if(!usedPop[sel])
														foundParent = true;
													else
														sel++;
												} while (!foundParent && sel < popSize);
												if(sel < popSize) {
													parent[b] = new CreatePolygonImage(populationLarge.get(better));
													usedPop[sel] = true;
												}
											}	
											break;
				}
								
				createdChildren = new CreatePolygonImage[2];
				
				/* Create the two children. */
				for (byte c = 0; c < 2; c++)	{			
					/* Create totally random child if ratio permits it. */
					if(random.nextFloat() < RANDOMCHILD_RATIO) {
						createdChildren[c] = new CreatePolygonImage(randomAlg.process(createImage));
						createdChildren[c].setFitness(getFitness(createdChildren[c].getImage(), compareImage));
					} else
						/* Create child as copy of its parent. */
						createdChildren[c] = new CreatePolygonImage(parent[c]);
				}
				
				/* Do two-point cross-over if ratio permits it. */
				if(random.nextFloat() < CROSSOVER_RATIO) {
					byte pos1 = (byte)random.nextInt(maxIterations);
					byte pos2 = (byte)(random.nextInt(maxIterations - pos1) + pos1);
					for (byte child = 0; child < 2; child++) {
						for (byte b = (byte)pos1; b < pos2; b++)
							createdChildren[child].setPolygon(b, parent[1 - child].getPolygon(b));
						createdChildren[child].setFitness(getFitness(createdChildren[child].getImage(), compareImage));
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
							poly.createRandomPolar(createImage.getWidth(), createImage.getHeight());
							createdChildren[c].setPolygon(pos, poly);
						}
						createdChildren[c].setFitness(getFitness(createdChildren[c].getImage(), compareImage));
					}
				}
				
				/* Copy the children to bottom half of population array. */
				for (byte c = 0; c < 2; c++)
					populationLarge.add(new CreatePolygonImage(createdChildren[c]));
			}
						
			/* Check if current fitness is the better one. */
			for (byte n = 0; n < populationLarge.size(); n++)
				if(populationLarge.get(n).getFitness() < bestChromosome.getFitness()) {
					bestChromosome = new CreatePolygonImage(populationLarge.get(n));
					setProcessingImage(bestChromosome);
				}
			
			/* Sort newPopulation by fitness. */
			Collections.sort(populationLarge);
			
			/* Remove the worse half of population. */
			for (int p = populationLarge.size() - 1; p > popSize - 1; p--)
				populationLarge.remove(p);
			
			nanoTimer.stopTimer();
			/* Print every 5 generations. */
			if(iterations % 200 == 0)
				nanoTimer.printElapsedTime(" Gen #: " + iterations + " Best fitness: " + bestChromosome.getFitness());
			
			iterations++;
			diffRatio = bestChromosome.getFitness() / (float)startFitness;
		}
				
		return bestChromosome;
	}
	
	public CreatePolygonImage getProcessingImage() {
		return processingImage;
	}

	public void setProcessingImage(CreatePolygonImage processingImage) {
		this.processingImage = new CreatePolygonImage(processingImage);
	}
	
	public ArrayList<CreatePolygonImage> getPopulation() {
		return populationLarge;
	}
	
	public void setPopulation(ArrayList<CreatePolygonImage> populationLarge) {
		this.populationLarge.clear();
		Collections.addAll(this.populationLarge, (CreatePolygonImage[])populationLarge.toArray());
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
	
	public static long getFitness(BufferedImage image, BufferedImage compareImage) {
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
	
	// DEBUG
	public void printArray(ArrayList<CreatePolygonImage> list) {
		for (byte i = 0; i < list.size(); i++)
			System.out.println(i + ": " + list.get(i).getFitness());
		System.out.println();
	}
}
