package com.msg.geneticimage.main;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.msg.geneticimage.algorithm.Algorithm;
import com.msg.geneticimage.algorithm.GeneticAlgorithm;
import com.msg.geneticimage.algorithm.RandomAlgorithm;
import com.msg.geneticimage.gfx.PolygonImage;
import com.msg.geneticimage.interfaces.Constants;

@SuppressWarnings("serial")
public class GeneticImage extends JPanel implements Constants {
	
	private GeneticAlgorithm geneticAlg;
	private BufferedImage image, compareImage;
	long startFitness;
	
	public GeneticImage(String imagePath) {
		JFrame frame = new JFrame();
		JLabel compareImageLabel, bestPolygonImageLabel;
		Thread thread;	
		GeneticAlgThread runnableGenAlg;
		
		/* Load compare image. */
		try {
			this.image = ImageIO.read(this.getClass().getClassLoader().getResource(imagePath));
		} catch (IOException e) {
			System.out.println("Cannot find image file!");
			System.exit(0);
		}

		Point startSize = new Point(this.image.getWidth(), this.image.getHeight()), currentSize;
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setAlwaysOnTop(true);

		/* Multi-level scaling loop. Bit-shifting from 5 to 0, meaning divide by 32 to 1.
		 * Running genetic algorithm first on the 1/32 scale image, then taking the result
		 * into the next scaling level (1/16 and so on).
		 */
		int imageArea = startSize.x * startSize.y, scaledArea = imageArea;
		Point scaledSize = startSize;
		byte bitShift = 1;
		while (scaledArea > 300) {
			scaledSize = new Point(startSize.x >> bitShift, startSize.y >> bitShift);
			scaledArea = scaledSize.x * scaledSize.y;
			bitShift++;
		}
		bitShift--;
		
		// DEBUG
		bitShift = 1;
		
		currentSize = new Point(startSize.x >> bitShift, startSize.y >> bitShift);
		compareImage = getScaledImage(image, currentSize);
		
		/* 
		 * Random algorithm phase. Create initial population randomly.
		 */		
		Algorithm<PolygonImage> randomAlg = new RandomAlgorithm();
		PolygonImage[] population = new PolygonImage[POPULATION_SIZE];
		PolygonImage polygonImage = new PolygonImage(compareImage.getWidth(), compareImage.getHeight());
		PolygonImage currentBestImage;
		/* Create images based on POLYGON_COUNT number of random polygons. */
		randomAlg.setMaxIterations(POLYGON_COUNT);		
		/* Create POPULATION_SIZE number of random PolygonImages as chromosomes. */
		for (byte i = 0; i < POPULATION_SIZE; i++) {
			polygonImage = randomAlg.process(polygonImage);
			polygonImage.setFitness(PolygonImage.getFitness(polygonImage.getImage(), compareImage));
			population[i] = polygonImage;
		}
		
		currentBestImage = population[0];
		startFitness = population[0].getFitness();
		
		/*
		 * Genetic algorithm multi-level phase of bitShift+1 number of levels.
		 */
		for (int i = bitShift; i >= 0; i--) {
			// DEBUG
			System.out.println("\nInitial (start) size: " + startSize);
			
			currentSize = new Point(startSize.x >> i, startSize.y >> i);
			
			// DEBUG
			System.out.println("Shifting image by: " + i + " -> Current size: " + currentSize);
			
			compareImage = getScaledImage(this.image, currentSize);
			
			geneticAlg = new GeneticAlgorithm(compareImage);
			/* Set number of polygons for the algorithm. */
			geneticAlg.setMaxIterations(POLYGON_COUNT);
			/* Set ratio for how much smaller best fitness must be compared to initial fitness
			 * before stopping algorithm loop. 
			 */
			geneticAlg.setMaxFitnessRatio(MAX_CURRENT_START_FITNESS_RATIO);
			
			// DEBUG
			System.out.println("Start fitness: " + startFitness);
			
			runnableGenAlg = new GeneticAlgThread(population);
			thread = new Thread(runnableGenAlg); 
			thread.start();
					
			compareImageLabel = new JLabel(new ImageIcon(compareImage));
			bestPolygonImageLabel = new JLabel(new ImageIcon(currentBestImage.getImage()));
			
			frame.add(this);
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
			frame.remove(this);
			frame.pack();
			
//			currentBestImage = runnableGenAlg.getProcessingImage();
			
			// DEBUG
			if(currentBestImage != null)
				System.out.println("Final fitness = " + currentBestImage.getFitness());
		}
	}
	
	public static BufferedImage getScaledImage(BufferedImage image, Point size) {
		BufferedImage scaledImage = new BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_ARGB);
		Graphics g = scaledImage.createGraphics();
		g.drawImage(image, 0, 0, size.x, size.y, null);
		g.dispose();
		return scaledImage;
	}
	
	public static void main(String[] args) {
		new GeneticImage(IMAGE_PATH);
	}
	
	private class GeneticAlgThread implements Runnable {
		
		PolygonImage[] threadPopulation;
		
		public GeneticAlgThread(PolygonImage[] threadPopulation) {
			this.threadPopulation = threadPopulation;
		}
		
		@Override
		public void run() {
			threadPopulation = geneticAlg.process(threadPopulation);
		}
		
		public PolygonImage getProcessingImage() {
			return geneticAlg.getProcessingImage();
		}
		
		public PolygonImage[] getPopulation() {
			return threadPopulation;
		}		
	}
}
