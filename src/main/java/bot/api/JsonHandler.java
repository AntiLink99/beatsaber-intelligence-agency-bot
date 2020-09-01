package bot.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.json.JSONObject;

public class JsonHandler {

	public JSONObject getJsonFromUrl(String urlString) throws IOException {
		try {
			URL url;
			url = new URL(urlString);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");

			InputStream stream = conn.getInputStream();
			Scanner scn = new Scanner(stream);
			scn.useDelimiter("\\A");

			String buffer = scn.hasNext() ? scn.next() : "";
			scn.close();
			stream.close();
			
			return new JSONObject(buffer);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw e;
		}
		return null;
	}
}
