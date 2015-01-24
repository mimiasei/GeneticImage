package com.msg.geneticimage.interfaces;

public interface Cons {
	
	static final double ONE_THIRD = 1.0 / 3.0;
	static final int EXT_PLT = 0;
	static final int EXT_CSV = 1;
	
	 /* Input image path. */
//	 static final String IMAGE_PATH = "images/monalisahead128px.png";
//	 static final String IMAGE_PATH = "images/Lena128px.png";
//	 static final String IMAGE_PATH = "images/ask128px.png";
//	 static final String IMAGE_PATH = "images/nelsonMandela.jpg";
	// static final String IMAGE_PATH = "images/miriamOgAsk_brygga.jpg";
	// static final String IMAGE_PATH = "images/islandVillage.jpg";
//	 static final String IMAGE_PATH = "images/FromUpOnPoppyHill184px.png";
//	 static final String IMAGE_PATH = "images/totorocatbus384px.png";
//	 static final String IMAGE_PATH = "images/simpleTest128px.png";
//	 static final String IMAGE_PATH = "images/4squares128px.png";
//	 static final String IMAGE_PATH = "images/solidGreen128px.png";
	 static final String IMAGE_PATH = "images/Angelina-Jolie_256px.png";
//	 static final String IMAGE_PATH = "images/verySimpleRedPoly-256px.png";
//	 static final String IMAGE_PATH = "images/rembrandt-self-portrait_256px.png";	 
	 
	 /* Millisecond frequency for how often best image is updated from algorithm thread. */ 
	 static final int CHECK_FREQUENCY = 50;
	 /* Frequency for how often print out of updates occur, once every PRINT_FREQUENCY times. */ 
	 static final int PRINT_FREQUENCY = 200;
	 /* Set to true for plotting data for use in GnuPlot. */
	 static final boolean PLOT_DATA = false;
	 /* Number of generations to iterate generic algorithm loop. */
	 static final int NBR_OF_GENERATIONS = 10000;
	 /* Number of stagnations causing new blood injection, replacing worse half of population. */
	 static final int NBR_OF_STAGNATIONS = 2000;
	 /* Number of generations when mutation ratio goes to default CHANCE_MUTATION_RATIO. */
	 static final int NBR_GENS_DEFAULT_MUT_RATIO = 1500;
	 /* Percentage of previous best fitness that current best has to be better than, or stagnation increments. */
	 static final double NBR_MIN_FITNESS_DIFF_RATIO = 0.002;
	 /* Ideal number of polygons in a PolygonImage. More gives worse fitness. */
	 static final int NBR_POLYGON_COUNT = 50; // Minimum 4.
	 /* Bit shift amount on NBR_POLYGON_COUNT for starting polygon count. */
	 static final int NBR_POLYCOUNT_INITIATE_SHIFT = 4;
	 /* Minimum area of down shifted image for calculating maxShift. 0 = Full size. */
	 static final int SHIFT_MIN_IMAGE_AREA = 800000;
	 /* Number of chunks for splitting image into parallel processes. */
	 static final byte IMAGE_CHUNKS = 1;
	 /* Minimum image bit shift, sets maximum size of image compared original size. 0 = original size. */
	 static final byte MINIMUM_IMAGE_BITSHIFT = 0;
	 /* How much NBR_POLYGON_COUNT is shifted down. */
	 static final byte POLYGON_BITSHIFT = 0;
	 /* Fitness calculation pixel step size. */
	 static final int FITNESS_PIXEL_STEP = 1;
	
	 /* Number of parent chromosomes to be randomly selected. */
	 static final byte NUMBER_OF_PARENTS = 2;
	 /* Percent chance of entering cross-over stage. */
	 static final double CHANCE_CROSSOVER_RATIO = 0.8;
	 /* Percent chance of entering mutation stage. */
	 static final double CHANCE_MUTATION_RATIO = 0.05;
	 /* Max possible chance of mutation. */
	 static final double CHANCE_MAX_MUTATION_RATIO = 0.7;
	 /* Population size of chromosomes. */
	 static final int NBR_POPULATION_SIZE = 40; // must be even number
	 
	 /* Random max number of polygon vertices, plus 3 (= minimum). */
	 static final int NBR_POLYGON_VERTICES = 3; 
	 
	 // Scale factors:
	 /* Factor for max percent fuzziness set to vertices or polygons when mutating. */
	 static final double FACTOR_VERTICES_FUZZINESS = 0.03;
	 /* Factor for max percent fuzziness set to colour channels when mutating. */
	 static final double FACTOR_COLOUR_FUZZINESS = 0.03;
	 /* Minimum alpha dblValue (0.0 to 1.0). */
	 static final double FACTOR_MIN_ALPHA = 0.01;
	 /* Maximum alpha dblValue (0.0 to 1.0). */
	 static final double FACTOR_MAX_ALPHA = 0.125;
	 
	 // Percent chance ratios: 
	 /* Percent chance of resetting mutation ratio to CHANCE_MUTATION_RATIO. Should be low! */
	 static final double CHANCE_OF_MUT_RATIO_RESET = 0.0;
	 /* Percent chance of chromosomes in gene can dying. */
	 static final double CHANCE_OF_CAN_GENES_DYING = 0.25;
	 /* Percent chance of replacing an object by a new random one. */
	 static final double CHANCE_RANDOM_NEW_RATIO = 0.01;
	 /* Percent chance of picking a parent from the gene can. */
	 static final double CHANCE_GENECAN_PARENT_RATIO = 0.1;
	 /* Percent chance of adjusting position of polygon vertices. */
	 static final double CHANCE_VERTICES_RATIO = 0.5;
	 /* Percent chance of changing number of polygon vertices. */
	 static final double CHANCE_VERTICES_COUNT_RATIO = 0.5;
	 /* Percent chance of changing colour of polygon. */
	 static final double CHANCE_COLOUR_RATIO = 0.5;
	 /* Percent chance of adding or removing a polygon. */
	 static final double CHANCE_POLYCOUNT_RATIO = 0.02;
	 /* Percent chance of removing a polygon. */
	 static final double CHANCE_REMOVE_POLY_RATIO = 0.5;
}
