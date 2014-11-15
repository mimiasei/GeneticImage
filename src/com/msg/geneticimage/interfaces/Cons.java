package com.msg.geneticimage.interfaces;

public interface Cons {
 
 // Main class:
 
 /* Input image path. */
 static final String IMAGE_PATH = "images/monalisahead128px.png";
// static final String IMAGE_PATH = "images/Lena.jpg";
// static final String IMAGE_PATH = "images/ask256px.png";
// static final String IMAGE_PATH = "images/spiritedAway_bathroomHouse.jpg";
// static final String IMAGE_PATH = "images/miriamOgAsk_brygga.jpg";
// static final String IMAGE_PATH = "images/islandVillage.jpg";
// static final String IMAGE_PATH = "images/FromUpOnPoppyHill320px.png";
// static final String IMAGE_PATH = "images/totorocatbus384px.png";
// static final String IMAGE_PATH = "images/simpleTest128px.png";
// static final String IMAGE_PATH = "images/4squares128px.png";
// static final String IMAGE_PATH = "images/solidGreen128px.png";
 
 /* Millisecond frequency for how often best image is updated from algorithm thread. */ 
 static final int CHECK_FREQUENCY = 50;
 /* Frequency for how often print out of updates occur, once every PRINT_FREQUENCY times. */ 
 static final int PRINT_FREQUENCY = 500;
 /* Number of generations to iterate generic algorithm loop. */
 static final int NUMBER_OF_GENERATIONS = 2000000;
 /* Number of stagnations causing new blood injection, replacing worse half of population. */
 static final int NUMBER_OF_STAGNATIONS = 1500;
 /* Percentage of previous best fitness that current best has to be better than, or stagnation increments. */
 static final double MIN_FITNESS_DIFF_RATIO = 0.0001;
 /* Minimum area of down shifted image for calculating maxShift. 0 = Full size. */
 static final int SHIFT_MIN_IMAGE_AREA = 400000;
 /* Minimum image bit shift, sets maximum size of image compared original size. 0 = original size. */
 static final byte MINIMUM_IMAGE_BITSHIFT = 0;
 /* How much POLYGON_COUNT is shifted down. */
 static final byte POLYGON_BITSHIFT = 0;
 /* Set to true for using edge detection in fitness calculation. */
 static final boolean USE_EDGE_DETECTION = false;
 /* Set to true for using average colour of original image as background colour. */
 static final boolean USE_AVERAGE_BGCOLOUR = false;
 /* Ideal number of polygons in a PolygonImage. More gives worse fitness. */
 static final int POLYGON_COUNT = 50; // Minimum 4.
 /* Bit shift amount on POLYGON_COUNT for starting polygon count. */
 static final int POLYCOUNT_INITIATE_SHIFT = 4;
 

 
 // Main genetic algorithm class:
 
 /* enums for setting type of parent selection. */
 static enum ParentChoice {
  RND_TWO, RND_BEST_WORST, TOP_TWO_BEST, TOP_BEST_WORST;

     private final int value;

     private ParentChoice() {
         this.value = ordinal();
     }
 }
 /* Percent chance of entering cross-over stage. */
 static final double CROSSOVER_RATIO = 0.80;
 /* Percent chance of entering mutation stage. */
 static final double MUTATION_RATIO = 0.2;
 /* Max percentage of POLYGON_COUNT mutations per child. */
 static final double MAX_MUTATIONS = 0.01;
 /* Population size of chromosomes. */
 static final int POPULATION_SIZE = 20; // must be even number
 
 // Colour class:
 
 /* Minimum alpha value (0.0 to 1.0). */
 static final float MIN_ALPHA = 0.001f;
 /* Maximum alpha value (0.0 to 1.0). */
 static final float MAX_ALPHA = 0.05f;
 
 
 // Polygon class:
 
 /* Set to true for using spline based polygons (ellipses). */
 static final boolean USE_SPLINE_POLYGONS = true;
 /* Random max number of polygon vertices, plus 3 (= minimum). */
 static final int POLYGON_VERTICES = 3;
 
 // Scale factors:
 /* Factor for how much smaller length and height of polygons should be compared to image. */
 static final double POLYGON_FUZZINESS_SCALE = 0.99; // Max must be < 1.0
 /* Factor for max random theta radians of rotation, plus or minus. */
 static final double POLYGON_ROTATION_SCALE = Math.PI / 8.0;
 /* Factor for max percent fuzziness set to vertices when mutating. */
 static final double VERTICES_FUZZINESS_SCALE = 0.1;
 
 // Percent chance ratios:
 /* Percent chance of random fuzziness to polygon origo position. */
 static final double RANDOM_ORIGO_RATIO = 0.5;
 /* Percent chance of random fuzziness to polygon rotation. */
 static final double RANDOM_ROTATION_RATIO = 0.0;
 /* Percent chance of random fuzziness to polygon radius. */
 static final double RANDOM_RADIUS_RATIO = 0.0;
 /* Percent chance of random fuzziness to polygon scale. */
 static final double RANDOM_SCALE_RATIO = 0.5;
 /* Percent chance of changing number of polygon vertices. */
 static final double CHANGE_VERTICES_RATIO = 0.5;
 /* Percent chance of changing number of polygon vertices. */
 static final double CHANGE_VERTICES_COUNT_RATIO = 0;
 /* Percent chance of changing colour of polygon. */
 static final double CHANGE_COLOUR_RATIO = 0.5;
 /* Percent chance of adding or removing a polygon. */
 static final double CHANGE_POLYCOUNT_RATIO = 0.1;
}
