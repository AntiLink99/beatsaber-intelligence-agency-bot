package bot.api;

import java.util.List;

public class ApiConstants {

    //Backend
    public static final String BACKEND_URL = "http://localhost:3001";
    public static final String COMPARISON_URL = BACKEND_URL + "/comparisonImage";

    // ScoreSaber
    public static final String SS_PRE_URL = "https://scoresaber.com";
    public static final String USER_PRE_URL = SS_PRE_URL + "/u/";
    public static final String SS_PLAYER_PRE_URL = SS_PRE_URL + "/api/player/";
    public static final String SS_PLAYER_POST_URL = "/full";
    public static final String SS_PLAYER_TOP_SCORES_POST_URL = "/scores?sort=top";
    public static final String SS_PLAYER_RECENT_SCORES_POST_URL = "/scores?sort=recent";
    public static final String SS_LEADERBOARD_PRE_URL = "https://scoresaber.com/leaderboard/";
    public static final String QUALIFIED_URL = "https://scoresaber.com/api.php?function=get-leaderboards&cat=5&limit=100&page=1&unique=1&qualified=1";
    public static final String NO_AVATAR_URL = "https://images.squarespace-cdn.com/content/v1/55fc0004e4b069a519961e2d/1442590746571-RPGKIXWGOO671REUNMCB/image-asset.gif?format=300w";
    public static final String PLAYER_LEADERBOARDS_API_URL = "https://scoresaber.com/api/players?page=";
    public static final String PLAYER_LEADERBOARDS_URL = "https://scoresaber.com/rankings?page=";

    // Beat Saver
    public static final String BS_PRE_URL = "https://beatsaver.com";
    public static final String BS_API_PRE_URL = "https://api.beatsaver.com";

    public static final String BS_MAP_BY_KEY = BS_API_PRE_URL + "/maps/id/";
    public static final String BS_MAP_BY_HASH_URL = BS_API_PRE_URL + "/maps/hash/";

    public static final String BS_DEFAULT_MAP_URL = BS_PRE_URL + "/maps/";

    public static final String BS_RANKED_MAPS_PRE_URL = BS_API_PRE_URL + "/search/text/";
    public static final String BS_RANKED_MAPS_POST_URL = "?sortOrder=Latest&ranked=true";

    // Beat Saviour
    public static final String BSAVIOR_PRE_URL = "https://beat-savior.herokuapp.com";
    public static final String BSAVIOR_LIVESCORES_URL = BSAVIOR_PRE_URL + "/api/livescores/player/";

    //BeatLeader
    public static final String BL_PRE_URL = "https://api.beatleader.xyz";
    public static final String BL_SCORES_URL = "/player/%s/scores?page=%d&sortBy=%s&order=desc&search=&diff=&type=&stars_from=&stars_to=&eventId=";
    public static final String BL_USER_HISTORY_URL = "/player/%s/history?count=30";
    public static final String BL_USER_PLAYER_BY_DISCORD_URL = "/player/discord/%d";
    public static String getBeatLeaderTopScoresURL(String playerId, int pageNr) {
        return String.format(BL_PRE_URL + BL_SCORES_URL, playerId, pageNr, "pp");
    }
    public static String getBeatLeaderRecentScoresURL(String playerId, int pageNr) {
        return String.format(BL_PRE_URL + BL_SCORES_URL, playerId, pageNr, "date");
    }
    public static String getBeatLeaderPlayerHistoryURL(String playerId) {
        return String.format(BL_PRE_URL + BL_USER_HISTORY_URL, playerId);
    }
    public static final String BL_USER_PLAYER_BY_ID_URL = "/player/%d";
    public static String getBeatLeaderPlayerByIdURL(long discordId) {
        return String.format(BL_PRE_URL + BL_USER_PLAYER_BY_ID_URL, discordId);
    }
    public static String getBeatLeaderPlayerByDiscordId(long discordId) {
        return String.format(BL_PRE_URL + BL_USER_PLAYER_BY_DISCORD_URL, discordId);
    }
    public static final String BL_USER_PRE_URL = "https://www.beatleader.xyz/u/";
    public static final String BL_LEADERBOARD_PRE_URL = "https://www.beatleader.xyz/leaderboard/global/";
    public static final String BL_REPLAY_PRE_URL = "https://replay.beatleader.xyz/?scoreId=";

    //Acc Saber
    public static final String ACC_PRE_URL = "https://accsaber.com";
    public static final String ACC_API_PRE_URL = "https://api.accsaber.com";
    public static final String ACC_SCORES_URL = "/profile/%s/overall/scores?page=%d&_data=routes%%2Fprofile%%2F%%24userId%%2F%%24category%%2Fscores&sortBy=%s&reverse";
    public static String getAccSaberTopScoresURL(String playerId, int pageNr) {
        return String.format(ACC_PRE_URL + ACC_SCORES_URL, playerId, pageNr, "ap");
    }
    public static String getAccSaberRecentScoresURL(String playerId, int pageNr) {
        System.out.println(String.format(ACC_PRE_URL + ACC_SCORES_URL, playerId, pageNr, "timeSet"));
        return String.format(ACC_PRE_URL + ACC_SCORES_URL, playerId, pageNr, "timeSet");
    }
    public static final String ACC_USER_URL = "/profile/%s/overall/scores";
    public static String getAccSaberUserURL(String playerId) {
        return String.format(ACC_PRE_URL + ACC_USER_URL, playerId);
    }
    public static final String ACC_API_USER_HISTORY = "/players/%s/recent-rank-history";
    public static String getAccSaberUserHistoryURL(String playerId) {
        return String.format(ACC_API_PRE_URL + ACC_API_USER_HISTORY, playerId);
    }
    public static final String ACC_API_PLAYER_URL = "/players/%s";
    public static String getAccSaberProfileURL(String playerId) {
        return String.format(ACC_API_PRE_URL + ACC_API_PLAYER_URL, playerId);
    }
    public static final String ACC_LEADERBOARD_PRE_URL = "https://accsaber.com/maps/";

    // Meme API
    public static final List<String> MEME_SUBREDDITS = List.of(
        "dankmemes",
        "shitposting",
        "suspiciouslyspecific",
        "animememes",
        "Discordmemes",
        "okbuddyretard",
        "OkBrudiMongo",
        "deutschememes",
        "technicallythetruth",
        "schkreckl",
        "SchnitzelVerbrechen"
    );
    public static final String MEME_URL = "https://meme-api.com/gimme/";
}
