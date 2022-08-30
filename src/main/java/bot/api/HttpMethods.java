package bot.api;

import bot.utils.DiscordLogger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class HttpMethods {

    final HttpClient http;

    public HttpMethods() {
        http = new HttpClient();
        http.getParams().setSoTimeout(20000);
        http.getParams().setConnectionManagerTimeout(5000);
        http.getParams().setParameter(HttpClientParams.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
    }

    public static BufferedImage getBufferedImagefromUrl(String urlString) throws IOException, TimeoutException, ExecutionException, InterruptedException {
        BufferedImage image;
        final URL url = new URL(urlString);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        connection.setRequestProperty("Accept-Language", "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7");

        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Object> task = () -> ImageIO.read(connection.getInputStream());

        Future<Object> future = executor.submit(task);
        try {
            image = (BufferedImage) future.get(8, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException ex) {
            DiscordLogger.sendLogInChannel(ex.getMessage(), DiscordLogger.HTTP_ERRORS);
            throw ex;
        } finally {
            future.cancel(true);
            connection.disconnect();
        }
        return image;
    }

    public InputStream get(String url) throws IOException {
        GetMethod get = new GetMethod(url);
        setAgent(get);
        int statusCode = http.executeMethod(get);
        if (statusCode != 200) {
            DiscordLogger.sendLogInChannel("Data could not be fetched. (" + url + ")\nStatuscode: " + statusCode, DiscordLogger.HTTP_ERRORS);
            return null;
        }

        InputStream response = null;
        try {
            response = get.getResponseBodyAsStream();
        } catch (IOException e) {
            DiscordLogger.sendLogInChannel(e.getMessage(), DiscordLogger.HTTP_ERRORS);
        }
        return response;
    }

    public JsonObject fetchJsonObject(String url) {
        InputStream fetchedStream = null;
        try {
            fetchedStream = get(url);
            if (fetchedStream == null) {
                return null;
            }
            JsonObject response = JsonParser.parseString(IOUtils.toString(fetchedStream, StandardCharsets.UTF_8)).getAsJsonObject();
            fetchedStream.close();
            return response;
        } catch (IOException e) {
            if (fetchedStream != null) {
                try {
                    fetchedStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            DiscordLogger.sendLogInChannel(e.getMessage(), DiscordLogger.HTTP_ERRORS);
            return null;
        }
    }

    public JsonArray fetchJsonArray(String url) {
        InputStream fetchedStream = null;
        try {
            fetchedStream = get(url);
            if (fetchedStream == null) {
                return null;
            }
            return JsonParser.parseString(IOUtils.toString(fetchedStream, StandardCharsets.UTF_8)).getAsJsonArray();
        } catch (IOException e) {
            if (fetchedStream != null) {
                try {
                    fetchedStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            DiscordLogger.sendLogInChannel(e.getMessage(), DiscordLogger.HTTP_ERRORS);
            return null;
        }
    }

    private void setAgent(HttpMethod method) {
        method.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        method.setRequestHeader("Accept-Language", "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7");
        method.setRequestHeader("Cookie", "__cfduid=de1f9ce53366f28f39d9f3907233af7e41606754887");
        method.setRequestHeader("Server", "BeatSaber Intelligence Agency Discord Bot by AntiLink#1337");
        method.setRequestHeader("Accept", "*/*");
    }
}