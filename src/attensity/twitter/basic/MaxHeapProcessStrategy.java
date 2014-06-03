package attensity.twitter.basic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;

import com.google.common.collect.MinMaxPriorityQueue;

/*
 * The strategy to get top K tweeted is using hash table to count the frequency of tweeted hash tag, 
 * and then use Max Heap to get the top K hash tag
 */
public class MaxHeapProcessStrategy<T> implements ProcessStrategyInterface<T> {
	private MinMaxPriorityQueue<HeapNode<T>> maxHeap;
	private Hashtable<T, HeapNode<T>> hashtable;
	private long lifeTime = 0;

	private final Comparator<HeapNode<T>> comparator = new Comparator<HeapNode<T>>() {
		@Override
		public int compare(HeapNode<T> n1, HeapNode<T> n2) {
			return n1.getCount() - n2.getCount();
		}
	};

	public MaxHeapProcessStrategy(long lifeTime) {
		maxHeap = MinMaxPriorityQueue.orderedBy(comparator).create();
		hashtable = new Hashtable<T, HeapNode<T>>();
		this.lifeTime = lifeTime;
	}

	@Override
	public ArrayList<T> getTopKTweeted(int k) {
		ArrayList<T> result = new ArrayList<T>();
		while (k > 0 && !maxHeap.isEmpty()) {
			HeapNode<T> n = maxHeap.pollLast();
			result.add(n.getValue());
			k--;
		}
		return result;
	}

	@Override
	public void initialize(ElementInterface<T>[] elements) {
		hashtable.clear();
		maxHeap.clear();
		long current = System.currentTimeMillis();
		for (int i = 0; i < elements.length; ++i) {
			ElementInterface<T> e = elements[i];			
			if (current - e.getTimestamp() > lifeTime)
				continue; // expired, not consider
			
			HeapNode<T> n = hashtable.get(e.getElement());
			if (n == null) {
				n = new HeapNode<T>(e.getElement());
			} else {
				n.setCount(n.getCount() + 1);
			}
			hashtable.put(e.getElement(), n);
		}
		maxHeap.addAll(hashtable.values());
	}

}
