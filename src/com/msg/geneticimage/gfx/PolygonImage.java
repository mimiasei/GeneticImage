package com.msg.geneticimage.gfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Random;

import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.interfaces.Gene;
import com.msg.geneticimage.main.GeneticImage;

public class PolygonImage implements Gene, Comparable<PolygonImage> { // extends JComponent
	
	private ArrayList<Polygon> polygons = new ArrayList<Polygon>();
	private GeneticImage geneticImage;
	private long fitness = Long.MAX_VALUE;
	private int numberOfPolygons, age;
	private String name;
	
	/**
	 * 
	 * @param geneticImage
	 * @param numberOfPolygons
	 */
	public PolygonImage(GeneticImage geneticImage, int numberOfPolygons) {
		this.geneticImage = geneticImage;
		this.numberOfPolygons = numberOfPolygons;
		this.name = "PolygonImage";
		resetAge();
	}
	
	public PolygonImage(PolygonImage clone) {
		this.geneticImage = clone.geneticImage;
		this.fitness = clone.fitness;
		this.numberOfPolygons = clone.numberOfPolygons;
		this.polygons.clear();
		this.polygons.addAll(clone.polygons);
		this.name = clone.name;
		this.age = clone.age;
	}
	
	public void addPolygon(Polygon polygon) {
		polygons.add(polygon);
	}
	
	public void removePolygon(int index) {	
		polygons.remove(index);
	}
	
	public void setNewPolyCount(int polyCount) {
		int addPolyCount = polyCount - polygons.size();
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
	
	public Polygon[] getPolygons() {
		Polygon[] polys = new Polygon[polygons.size()];
		return polygons.toArray(polys);
	}
	
	public void setPolygons(ArrayList<Polygon> polygons) {
		this.polygons.clear();
		this.polygons.addAll(polygons);
		numberOfPolygons = polygons.size();
	}
	
	public int getNumberOfPolygons() {
		return polygons.size();
	}

	public static BufferedImage paintImage(int w, int h, Polygon[] polygons, int shiftAmount, Color bgColour) {
		BufferedImage offImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D)offImg.createGraphics();
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(bgColour);
		g2.fillRect(0, 0, w, h);			
		for (Polygon poly : polygons) {		
			g2.setColor(poly.getColour());
			if(shiftAmount > 0)
				g2.fillPolygon(scaleCoords(poly.getPolygonX(), shiftAmount), 
						scaleCoords(poly.getPolygonY(), shiftAmount), poly.getVertexLength());
			else
				g2.fillPolygon(poly.getPolygonX(), poly.getPolygonY(), poly.getVertexLength());
		}
		g2.dispose();
		return offImg;
	}
	
	public static BufferedImage paintImage(int w, int h, Polygon[] polygons, int shiftAmount) {
		return paintImage(w, h, polygons, shiftAmount, Color.BLACK);
	}
	
	public static int[] scaleCoords(int[] coordsArray, int shiftAmount) {
		for (int i = 0; i < coordsArray.length; i++)
			coordsArray[i] = coordsArray[i] >> shiftAmount;
		return coordsArray;
	}
 	
	public BufferedImage getImage() {
		return paintImage(geneticImage.getWidth(), geneticImage.getHeight(), 
				getPolygons(), geneticImage.getBitShift(), geneticImage.getBgColour());
	}
	
	public GeneticImage getGeneticImage() {
		return geneticImage;
	}

