package com.msg.geneticimage.main;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.msg.geneticimage.algorithm.Algorithm;
import com.msg.geneticimage.algorithm.GeneticAlgorithm;
import com.msg.geneticimage.algorithm.RandomAlgorithm;
import com.msg.geneticimage.gfx.CannyEdgeDetector;
import com.msg.geneticimage.gfx.PolygonImage;
import com.msg.geneticimage.interfaces.Constants;

public class GeneticImage implements Constants {
	
	private GeneticAlgorithm geneticAlg;
	private BufferedImage image, compareImage, edgeCompareImage;
	private byte bitShift, maxBitShift;
	
	public GeneticImage(String imagePath) {
		JFrame frame = new JFrame();
		JLabel compareImageLabel, bestPolygonImageLabel;
		Thread thread;	
		GeneticAlgThread runnableGenAlg;
		
		/* Create a canny edge detector object. */
		CannyEdgeDetector detector = new CannyEdgeDetector();
		// Adjust parameters.
		detector.setLowThreshold(0.5f);
		detector.setHighThreshold(8.0f);
		detector.setGaussianKernelRadius(1.9f);
		detector.setGaussianKernelWidth(40);		
		
		/* Load compare image. */
		try {
			image = ImageIO.read(this.getClass().getClassLoader().getResource(imagePath));
		} catch (IOException e) {
			System.out.println("Cannot find image file!");
			System.exit(0);
		}
				
		// Apply image to edge detector as a buffered image.
		detector.setSourceImage(image);
		detector.process();
 		edgeCompareImage = detector.getEdgesImage();
		
		image = getScaledImage(image, new Point(image.getWidth(), image.getHeight()));	
		int[] imagePixels = getCurrentImagePixels(image);
 		
 		/* Convert edge image into pixel array. */
		edgeCompareImage = getScaledImage(edgeCompareImage, new Point(edgeCompareImage.getWidth(), edgeCompareImage.getHeight()));	
		int[] edgeImagePixels = getCurrentImagePixels(edgeCompareImage);

		Point startSize = new Point(image.getWidth(), image.getHeight()), currentSize;
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setAlwaysOnTop(true);

		/* Multi-level scaling loop. Bit-shifting from 5 to 0, meaning divide by 32 to 1.
		 * Running genetic algorithm first on the 1/32 scale image, then taking the result
		 * into the next scaling level (1/16 and so on).
		 */
		
		int imageArea = startSize.x * startSize.y, scaledArea = imageArea;
		Point scaledSize = startSize;
		maxBitShift = 1;
		while (scaledArea > SHIFT_MIN_IMAGE_AREA) {
			scaledSize = new Point(startSize.x >> maxBitShift, startSize.y >> maxBitShift);
			scaledArea = scaledSize.x * scaledSize.y;
			maxBitShift++;
		}
		maxBitShift--;
				
		currentSize = new Point(startSize.x >> maxBitShift, startSize.y >> maxBitShift);
		compareImage = getScaledImage(imagePixels, startSize, currentSize);
		
		/* 
		 * Random algorithm phase. Create initial population randomly.
		 */		
		Algorithm<PolygonImage> randomAlg = new RandomAlgorithm();
		PolygonImage[] population = new PolygonImage[POPULATION_SIZE];
		PolygonImage polygonImage = new PolygonImage(this, getPolyCount(maxBitShift));
		PolygonImage currentBestImage;
		
		/* Create images based on currentPolyCount number of random polygons. */
		randomAlg.setMaxIterations(getPolyCount(maxBitShift));
		
		/* Create POPULATION_SIZE number of random PolygonImages as chromosomes. */
		for (byte i = 0; i < population.length; i++) {
			polygonImage = randomAlg.process(polygonImage);
			polygonImage.calculateFitness();
			population[i] = polygonImage;
		}
		
		currentBestImage = population[0];
		
		/*
		 * Genetic algorithm multi-level phase of bitShift+1 number of levels.
		 */
		for (byte i = maxBitShift; i >= 0; i--) {
			
			/* Assign current bit shift amount to the bitShift variable. */
			bitShift = i;
//			bitShift = 1;

			/* Adjust all polygon images in population for twice as many polygons. */
			if(population[0].getNumberOfPolygons() < (getPolyCount(bitShift)))
				for (PolygonImage polyImg : population) {
					polyImg.setNewPolyCount(getPolyCount(bitShift));
					polyImg.calculateFitness();
				}
			
			// DEBUG
			System.out.println("\nInitial (start) size: " + startSize);			
			currentSize = new Point(startSize.x >> bitShift, startSize.y >> bitShift);
			System.out.println("Shifting image by: " + bitShift + " -> Current size: " + currentSize);
			
			compareImage = getScaledImage(imagePixels, startSize,
					new Point(startSize.x >> bitShift, startSize.y >> bitShift));
			
			edgeCompareImage = getScaledImage(edgeImagePixels, startSize,
					new Point(startSize.x >> bitShift, startSize.y >> bitShift));
			
			geneticAlg = new GeneticAlgorithm(this);
			/* Set population as current population. */
			geneticAlg.setPopulation(population);
			/* Set number of polygons for the algorithm. */
			geneticAlg.setMaxIterations(getPolyCount(bitShift));

			
			/* Set ratio for how much smaller best fitness must be compared to initial fitness
			 * before stopping algorithm loop. 
			 */
			runnableGenAlg = new GeneticAlgThread(geneticAlg);
			thread = new Thread(runnableGenAlg); 
			thread.start();
					
//			compareImageLabel = new JLabel(new ImageIcon(compareImage));
			compareImageLabel = new JLabel(new ImageIcon(edgeCompareImage));
			bestPolygonImageLabel = new JLabel(new ImageIcon(currentBestImage.getImage()));
			
			frame.getContentPane().setLayout(new FlowLayout());
			frame.getContentPane().add(compareImageLabel);
			frame.getContentPane().add(bestPolygonImageLabel);
			frame.setTitle("Best: " + currentBestImage.getFitness());
			frame.pack();
			frame.setVisible(true);
	
			/* Run until algorithm thread is finished thus terminated. */
			while (thread.getState() != Thread.State.TERMINATED) {
				/* Check current best image by fitness every CHECK_FREQUENCY seconds. */
				try { 
					Thread.sleep(CHECK_FREQUENCY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				population = runnableGenAlg.getPopulation();
				frame.getContentPane().remove(bestPolygonImageLabel);
				currentBestImage = runnableGenAlg.getProcessingImage();
				bestPolygonImageLabel = new JLabel(new ImageIcon(currentBestImage.getImage()));
				frame.getContentPane().add(bestPolygonImageLabel);
				frame.setTitle("Best: " + currentBestImage.getFitness());
				frame.pack();
			}
			
			/* Hide and empty the frame window. */
			if(frame.isVisible())
				frame.setVisible(false);
			frame.remove(compareImageLabel);
			frame.remove(bestPolygonImageLabel);		
						
			// DEBUG
			if(currentBestImage != null)
				System.out.println("Final fitness = " + currentBestImage.getFitness());
		}
	}
	
	public static void main(String[] args) {
		new GeneticImage(IMAGE_PATH);
	}
	
	public static BufferedImage getScaledImage(BufferedImage image, Point size) {
		BufferedImage scaledImage = new BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_ARGB);
//		BufferedImage scaledImage = new BufferedImage(size.x, size.y, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = scaledImage.createGraphics();
		g.drawImage(image, 0, 0, size.x, size.y, null);
		g.dispose();
		return scaledImage;
	}
	
	public static BufferedImage getScaledImage(int[] imagePixels, Point oldSize, Point size) {
	   BufferedImage image = new BufferedImage(oldSize.x, oldSize.y, BufferedImage.TYPE_INT_ARGB);
	   BufferedImage currentImage = new BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_ARGB);
	   int[] imgData = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
	   System.arraycopy(imagePixels, 0, imgData, 0, imagePixels.length);
	   Graphics g = currentImage.createGraphics();
	   g.drawImage(image, 0, 0, size.x, size.y, null);
	   g.dispose();
	   return currentImage;
	}
	
