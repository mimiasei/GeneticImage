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
		this.image = image;
		compareImage = new BufferedImage(this.image.getWidth(), this.image.getWidth(), BufferedImage.TYPE_INT_ARGB);
		
		/* Recursive loop starting from 1/32 size of input image, up to full scale. */
		Point startSize = new Point(compareImage.getWidth(), compareImage.getWidth()), currentSize;
		System.out.println("Start size: " + startSize);
		for (int i = 5; i >= 0; i--) {
			currentSize = new Point(startSize.x >> i, startSize.y >> i);
			System.out.println("Shifting image by: " + i + " -> Current size: " + currentSize);
			compareImage = new BufferedImage(currentSize.x, currentSize.y, BufferedImage.TYPE_INT_ARGB);
			Graphics g = compareImage.createGraphics();
			g.drawImage(this.image, 0, 0, currentSize.x, currentSize.y, null);
			g.dispose();
		}
		
		startSize = new Point(compareImage.getWidth(), compareImage.getWidth());
		System.out.println("new Start size: " + startSize);
		
		JFrame frame = new JFrame();	
		createImage = new CreatePolygonImage(compareImage.getWidth(), compareImage.getHeight());
		
		geneticAlg = new GeneticAlgorithm(compareImage);
//		geneticAlg.setMaxIterations(1);
		geneticAlg.setMaxFitnessRatio(0.999f);
		createImage = geneticAlg.process(createImage);
		startFitness = createImage.getFitness();
		fitness = startFitness;
		
		/* Set number of generations. */
//		geneticAlg.setMaxIterations(NUMBER_OF_GENERATIONS);
		geneticAlg.setMaxFitnessRatio(MAX_CURRENT_START_FITNESS_RATIO);
		
		GeneticAlgThread runnableGenAlg = new GeneticAlgThread(createImage);
		Thread thread = new Thread(runnableGenAlg, "GeneticAlgorithm Thread"); 
		thread.start();
		
		System.out.println("Start fitness: " + startFitness);
		
		JLabel label = new JLabel(new ImageIcon(createImage.getImage()));
		
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(new JLabel(new ImageIcon(compareImage)));
		frame.getContentPane().add(label);
		frame.pack();
		frame.setVisible(true);

		/* Run until current fitness is half the magnitude of starting fitness. */
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
			frame.pack();
		}
		createImage = runnableGenAlg.getCreateImage();
		if(createImage != null)
			System.out.println("Final fitness = " + createImage.getFitness());
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
		try {
		    compareImage = ImageIO.read(new File("src/com/msg/geneticimage/main/ask.jpg"));
		} catch (IOException e) {
			System.out.println("Image file not found!");
		}
		new GeneticImage(compareImage);
	}

}
