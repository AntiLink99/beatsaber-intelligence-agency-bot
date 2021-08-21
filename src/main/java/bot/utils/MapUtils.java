package bot.utils;

import bot.dto.player.PlayerImprovement;
import org.apache.commons.collections4.map.LinkedMap;

import java.util.*;

public class MapUtils {

    public static Map<String, String> convertPlayerImprovements(List<PlayerImprovement> improvements) {
        Collections.sort(improvements);
        improvements.stream().forEach(i -> i.setPlayername(Format.underline(i.getPlayername()) + " - #" + (improvements.indexOf(i) + 1) + "/" + improvements.size()));
        Map<String, String> playernamesWithImprovement = new LinkedMap<>();
        for (PlayerImprovement improvement : improvements) {
            playernamesWithImprovement.put(improvement.getPlayername(), improvement.getImprovementString());
        }
        return playernamesWithImprovement;
    }

    public static <K, V> LinkedMap<K, V> zipToMap(List<K> keys, List<V> values) {
        if (keys.size() != values.size())
            return null;
        LinkedMap<K, V> map = new LinkedMap<>();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), values.get(i));
        }
        return map;
    }

    public static <K, V> LinkedMap<K, V> getSubMap(Map<K, V> map, int begin, int end) {
        Set<K> keys = map.keySet();
        Collection<V> values = map.values();

        List<K> subKeys = new ArrayList<>(keys);

        List<V> subValues = new ArrayList<>(values);

        subKeys = subKeys.subList(begin, end);
        subValues = subValues.subList(begin, end);

        return zipToMap(subKeys, subValues);
    }
}
