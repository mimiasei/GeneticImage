package com.msg.geneticimage.interfaces;

public interface Constants {
	
	// Main class:
	
	/* Input image path. */
//	static final String IMAGE_PATH = "images/monalisahead768px.jpg";
//	static final String IMAGE_PATH = "images/ask.jpg";
//	static final String IMAGE_PATH = "images/spiritedAway_bathroomHouse.jpg";
//	static final String IMAGE_PATH = "images/miriamOgAsk_brygga.jpg";
//	static final String IMAGE_PATH = "images/islandVillage.jpg";
//	static final String IMAGE_PATH = "images/FromUpOnPoppyHill.jpg";
	static final String IMAGE_PATH = "images/simpleTest512px.png";
	
	
	/* Millisecond frequency for how often best image is updated from algorithm thread. */ 
	static final int CHECK_FREQUENCY = 50;
	/* Number of generations to iterate generic algorithm loop. */
	static final int NUMBER_OF_GENERATIONS = 800;
	/* Maximum difference ratio of current and starting fitness score. (must be less than this) */
	static final double MIN_FITNESS_DIFF_RATIO = 0.002f;
	/* Minimum area of down shifted image for calculating maxShift. */
	static final int SHIFT_MIN_IMAGE_AREA = 400;
	/* How much less POLYGON_COUNT is shifted down than image size. */
	static final byte SUBTRACT_FROM_BITSHIFT = 3;
	/* Number of polygons in a PolygonImage. */
	static final int POLYGON_COUNT = 50; // Minimum 4.
	
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
	static final float CROSSOVER_RATIO = 0.85f;
	/* Percent chance of entering mutation stage. */
	static final float MUTATION_RATIO = 0.1f;
	/* Max percentage of POLYGON_COUNT mutations per child. */
	static final float MAX_MUTATIONS = 0.02f;
	/* Population size of chromosomes. */
	static final int POPULATION_SIZE = 50; // must be even number
	
	// Polygon class:
	
	/* Random max number of polygon vertices, plus 3 (= minimum). */
	static final int POLYGON_VERTICES = 20;
	/* Factor for how much smaller length and height of polygons should be compared to image. */
	static final float POLYGON_FUZZINESS_SCALE = 1.0f; // Max must be < 1.0
	/* Factor for max percent fuzziness set to vertices when mutating. */
	static final float VERTICES_FUZZINESS_SCALE = 0.2f;
	/* Percent chance of changing number of polygon vertices. */
	static final float CHANGE_VERTICES_RATIO = 0.1f;
	/* Percent chance of changing colour of polygon. */
	static final float CHANGE_COLOUR_RATIO = 0.1f;
}
