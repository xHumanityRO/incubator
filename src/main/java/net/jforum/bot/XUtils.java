package net.jforum.bot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

import net.jforum.exceptions.ForumException;

public class XUtils {

	public static boolean isEmailValid(String email) {
		String regex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
		return Pattern.matches(regex, email);
	}

	public static void loadProps(Properties destination, File file) {
		try {
			InputStream is = new FileInputStream(file);
			try {
				destination.load(is);
			} finally {
				is.close();
			} // try..finally
		} catch (IOException e) {
			throw new ForumException(e);
		}
	}

}
