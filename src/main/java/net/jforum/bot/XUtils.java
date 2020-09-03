package net.jforum.bot;

import java.util.regex.Pattern;

public class XUtils {

	public static boolean isEmailValid(String email) {
		String regex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
		return Pattern.matches(regex, email);
	}
}
