package bot.dto.player;

import bot.api.ApiConstants;

import java.io.Serializable;
import java.util.List;

public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String profilePicture;
    private int rank;
    private int countryRank;
    private float pp;
    private String country;
    private long discordUserId;
    private List<Integer> historyValues;
    private String histories;
    private transient String customAccGridImage;

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
        return ApiConstants.USER_PRE_URL + this.id;
    }

}
