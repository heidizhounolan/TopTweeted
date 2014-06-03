package attensity.twitter.basic;

public class HeapNode<T> {
	private T value;
	private int count;
	
	public HeapNode(T v) {
		this.value = v;
		count=1;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int c) {
		this.count = c;
	}
	
	public T getValue() {
		return value;
	}

}
