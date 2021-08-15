package bot.utils;

import org.apache.commons.validator.routines.UrlValidator;

public class WebUtils {
	private static String[] schemes = { "http", "https" }; // DEFAULT schemes = "http", "https", "ftp"
	private static UrlValidator urlValidator = new UrlValidator(schemes);

	public static boolean isURL(String url) {
		return urlValidator.isValid(url);
	}
}
