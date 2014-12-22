package com.msg.geneticimage.main;

import java.awt.FlowLayout;
import java.awt.Graphics2D;
//import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.msg.geneticimage.algorithm.RandomAlgorithm;
import com.msg.geneticimage.gfx.Polygon;
import com.msg.geneticimage.gfx.PolygonImage;
import com.msg.geneticimage.interfaces.Cons;

public class Main {
	
	private BufferedImage image;
	
	public Main() {
				
		image = Tools.image;
						
		byte chunksCount = Cons.IMAGE_CHUNKS;
		
		/* Start a thread of GeneticImage for each image chunk. */
		ArrayList<GenAlgThread> threadsList = new ArrayList<GenAlgThread>();
		PolygonImage[] polyImages = new PolygonImage[chunksCount];
		Thread thread;
		
		for (byte i = 0; i < chunksCount; i++) {
			GenAlgThread genAlgThread = new GenAlgThread(image, chunksCount, i, (byte)-1, null);
			threadsList.add(genAlgThread);
			thread = new Thread(genAlgThread);
			System.out.println("starting thread...");
			thread.start();
		}
		
		/* Loop until all chunks are done. */
		boolean notDone = true;
		while (notDone) {
			notDone = false;
			for (int i = 0; i < threadsList.size(); i++) {
				polyImages[i] = threadsList.get(i).getPolyImage();
				if(polyImages[i] == null)
					notDone = true;
				else
					polyImages[i].setName("Chunk#" + i);
			}
		}
		
		PolygonImage finalPolyImage = null;
		
		/* Put together all chunks and add to new population of otherwise random polyImages. */	
		if(Cons.IMAGE_CHUNKS > 1) {	
			PolygonImage[] population = new PolygonImage[Cons.POPULATION_SIZE];
			population[0] = assemblePolyImage(
					polyImages, image.getWidth(), image.getHeight(), chunksCount);
			RandomAlgorithm randomAlg = new RandomAlgorithm(population[0].getNumberOfPolygons());
			for (int i = 1; i < population.length; i++)
				population[i] = randomAlg.process(population[0]);
			
			/* Start new thread for algorithm on complete polyImage. */
			GenAlgThread genAlgThread = new GenAlgThread(image, 1, 1, (byte)0, population);
			thread = new Thread(genAlgThread);
			thread.start();
			
			/* Loop until image is done. */	
			while (thread.getState() != Thread.State.TERMINATED)
				finalPolyImage = genAlgThread.getPolyImage();
			
		} else {
			finalPolyImage = polyImages[0];
		}
		
		finalPolyImage.setName("Final image");

		/* Display final constructed image. */
		PolygonImage[] finalPolyImageArray = {finalPolyImage};
		JLabel compareImageLabel = new JLabel(new ImageIcon(image));
		JLabel bestPolygonImageLabel = new JLabel(new ImageIcon(
				constructImage(finalPolyImageArray, image.getWidth(), image.getHeight(), 1)));
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(compareImageLabel);
		frame.getContentPane().add(bestPolygonImageLabel);
		frame.setTitle("Final image");
		frame.pack();
		frame.setVisible(true);
	}
	
	/** 
	 * Construct a BufferedImage of w * h dimension out of chunksCount number of PolygonImage parts (chunks).
	 * 
	 * @param polyImages
	 * @param w
	 * @param h
	 * @param chunksCount
	 * @return BufferedImage
	 */
	public static BufferedImage constructImage(PolygonImage[] polyImages, int w, int h, int chunksCount) {
		PolygonImage polyImage;
		if(polyImages.length > 1)
			polyImage = assemblePolyImage(polyImages, w, h, chunksCount);
		else
			polyImage = polyImages[0];
		return PolygonImage.paintImage(w, h, polyImage.getPolygons(), 0);
	}
	
