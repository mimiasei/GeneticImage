package com.msg.geneticimage.interfaces;

public interface Constants {
	
	// Main class:
	
	/* Input image path. */
//	static final String IMAGE_PATH = "images/monalisahead1280px.png";
//	static final String IMAGE_PATH = "images/ask.jpg";
//	static final String IMAGE_PATH = "images/spiritedAway_bathroomHouse.jpg";
	static final String IMAGE_PATH = "images/miriamOgAsk_brygga.jpg";
	
	/* Millisecond frequency for how often best image is updated from algorithm thread. */ 
	static final int CHECK_FREQUENCY = 3000;
	/* Number of generations to iterate generic algorithm loop. */
	static final int NUMBER_OF_GENERATIONS = 400000;
	/* Maximum difference ratio of current and starting fitness score. (must be less than this) */
	static final float MAX_CURRENT_START_FITNESS_RATIO = 0.1f;
	/* Factor being multiplied by the power of i to POLYGON_COUNT */
	static final float POLYGON_COUNT_POWER_FACTOR = 0.92f;
	
	// Random algorithm class:
	
	static final int POLYGON_COUNT = 50;
	
	// Main genetic algorithm class:
	
	/* enums for setting type of parent selection. */
	static enum ParentChoice {
		RND_TWO, RND_BEST_WORST, TOP_TWO_BEST;

	    private final int value;

	    private ParentChoice() {
	        this.value = ordinal();
	    }
	}
	/* Percent chance of entering cross-over stage. */
	static final ParentChoice PARENT_SELECTION = ParentChoice.TOP_TWO_BEST;	
	/* Percent chance of entering cross-over stage. */
	static final float CROSSOVER_RATIO = 0.85f;
	/* Percent chance of entering mutation stage. */
	static final float MUTATION_RATIO = 0.1f;
	/* Max percentage of POLYGON_COUNT mutations per child. */
	static final float MAX_MUTATIONS = 0.01f;
	/* Population size of chromosomes. */
	static final int POPULATION_SIZE = 50; // must be even number
	
	// Polygon class:
	
	/* How many vertices each polygon is made up of. */
	static final int POLYGON_VERTICES = 6; // must be even number
	/* Random radius factor. */
	static final float RANDOM_RADIUS = 0.5f;
	/* Factor for how much smaller length and height of polygons should be compared to image. */
	static final float POLYGON_FUZZINESS_SCALE = 2.0f;
}
