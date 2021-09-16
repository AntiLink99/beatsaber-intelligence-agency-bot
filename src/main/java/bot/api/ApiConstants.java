package bot.api;

public class ApiConstants {

    public static final String USER_ID_REGEX = "scoresaber\\.com\\/u\\/(\\d+)";
    // ScoreSaber
    public static final String USER_PRE_URL = "https://scoresaber.com/u/";
    public static final String SS_PRE_URL = "https://new.scoresaber.com";
    public static final String SS_PLAYER_PRE_URL = SS_PRE_URL + "/api/player/";
    public static final String SS_PLAYER_POST_URL = "/full";
    public static final String SS_PLAYER_TOP_SCORES_POST_URL = "/scores/top";
    public static final String SS_PLAYER_RECENT_SCORES_POST_URL = "/scores/recent";
    public static final String SS_LEADERBOARD_PRE_URL = "https://scoresaber.com/leaderboard/";
    public static final String QUALIFIED_URL = "http://scoresaber.com/api.php?function=get-leaderboards&cat=5&limit=100&page=1&unique=1&qualified=1";
    public static final String NO_AVATAR_URL = "https://scoresaber.com/imports/images/usr-avatars/404.jpg";

    // Beat Saver
    public static final String BS_PRE_URL = "https://cdn.beatsaver.com";
    public static final String BS_API_PRE_URL = "https://api.beatsaver.com";

    public static final String BS_MAP_BY_KEY = BS_API_PRE_URL + "/maps/id/";
    public static final String BS_MAP_BY_HASH_URL = BS_API_PRE_URL + "/maps/hash/";

    public static final String BS_DEFAULT_MAP_URL = BS_PRE_URL + "/maps/";

    public static final String BS_RANKED_MAPS_PRE_URL = BS_API_PRE_URL + "/search/text/";
    public static final String BS_RANKED_MAPS_POST_URL = "?sortOrder=Latest&ranked=true";

    // Beat Saviour
    public static final String BSAVIOUR_PRE_URL = "https://www.beatsavior.io";
    public static final String BSAVIOUR_RANKED_MAPS_URL = BSAVIOUR_PRE_URL + "/api/maps/ranked";
    public static final String BSAVIOUR_LIVESCORES_URL = BSAVIOUR_PRE_URL + "/api/livescores/player/";

    // Meme API
    public static final String MEME_URL = "https://meme-api.herokuapp.com/gimme";
}
