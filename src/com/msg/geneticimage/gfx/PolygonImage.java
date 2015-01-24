package com.msg.geneticimage.gfx;

//import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.interfaces.Gene;
import com.msg.geneticimage.main.FitnessCalc;
import com.msg.geneticimage.main.GeneticImage;
import com.msg.geneticimage.tools.Tools;

public class PolygonImage implements Gene, Comparable<PolygonImage> { // extends JComponent
	
	private ArrayList<Polygon> polygons = new ArrayList<Polygon>();
	private GeneticImage genImage;
	private long fitness = Long.MAX_VALUE;
	private int numberOfPolygons;
	private String name;
	private boolean isDirty;
	
	/**
	 * 
	 * @param genImage
	 * @param numberOfPolygons
	 */
	public PolygonImage(GeneticImage genImage, int numberOfPolygons) {
		this.genImage = genImage;
		this.numberOfPolygons = numberOfPolygons;
		this.name = "PolygonImage";
		isDirty = false;
	}
	
	public PolygonImage(PolygonImage clone) {
		this.genImage = clone.genImage;
		this.fitness = clone.fitness;
		this.numberOfPolygons = clone.numberOfPolygons;
		this.polygons.clear();
		this.polygons.addAll(clone.polygons);
		this.name = clone.name;
	}
 	
	public BufferedImage getImage() {
		return Renderer.paintImage(genImage.getCompareImage().getWidth(), genImage.getCompareImage().getHeight(), 
				getPolygons(), genImage.getBitShift(), genImage.getBgColour());
	}
	
	public GeneticImage getGeneticImage() {
		return genImage;
	}
	
	public void addPolygon(Polygon polygon) {
		if(polygons.size() < Cons.NBR_POLYGON_COUNT >> genImage.getBitShift())
			polygons.add(polygon);
	}
	
	public void removePolygon(int index) {	
		polygons.remove(index);
	}
	
	public void setNewPolyCount(int polyCount) {
		int addPolyCount = polyCount - polygons.size();
		if(addPolyCount > 0) {
			for (int i = 0; i < addPolyCount; i++)
				addPolygon(new Polygon(genImage.getCompareImage().getWidth(), 
						genImage.getCompareImage().getHeight()));
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
		fitness = FitnessCalc.getFitness(genImage.getCurrentImagePixels(), 
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
			this.addPolygon(new Polygon(genImage.getCompareImage().getWidth(), 
					genImage.getCompareImage().getHeight()));
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
		if(Tools.mutatable(Cons.CHANCE_REMOVE_POLY_RATIO) && polygons.size() > 2) {
			removePolygon(Tools.rndInt(0, polygons.size()-1));
		} else {
			addPolygon(new Polygon(genImage.getCompareImage().getWidth(), 
					genImage.getCompareImage().getHeight()));
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
			Polygon polygon = new Polygon(genImage.getCompareImage().getWidth(), 
					genImage.getCompareImage().getHeight());
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

