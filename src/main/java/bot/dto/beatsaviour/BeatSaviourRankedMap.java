package bot.dto.beatsaviour;

import java.util.List;

public class BeatSaviourRankedMap {

	String _id;
	RankedMapMetadata metadata;
	RankedMapStats stats;
	String description;
	Object deletedAt;
	String key;
	String name;
	RankedMapUploader uploader;
	String uploaded;
	String hash;
	String directDownload;
	String downloadURL;
	String coverURL;
	String songName;
	String songSubName;
	String songAuthorName;
	String levelAuthorName;
	private List<Float> stars;
	RankedMapRootDifficulties diffs;
	double bpm;
	int playedCount;
	int upvotes;
	int downvotes;	
	boolean ranked;
	long timestamp;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public RankedMapMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(RankedMapMetadata metadata) {
		this.metadata = metadata;
	}

	public RankedMapStats getStats() {
		return stats;
	}

	public void setStats(RankedMapStats stats) {
		this.stats = stats;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Object getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Object deletedAt) {
		this.deletedAt = deletedAt;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RankedMapUploader getUploader() {
		return uploader;
	}

	public void setUploader(RankedMapUploader uploader) {
		this.uploader = uploader;
	}

	public RankedMapRootDifficulties getDiffs() {
		return diffs;
	}

	public void setDiffs(RankedMapRootDifficulties diffs) {
		this.diffs = diffs;
	}

	public void setBpm(double bpm) {
		this.bpm = bpm;
	}
	public String getUploaded() {
		return uploaded;
	}

	public void setUploaded(String uploaded) {
		this.uploaded = uploaded;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getDirectDownload() {
		return directDownload;
	}

	public void setDirectDownload(String directDownload) {
		this.directDownload = directDownload;
	}

	public String getDownloadURL() {
		return downloadURL;
	}

	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}

	public String getCoverURL() {
		return coverURL;
	}

	public void setCoverURL(String coverURL) {
		this.coverURL = coverURL;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

	public String getSongSubName() {
		return songSubName;
	}

	public void setSongSubName(String songSubName) {
		this.songSubName = songSubName;
	}

	public String getSongAuthorName() {
		return songAuthorName;
	}

	public void setSongAuthorName(String songAuthorName) {
		this.songAuthorName = songAuthorName;
	}

	public String getLevelAuthorName() {
		return levelAuthorName;
	}

	public void setLevelAuthorName(String levelAuthorName) {
		this.levelAuthorName = levelAuthorName;
	}

	public double getBpm() {
		return bpm;
	}

	public void setBpm(int bpm) {
		this.bpm = bpm;
	}

	public int getPlayedCount() {
		return playedCount;
	}

	public void setPlayedCount(int playedCount) {
		this.playedCount = playedCount;
	}

	public int getUpvotes() {
		return upvotes;
	}

	public void setUpvotes(int upvotes) {
		this.upvotes = upvotes;
	}

	public int getDownvotes() {
		return downvotes;
	}

	public void setDownvotes(int downvotes) {
		this.downvotes = downvotes;
	}

	public boolean isRanked() {
		return ranked;
	}

	public void setRanked(boolean ranked) {
		this.ranked = ranked;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public List<Float> getStars() {
		return stars;
	}

	public void setStars(List<Float> stars) {
		this.stars = stars;
	}

	public class RankedMapMetadata {
		private SongDifficulties difficulties;
		private int duration;
		boolean automapper;
		private List<RankedMapCharacteristics> characteristics;

		public SongDifficulties getDifficulties() {
			return difficulties;
		}

		public void setDifficulties(SongDifficulties difficulties) {
			this.difficulties = difficulties;
		}

		public int getDuration() {
			return duration;
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}

		public List<RankedMapCharacteristics> getCharacteristics() {
			return characteristics;
		}

		public void setCharacteristics(List<RankedMapCharacteristics> characteristics) {
			this.characteristics = characteristics;
		}
	}

	class RankedMapStats {
		int downloads;
		int plays;
		int downVotes;
		int upVotes;
		float heat;
		double rating;

		public int getDownloads() {
			return downloads;
		}

		public void setDownloads(int downloads) {
			this.downloads = downloads;
		}

		public int getPlays() {
			return plays;
		}

		public void setPlays(int plays) {
			this.plays = plays;
		}

		public int getDownVotes() {
			return downVotes;
		}

		public void setDownVotes(int downVotes) {
			this.downVotes = downVotes;
		}

		public int getUpVotes() {
			return upVotes;
		}

		public void setUpVotes(int upVotes) {
			this.upVotes = upVotes;
		}

		public float getHeat() {
			return heat;
		}

		public void setHeat(float heat) {
			this.heat = heat;
		}

		public double getRating() {
			return rating;
		}

		public void setRating(double rating) {
			this.rating = rating;
		}
	}

	class RankedMapUploader {
		private String _id;
		private String username;

		public String get_id() {
			return _id;
		}

		public void set_id(String _id) {
			this._id = _id;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}
	}
}
