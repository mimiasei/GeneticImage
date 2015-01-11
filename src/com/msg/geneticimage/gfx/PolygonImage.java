package com.msg.geneticimage.gfx;

//import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.interfaces.Gene;
import com.msg.geneticimage.main.FitnessCalc;
import com.msg.geneticimage.main.GeneticImage;
import com.msg.geneticimage.main.Tools;

public class PolygonImage implements Gene, Comparable<PolygonImage> { // extends JComponent
	
	private ArrayList<Polygon> polygons = new ArrayList<Polygon>();
	private GeneticImage geneticImage;
	private long fitness = Long.MAX_VALUE;
	private int numberOfPolygons;
	private String name;
	private boolean isDirty;
	
	/**
	 * 
	 * @param geneticImage
	 * @param numberOfPolygons
	 */
	public PolygonImage(GeneticImage geneticImage, int numberOfPolygons) {
		this.geneticImage = geneticImage;
		this.numberOfPolygons = numberOfPolygons;
		this.name = "PolygonImage";
		isDirty = false;
	}
	
	public PolygonImage(PolygonImage clone) {
		this.geneticImage = clone.geneticImage;
		this.fitness = clone.fitness;
		this.numberOfPolygons = clone.numberOfPolygons;
		this.polygons.clear();
		this.polygons.addAll(clone.polygons);
		this.name = clone.name;
	}
 	
	public BufferedImage getImage() {
		return Renderer.paintImage(geneticImage.getCompareImage().getWidth(), geneticImage.getCompareImage().getHeight(), 
				getPolygons(), geneticImage.getBitShift(), geneticImage.getBgColour());
	}
	
	public GeneticImage getGeneticImage() {
		return geneticImage;
	}

	public void setGeneticImage(GeneticImage geneticImage) {
		this.geneticImage = geneticImage;
	}
	
	public void addPolygon(Polygon polygon) {
		if(polygons.size() < Cons.POLYGON_COUNT + 
			Tools.rndInt(0, (Cons.POLYGON_COUNT >> Cons.POLYCOUNT_INITIATE_SHIFT))) {
			polygons.add(polygon);
		}
	}
	
	public void removePolygon(int index) {	
		polygons.remove(index);
	}
	
	public void setNewPolyCount(int polyCount) {
		int addPolyCount = polyCount - polygons.size();
		if(addPolyCount > 0) {
			for (int i = 0; i < addPolyCount; i++)
				addPolygon(new Polygon(geneticImage.getCompareImage().getWidth(), 
						geneticImage.getCompareImage().getHeight()));
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
	
	public int getPolyCount() {
		return polygons.size();
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	public long getFitness() {
		return fitness;
	}

	public void setFitness(long fitness) {
		this.fitness = fitness;
	}
	
	public void calculateFitness() {
		fitness = FitnessCalc.getFitness(geneticImage.getCurrentImagePixels(), 
				Renderer.getPixelsArray(this.getImage()));
	}
	
	/**
	 * Injects random number of new polygons between given min and max values. 
	 * @param min
	 * @param max
	 */
	public void bloodInjection(int min, int max) {
		int polyCount = Tools.rndInt(min, max);
		for (int i = 0; i < polyCount; i++)
			this.addPolygon(new Polygon(geneticImage.getCompareImage().getWidth(), 
					geneticImage.getCompareImage().getHeight()));
	}
	
	/**
	 * Swap two random polygon positions in the list.
	 */
	public void swapRandom() {
		int pos1 = Tools.rndInt(0, polygons.size()-1);
		int pos2 = Tools.rndInt(0, polygons.size()-1);
		Polygon temp = polygons.get(pos1);
		polygons.set(pos1, polygons.get(pos2));
		polygons.set(pos2, temp);
	}
	
	/**
	 * Mutate number of polygons.
	 */
	public void mutatePolyCount() {
		if(Tools.mutatable(Cons.REMOVE_POLY_RATIO)) {
			if(polygons.size() > 2)
				removePolygon(Tools.rndInt(0, polygons.size()-1));
		} else {
			addPolygon(new Polygon(geneticImage.getCompareImage().getWidth(), 
					geneticImage.getCompareImage().getHeight()));
		}
	}

	@Override
	public void mutate() {
		for (Polygon poly : polygons)
			poly.mutate();
	}

	@Override
	public void generateRandom() {
		/* Create and add numberOfPolygons number of polygons 
		 * to PolygonImage object. */
		polygons.clear();
		for (int i = 0; i < numberOfPolygons; i++) {
			Polygon polygon = new Polygon(geneticImage.getCompareImage().getWidth(), 
					geneticImage.getCompareImage().getHeight());
			/* Add the new polygon to list in PolygonImage. */
			addPolygon(polygon);
		}
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
}

