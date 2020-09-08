package bot.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.LinkedMap;

import bot.foaa.dto.PlayerImprovement;

public class MapUtils {

	public static Map<String, String> convertPlayerImprovements(List<PlayerImprovement> improvements) {
		Collections.sort(improvements);
		improvements.stream().forEach(i -> i.setPlayername(Format.underline(i.getPlayername()) + " - #" + (improvements.indexOf(i) + 1) + "/" + improvements.size()));
		Map<String, String> playernamesWithImprovement = new LinkedMap<String, String>();
		for (PlayerImprovement improvement : improvements) {
			playernamesWithImprovement.put(improvement.getPlayername(), improvement.getImprovementString());
		}
		return playernamesWithImprovement;
	}

	public static <K, V> LinkedMap<K, V> zipToMap(List<K> keys, List<V> values) {
		if (keys.size() != values.size())
			return null;
		LinkedMap<K, V> map = new LinkedMap<K, V>();
		for (int i = 0; i < keys.size(); i++) {
			map.put(keys.get(i), values.get(i));
		}
		return map;
	}

	public static <K, V> LinkedMap<K, V> getSubMap(Map<K, V> map, int begin, int end) {
		Set<K> keys = map.keySet();
		Collection<V> values = map.values();

		List<K> subKeys = new ArrayList<K>();
		subKeys.addAll(keys);

		List<V> subValues = new ArrayList<V>();
		subValues.addAll(values);

		subKeys = subKeys.subList(begin, end);
		subValues = subValues.subList(begin, end);

		return zipToMap(subKeys, subValues);
	}
}
