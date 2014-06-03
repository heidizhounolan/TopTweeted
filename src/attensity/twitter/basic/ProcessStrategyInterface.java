package attensity.twitter.basic;

import java.util.ArrayList;

/*
 * Interface of the strategy to retrieve top K tweeted
 * 
 * Currently, the only implementation is max heap strategy. 
 * 
 * Other fancy algorithms can be implemented, e.g. lossy counting, merge sort using multiple machines. 
 */
public interface ProcessStrategyInterface<T> {
	
	public void initialize(ElementInterface<T>[] ta);
	
	public ArrayList<T> getTopKTweeted(int k);

}
