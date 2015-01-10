package com.msg.geneticimage.main;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;




//import java.io.IOException;
//
//import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.msg.geneticimage.algorithm.Algorithm;
import com.msg.geneticimage.algorithm.GeneticAlgorithm;
import com.msg.geneticimage.algorithm.RandomAlgorithm;
import com.msg.geneticimage.gfx.PolygonImage;
import com.msg.geneticimage.gfx.Renderer;
import com.msg.geneticimage.interfaces.Cons;

public class GeneticImage {
	
	private GeneticAlgorithm geneticAlg;
	private BufferedImage image, compareImage;
	private byte bitShift, maxBitShift;
	private int polyCount;
	private PolygonImage[] population;
	private Color bgColour = Color.BLACK;
	
	public GeneticImage(BufferedImage inputImg) {	
		this.image = inputImg;
		/* Max bit shift = -1 means no population. */
		this.maxBitShift = -1;
	}
	
	public GeneticImage(BufferedImage inputImg, byte maxBitShift, PolygonImage[] population) {	
		this.image = inputImg;
		this.maxBitShift = maxBitShift;
		this.population = population;
	}
	
	public PolygonImage process(int polyCount) {
		JFrame frame = new JFrame();
		JLabel compareImageLabel, bestPolygonImageLabel;
		Thread thread;	
		GeneticAlgThread runnableGenAlg;
		this.polyCount = polyCount;
		
		image = getScaledImage(image, new Point(image.getWidth(), image.getHeight()));	
		int[] imagePixels = getCurrentImagePixels(image);

		Point startSize = new Point(image.getWidth(), image.getHeight());
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setAlwaysOnTop(true);

		/* Multi-level scaling loop. Bit-shifting from 5 to 0, meaning divide by 32 to 1.
		 * Running genetic algorithm first on the 1/32 scale image, then taking the result
		 * into the next scaling level (1/16 and so on).
		 */
		/* If SHIFT_MIN_IMAGE_AREA = 0 then only process full scale image (no bit shifting). */
		if(Cons.SHIFT_MIN_IMAGE_AREA > 0)
			maxBitShift = calculateMaxBitShift(startSize, maxBitShift);
		else
			maxBitShift = 0;
				
		Point currentSize = new Point(startSize.x >> maxBitShift, startSize.y >> maxBitShift);
		compareImage = getScaledImage(imagePixels, startSize, currentSize);
		
		/* 
		 * Random algorithm phase. Create initial population randomly.
		 */
		if(population == null) {
			Algorithm<PolygonImage> randomAlg = new RandomAlgorithm(getPolyCount(maxBitShift));
			population = new PolygonImage[Cons.POPULATION_SIZE];
			PolygonImage polygonImage = new PolygonImage(this, getPolyCount(maxBitShift));
			
			/* Create POPULATION_SIZE number of random PolygonImages as chromosomes. */
			for (byte i = 0; i < population.length; i++) {
				polygonImage = randomAlg.process(polygonImage);
				polygonImage.calculateFitness();
				population[i] = polygonImage;
			}
		}
		
		PolygonImage currentBestImage = population[0];
		
		/*
		 * Genetic algorithm multi-level phase of bitShift+1 number of levels.
		 */
		for (byte i = maxBitShift; i >= Cons.MINIMUM_IMAGE_BITSHIFT; i--) {
			
			/* Assign current bit shift amount to the bitShift variable. */
			bitShift = i;

			/* Adjust all polygon images in population for twice as many polygons. */
			if(population[0].getPolyCount() < (getPolyCount(bitShift)))
				for (PolygonImage polyImage : population) {
					polyImage.setNewPolyCount(getPolyCount(bitShift));
					polyImage.calculateFitness();
				}
			
			// DEBUG
			System.out.println("\nInitial (start) size: " + startSize);			
			currentSize = new Point(startSize.x >> bitShift, startSize.y >> bitShift);
			System.out.println("Shifting image by: " + bitShift + " -> Current size: " + currentSize);
			
			compareImage = getScaledImage(imagePixels, startSize,
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
					
			compareImageLabel = new JLabel(new ImageIcon(compareImage));
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
					Thread.sleep(Cons.CHECK_FREQUENCY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				population = runnableGenAlg.getPopulation();
				frame.getContentPane().remove(bestPolygonImageLabel);
				currentBestImage = runnableGenAlg.getBestImage();
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
		return currentBestImage;
	}
	
	/**
	 * 
	 * @param startSize
	 * @param maxBitShift
	 * @return maxBitShift
	 */
	public byte calculateMaxBitShift(Point startSize, byte maxBitShift) {
		byte shift = maxBitShift;
		if(shift < 0) {
			int imageArea = startSize.x * startSize.y, scaledArea = imageArea;
			Point scaledSize = startSize;
			shift = 1;
			while (scaledArea > Cons.SHIFT_MIN_IMAGE_AREA) {
				scaledSize = new Point(startSize.x >> shift, startSize.y >> shift);
				scaledArea = scaledSize.x * scaledSize.y;
				shift++;
			}
			shift--;
		}
		return shift;
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
	   int[] imgData = Renderer.getPixelsArray(image);
	   System.arraycopy(imagePixels, 0, imgData, 0, imagePixels.length);
	   Graphics g = currentImage.createGraphics();
	   g.drawImage(image, 0, 0, size.x, size.y, null);
	   g.dispose();
	   return currentImage;
	}
	
	public byte getPolyShift(byte bitShift) {
		byte shift = 0;
		if(Cons.POLYGON_BITSHIFT > 0)
			shift = (byte)((bitShift / maxBitShift) * Cons.POLYGON_BITSHIFT);
		return (shift < 0 ? 0 : shift);
	}
	
	public int getPolyCount(byte bitShift) {
		byte multiplier = (byte)((maxBitShift - bitShift) + 1);
		multiplier = (multiplier < 1 ? 1 : multiplier);
		int polyCount = (this.polyCount >> getPolyShift(maxBitShift)) * multiplier;
		return (polyCount > this.polyCount ? this.polyCount : polyCount);
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public BufferedImage getCompareImage() {
		return compareImage;
	}
	
	public int getBitShift() {
		return bitShift;
	}

	public byte getMaxBitShift() {
		return maxBitShift;
	}

	public int[] getCurrentImagePixels() {
		return Renderer.getPixelsArray(compareImage);
	}
	
	public static int[] getCurrentImagePixels(BufferedImage image) {
		if(image != null)
			return Renderer.getPixelsArray(image);
		else
			return null;
	}

	public Color getBgColour() {
		return bgColour ;
	}
}
