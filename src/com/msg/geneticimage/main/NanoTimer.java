package com.msg.geneticimage.main;

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
	
	public long getElapsedTime() {
		return elapsedNanoTime;
	}
	
	public void printElapsedTime() {
		System.out.println(this);
	}
	
	public void printElapsedTime(String addOn) {
		System.out.println(this + addOn);
	}
	
	@Override
	public String toString() {
		return "Elapsed time: " + elapsedNanoTime / 1000000000.0 + " seconds.";
	}
}
