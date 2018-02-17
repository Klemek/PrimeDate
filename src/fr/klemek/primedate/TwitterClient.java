package fr.klemek.primedate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Contains the useful functions to connect to twitter
 * @author Kleme
 */
public abstract class TwitterClient {

	//http://twitter4j.org/en/code-examples.html
	
	private final static String ACCESS_TOKEN_FILE = "access_token.txt";

	private static Twitter twitter;
	
	/**
	 * Load keys and tokens to connect to the correct twitter account
	 * @param keyFile the file containing the customer keys
	 * @return
	 */
	public static boolean setUpTwitter(String keyFile) {
		try {
			File consumer_file = new File(keyFile);
			if(!consumer_file.exists()) {
				System.out.println("Invalid consumer key file");
				return false;
			}
			
			String[] consumer_key = loadConsumerKey(keyFile);
			
			if(consumer_key.length < 2) {
				System.out.println("Invalid consumer key file");
				return false;
			}
			
			File token_file = new File(ACCESS_TOKEN_FILE);
			
			if(!token_file.exists()) {
				twitter = TwitterFactory.getSingleton();
				twitter.setOAuthConsumer(consumer_key[0], consumer_key[1]);
				RequestToken requestToken = twitter.getOAuthRequestToken();
				AccessToken accessToken = null;
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				while (null == accessToken) {
					System.out.println("Open the following URL and grant access to your account:");
					System.out.println(requestToken.getAuthorizationURL());
					System.out.print("Enter the PIN(if available) or just hit enter.[PIN]:");
					String pin;

					pin = br.readLine();
					if (pin.length() > 0) {
						accessToken = twitter.getOAuthAccessToken(requestToken, pin);
					} else {
						accessToken = twitter.getOAuthAccessToken();
					}
				}
				// persist to the accessToken for future reference.
				storeAccessToken(accessToken);
			}else {
				/*TwitterFactory factory = new TwitterFactory();
			    
			    twitter = factory.getInstance();
			    twitter.setOAuthConsumer(consumer_key[0], consumer_key[1]);
			    twitter.setOAuthAccessToken(accessToken);*/
				AccessToken accessToken = loadAccessToken();
			    ConfigurationBuilder cb = new ConfigurationBuilder();
			    cb.setDebugEnabled(true)
			      .setOAuthConsumerKey(consumer_key[0])
			      .setOAuthConsumerSecret(consumer_key[1])
			      .setOAuthAccessToken(accessToken.getToken())
			      .setOAuthAccessTokenSecret(accessToken.getTokenSecret());
			    TwitterFactory tf = new TwitterFactory(cb.build());
			    twitter = tf.getInstance();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (TwitterException te) {
			if (401 == te.getStatusCode()) {
				System.out.println("Unable to get the access token.");
			} else {
				te.printStackTrace();
			}
			return false;
		}
		
		System.out.println("Successfuly connected to twitter");
		
		return true;
	}

	/**
	 * Store the access tokens into the constant file
	 * @param accessToken
	 * @throws IOException
	 */
	private static void storeAccessToken(AccessToken accessToken) throws IOException {
		BufferedWriter bw = null;
		try {
			File file = new File(ACCESS_TOKEN_FILE);
			file.createNewFile();
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			bw.write(accessToken.getToken());
			bw.newLine();
			bw.write(accessToken.getTokenSecret());
		}finally {
			bw.close();
		}
	}

	/**
	 * Load the access tokens from the constant file
	 * @return
	 * @throws IOException
	 */
	private static AccessToken loadAccessToken() throws IOException {
		BufferedReader br = null;
		try {
			File file = new File(ACCESS_TOKEN_FILE);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String token = br.readLine();
			String tokenSecret = br.readLine();
			return new AccessToken(token, tokenSecret);
		}finally {
			br.close();
		}
	}
	
	/**
	 * Load the consumer keys from the given file
	 * @param file_name
	 * @return
	 * @throws IOException
	 */
	private static String[] loadConsumerKey(String file_name) throws IOException{
		BufferedReader br = null;
		try {
			ArrayList<String> file_lines = new ArrayList<>();
			File file = new File(file_name);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line;
			while((line = br.readLine()) != null) {
				file_lines.add(line);
			}
			return file_lines.toArray(new String[0]);
		}finally {
			br.close();
		}
	}

	/**
	 * Update the status of the twitter account
	 * @param msg
	 */
	public static void tweet(String msg) {
		try {
			twitter.updateStatus(msg);
			System.out.println("Tweeted : "+msg);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
}
