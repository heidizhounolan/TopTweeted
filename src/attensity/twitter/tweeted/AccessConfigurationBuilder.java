package attensity.twitter.tweeted;

import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class AccessConfigurationBuilder {
	final static private String CONSUMER_KEY = "ccJZQxoZX797UTrnjlm3KZ2ih";
	final static private String CONSUMER_SECRET = "aDD97RfJHS7BTosNeiB43u6zkpU9kEiWuYswZW6qJxdMkFoLqV";
	private static final String ACCESS_TOKEN_SECRET = "Br2O5glREQHIgxPxUVJkPOTwgYi8x5lGKX9P70l6gtxkG";
	private static final String ACCESS_TOKEN = "2468818754-EtHXQy1dyl87s49tQmblSMGruOP5n9FXrTdgnsD";
	private ConfigurationBuilder cb;

	public AccessConfigurationBuilder() {
		cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET)
				.setOAuthAccessToken(ACCESS_TOKEN).setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
	}

	public Configuration build() {
		return cb.build();
	}

}
