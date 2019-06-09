package fr.klemek.primedate;

import java.util.Calendar;

public class PrettyDate {

	private Calendar cal;

	public PrettyDate() {
		cal = Calendar.getInstance();
	}

	public PrettyDate(Calendar cal) {
		this.cal = cal;
	}

	private String getPrettyDayOfMonth() {
		final int n = cal.get(Calendar.DAY_OF_MONTH);
		if (n >= 11 && n <= 13) {
			return n+"th";
		}
		switch (n % 10) {
		case 1:
			return n + "st";
		case 2:
			return n + "nd";
		case 3:
			return n + "rd";
		default:
			return n + "th";
		}
	}

	public String getPrettyDate() {
		return String.format("%1$tB the %2$s, %1$tY", cal.getTime(), getPrettyDayOfMonth());
	}
	
	public String getPrettyTime() {
		return String.format("%1$tH:%1$tM GMT", cal.getTime());
	}

}
