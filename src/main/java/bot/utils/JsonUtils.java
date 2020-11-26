package bot.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import bot.api.HttpMethods;
import bot.dto.ScrapedMapData;

public class JsonUtils {

	static List<Entry<String, ScrapedMapData>> entries;
	static File coversByHashFile;
	private static String mapDataFilePath = "src/main/resources/mapData.json";
	private static String mapDataUrl = "https://cdn.wes.cloud/beatstar/bssb/v2-all.json";
	private static String coversByHashFilePath = "src/main/resources/coversByHash.json";

	public static ScrapedMapData getMapByHash(String hash) {
		List<Entry<String, ScrapedMapData>> hashEntries = entries.stream().filter(e -> e.getKey().equals(hash)).collect(Collectors.toList());
		if (hashEntries.size() > 0) {
			return hashEntries.get(0).getValue();
		}
		return null;
	}

	public static void loadJsonMapFile() {
		coversByHashFile = new File(coversByHashFilePath);
		if (!coversByHashFile.exists()) {
			try {
				coversByHashFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Gson gson = new Gson();
		try {
			Type type = new TypeToken<ScrapedMapData>() {
			}.getType();
			Object o = gson.fromJson(new FileReader(mapDataFilePath), Object.class);
			if (o instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> mapObjects = (Map<String, Object>) o;
				Map<String, ScrapedMapData> scrapedMaps = new HashMap<String, ScrapedMapData>();
				for (Entry<String, Object> map : mapObjects.entrySet()) {
					String valueJson = gson.toJson(map.getValue());
					scrapedMaps.put(map.getKey(), gson.fromJson(valueJson, type));
				}
				entries = scrapedMaps.entrySet().stream().collect(Collectors.toList());
			}
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Map<String, String> getCoverByHashList() {
		if (entries == null) {
			JsonUtils.loadJsonMapFile();
		}
		try {
			Type type = new TypeToken<Map<String, String>>() {
			}.getType();
			Map<String, String> coversByHash = new Gson().fromJson(new FileReader(coversByHashFile), type);
			return coversByHash;
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void addToCoversByHash(HashMap<String, String> newCoversByHash) {
		Map<String, String> coversByHash = getCoverByHashList();
		if (coversByHash != null) {
			coversByHash.putAll(newCoversByHash);
		} else {
			coversByHash = newCoversByHash;
		}
		File coversByHashFile = new File(coversByHashFilePath);
		String newJson = new Gson().toJson(coversByHash);
		try {
			FileUtils.writeStringToFile(coversByHashFile, newJson, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void downloadJsonMapFile() {
		new HttpMethods().downloadFileFromUrl(mapDataUrl, mapDataFilePath);
	}
}
