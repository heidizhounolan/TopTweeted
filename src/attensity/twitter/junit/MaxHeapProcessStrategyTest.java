package attensity.twitter.junit;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import attensity.twitter.basic.ElementInterface;
import attensity.twitter.basic.HashTagElement;
import attensity.twitter.basic.MaxHeapProcessStrategy;
import attensity.twitter.basic.ProcessStrategyInterface;
import attensity.twitter.tweeted.TopTweeted;

public class MaxHeapProcessStrategyTest {

	@Test
	public void testGetTopKNodes() {
		ProcessStrategyInterface<String> maxHeapStrategy = new MaxHeapProcessStrategy<String>(TopTweeted.SIXTY_SECONDS_IN_MILLIS);
		int arrayLen=80;
		@SuppressWarnings("unchecked")
		ElementInterface<String>[] ta = (ElementInterface<String>[]) new HashTagElement[arrayLen];
		int i=0;
		for(; i < ta.length/8; ++i) {
			ta[i] = new HashTagElement<String>("a", System.currentTimeMillis());
		}
		for(int j=0; j < ta.length/4 && i < ta.length; j++) {
			ta[i++] = new HashTagElement<String>("b", System.currentTimeMillis());
		}
		for(int j=0; j < ta.length*5/8 && i < ta.length; j++) {
			ta[i++] = new HashTagElement<String>("c", System.currentTimeMillis());
		}
		maxHeapStrategy.initialize(ta);
		ArrayList<String> topKs = maxHeapStrategy.getTopKTweeted(3);
		//In descending order
		assertTrue(topKs.get(0).equals("c") && topKs.get(1).equals("b") && topKs.get(2).equals("a"));			
	}

	@Test
	public void testGetTopKNodesWith0Input() {
		ProcessStrategyInterface<String> maxHeapStrategy = new MaxHeapProcessStrategy<String>(TopTweeted.SIXTY_SECONDS_IN_MILLIS);
		int arrayLen=0;
		@SuppressWarnings("unchecked")
		ElementInterface<String>[] ta = (ElementInterface<String>[]) new HashTagElement[arrayLen];
		maxHeapStrategy.initialize(ta);
		ArrayList<String> topKs = maxHeapStrategy.getTopKTweeted(3);
		assertTrue(topKs.size() == 0);
	}

	@Test
	public void testGetTopKNodesWithOneInput() {
		ProcessStrategyInterface<String> maxHeapStrategy = new MaxHeapProcessStrategy<String>(TopTweeted.SIXTY_SECONDS_IN_MILLIS);
		int arrayLen=1;
		@SuppressWarnings("unchecked")
		ElementInterface<String>[] ta = (ElementInterface<String>[]) new HashTagElement[arrayLen];
		ta[0] = new HashTagElement<String>("a", System.currentTimeMillis());
		maxHeapStrategy.initialize(ta);
		ArrayList<String> topKs = maxHeapStrategy.getTopKTweeted(3);
		assertTrue(topKs.get(0).equals("a") && topKs.size() ==1);
	}
	
	@Test
	public void testGetTopKNodesWithoutInitialization() {
		ProcessStrategyInterface<String> maxHeapStrategy = new MaxHeapProcessStrategy<String>(TopTweeted.SIXTY_SECONDS_IN_MILLIS);
		ArrayList<String> topKs = maxHeapStrategy.getTopKTweeted(3);
		assertTrue(topKs.size() == 0);
	}
}
