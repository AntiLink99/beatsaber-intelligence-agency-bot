package bot.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HttpMethods {

	HttpClient http;

	public HttpMethods() {
		http = new HttpClient();
		http.getParams().setSoTimeout(10000);
		http.getParams().setConnectionManagerTimeout(5000);
		http.getParams().setParameter(HttpClientParams.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
	}

	public InputStream get(String url) throws IOException {
		GetMethod get = new GetMethod(url);
		setAgent(get);

		int statusCode = http.executeMethod(get);
		if (statusCode != 200) {
			System.out.println("Data could not be fetched. Statuscode: " + statusCode);
			return null;
		}

		InputStream response = null;
		try {
			response = get.getResponseBodyAsStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	public File downloadFileFromUrl(String url, String filePath) {
		File file = new File(filePath);
		try {
			InputStream binary = get(url);
			FileUtils.copyInputStreamToFile(binary, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	public JsonObject fetchJsonObject(String url) {
		try {
			System.out.println("Fetching " + url + "...");
			InputStream fetchedStream = get(url);
			if (fetchedStream == null) {
				return null;
			}
			return JsonParser.parseString(IOUtils.toString(fetchedStream, "UTF-8")).getAsJsonObject();
		} catch (IOException e) {
			return null;
		}
	}
	
	public JsonArray fetchJsonArray(String url) {
		try {
			System.out.println("Fetching " + url + "...");
			InputStream fetchedStream = get(url);
			if (fetchedStream == null) {
				return null;
			}
			return JsonParser.parseString(IOUtils.toString(fetchedStream, "UTF-8")).getAsJsonArray();
		} catch (IOException e) {
			return null;
		}
	}


	private void setAgent(HttpMethod method) {
		method.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		method.setRequestHeader("Accept-Language","de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7");
		method.setRequestHeader("Cookie","__cfduid=de1f9ce53366f28f39d9f3907233af7e41606754887");
		method.setRequestHeader("Accept","*/*");
	}
}