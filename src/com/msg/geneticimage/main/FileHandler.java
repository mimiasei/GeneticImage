package com.msg.geneticimage.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.msg.geneticimage.gfx.PolygonImage;

public class FileHandler {
	
	private PrintWriter plot;
	private long startFitness;
	
	/**
	 * Opens text file for saving data.
	 * @param polyImage
	 */
	public FileHandler(PolygonImage polyImage) {
		startFitness = polyImage.getFitness();
		String date = new SimpleDateFormat("MM_dd_hhmm").format(new Date());
		try {  // Catch errors in I/O if necessary.
			plot = new PrintWriter(new File(
					FileHandler.class.getClassLoader().getResource("").getPath() + 
								polyImage.getPolyCount() + "n_" + 
								date + ".plt"));
			plot.println("# x:Generation   Y1:Best fitness percent   Y2:Worst fitness percent");
		} catch(IOException exc) {
			exc.printStackTrace(); // If there was an error, print the info.
		}
	}
	
	/**
	 * Saving data from input population to a text file
	 * for use with the external plotter GnuPlot.<br>
	 * Text file name = "< polyImage size >n_< edge count >e_< current date >.plt".
	 * @param population
	 * @param gen
	 */
	public void saveData(ArrayList<PolygonImage> population, int gen) {
		PolygonImage bestG = population.get(0);
		PolygonImage worstG = population.get(population.size()-1);
		double bestP = Tools.nDecimals(1.0 - (bestG.getFitness() / (double)startFitness), 5);
		double worstP = Tools.nDecimals(1.0 - (worstG.getFitness() / (double)startFitness), 5);
		/* Save this population's current best and worst polyImage's fitness. */
		plot.println(gen + " " + bestP + " " + worstP);
	}
	
	/**
	 * Closes the text file for further saving.
	 */
	public void closeFile() {
		plot.close();
	}
	
//	public static PolygonImage loadPolyImage(int nodes, int edges) {
//		
//		PolygonImage polyImage = null;
//		// Wrap all in a try/catch block to trap I/O errors.
//		try {
//			InputStreamReader isReader = new InputStreamReader(
//                        FileHandler.class.getClassLoader().getResourceAsStream("" + 
//															nodes + "n_" + 
//															edges + "e.polyImage"));
//	
//			// Create an ObjectInputStream to get objects from save file.
//			BufferedReader load = new BufferedReader(isReader);
//			
//			// Now we do the loading.
//			int size = Integer.parseInt(load.readLine()); // read polyImage size
//			polyImage = new PolygonImage(size);
//			for (int n = 0; n < size; n++) // read colour int of each node
//				polyImage.setNode(n, new Node(Integer.parseInt(load.readLine())));
//			for (int i = 0; i < size; i++) // read boolean value of each matrix cell
//				for (int j = 0; j < size; j++)
//					polyImage.setEdge(i, j, load.readLine().equals("1") ? true : false);
//			polyImage.setEdgeSize(Integer.parseInt(load.readLine()));
//			polyImage.setFitness(Integer.parseInt(load.readLine()));
//			
//			// Close the file.
//			load.close(); // This also closes loadFile.
//		
//		} catch(IOException e) {
//			System.out.println("Unable to find stored specified polyImage.");
//		}
//		return polyImage;
//	}

//	public static void savePolyImage(PolygonImage polyImage) {
//		
//		try {  // Catch errors in I/O if necessary.
//			PrintWriter save = new PrintWriter(new File(
//					FileHandler.class.getClassLoader().getResource("").getPath() + 
//								polyImage.getNumberOfPolygons() + "n_" + ".polyImage"));
//	
//			// Now we do the saving.
//			save.println(polyImage.getSize()); // start by saving the polyImage size
//			for (int n = 0; n < polyImage.getNodes().length; n++)
//				save.println(polyImage.getNodes()[n].getColour());
//			for (int i = 0; i < polyImage.getAdjMatrix().length; i++)
//				for (int j = 0; j < polyImage.getAdjMatrix()[i].length; j++)
//					save.println(polyImage.getAdjMatrix()[i][j] ? 1 : 0); // save as integer 1 or 0
//			save.println(polyImage.getEdgeSize());
//			save.println(polyImage.getFitness());
//	
//			// Close the file.
//			save.close(); // This also closes saveFile.
//		} catch(IOException exc) {
//			exc.printStackTrace(); // If there was an error, print the info.
//		}
//	}
}



