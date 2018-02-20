package fr.klemek.primedate;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main process to be launched
 * 
 * @author Kleme
 */
public abstract class MainProcess {

	private final static String VERSION = "v1.5";

	private final static SimpleDateFormat DATE_TO_NUM = new SimpleDateFormat("yyyyMMddHHmm");

	private final static String[] GREETINGS_SENTENCES = new String[] { "Hi", "Hello there", "Good %s", "What's up?",
			"Greetings", "How are you?", "Hey", "How are you doing?", "How's life?", "Long time no see",
			"It's been a while", "How do you do?", "Yo", "Howdy", "Sup?", "Whazzup?", "Yoooooo", "Bonjour", "Hola",
			"Hallo", "Salam", "Aloha", "Hey", "Heyy", "Heyy", "Heyyy", "Heyyyy", "Heyyyyy" };

	private final static String[] DATE_SENTENCES = new String[] { "The date is %1$s and it's %2$s",
			"We are %1$s and it's already %2$s", "It's already %2$s today", "Today is %1$s and it's %2$s",
			"It's %2$s and today's %1$s", "It's %2$s on %1$s", "Already %2$s on %1$s" };

	private final static String[] PRIME_SENTENCES = new String[] { "%s is a prime number",
			"%s cannot be divided by another number", "nobody can divide %s", "%s is prime", "%s is prime as fuck",
			"%s is prime as hell", "%s is prime as shit", "%s sure is prime", "%s is pretty prime",
			"you can trust %s to be prime", "%s is like other primes" };

	private final static String[] END_SENTENCES = new String[] { "", "Pretty cool, huh?", "It blows your mind!",
			"You can forget it now.", "You wasted 20 seconds of your time.", "That's cool!", "Isn't it cool?",
			"You should stop reading these tweets...", "That's a good password.", "Maybe not.", "Why ? ...",
			"Pls help stuck in prime factory", "Why do you read these ?", "Can someone check ?", "Really ?", "Cool.",
			"You can use it.", "Google it.", "How do I stop this?", "Send STOP to not receive this anymore.", "Yes.",
			"It's true.", "Move on.", "That's no FAKE news!", "Soooo hard to calculate by hand!", "It is also my IQ.",
			"Add 1 and it doesn't work anymore.", "I'm a bot, I know that stuff.", "Awesome!",
			"Next one should be better!", "Let's calculate its factorial now...", "There's a phone number in it.",
			"Don't do this at home, kids.", "Highest one so far!", "Next one will blow your mind!",
			"That's the exact number of bacteria in your body.", "Don't believe me? Fine.",
			"Funniest thing of the day.", "See you next time!", "Bye!", "See ya!", "See you later!" };

	private final static char[] SENTENCE_ENDS = new char[] { '.', '!', ',' };

	private static void checkTime(Calendar currentTime, boolean fake) {
		try {
			long currentTimeValue = Long.parseLong(DATE_TO_NUM.format(currentTime.getTime()));

			if (PrimeCalculator.isPrime(currentTimeValue)) {
				String msg;
				do {
					msg = constructSentence(currentTime, currentTimeValue);
				} while (msg.length() > 280);
				if (fake)
					System.out.println(msg);
				else
					TwitterClient.tweet(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String constructSentence(Calendar time, long timeValue) {

		final int hour = time.get(Calendar.HOUR_OF_DAY);
		final String dayPeriod = hour >= 5 && hour <= 12 ? "morning" : (hour <= 18 ? "afternoon" : "evening");

		final PrettyDate time2 = new PrettyDate(time);

		final int r1 = ThreadLocalRandom.current().nextInt(0, GREETINGS_SENTENCES.length);
		final int r2 = ThreadLocalRandom.current().nextInt(0, SENTENCE_ENDS.length);
		final int r3 = ThreadLocalRandom.current().nextInt(0, DATE_SENTENCES.length);
		final int r4 = ThreadLocalRandom.current().nextInt(0, SENTENCE_ENDS.length);
		final int r5 = ThreadLocalRandom.current().nextInt(0, PRIME_SENTENCES.length);
		final int r6 = ThreadLocalRandom.current().nextInt(0, SENTENCE_ENDS.length);
		final int r7 = ThreadLocalRandom.current().nextInt(0, END_SENTENCES.length);

		// GREETINGS

		String greetings = String.format(GREETINGS_SENTENCES[r1], dayPeriod);
		if (!greetings.endsWith("?"))
			greetings += SENTENCE_ENDS[r2];

		// DATE

		String date = String.format(DATE_SENTENCES[r3], time2.getPrettyDate(), time2.getPrettyTime());
		if (greetings.endsWith(","))
			date = date.substring(0, 1).toLowerCase() + date.substring(1);
		date += SENTENCE_ENDS[r4];
		if (date.endsWith(","))
			date = date.substring(0, date.length() - 1) + " and";

		// PRIME

		String prime = String.format(PRIME_SENTENCES[r5], NumberFormat.getNumberInstance().format(timeValue));
		if (date.endsWith("d"))
			prime = prime.substring(0, 1).toLowerCase() + prime.substring(1);

		if (END_SENTENCES[r7].length() == 0 && SENTENCE_ENDS[r6] == ',')
			prime += ".";
		else
			prime += SENTENCE_ENDS[r6];

		// END

		String end = END_SENTENCES[r7];
		if (prime.endsWith(","))
			end = end.substring(0, 1).toLowerCase() + end.substring(1);

		return greetings + " " + date + " " + prime + " " + end;
	}

	public static void main(String[] args) {

		Locale.setDefault(Locale.ENGLISH);

		final int max_random = GREETINGS_SENTENCES.length * DATE_SENTENCES.length * PRIME_SENTENCES.length
				* END_SENTENCES.length;

		System.out.println(String.format("PrimeDate %s%n%s%n%s+ sentences available randomly", VERSION,
				Calendar.getInstance().getTime(), NumberFormat.getInstance().format(max_random)));

		if (args.length < 1) {
			System.out.println("Argument 1 must be a file containing customer keys");
			System.exit(0);
		}

		if (TwitterClient.setUpTwitter(args[0])) {

			PrimeCalculator.computeList();

			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					Calendar currentTime = Calendar.getInstance();
					currentTime.add(Calendar.MILLISECOND,
							-currentTime.getTimeZone().getOffset(currentTime.getTimeInMillis()));
					checkTime(currentTime, false);
				}
			}, 0, 1 * 60 * 1000);

			/*
			 * Calendar time = Calendar.getInstance(); while (true) { checkTime(time, true);
			 * time.add(Calendar.MINUTE, 1); }
			 */
		}
	}

}
