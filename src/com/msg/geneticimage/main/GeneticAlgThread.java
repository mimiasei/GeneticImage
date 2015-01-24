package com.msg.geneticimage.main;

import com.msg.geneticimage.algorithm.GeneticAlgorithm;
import com.msg.geneticimage.gfx.PolygonImage;

public class GeneticAlgThread implements Runnable {
	
	PolygonImage[] threadPopulation;
	GeneticAlgorithm genAlg;
	
	
	public GeneticAlgThread(GeneticAlgorithm genAlg) {
		this.genAlg = genAlg;
		this.threadPopulation = genAlg.getPopulation();
	}
	
	@Override
	public void run() {
		threadPopulation = genAlg.process(threadPopulation);
	}
	
	public PolygonImage getProcessingImage() {
		return genAlg.getCurrentImage();
	}
	
	public PolygonImage getBestImage() {
		return genAlg.getCurrentBestImage();
	}
	
	public PolygonImage[] getPopulation() {
		return threadPopulation;
	}
}
