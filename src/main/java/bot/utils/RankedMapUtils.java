package bot.utils;

import bot.dto.rankedmaps.BeatSaverRankedMap;
import bot.dto.rankedmaps.RankedMaps;

public class RankedMapUtils {

    public static BeatSaverRankedMap findRankedMapBySongHash(RankedMaps ranked, String songHash) {
        if (ranked == null || ranked.getRankedMaps()  == null) {
            return null;
        }
        for (BeatSaverRankedMap map : ranked.getRankedMaps()) {
            try {
                if (map.getLatestVersion().getHash().equalsIgnoreCase(songHash)) {
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
