package bot.dto;

import java.util.List;

public class ScrapedMapData {
//	{
//		   "diffs":[
//		      {
//		         "pp":"0",
//		         "star":"0",
//		         "scores":"0",
//		         "diff":"Expert",
//		         "type":1,
//		         "len":165,
//		         "njs":0,
//		         "njt":0,
//		         "bmb":0,
//		         "nts":750,
//		         "obs":13
//		      }
//		   ],
//		   "key":"ef08",
//		   "mapper":"",
//		   "song":"",
//		   "bpm":189,
//		   "downloadCount":308,
//		   "upVotes":0,
//		   "downVotes":3,
//		   "heat":1676.45,
//		   "rating":0.329406
//		}
	private List<ScrapedMapDiff> diffs;
	private String key;
	private String mapper;
	private String song;
	private int bpm;
	private int downloadCount;
	private int upVotes;
	private double heat;
	private double rating;

	public List<ScrapedMapDiff> getDiffs() {
		return diffs;
	}

	public void setDiffs(List<ScrapedMapDiff> diffs) {
		this.diffs = diffs;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getMapper() {
		return mapper;
	}

	public void setMapper(String mapper) {
		this.mapper = mapper;
	}

	public String getSong() {
		return song;
	}

	public void setSong(String song) {
		this.song = song;
	}

	public int getBpm() {
		return bpm;
	}

	public void setBpm(int bpm) {
		this.bpm = bpm;
	}

	public int getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}

	public int getUpVotes() {
		return upVotes;
	}

	public void setUpVotes(int upVotes) {
		this.upVotes = upVotes;
	}

	public double getHeat() {
		return heat;
	}

	public void setHeat(double heat) {
		this.heat = heat;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}
}
