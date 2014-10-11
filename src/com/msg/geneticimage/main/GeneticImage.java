package com.msg.geneticimage.main;

import java.awt.FlowLayout;
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
	private BufferedImage compareImage;
	private CreatePolygonImage createImage;
	
	public GeneticImage() {
		JFrame frame = new JFrame();
		compareImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

		try {
		    compareImage = ImageIO.read(new File("src/com/msg/geneticimage/main/ask.jpg"));
		} catch (IOException e) {
			System.out.println("Image file not found!");
		}

		createImage = new CreatePolygonImage(compareImage.getWidth(), compareImage.getHeight());
		
		geneticAlg = new GeneticAlgorithm(compareImage);
		/* Set number of generations. */
		geneticAlg.setMaxIterations(NUMBER_OF_GENERATIONS);
		
		GeneticAlgThread runnableGenAlg = new GeneticAlgThread(createImage);
		Thread thread = new Thread(runnableGenAlg, "GeneticAlgorithm Thread"); 
		thread.start();
		
		JLabel label = new JLabel(new ImageIcon(createImage.getImage()));
		
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(new JLabel(new ImageIcon(compareImage)));
		frame.getContentPane().add(label);
		frame.pack();
		frame.setVisible(true);

		while(true) {
			/* Check current best image by fitness every CHECK_FREQUENCY seconds. */
			try { 
				Thread.sleep(CHECK_FREQUENCY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			createImage = runnableGenAlg.getProcessingImage();
			frame.setVisible(false);
			frame.getContentPane().remove(label);
			frame.pack();
			label = new JLabel(new ImageIcon(createImage.getImage()));
			frame.getContentPane().add(label);
			frame.pack();
			frame.setVisible(true);
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
		new GeneticImage();
	}

}
