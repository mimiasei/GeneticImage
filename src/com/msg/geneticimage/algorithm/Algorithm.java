package com.msg.geneticimage.algorithm;

public abstract class Algorithm<T> {
	
	protected int maxIterations;
	
	/**
	 * Method for processing given object type T array,
	 * returning new object type T.
	 * 
	 * @param generic object T
	 * @return new generic object T
	 */
	public abstract T process(T object);
	
	public int getMaxIterations() {
		return maxIterations;
	}

	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}
}
