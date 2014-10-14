package com.msg.geneticimage.main;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.msg.geneticimage.algorithm.GeneticAlgorithm;
import com.msg.geneticimage.gfx.CreatePolygonImage;
import com.msg.geneticimage.interfaces.Constants;

@SuppressWarnings("serial")
public class GeneticImage extends JPanel implements Constants {
	
	private GeneticAlgorithm geneticAlg;
	private BufferedImage image, compareImage;
	private CreatePolygonImage createImage, resultImage = null;
	long startFitness, fitness;
	
	public GeneticImage(BufferedImage image) {
		JFrame frame = new JFrame();
		JLabel label, label2;
		Thread thread;	
		GeneticAlgThread runnableGenAlg;
		this.image = image;
		
		try {
//			compareImage = ImageIO.read(new File(IMAGE_PATH));
			this.image = ImageIO.read(this.getClass().getClassLoader().getResource(IMAGE_PATH));
		} catch (IOException e) {
			System.out.println("Cannot find image file!");
		}
		compareImage = new BufferedImage(this.image.getWidth(), this.image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		Point startSize = new Point(compareImage.getWidth(), compareImage.getHeight()), currentSize;
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setAlwaysOnTop(true);

		/* Multi-level scaling loop. Bit-shifting from 5 to 0, meaning divide by 32 to 1.
		 * Running genetic algorithm first on the 1/32 scale image, then taking the result
		 * into the next scaling level (1/16 and so on).
		 */
		int imageArea = startSize.x * startSize.y;
		byte bitShift = 1;
		while (imageArea > 300) 
			imageArea >>= bitShift++;
		bitShift--;
		
		for (int i = bitShift; i >= 0; i--) {
			// DEBUG
			System.out.println("\nInitial (start) size: " + startSize);
			currentSize = new Point(startSize.x >> i, startSize.y >> i);
			// DEBUG
			System.out.println("Shifting image by: " + i + " -> Current size: " + currentSize);
			compareImage = new BufferedImage(currentSize.x, currentSize.y, BufferedImage.TYPE_INT_ARGB);
			Graphics g = compareImage.createGraphics();
			g.drawImage(this.image, 0, 0, currentSize.x, currentSize.y, null);
			g.dispose();
		
			Point newSize = new Point(compareImage.getWidth(), compareImage.getHeight());
			// DEBUG
			System.out.println("new Start size: " + newSize);

			createImage = new CreatePolygonImage(compareImage.getWidth(), compareImage.getHeight());
			
			geneticAlg = new GeneticAlgorithm(compareImage);
			geneticAlg.setMaxFitnessRatio(0.999f);
//			float polygonRatio = (float)Math.pow(POLYGON_COUNT_POWER_FACTOR, i);
//			geneticAlg.setMaxIterations((int)(POLYGON_COUNT * polygonRatio));
			geneticAlg.setMaxIterations(POLYGON_COUNT);
			
			// DEBUG
			System.out.println("Polygon count for random alg: " + geneticAlg.getMaxIterations());
			
			createImage = geneticAlg.process(createImage);
			startFitness = createImage.getFitness();
			fitness = startFitness;
			
			/* Set number of generations. */
			geneticAlg.setMaxFitnessRatio(MAX_CURRENT_START_FITNESS_RATIO);
			
			runnableGenAlg = new GeneticAlgThread(createImage);
			thread = new Thread(runnableGenAlg); 
			thread.start();
			// DEBUG
			System.out.println("Start fitness: " + startFitness);
			
			label = new JLabel(new ImageIcon(createImage.getImage()));
			label2 = new JLabel(new ImageIcon(compareImage));
			
			frame.add(this);
			frame.getContentPane().setLayout(new FlowLayout());
			frame.getContentPane().add(label2);
			frame.getContentPane().add(label);
			frame.setTitle("Best: " + createImage.getFitness());
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
				createImage = runnableGenAlg.getProcessingImage();
				frame.getContentPane().remove(label);
				label = new JLabel(new ImageIcon(createImage.getImage()));
				frame.getContentPane().add(label);
				frame.setTitle("Best: " + createImage.getFitness());
				frame.pack();
			}
			
			/* Hide and empty the frame window. */
			if(frame.isVisible())
				frame.setVisible(false);
			frame.remove(label);
			frame.remove(label2);			
			frame.remove(this);
			
			createImage = runnableGenAlg.getCreateImage();
			
			// DEBUG
			if(createImage != null)
				System.out.println("Final fitness = " + createImage.getFitness());
		}
	}
	
	private class GeneticAlgThread implements Runnable {
		
		CreatePolygonImage threadCreateImage;
		
		public GeneticAlgThread(CreatePolygonImage threadCreateImage) {
			this.threadCreateImage = threadCreateImage;
		}
		
		@Override
		public void run() {
			threadCreateImage = geneticAlg.process(new CreatePolygonImage(compareImage.getWidth(), compareImage.getHeight()));
		}
		
		public CreatePolygonImage getProcessingImage() {
			return geneticAlg.getProcessingImage();
		}
		
		public CreatePolygonImage getCreateImage() {
			return threadCreateImage;
		}		
	}
	
	public static void main(String[] args) {
		BufferedImage compareImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		new GeneticImage(compareImage);
	}

}
