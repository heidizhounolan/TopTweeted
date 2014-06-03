package attensity.twitter.basic;

public class HashTagElement<T> implements ElementInterface<T>{
	T tag;
	private long timestamp;

	public HashTagElement(T tag, long timestamp) {
		this.tag = tag;
		this.timestamp = timestamp;
	}

	@Override
	public T getElement() {
		return tag;
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

}
