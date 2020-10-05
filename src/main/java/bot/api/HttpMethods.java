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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.dv8tion.jda.api.exceptions.HttpException;

public class HttpMethods {

	HttpClient http;

	public HttpMethods() {
		http = new HttpClient();
		http.getParams().setSoTimeout(5000);
		http.getParams().setConnectionManagerTimeout(5000);
		http.getParams().setParameter(HttpClientParams.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
	}

	public InputStream get(String url) throws IOException {
		GetMethod get = new GetMethod(url);
		setAgent(get);

		int statusCode = http.executeMethod(get);
		if (statusCode != 200) {
			throw new HttpException("Data could not be fetched. Statuscode: " + statusCode);
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

	public JsonObject fetchJson(String url) {
		try {
			return JsonParser.parseString(IOUtils.toString(get(url), "UTF-8")).getAsJsonObject();
		} catch (IOException e) {
			return null;
		}
	}

	private void setAgent(HttpMethod method) {
		method.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
	}
}