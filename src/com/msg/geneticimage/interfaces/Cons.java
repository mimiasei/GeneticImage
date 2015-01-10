package com.msg.geneticimage.interfaces;

public interface Cons {

	
	static final double ONE_THIRD = 1.0 / 3.0;
	
	 /* Input image path. */
	 static final String IMAGE_PATH = "images/monalisahead128px.png";
	// static final String IMAGE_PATH = "images/Lena128px.png";
//	 static final String IMAGE_PATH = "images/ask128px.png";
	// static final String IMAGE_PATH = "images/spiritedAway_bathroomHouse.jpg";
	// static final String IMAGE_PATH = "images/miriamOgAsk_brygga.jpg";
	// static final String IMAGE_PATH = "images/islandVillage.jpg";
//	 static final String IMAGE_PATH = "images/FromUpOnPoppyHill184px.png";
	// static final String IMAGE_PATH = "images/totorocatbus384px.png";
	// static final String IMAGE_PATH = "images/simpleTest128px.png";
//	 static final String IMAGE_PATH = "images/4squares128px.png";
//	 static final String IMAGE_PATH = "images/solidGreen128px.png";
//	 static final String IMAGE_PATH = "images/Angelina-Jolie_256px.png";
//	 static final String IMAGE_PATH = "images/verySimpleRedPoly-256px.png";
	 
	 /* Millisecond frequency for how often best image is updated from algorithm thread. */ 
	 static final int CHECK_FREQUENCY = 50;
	 /* Frequency for how often print out of updates occur, once every PRINT_FREQUENCY times. */ 
	 static final int PRINT_FREQUENCY = 200;
	 /* Set to true for plotting data for use in GnuPlot. */
	 static final boolean PLOT_DATA = false;
	 /* Number of generations to iterate generic algorithm loop. */
	 static final int NUMBER_OF_GENERATIONS = 1000000;
	 /* Number of stagnations causing new blood injection, replacing worse half of population. */
	 static final int NUMBER_OF_STAGNATIONS = 2000;
	 /* Number of generations when mutation ratio goes to default MUTATION_RATIO. */
	 static final int NUMBER_OF_GENS_DEFAULT_MUT_RATIO = 1500;
	 /* Percentage of previous best fitness that current best has to be better than, or stagnation increments. */
	 static final double MIN_FITNESS_DIFF_RATIO = 0.002;
	 /* Minimum area of down shifted image for calculating maxShift. 0 = Full size. */
	 static final int SHIFT_MIN_IMAGE_AREA = 200000;
	 /* Number of chunks for splitting image into parallel processes. */
	 static final byte IMAGE_CHUNKS = 1;
	 /* Minimum image bit shift, sets maximum size of image compared original size. 0 = original size. */
	 static final byte MINIMUM_IMAGE_BITSHIFT = 0;
	 /* How much POLYGON_COUNT is shifted down. */
	 static final byte POLYGON_BITSHIFT = 0;
	 /* Ideal number of polygons in a PolygonImage. More gives worse fitness. */
	 static final int POLYGON_COUNT = 50; // Minimum 4.
	 /* Bit shift amount on POLYGON_COUNT for starting polygon count. */
	 static final int POLYCOUNT_INITIATE_SHIFT = 5;
	 /* Fitness calculation pixel step size. */
	 static final int FITNESS_PIXEL_STEP = 1;
	
	 /* Number of parent chromosomes to be randomly selected. */
	 static final byte NUMBER_OF_PARENTS = 2;
	 /* Percent chance of entering cross-over stage. */
	 static final double CROSSOVER_RATIO = 0.7;
	 /* Percent chance of entering mutation stage. */
	 static final double MUTATION_RATIO = 0.1;
	 /* Max possible chance of mutation. */
	 static final double MAX_MUTATION_RATIO = 0.7;
	 /* Population size of chromosomes. */
	 static final int POPULATION_SIZE = 16; // must be even number
	 
	 // Colour class: 
	 /* Minimum alpha value (0.0 to 1.0). */
	 static final float MIN_ALPHA = 0.01f;
	 /* Maximum alpha value (0.0 to 1.0). */
	 static final float MAX_ALPHA = 0.15f;
	 
	 /* Random max number of polygon vertices, plus 3 (= minimum). */
	 static final int POLYGON_VERTICES = 2; 
	 /* Max starting age of new chromosomes. */
	 static final int CHROMOSOME_MAX_STARTING_AGE = 5;
	 
	 // Scale factors:
	 /* Factor for max percent fuzziness set to vertices or polygons when mutating. */
	 static final double POLYGON_FUZZINESS_SCALE = 0.5; // NOT USED
	 /* Factor for max percent fuzziness set to vertices or polygons when mutating. */
	 static final double VERTICES_FUZZINESS_SCALE = 0.04;
	 /* Factor for max percent fuzziness set to colour channels when mutating. */
	 static final double COLOUR_FUZZINESS_SCALE = 0.04;
	 
	 // Percent chance ratios: 
	 /* Percent chance of resetting mutation ratio to MUTATION_RATIO. Should be low! */
	 static final double CHANCE_OF_MUT_RATIO_RESET = 0.05;
	 /* Percent chance of chromosomes in gene can dying. */
	 static final double CHANCE_OF_CAN_GENES_DYING = 0.1;
	 /* Percent chance of replacing an object by a new random one. */
	 static final double RANDOM_NEW_RATIO = 0.05;
	 /* Percent chance of picking a parent from the gene can. */
	 static final double PARENT_FROM_GENECAN_RATIO = 0.10;
	 /* Percent chance of adjusting position of polygon vertices. */
	 static final double CHANGE_VERTICES_RATIO = 0.5;
	 /* Percent chance of changing number of polygon vertices. */
	 static final double CHANGE_VERTICES_COUNT_RATIO = 0.5;
	 /* Percent chance of changing colour of polygon. */
	 static final double CHANGE_COLOUR_RATIO = 0.5;
	 /* Percent chance of adding or removing a polygon. */
	 static final double CHANGE_POLYCOUNT_RATIO = 0.1;
	 /* Percent chance of removing a polygon. */
	 static final double REMOVE_POLY_RATIO = 0.3;
}