	/**
	 * Assemble a PolygonImage of w * h dimension out of chunksCount PolygonImage parts (chunks).
	 * 
	 * @param polyImages
	 * @param w
	 * @param h
	 * @param chunksCount
	 * @return assembledPolyImage
	 */
	public static PolygonImage assemblePolyImage(PolygonImage[] polyImages, int w, int h, int chunksCount) {
		PolygonImage assembledPolyImage = null;
		int rows = getRows(chunksCount);
		int columns = getColumns(chunksCount, rows);
		int chunkW = w / columns;
		int chunkH = h / rows;
		int xPos, yPos;
		ArrayList<Polygon> polygons = new ArrayList<Polygon>();
		for (int i = 0; i < polyImages.length; i++)
			for (Polygon poly : polyImages[i].getPolygons()) {
				xPos = getColumn(i, columns) * chunkW;
				yPos = getRow(i, columns) * chunkH;
				poly.move(xPos, yPos);
				polygons.add(poly);
			}
		assembledPolyImage = new PolygonImage(polyImages[0]);
		assembledPolyImage.setPolygons(polygons);
		return assembledPolyImage;
	}
	
	private BufferedImage getImageChunk(BufferedImage image, int chunksCount, int chunkNbr) {
		BufferedImage chunk = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr;
		int rows = 1, columns = 1, w, h, x = 0, y = 0;
		rows = getRows(chunksCount);
		columns = getColumns(chunksCount, rows);
		if(image.getWidth() < image.getHeight()) {
			int dummy = rows;
			rows = columns;
			columns = dummy;
		}
		w = image.getWidth() / columns;
		h = image.getHeight() / rows;
		y = getRow(chunkNbr, columns);
		x = getColumn(chunkNbr, columns);
		chunk = image.getSubimage(x * w, y * h, w, h);
		gr = chunk.createGraphics();
		gr.dispose();
		return chunk;		
	}
	
	/**
	 * 
	 * @param partsCount
	 * @return rows
	 */
	public static int getRows(int partsCount) {
		int rows = (int) Math.floor(Math.sqrt(partsCount));
		while(partsCount % rows != 0)
			rows = rows - 1;
		return rows;
	}
	
	/**
	 * 
	 * @param partsCount
	 * @param rows
	 * @return columns
	 */
	public static int getColumns(int partsCount, int rows) {
		return partsCount / rows;
	}
	
	/**
	 * 
	 * @param partNumber
	 * @param columns
	 * @return row
	 */
	public static int getRow(int partNumber, int columns) {
		return partNumber / columns;
	}
	
	/**
	 * 
	 * @param partNumber
	 * @param columns
	 * @return column
	 */
	public static int getColumn(int partNumber, int columns) {
		return partNumber % columns;
	}

	public static void main(String[] args) {
		new Main();
	}
	
	/**
	 * Converts an object of given type to an array.
	 * 
	 * @param object
	 * @return array of the object
	 */
	public static <T> T[] convertToArray(T object) {
		ArrayList<T> list = new ArrayList<T>();
		list.add(object);
		@SuppressWarnings("unchecked")
		T[] array = (T[]) java.lang.reflect.Array.newInstance(object.getClass(), 1);
		return list.toArray(array);
	}
	
	class GenAlgThread implements Runnable {
		
		private int count, nbr;
		private PolygonImage polyImage;
		private BufferedImage completeImage;
		private byte maxBitShift;
		PolygonImage[] population = null;
		
		/**
		 * MaxBitShift -1 = no population (for starting algorithm from scratch).
		 * For this, use convertToArray(object) as population parameter.
		 * 
		 * @param completeImage
		 * @param count
		 * @param nbr
		 * @param maxBitShift
		 * @param population
		 */
		public GenAlgThread(BufferedImage completeImage, 
				int count, int nbr, byte maxBitShift, PolygonImage[] population) {
			this.polyImage = null;
			this.count = count;
			this.nbr = nbr;
			this.completeImage = completeImage;
			this.maxBitShift = maxBitShift;
			this.population = population;
		}

		@Override
		public void run() {
			int polyCount = Cons.POLYGON_COUNT >> Cons.POLYCOUNT_INITIATE_SHIFT;
			BufferedImage chunk = this.completeImage;
			if(count > 1)
				chunk = getImageChunk(this.completeImage, count, nbr);
			if(chunk != null)
				/* Round poly count upwards. */
				if(maxBitShift < 0)
					polyImage = new GeneticImage(chunk).process(
							(int)Math.round(polyCount/(double)count));
				else
					polyImage = new GeneticImage(chunk, maxBitShift, population).process(
							(int)Math.round(polyCount/(double)count));
		}
		
		public PolygonImage getPolyImage() {
			return polyImage;
		}	
	}

}