	public void setGeneticImage(GeneticImage geneticImage) {
		this.geneticImage = geneticImage;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getAge() {
		return age;
	}

	public void resetAge() {
		this.age = 0;
	}
	
	public void incrAge() {
		age++;
	}
	
	public int agedFitness() {
		return age * age;
	}

	public long getFitness() {
		incrAge();
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
		float edgeR = 1.0f, edgeG = 1.0f, edgeB = 1.0f;
		int step = Cons.FITNESS_PIXEL_STEP << 2; // <<2 = *4
		for (int pixel = 0; pixel < comparePixels.length; pixel += step) {
			/* Get compareImage pixel colours per channel. */
			blue = (int)(comparePixels[pixel + 1] & 0xff); // blue
			green = (int)((comparePixels[pixel + 2] >> 8) & 0xff); // green
			red = (int)((comparePixels[pixel + 3] >> 16) & 0xff); // red
			
			if(edgeComparePixels != null) {
				/* Get edgeCompareImage pixel normalized values (0 - 1.0) per channel. */
				edgeB = ((edgeComparePixels[pixel + 1] & 0xff) / 0xff); // blue
				edgeG = (((edgeComparePixels[pixel + 2] >> 8) & 0xff) / 0xff); // green
				edgeR = (((edgeComparePixels[pixel + 3] >> 16) & 0xff) / 0xff); // red
				
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
			}
			
			/* Get delta per colour. */
			int dB = (int) (((imagePixels[pixel + 1] & 0xff) - blue) * edgeB);
			int dG = (int) ((((imagePixels[pixel + 2] >> 8) & 0xff) - green) * edgeG);
			int dR = (int) ((((imagePixels[pixel + 3] >> 16) & 0xff) - red) * edgeR);
			int penalty = (polygons.size() > Cons.POLYGON_COUNT) ? polygons.size() - Cons.POLYGON_COUNT : 0;
			penalty <<= 1;
			pixelFitness += (dB * dB + dG * dG + dR * dR) * (step >> 2) + penalty;
		}
		return pixelFitness;
	}
	
	public static Color getAverageColour(int[] imagePixels) {
		int red = 0, green = 0, blue = 0, counter = 0;
		for (int pixel = 0; pixel < imagePixels.length; pixel += 4) {
			/* Get compareImage pixel colours per channel. */
			blue += (int)(imagePixels[pixel + 1] & 0xff); // blue
			green += (int)((imagePixels[pixel + 2] >> 8) & 0xff); // green
			red += (int)((imagePixels[pixel + 3] >> 16) & 0xff); // red	
			counter++;			
		}
		return new Color(red / counter, green / counter, blue / counter);
	}
	
	/**
	 * Injects random number of new polygons between given min and max values. 
	 * @param min
	 * @param max
	 */
	public void bloodInjection(int min, int max) {
		Random random = new Random(System.nanoTime());		
		int polyCount = random.nextInt(max - min) + min;
		for (int i = 0; i < polyCount; i++)
			this.addPolygon(new Polygon(this));
	}
	
	/**
	 * Swap two random polygon positions in the list.
	 */
	public void swapRandom() {
		Random random = new Random(System.nanoTime());
		int pos1 = random.nextInt(polygons.size());
		int pos2 = random.nextInt(polygons.size());
		Polygon temp = polygons.get(pos1);
		polygons.set(pos1, polygons.get(pos2));
		polygons.set(pos2, temp);
	}
	
	/**
	 * Mutate number of polygons.
	 */
	public void mutatePolyCount() {
		Random random = new Random(System.nanoTime());
		if(random.nextDouble() < Cons.CHANGE_POLYCOUNT_RATIO)
			if(random.nextBoolean())
				addPolygon(new Polygon(this));
			else
				if(polygons.size() > (Cons.POLYGON_COUNT >> Cons.POLYCOUNT_INITIATE_SHIFT))
					removePolygon(random.nextInt(polygons.size()));
	}
	
	@Override
	public int compareTo(PolygonImage o) {
        if(this.fitness > o.getFitness()) return 1;
        if(this.fitness < o.getFitness()) return -1;
        return 0;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public void mutate() {
		for (Polygon poly : polygons)
			poly.mutate();
	}

	@Override
	public void generateRandom() {
		/* Create and add numberOfPolygons number of polygons 
		 * to PolygonImage object.
		 */
		for (int i = 0; i < numberOfPolygons; i++)		
			/* Add the new polygon to list in PolygonImage. */
			addPolygon(new Polygon(this));		
	}
}

