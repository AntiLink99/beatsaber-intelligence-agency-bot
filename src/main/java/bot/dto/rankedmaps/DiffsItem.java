package bot.dto.rankedmaps;

import com.google.gson.annotations.SerializedName;

public class DiffsItem{

	@SerializedName("notes")
	private int notes;

	@SerializedName("offset")
	private double offset;

	@SerializedName("obstacles")
	private int obstacles;

	@SerializedName("bombs")
	private int bombs;

	@SerializedName("length")
	private double length;

	@SerializedName("chroma")
	private boolean chroma;

	@SerializedName("stars")
	private double stars;

	@SerializedName("characteristic")
	private String characteristic;

	@SerializedName("nps")
	private double nps;

	@SerializedName("difficulty")
	private String difficulty;

	@SerializedName("paritySummary")
	private ParitySummary paritySummary;

	@SerializedName("seconds")
	private double seconds;

	@SerializedName("njs")
	private int njs;

	@SerializedName("ne")
	private boolean ne;

	@SerializedName("me")
	private boolean me;

	@SerializedName("cinema")
	private boolean cinema;

	@SerializedName("events")
	private int events;

	public void setNotes(int notes){
		this.notes = notes;
	}

	public int getNotes(){
		return notes;
	}

	public void setOffset(double offset){
		this.offset = offset;
	}

	public double getOffset(){
		return offset;
	}

	public void setObstacles(int obstacles){
		this.obstacles = obstacles;
	}

	public int getObstacles(){
		return obstacles;
	}

	public void setBombs(int bombs){
		this.bombs = bombs;
	}

	public int getBombs(){
		return bombs;
	}

	public void setLength(double length){
		this.length = length;
	}

	public double getLength(){
		return length;
	}

	public void setChroma(boolean chroma){
		this.chroma = chroma;
	}

	public boolean isChroma(){
		return chroma;
	}

	public void setStars(double stars){
		this.stars = stars;
	}

	public double getStars(){
		return stars;
	}

	public void setCharacteristic(String characteristic){
		this.characteristic = characteristic;
	}

	public String getCharacteristic(){
		return characteristic;
	}

	public void setNps(double nps){
		this.nps = nps;
	}

	public double getNps(){
		return nps;
	}

	public void setDifficulty(String difficulty){
		this.difficulty = difficulty;
	}

	public String getDifficulty(){
		return difficulty;
	}

	public void setParitySummary(ParitySummary paritySummary){
		this.paritySummary = paritySummary;
	}

	public ParitySummary getParitySummary(){
		return paritySummary;
	}

	public void setSeconds(double seconds){
		this.seconds = seconds;
	}

	public double getSeconds(){
		return seconds;
	}

	public void setNjs(int njs){
		this.njs = njs;
	}

	public int getNjs(){
		return njs;
	}

	public void setNe(boolean ne){
		this.ne = ne;
	}

	public boolean isNe(){
		return ne;
	}

	public void setMe(boolean me){
		this.me = me;
	}

	public boolean isMe(){
		return me;
	}

	public void setCinema(boolean cinema){
		this.cinema = cinema;
	}

	public boolean isCinema(){
		return cinema;
	}

	public void setEvents(int events){
		this.events = events;
	}

	public int getEvents(){
		return events;
	}
}