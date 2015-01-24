package com.msg.geneticimage.tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.msg.geneticimage.gfx.PolygonImage;

public class FileHandler {
	
	public final static String[] EXT = {"plt", "csv"};
	public final static char[] DELIMS = {' ', ','};
	private PrintWriter plot;
	private long startFitness;
	private int fileType;
	
	/**
	 * Opens text file for saving data.
	 * @param polyImage
	 */
	public FileHandler(PolygonImage polyImage) {
		fileType = 0;
		startFitness = polyImage.getFitness();
		long[] dataSet = {polyImage.getPolyCount()};
		String[] labels = {"Generation", "Best fitness percent", "Worst fitness percent"};
		initFile(dataSet , labels);
	}
	
	/**
	 * Constructor for saving plot data, using input dataSet as 
	 * values and labels as name of each value.<br>
	 * Extension is passed in as whole name (i.e. "plot", not "plt").
	 * @param dataSet
	 * @param labels
	 */
	public FileHandler(long[] dataSet, String[] labels, int fileType) {
		this.fileType = fileType;
		startFitness = dataSet[0];
		/* Remove first entry of array by making a new shorter one. */
		long[] newArray = new long[dataSet.length - 1];
		/* Remember System.arraycopy is a shallow copy, so any changes made
		 * to the new array will affect the original. */ 
		System.arraycopy(dataSet, 1, newArray, 0, newArray.length);
		initFile(newArray, labels);
	}
	
	public void initFile(long[] dataSet, String[] labels) {
		String date = new SimpleDateFormat("dd-MM-YY_hhmmss").format(new Date());
		try {
			plot = new PrintWriter(new File(
					FileHandler.class.getClassLoader().getResource("").getPath() + 
								dataSet[0] + "n_" + 
								date + "." + EXT[fileType]));
			String plotLine = "# x:" + labels[0];
			if(labels.length > 1)
				for(int i = 1; i < labels.length; i++)
					plotLine += "\tY" + i + ": " + labels[i];
			plot.println(plotLine);
		} catch(IOException exc) {
			exc.printStackTrace();
		}
	}
	
	/**
	 * Saving data from input population to a text file
	 * for use with the external plotter GnuPlot.<br>
	 * Text file name = "< polyImage size >n_< edge count >e_< current date >.plt".
	 * @param population
	 * @param gen
	 */
	public void saveData(long[] dataSet, int gen) {
		double fit = Tools.nDecimals(1.0 - (dataSet[0] / (double)startFitness), 5);
		double bestFit = Tools.nDecimals(1.0 - (dataSet[1] / (double)startFitness), 5);
		/* Save this population's current best and worst polyImage's fitness. */
		String dataLine = String.valueOf(gen) + DELIMS[fileType];
		dataLine += String.valueOf(fit) + DELIMS[fileType];
		dataLine += String.valueOf(bestFit);
		if(dataSet.length > 2)
			for (int i = 2; i < dataSet.length; i++)
				dataLine += DELIMS[fileType] + String.valueOf(dataSet[i]);
		plot.println(dataLine);
	}
	
	/**
	 * Closes the text file for further saving.
	 */
	public void closeFile() {
		plot.close();
	}
}



