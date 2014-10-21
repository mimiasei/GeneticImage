package com.msg.geneticimage.gfx;

import java.awt.Color;
import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

import com.msg.geneticimage.interfaces.Gene;
import com.msg.geneticimage.main.GeneticImage;
import com.msg.geneticimage.main.NanoTimer;

public class PolygonImage implements Comparable<PolygonImage> { // extends JComponent
	
	private ArrayList<Polygon> polygons = new ArrayList<Polygon>();
	private GeneticImage geneticImage;
	private long fitness = Long.MAX_VALUE;
	private int numberOfPolygons;
	
	public PolygonImage(GeneticImage geneticImage, int numberOfPolygons) {
		this.geneticImage = geneticImage;
		this.numberOfPolygons = numberOfPolygons;
	}
	
	public PolygonImage(PolygonImage clone) {
		this.geneticImage = clone.geneticImage;
		this.fitness = clone.fitness;
		this.numberOfPolygons = clone.numberOfPolygons;
		this.polygons.clear();
		this.polygons.addAll(clone.polygons);
	}
	
	public void addPolygon(Polygon polygon) {
		polygons.add(polygon);
	}
	
	public void setNewPolyCount(int polyCount) {
		int addPolyCount = polyCount - numberOfPolygons;
		if(addPolyCount > 0) {
			for (int i = 0; i < addPolyCount; i++)
				polygons.add(new Polygon(this));
			numberOfPolygons = polygons.size();
		}
	}
	
	public Polygon getPolygon(int index) {
		return polygons.get(index);
	}
	
	public void setPolygon(int index, Polygon polygon) {
		if(index < polygons.size())
			polygons.set(index, polygon);
	}
	
	public int getNumberOfPolygons() {
		return numberOfPolygons;
	}

	public BufferedImage paintImage() {
		int w = geneticImage.getWidth(), h = geneticImage.getHeight();
		BufferedImage offImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
//		BufferedImage offImg = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g2 = offImg.createGraphics();
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.clearRect(0, 0, w, h);
				
		for (Polygon poly : polygons) {		
			g2.setColor(poly.getColour());
			g2.fillPolygon(scaleCoords(poly.getPolygonX()), 
					scaleCoords(poly.getPolygonY()), poly.getVertexLength());
		}
		g2.setColor(Color.LIGHT_GRAY);
		g2.dispose();
		return offImg;
	}
	
	public int[] scaleCoords(int[] coordsArray) {
		int shiftAmount = geneticImage.getBitShift();
		for (int i = 0; i < coordsArray.length; i++)
			coordsArray[i] = coordsArray[i] >> shiftAmount;
		return coordsArray;
	}
 	
	public BufferedImage getImage() {
		return paintImage();
	}
	
	public GeneticImage getGeneticImage() {
		return geneticImage;
	}

	public void setGeneticImage(GeneticImage geneticImage) {
		this.geneticImage = geneticImage;
	}

	public long getFitness() {
		return fitness;
	}

	public void setFitness(long fitness) {
		this.fitness = fitness;
	}
	
	public void calculateFitness() {
		fitness = getFitness(geneticImage.getCurrentImagePixels(), this.getImage());
	}
	
	public long getFitness(int[] imagePixels, BufferedImage compareImage) {
		final int[] comparePixels = ((DataBufferInt) compareImage.getRaster().getDataBuffer()).getData();
		final int[] edgeComparePixels = GeneticImage.getCurrentImagePixels(geneticImage.getEdgeCompareImage());
		long pixelFitness = 0;
		int red, green, blue;
		float edgeR, edgeG, edgeB;
		for (int pixel = 0; pixel < comparePixels.length; pixel += 4) {
			/* Get compareImage pixel colours per channel. */
			blue = (int)(comparePixels[pixel + 1] & 0xff); // blue
			green = (int)((comparePixels[pixel + 2] >> 8) & 0xff); // green
			red = (int)((comparePixels[pixel + 3] >> 16) & 0xff); // red
			
			/* Get edgeCompareImage pixel normalized values (0 - 1.0) per channel. */
			edgeB = ((edgeComparePixels[pixel + 1] & 0xff) / 255); // blue
			edgeG = (((edgeComparePixels[pixel + 2] >> 8) & 0xff) / 255); // green
			edgeR = (((edgeComparePixels[pixel + 3] >> 16) & 0xff) / 255); // red
			
			/*
			 * I want to use an edge pixel colour value to make high contrast areas
			 * of the bitmap compare image more attractive to the algorithm. The
			 * way I'm thinking of doing this is increasing the fitness score more in
			 * the high contrast areas, thus luring the algorithm towards focusing
			 * on those areas.
			 * 
			 * deltaP = original image pixel - generated image pixel
			 * High Contrast (HC), Low Contrast (LC), Fitness (F)
			 * F(HC) = deltaP * 1.0 = Normal fitness score
			 * F(LC) = deltaP * 2.0 = High fitness score
			 * edgeP = 0.0 (LC) - 1.0 (HC)
			 * F = deltaP * (2.0 - edgeP) => high fitness when LC
			 */
			
			edgeB = (1.5f - (edgeB * 0.5f));
			edgeG = (1.5f - (edgeG * 0.5f));
			edgeR = (1.5f - (edgeR * 0.5f));
			
			/* Get delta per colour. */
			pixelFitness += Math.abs((int)(imagePixels[pixel + 1] & 0xff) - blue) * edgeB;
			pixelFitness += Math.abs((int)((imagePixels[pixel + 2] >> 8) & 0xff) - green) * edgeG;
			pixelFitness += Math.abs((int)((imagePixels[pixel + 3] >> 16) & 0xff) - red) * edgeR;
		}
		return pixelFitness;
	}
	
//	public static long getFitness(BufferedImage image, BufferedImage compareImage) {
//		long fitness = 0; 
//		long pixelFitness;
//		int imageC, compareC;
//		for (int y = 0; y < image.getHeight(); y ++)
//			for (int x = 0; x < image.getWidth(); x ++) {
//				pixelFitness = 0;
//				imageC = image.getRGB(x, y);
//				compareC = compareImage.getRGB(x, y);
//				
//				/* Get delta per colour. */
//				pixelFitness += Math.abs((imageC & 0xff) - (compareC & 0xff));
//				pixelFitness += Math.abs(((imageC >> 8) & 0xff) - ((compareC >> 8) & 0xff));
//				pixelFitness += Math.abs(((imageC >> 16) & 0xff) - ((compareC >> 16) & 0xff));
//		 
//		        /* Add the pixel fitness to the total fitness (lower is better). */
//				fitness += pixelFitness;
//			}	 
//		return fitness;
//	}
	
	@Override
	public int compareTo(PolygonImage o) {
        if(this.fitness > o.getFitness()) return 1;
        if(this.fitness < o.getFitness()) return -1;
        return 0;
	}
}