	public byte getPolyShift(byte bitShift) {
		byte shift = (byte)(bitShift - SUBTRACT_FROM_BITSHIFT);
		return (shift < 0 ? 0 : shift);
	}
	
	public int getPolyCount(byte bitShift) {
		byte multiplier = (byte)((maxBitShift - bitShift) + 1);
		multiplier = (multiplier < 1 ? 1 : multiplier);
		int polyCount = (POLYGON_COUNT >> getPolyShift(maxBitShift)) * multiplier;
		return (polyCount > POLYGON_COUNT ? POLYGON_COUNT : polyCount);
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public BufferedImage getCompareImage() {
		return compareImage;
	}
	
	public BufferedImage getEdgeCompareImage() {
		return edgeCompareImage;
	}
	
	public int getWidth() {
		return compareImage.getWidth();
	}
	
	public int getHeight() {
		return compareImage.getHeight();
	}
	
	public int getBitShift() {
		return bitShift;
	}

	public byte getMaxBitShift() {
		return maxBitShift;
	}

	public int[] getCurrentImagePixels() {
		return ((DataBufferInt) compareImage.getRaster().getDataBuffer()).getData();
	}
	
	public static int[] getCurrentImagePixels(BufferedImage image) {
		return ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}

	private class GeneticAlgThread implements Runnable {
		
		PolygonImage[] threadPopulation;
		GeneticAlgorithm genAlg;
		
		public GeneticAlgThread(GeneticAlgorithm genAlg) {
			this.genAlg = genAlg;
			this.threadPopulation = geneticAlg.getPopulation();
		}
		
		@Override
		public void run() {
			threadPopulation = geneticAlg.process(threadPopulation);
		}
		
		public PolygonImage getProcessingImage() {
			return geneticAlg.getCurrentBestImage();
		}
		
		public PolygonImage[] getPopulation() {
			return threadPopulation;
		}
	}
}
