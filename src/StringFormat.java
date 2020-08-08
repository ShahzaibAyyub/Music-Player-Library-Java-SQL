

public class StringFormat {
	/**
	 * Formats the given number of seconds as a colon-separated String.
	 * @param totalSeconds Number of seconds to format
	 * @return Formatted string in mM:SS format.
	 */
	public static String formatTime(int totalSeconds) {
		int minutes = totalSeconds / 60;
		int seconds = totalSeconds % 60;
		return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
	}
}
