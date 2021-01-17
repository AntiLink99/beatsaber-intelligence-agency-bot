package bot.utils;

import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.commons.lang3.StringUtils;

public class Format {

	public static String bold(String msg) {
		return "**" + msg + "**";
	}

	public static String italic(String msg) {
		return "*" + msg + "*";
	}

	public static String underline(String msg) {
		return "__" + msg + "__";
	}

	public static String ping(String msg) {
		return "<@" + msg + ">";
	}

	public static String decimal(float num) {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		DecimalFormat sep = new DecimalFormat("0.##");
		sep.setDecimalFormatSymbols(symbols);
		return sep.format(num);
	}

	public static String fixedLength(String str, int length) {
		return StringUtils.rightPad(str, length);
	}

	public static String code(String msg) {
		return "```" + msg + "```";
	}

	public static String greenCode(String msg) {
		return "```yaml\n" + msg + "```";
	}

	public static String redCode(String msg) {
		return "```diff\n- " + msg + "```";
	}

	public static String blueCode(String msg) {
		return "```CSS\n" + msg + "\n```";
	}

	public static String codeProlog(String msg) {
		return "```prolog\n" + msg + "\n```";
	}

	public static String codeAutohotkey(String msg) {
		return "```autohotkey\n" + msg + "\n```";
	}

	public static String foaaRole(String role) {
		return "[ " + role + " ]";
	}

	public static boolean isUrl(String str) {
		try {
			new URI(str);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}

	public static int roundDouble(double d) {
		DecimalFormat df = new DecimalFormat("#.####");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return Integer.parseInt(df.format(d));
	}
}
