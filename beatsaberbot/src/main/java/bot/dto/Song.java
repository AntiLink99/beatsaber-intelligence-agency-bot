package bot.dto;

import java.util.Date;
import java.util.List;

public class Song {

	public String id;
	public String name;
	public String description;
	public Uploader uploader;
	public Metadata metadata;
	public Stats stats;
	public Date uploaded;
	public boolean automapper;
	public boolean ranked;
	public boolean qualified;
	public List<Version> versions;
	
	public Song(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public class Uploader {
		public int id;
		public String name;
		public String hash;
		public String avatar;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getHash() {
			return hash;
		}

		public void setHash(String hash) {
			this.hash = hash;
		}

		public String getAvatar() {
			return avatar;
		}

		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}
	}

	public class Metadata {
		public int bpm;
		public int duration;
		public String songName;
		public String songSubName;
		public String songAuthorName;
		public String levelAuthorName;

		public int getBpm() {
			return bpm;
		}

		public void setBpm(int bpm) {
			this.bpm = bpm;
		}

		public int getDuration() {
			return duration;
		}

		public void setDuration(int duration) {
			this.duration = duration;
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
	}

	public class Stats {
		public int plays;
		public int downloads;
		public int upvotes;
		public int downvotes;
		public double score;

		public int getPlays() {
			return plays;
		}

		public void setPlays(int plays) {
			this.plays = plays;
		}

		public int getDownloads() {
			return downloads;
		}

		public void setDownloads(int downloads) {
			this.downloads = downloads;
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

		public double getScore() {
			return score;
		}

		public void setScore(double score) {
			this.score = score;
		}
	}

	public class ParitySummary {
		public int errors;
		public int warns;
		public int resets;

		public int getErrors() {
			return errors;
		}

		public void setErrors(int errors) {
			this.errors = errors;
		}

		public int getWarns() {
			return warns;
		}

		public void setWarns(int warns) {
			this.warns = warns;
		}

		public int getResets() {
			return resets;
		}

		public void setResets(int resets) {
			this.resets = resets;
		}
	}

	public class Diff {
		public int njs;
		public double offset;
		public int notes;
		public int bombs;
		public int obstacles;
		public double nps;
		public double length;
		public String characteristic;
		public String difficulty;
		public int events;
		public boolean chroma;
		public boolean me;
		public boolean ne;
		public boolean cinema;
		public double seconds;
		public ParitySummary paritySummary;

		public int getNjs() {
			return njs;
		}

		public void setNjs(int njs) {
			this.njs = njs;
		}

		public double getOffset() {
			return offset;
		}

		public void setOffset(double offset) {
			this.offset = offset;
		}

		public int getNotes() {
			return notes;
		}

		public void setNotes(int notes) {
			this.notes = notes;
		}

		public int getBombs() {
			return bombs;
		}

		public void setBombs(int bombs) {
			this.bombs = bombs;
		}

		public int getObstacles() {
			return obstacles;
		}

		public void setObstacles(int obstacles) {
			this.obstacles = obstacles;
		}

		public double getNps() {
			return nps;
		}

		public void setNps(double nps) {
			this.nps = nps;
		}

		public double getLength() {
			return length;
		}

		public void setLength(double length) {
			this.length = length;
		}

		public String getCharacteristic() {
			return characteristic;
		}

		public void setCharacteristic(String characteristic) {
			this.characteristic = characteristic;
		}

		public String getDifficulty() {
			return difficulty;
		}

		public void setDifficulty(String difficulty) {
			this.difficulty = difficulty;
		}

		public int getEvents() {
			return events;
		}

		public void setEvents(int events) {
			this.events = events;
		}

		public boolean isChroma() {
			return chroma;
		}

		public void setChroma(boolean chroma) {
			this.chroma = chroma;
		}

		public boolean isMe() {
			return me;
		}

		public void setMe(boolean me) {
			this.me = me;
		}

		public boolean isNe() {
			return ne;
		}

		public void setNe(boolean ne) {
			this.ne = ne;
		}

		public boolean isCinema() {
			return cinema;
		}

		public void setCinema(boolean cinema) {
			this.cinema = cinema;
		}

		public double getSeconds() {
			return seconds;
		}

		public void setSeconds(double seconds) {
			this.seconds = seconds;
		}

		public ParitySummary getParitySummary() {
			return paritySummary;
		}

		public void setParitySummary(ParitySummary paritySummary) {
			this.paritySummary = paritySummary;
		}
	}

	public class Version implements Comparable<Version> {
		public String hash;
		public String key;
		public String state;
		public Date createdAt;
		public int sageScore;
		public List<Diff> diffs;
		public String downloadURL;
		public String coverURL;
		public String previewURL;

		public String getHash() {
			return hash;
		}

		public void setHash(String hash) {
			this.hash = hash;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public Date getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(Date createdAt) {
			this.createdAt = createdAt;
		}

		public int getSageScore() {
			return sageScore;
		}

		public void setSageScore(int sageScore) {
			this.sageScore = sageScore;
		}

		public List<Diff> getDiffs() {
			return diffs;
		}

		public void setDiffs(List<Diff> diffs) {
			this.diffs = diffs;
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

		public String getPreviewURL() {
			return previewURL;
		}

		public void setPreviewURL(String previewURL) {
			this.previewURL = previewURL;
		}

		public Diff getDiffByName(String name) {
			return diffs.stream().filter(diff -> name.equals(diff.getDifficulty())).findFirst().orElse(null);
		}
		
		@Override
		public int compareTo(Version o) {
			return getCreatedAt().compareTo(o.getCreatedAt());
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Uploader getUploader() {
		return uploader;
	}

	public void setUploader(Uploader uploader) {
		this.uploader = uploader;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public Stats getStats() {
		return stats;
	}

	public void setStats(Stats stats) {
		this.stats = stats;
	}

	public Date getUploaded() {
		return uploaded;
	}

	public void setUploaded(Date uploaded) {
		this.uploaded = uploaded;
	}

	public boolean isAutomapper() {
		return automapper;
	}

	public void setAutomapper(boolean automapper) {
		this.automapper = automapper;
	}

	public boolean isRanked() {
		return ranked;
	}

	public void setRanked(boolean ranked) {
		this.ranked = ranked;
	}

	public boolean isQualified() {
		return qualified;
	}

	public void setQualified(boolean qualified) {
		this.qualified = qualified;
	}

	public List<Version> getVersions() {
		return versions;
	}

	public void setVersions(List<Version> versions) {
		this.versions = versions;
	}

	public Version getLatestVersion() {
		return versions == null ? null : versions.stream().sorted().findFirst().orElse(null);
	}
	
	public Version getVersionByHash(String hash) {
		return getVersions().stream().filter(ver -> hash.toLowerCase().equals(ver.getHash().toLowerCase())).findFirst().orElse(null);
	}
}
