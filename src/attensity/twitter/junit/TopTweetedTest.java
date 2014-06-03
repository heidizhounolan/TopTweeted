package attensity.twitter.junit;

import static org.junit.Assert.*;

import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;

import attensity.twitter.basic.ElementInterface;
import attensity.twitter.tweeted.AccessConfigurationBuilder;
import attensity.twitter.tweeted.TopTweeted;

public class TopTweetedTest {
	private Object sync = new Object();

	public class TestCachedTweetedTask extends TimerTask {
		final TopTweeted tt;
		long lifeTime = Integer.MAX_VALUE;
		
		public TestCachedTweetedTask(TopTweeted t, long lifeTime) {
			tt = t;
			this.lifeTime = lifeTime;
		}
		
		public void run() {
			ElementInterface<String>[] ta = tt.getCachedArray();
			
			if(ta.length == 0)
				return;
			
			long current = System.currentTimeMillis();
			for(int i=0; i < ta.length; ++i) {
				if(current - ta[i].getTimestamp() > lifeTime) {
					fail("There is too old tweeted tag");
				}
			}
			
			synchronized (sync) {
				sync.notify();
			}
		}
	}

	/*
	 * Test if the cached windows only store the tag within lifetime
	 */
	@Test
	public void testGetCachedArray() {
		long lifeTime = TopTweeted.SIXTY_SECONDS_IN_MILLIS;
		long period = TopTweeted.SIXTY_SECONDS_IN_MILLIS*2;
		AccessConfigurationBuilder accessConfigBuilder = new AccessConfigurationBuilder();
		TopTweeted tt = new TopTweeted(accessConfigBuilder);
		
		TestCachedTweetedTask task = new TestCachedTweetedTask(tt, lifeTime);
		Timer time = new Timer(); 
		time.schedule(task, 0, period); 

		if(!tt.startSample(lifeTime))
			fail("Can't sample twitter stream");
		
		synchronized (sync) {
			try {
				sync.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

}

