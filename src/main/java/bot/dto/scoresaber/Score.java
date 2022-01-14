package bot.dto.scoresaber;

import com.google.gson.annotations.SerializedName;
import org.ocpsoft.prettytime.PrettyTime;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Score{

	@SerializedName("pp")
	private double pp;

	@SerializedName("fullCombo")
	private boolean fullCombo;

	@SerializedName("multiplier")
	private float multiplier;

	@SerializedName("weight")
	private float weight;

	@SerializedName("baseScore")
	private int baseScore;

	@SerializedName("modifiers")
	private String modifiers;

	@SerializedName("maxCombo")
	private int maxCombo;

	@SerializedName("modifiedScore")
	private int modifiedScore;

	@SerializedName("rank")
	private int rank;

	@SerializedName("id")
	private int id;

	@SerializedName("missedNotes")
	private int missedNotes;

	@SerializedName("timeSet")
	private String timeSet;

	@SerializedName("badCuts")
	private int badCuts;

	@SerializedName("hasReplay")
	private boolean hasReplay;

	@SerializedName("hmd")
	private int hmd;

	private double accuracy = -1;

	public double getPp(){
		return pp;
	}

	public boolean isFullCombo(){
		return fullCombo;
	}

	public float getMultiplier(){
		return multiplier;
	}

	public float getWeight(){
		return weight;
	}

	public int getBaseScore(){
		return baseScore;
	}

	public String getModifiers(){
		return modifiers;
	}

	public int getMaxCombo(){
		return maxCombo;
	}

	public int getModifiedScore(){
		return modifiedScore;
	}

	public int getRank(){
		return rank;
	}

	public int getId(){
		return id;
	}

	public int getMissedNotes(){
		return missedNotes;
	}

	public String getTimeSet(){
		return timeSet;
	}

	public int getBadCuts(){
		return badCuts;
	}

	public boolean isHasReplay(){
		return hasReplay;
	}

	public int getHmd(){
		return hmd;
	}

	public LocalDateTime getTimeSetLocalDateTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return LocalDateTime.parse(timeSet, formatter);
	}

	public String getRelativeTimeString() {
		return new PrettyTime(Locale.ENGLISH).format(Date.from(getTimeSetLocalDateTime().atZone(ZoneOffset.UTC).toInstant()));
	}

	protected double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
}