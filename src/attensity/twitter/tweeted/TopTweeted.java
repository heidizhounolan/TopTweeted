package attensity.twitter.tweeted;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import twitter4j.HashtagEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import attensity.twitter.basic.ElementInterface;
import attensity.twitter.basic.HashTagElement;
import attensity.twitter.basic.MaxHeapProcessStrategy;
import attensity.twitter.basic.ProcessStrategyInterface;

public class TopTweeted {
	static public final long SIXTY_SECONDS_IN_MILLIS = 60 * 1000;

	//cache of the hash tags within last life time 
	private BlockingQueue<ElementInterface<String>> cachedWindow = new LinkedBlockingQueue<ElementInterface<String>>();
	private ProcessStrategyInterface<String> processStrategy = new MaxHeapProcessStrategy<String>(SIXTY_SECONDS_IN_MILLIS);
	private AccessConfigurationBuilder accessConfigBuilder;
	
	public class RetriveTopTweetedTask extends TimerTask {
		final ProcessStrategyInterface<String> processStrategy;
		int topK;
		
		public RetriveTopTweetedTask(ProcessStrategyInterface<String> s, int topK) {
			this.processStrategy =s;
			this.topK = topK;
		}
		
		public void run() {
			ElementInterface<String>[] ta = getCachedArray();
			
			if(ta.length != 0) {
				processStrategy.initialize(ta);
				ArrayList<String> topKs=processStrategy.getTopKTweeted(topK);
				System.out.println("Top tweeted @"+ System.currentTimeMillis());
				for(int i=topKs.size()-1; i >= 0; i--)
					System.out.println(topKs.get(i));
			}
		}
	}
	
	public class SampleStreamListener implements StatusListener {
		private long lifeTime;
		
		public SampleStreamListener(long lifeTime) {
			this.lifeTime = lifeTime;
		}

		public void onStatus(Status status) {
			long current = System.currentTimeMillis();
			prune(current, lifeTime);
			HashtagEntity[] hte = status.getHashtagEntities();
			for (int i = 0; i < hte.length; ++i) {
				cachedWindow.add(new HashTagElement<String>(hte[i].getText(), current));
			}
		}

		public void onException(Exception ex) {
			ex.printStackTrace();
		}

		@Override
		public void onScrubGeo(long arg0, long arg1) {

		}

		@Override
		public void onStallWarning(StallWarning arg0) {

		}

		@Override
		public void onDeletionNotice(StatusDeletionNotice arg0) {
		}

		@Override
		public void onTrackLimitationNotice(int arg0) {
		}		
	}
	
	public TopTweeted(ProcessStrategyInterface<String> strategy, AccessConfigurationBuilder accessConfigBuilder) {
		this.processStrategy = strategy;
		this.accessConfigBuilder = accessConfigBuilder;
	}

	public TopTweeted(AccessConfigurationBuilder accessConfigBuilder) {
		this.accessConfigBuilder = accessConfigBuilder;
	}

	/*
	 * Get the cached tweeted in array
	 */
	@SuppressWarnings("unchecked")
	public ElementInterface<String>[] getCachedArray() {
		return (ElementInterface<String>[]) cachedWindow.toArray(new HashTagElement[0]);
	}

	//prune the cache to store only in the last 60 seconds
	private void prune(long current, long lifeTime) {
		while (!cachedWindow.isEmpty()) {
			ElementInterface<String> e = cachedWindow.peek();
			if (current - e.getTimestamp() <= lifeTime)
				break; //no more expired records
			try {
				cachedWindow.take();
			} catch (InterruptedException e1) {
				// interrupted, break
				break;
			}
		}
	}

	/*
	 * Get top K tweeted every 'period' seconds in the last "lifeTime" seconds
	 * 
	 * @param topK : top K tweeted
	 * @param period : every "period" seconds to retrieve top K tweeted
	 * @param lifeTime: only retrieve among the records in the last "lifeTime" seconds
	 */
	public void getTopTweetedFromSampleStream(int topK, long period, long lifeTime) throws TwitterException, IOException {
		//schedule the task to retrieve top tweeted
		RetriveTopTweetedTask task = new RetriveTopTweetedTask(processStrategy, topK); 
		Timer time = new Timer(); 
		time.schedule(task, 0, period); 

		if(!startSample(lifeTime)) {
			System.out.println("Can't sample the twitter stream");
		}
	}

	public boolean startSample(long lifeTime) {
		if(accessConfigBuilder == null)
			return false;
		StatusListener listener = new SampleStreamListener(lifeTime);			
		TwitterStream twitterStream = new TwitterStreamFactory(accessConfigBuilder.build()).getInstance();
		twitterStream.addListener(listener);
		twitterStream.sample();
		return true;
	}

	static public void main(String args[]) {
		ProcessStrategyInterface<String> maxHeapStrategy = new MaxHeapProcessStrategy<String>(SIXTY_SECONDS_IN_MILLIS);
		AccessConfigurationBuilder accessConfigBuilder = new AccessConfigurationBuilder();
		TopTweeted to = new TopTweeted(maxHeapStrategy, accessConfigBuilder);
		
		try {
			//get top 10 tweeted every 30 seconds in the past 60 seconds
			to.getTopTweetedFromSampleStream(10, SIXTY_SECONDS_IN_MILLIS/2, SIXTY_SECONDS_IN_MILLIS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
