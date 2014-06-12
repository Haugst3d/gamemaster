package dk.livingcode.android.gamemaster.utility;

public class Strings {
	public static final String Empty = "";
	
	public static boolean isNullOrEmpty(final String s) {
		return (s == null || s.length() == 0);
	}
}