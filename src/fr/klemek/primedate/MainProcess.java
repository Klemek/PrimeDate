package fr.klemek.primedate;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main process to be launched
 * @author Kleme
 */
public abstract class MainProcess {

	private final static SimpleDateFormat date2num = new SimpleDateFormat("yyyyMMddHHmm");
	private final static SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' HH:mm", Locale.ENGLISH);
	private final static NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);

	private static void checkTime() {
		try {
			Calendar currentTime = Calendar.getInstance();
			currentTime.add(Calendar.MILLISECOND, -currentTime.getTimeZone().getOffset(currentTime.getTimeInMillis()));

			long currentTimeValue = Long.parseLong(date2num.format(currentTime.getTime()));

			if (PrimeCalculator.isPrime(currentTimeValue)) {
				String msg = String.format("Hi, the date is %s GMT and %s is a prime number",
						sdf.format(currentTime.getTime()), nf.format(currentTimeValue));
				TwitterClient.tweet(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		if(args.length < 1) {
			System.out.println("Argument 1 must be a file containing customer keys");
			System.exit(0);
		}
		
		if(TwitterClient.setUpTwitter(args[0])) {
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					checkTime();
				}
			}, 0, 1 * 60 * 1000);
		}
	}

}
