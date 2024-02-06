package bot.dto.player;

import bot.api.ApiConstants;
import bot.dto.LeaderboardService;
import bot.dto.LeaderboardServicePlayer;
import org.bson.Document;

import java.io.Serializable;
import java.util.List;

public class DataBasePlayer implements Serializable, LeaderboardServicePlayer {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String profilePicture;
    private String bio;
    private int rank;
    private int countryRank;
    private float pp;
    private String country;
    private long discordUserId;
    private List<Integer> historyValues;
    private String histories;
    private ScoreStats scoreStats;
    private transient String customAccGridImage;

    private transient LeaderboardService service = LeaderboardService.SCORESABER;

    @Override
    public String getAvatar() {
        return profilePicture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getPlayerIdLong() {
        return Long.parseLong(this.id);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getCountryRank() {
        return countryRank;
    }

    public void setCountryRank(int countryRank) {
        this.countryRank = countryRank;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public float getPp() {
        return pp;
    }

    public void setPp(float pp) {
        this.pp = pp;
    }

    public long getDiscordUserId() {
        return discordUserId;
    }

    public void setDiscordUserId(long discordUserId) {
        this.discordUserId = discordUserId;
    }

    public String getHistories() {
        return histories;
    }

    public void setHistories(String histories) {
        this.histories = histories;
    }

    public List<Integer> getHistoryValues() {
        return historyValues;
    }

    public void setHistoryValues(List<Integer> historyValues) {
        this.historyValues = historyValues;
    }

    public String getCustomAccGridImage() {
        return customAccGridImage;
    }

    public void setCustomAccGridImage(String customAccGridImage) {
        this.customAccGridImage = customAccGridImage;
    }

    public String getProfileURL() {
        if (service == LeaderboardService.SCORESABER) {
            return ApiConstants.USER_PRE_URL + this.id;
        }
        return ApiConstants.getAccSaberUserURL(this.id);
    }

    public void setService(LeaderboardService service) {
        this.service = service;
    }

    public ScoreStats getScoreStats() {
        return scoreStats;
    }

    public void setScoreStats(ScoreStats scoreStats) {
        this.scoreStats = scoreStats;
    }

    public Document toDocument() {
        return new Document()
                .append("player_id", Long.parseLong(id))
                .append("player_name", name)
                .append("player_avatar", profilePicture)
                .append("player_bio", bio)
                .append("player_rank", rank)
                .append("player_country_rank", countryRank)
                .append("player_pp", pp)
                .append("player_country", country)
                .append("player_discord_user_id", discordUserId)
                .append("player_historyValues", historyValues)
                .append("player_histories", histories)
                .append("player_scoreStats", scoreStats.toDocument())
                .append("user_customAccGridImage", customAccGridImage);
    }

    public static DataBasePlayer fromDocument(Document document) {
        if (document == null) return null;

        DataBasePlayer player = new DataBasePlayer();
        player.setId(String.valueOf(document.get("player_id")));
        player.setName(document.getString("player_name"));
        player.setProfilePicture(document.getString("player_avatar"));
        player.setBio(document.getString("player_bio"));
        player.setRank(document.getInteger("player_rank"));
        player.setCountryRank(document.getInteger("player_country_rank"));
        player.setPp(document.getDouble("player_pp").floatValue());
        player.setCountry(document.getString("player_country"));
        player.setDiscordUserId(document.getLong("player_discord_user_id"));
        player.setHistoryValues(document.getList("player_historyValues", Integer.class));
        player.setHistories(document.getString("player_histories"));
        player.setScoreStats(ScoreStats.fromDocument((Document) document.get("player_scoreStats")));
        player.setCustomAccGridImage(document.getString("user_customAccGridImage"));
        return player;
    }

}
