package com.msg.geneticimage.main;

import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.msg.geneticimage.gfx.Polygon;
import com.msg.geneticimage.gfx.PolygonImage;
import com.msg.geneticimage.interfaces.Constants;

public class Main implements Constants {
	
	private BufferedImage image;
	
	public Main() {
				
		/* Load compare image. */
		try {
			image = ImageIO.read(this.getClass().getClassLoader().getResource(IMAGE_PATH));
		} catch (IOException e) {
			System.out.println("Cannot find image file!");
			System.exit(0);
		}
		
		byte chunksCount = 8;
		
		/* Start a thread of GeneticImage for each image chunk. */
		ArrayList<GenAlgThread> threadsList = new ArrayList<GenAlgThread>();
		PolygonImage[] polyImages = new PolygonImage[chunksCount];
		Thread thread;
		
		for (byte i = 0; i < chunksCount; i++) {
			GenAlgThread genAlgThread = new GenAlgThread(chunksCount, i);
			threadsList.add(genAlgThread);
			thread = new Thread(genAlgThread);
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
			}
		}
		
		/* Put together all chunks and display resulting image. */
		JLabel compareImageLabel = new JLabel(new ImageIcon(image));
		JLabel bestPolygonImageLabel = new JLabel(new ImageIcon(
				constructImage(polyImages, image.getWidth(), image.getHeight(), chunksCount)));
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
	
	public BufferedImage constructImage(PolygonImage[] polyImages, int w, int h, int chunksCount) {
		int rows = getRows(chunksCount);
		int columns = getColumns(chunksCount, rows);
		int chunkW = w / columns;
		int chunkH = h / rows;
		int xPos, yPos;
		ArrayList<Polygon> polygons = new ArrayList<Polygon>();
		for (int i = 0; i < polyImages.length; i++)
			for (Polygon poly : polyImages[i].getPolygons()) {
				xPos = poly.getOrigo().x + getColumn(i, columns) * chunkW;
				yPos = poly.getOrigo().y + getRow(i, columns) * chunkH;
				poly.setOrigo(new Point(xPos, yPos));
				polygons.add(poly);
			}
		Polygon[] polys = new Polygon[polygons.size()];
		BufferedImage finalImage = PolygonImage.paintImage(w, h, polygons.toArray(polys), 0);
		return finalImage;
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
	
	class GenAlgThread implements Runnable {
		
		private int count, nbr;
		private PolygonImage polyImage;
		
		public GenAlgThread(int count, int nbr) {
			this.polyImage = null;
			this.count = count;
			this.nbr = nbr;
		}

		@Override
		public void run() {
			BufferedImage chunk = getImageChunk(image, count, nbr);
			if(chunk != null)
				/* Round poly count upwards. */
				polyImage = new GeneticImage(chunk).process((int)Math.round(POLYGON_COUNT/(double)count));
		}
		
		public PolygonImage getPolyImage() {
			return polyImage;
		}	
	}

}
