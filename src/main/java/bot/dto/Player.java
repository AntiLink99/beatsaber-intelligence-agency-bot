package bot.dto;

import java.io.Serializable;
import java.util.List;

public class Player implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String playerId;
	private String playerName;
	private String avatar;
	private int rank;
	private int countryRank;
	private float pp;
	private String country;
	private long discordUserId;
	private List<Integer> historyValues;
	private String history;

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
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

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public List<Integer> getHistoryValues() {
		return historyValues;
	}

	public void setHistoryValues(List<Integer> historyValues) {
		this.historyValues = historyValues;
	}

}
