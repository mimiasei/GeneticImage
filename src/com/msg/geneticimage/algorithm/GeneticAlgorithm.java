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
		
		/* The createImage array for both parents and children, of length popSize * 2. */
		CreatePolygonImage[] populationLarge = new CreatePolygonImage[popSize << 1];
		
		boolean[] usedPop = new boolean[popSize << 1];
		int iterations = 0;
		Random random = new Random();
		CreatePolygonImage bestChromosome;
		
		/* Create a final initial chromosome with max fitness score, for initiation purposes. */
		final CreatePolygonImage initialChromosome =
				new CreatePolygonImage(createImage.getWidth(), createImage.getHeight());
		
		CreatePolygonImage[] parent = new CreatePolygonImage[2];
				
		/* Initialize current best chromosome. */
		bestChromosome = new CreatePolygonImage(initialChromosome);
		
		Algorithm<CreatePolygonImage> randomAlg = new RandomAlgorithm();		
		/* Create images based on POLYGON_COUNT random polygons. */
		randomAlg.setMaxIterations(POLYGON_COUNT);
		
		/* Initialize populationLarge array. */
		Arrays.fill(populationLarge, new CreatePolygonImage(initialChromosome));
		
		/* Create popSize number of random images as chromosomes. */
		for (int i = 0; i < popSize; i++) {
			createImage = randomAlg.process(createImage);
			populationLarge[i] = createImage;
			populationLarge[i].setFitness(getFitness(createImage.getImage()));
		}
		
		/* Sort populationLarge by fitness. */
		Arrays.sort(populationLarge);
		
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
			
//			// DEBUG
//			for (int i = 0; i < popSize; i++)
//				System.out.println("Sorted populationLarge #" + i + " fitness: " + populationLarge[i].getFitness());
			
			/* Select mates by fitness. 
			 * Cross them and get two children using cross-over.
			 * Mutate children.
			 * Remove half of chromosomes by lowest fitness.
			 */
			int better = 0, worse = 0, counter = 0;		
			CreatePolygonImage[] createdChildren;
			
			/* Go through chromosomes via loop of half the size of popSize,
			 * since two parents are crossed each loop.
			 * 
			 * Selecting one parent from the top half fitness scores and the
			 * other parent from the bottom half.
			 */
			for (int i = 0; i < (popSize >> 1); i++) {			
				switch (PARENT_SELECTION) {
					case RND_TWO:			for (int b = 0; b < 2; b++) {
												do
													better = random.nextInt(popSize);
												while (usedPop[better]);
												parent[b] = new CreatePolygonImage(populationLarge[better]);
												usedPop[better] = true;
											}
											break;
					case RND_BEST_WORST:	for (int b = 0; b < 2; b++) {
												do
													better = random.nextInt((popSize >> 1)) + b * (popSize >> 1);
												while (usedPop[better]);
												parent[b] = new CreatePolygonImage(populationLarge[better]);
												usedPop[better] = true;
											}
											break;
					case TOP_TWO_BEST:		int sel = 0;
											boolean foundParent;
											for (int b = 0; b < 2; b++) {
												foundParent = false;
												do {
													better = sel;
													if(!usedPop[sel])
														foundParent = true;
													else
														sel++;
												} while (!foundParent && sel < popSize);
												if(sel < popSize) {
													parent[b] = new CreatePolygonImage(populationLarge[better]);
													usedPop[sel] = true;
												}
											}	
											break;
				}
				
				createdChildren = new CreatePolygonImage[2];
				
				/* Create the two children. */
				for (int c = 0; c < 2; c++)	{			
					/* Create totally random child if ratio permits it. */
					if(random.nextDouble() < RANDOMCHILD_RATIO)
					{
						System.out.print("R");
						createdChildren[c] = new CreatePolygonImage(randomAlg.process(createImage));
					}
					else
						/* Create child as copy of its parent. */
						createdChildren[c] = new CreatePolygonImage(parent[c]);
				}
				
				/* Do two-point cross-over if ratio permits it. */
				if(random.nextDouble() < CROSSOVER_RATIO) {				
					int pos1 = random.nextInt(POLYGON_COUNT >> 1);
					int pos2 = random.nextInt(POLYGON_COUNT >> 1) + pos1;
					for (int child = 0; child < 2; child++)
						for (int b = pos1; b < pos2; b++)
							createdChildren[child].setPolygon(b, parent[1 - child].getPolygon(b));
				}
					
				/* Mutate children if ratio permits it.
				 * Random number of mutations per child up to MAX_MUTATIONS percentage
				 * of POLYGON_COUNT. */
				Polygon poly;
				if(random.nextDouble() < MUTATION_RATIO) {
					for (int c = 0; c < 2; c++) {
						int nbr = (int)((random.nextDouble() * MAX_MUTATIONS) * POLYGON_COUNT) + 1;
						for (int n = 0; n < nbr; n++) {
							int pos = random.nextInt(POLYGON_COUNT);
							poly = new Polygon();
							poly.createRandomPolar(createImage.getWidth(), createImage.getHeight());
							createdChildren[c].setPolygon(pos, poly);
						}
					}
				}
				
				// Copy the children to bottom half of population array. */
				for (int c = 0; c < 2; c++)
					populationLarge[(counter + popSize) + c] = new CreatePolygonImage(createdChildren[c]);
				
				// DEBUG
				printArray(populationLarge);
				
				counter += 2;				
			}
						
			/* Check if current fitness is the better one. */
			for (int n = 0; n < populationLarge.length; n++)
				if(populationLarge[n].getFitness() < bestChromosome.getFitness()) {
					bestChromosome = new CreatePolygonImage(populationLarge[n]);
					setProcessingImage(bestChromosome);
				}
			
			/* Sort newPopulation by fitness. */
			Arrays.sort(populationLarge);
			
			/* Set the worst half of the population array to initiation chromosome,
			 * rendering them empty with max fitness score.
			 */
			for (int p = popSize; p < populationLarge.length; p++)
				populationLarge[p] = new CreatePolygonImage(initialChromosome);
			
			nanoTimer.stopTimer();
			/* Print every 5 generations. */
			if(iterations % 5 == 0)
				nanoTimer.printElapsedTime("   Best fitness: " + bestChromosome.getFitness());
			
			iterations++;
		}
				
		return bestChromosome;
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
	
	// DEBUG
	public void printArray(CreatePolygonImage[] array) {
		for (int i = 0; i < array.length; i++)
			System.out.println(i + ": " + array[i].getFitness());
		System.out.println();
	}
}
