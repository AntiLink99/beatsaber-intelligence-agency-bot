package bot.utils;

import bot.dto.beatsaviour.BeatSaviourRankedMap;
import bot.dto.beatsaviour.RankedMaps;

public class RankedMapUtils {

	public static BeatSaviourRankedMap findRankedMapBySongHash(RankedMaps ranked, String songHash) {
		if (ranked == null) {
			return null;
		}
		for (BeatSaviourRankedMap map : ranked.getRankedMaps()) {
			try {
				if (map.getHash().toUpperCase().equals(songHash.toUpperCase())) {
					return map;
				}
			} catch (NullPointerException e) {
				System.out.println("Null Pointer at ranked maps");
				System.out.println("SongHash: " + songHash);
				System.out.println("Map: " + map);
				System.out.println("RankedMaps: " + ranked.getRankedMaps().size());
			}
		}
		return null;
	}
}
