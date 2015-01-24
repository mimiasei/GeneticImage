package com.msg.geneticimage.tools;

public class NanoTimer {
	private long startNanoTime, elapsedNanoTime;
	
	public NanoTimer() {
		startTimer();
		stopTimer();
	}
	
	public void startTimer() {
		startNanoTime = System.nanoTime();
	}
	
	public void stopTimer() {
		elapsedNanoTime = System.nanoTime() - startNanoTime;
	}
	
	public String getElapsedTime() {
		return String.valueOf((double)Math.round(((System.nanoTime() - startNanoTime) / 
				60000000000.0) * 1000) / 1000) + " min";
	}
	
	@Override
	public String toString() {
		return String.valueOf((double)Math.round((elapsedNanoTime / 
				1000000000.0) * 1000) / 1000) + " sec";
	}
}
