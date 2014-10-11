package com.msg.geneticimage.interfaces;

public interface Constants {
	
	// Main class:
	
	/* Millisecond frequency for how often best image is updated from algorithm thread. */ 
	static final int CHECK_FREQUENCY = 4000;
	/* Number of generations to iterate generic algorithm loop. */
	static final int NUMBER_OF_GENERATIONS = 400000;
	
	// Random algorithm class:
	
	static final int POLYGON_COUNT = 60;
	
	// Main genetic algorithm class:
	
	/* enums for setting type of parent selection. */
	static enum ParentChoice {RND_BEST, RND_BEST_WORST, TOP_TWO_BEST, TOP_BEST_WORST, RND_WORST};
	/* Percent chance of entering cross-over stage. */
	static final ParentChoice PARENT_SELECTION = ParentChoice.TOP_TWO_BEST;	
	/* Percent chance of entering cross-over stage. */
	static final double CROSSOVER_RATIO = 0.85;
	/* Percent chance of entering mutation stage. */
	static final double RANDOMCHILD_RATIO = 0.0;
	/* Percent chance of entering mutation stage. */
	static final double MUTATION_RATIO = 0.1;
	/* Max percentage of POLYGON_COUNT mutations per child. */
	static final double MAX_MUTATIONS = 0.10;
	/* Population size of chromosomes. */
	static final int POPULATION_SIZE = 10; // must be even number
	
	// Polygon class:
	
	/* How many vertices each polygon is made up of. */
	static final int POLYGON_VERTICES = 6; // must be even number
	/* Factor for how much smaller length and height of polygons should be compared to image. */
	static final double POLYGON_MIN_SCALE = 0.1;
	static final double POLYGON_MAX_SCALE = 0.1;
}
