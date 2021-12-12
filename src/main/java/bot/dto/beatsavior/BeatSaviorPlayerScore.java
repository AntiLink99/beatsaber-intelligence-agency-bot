package bot.dto.beatsavior;

import bot.dto.beatsavior.trackers.PlayerTrackers;
import bot.dto.beatsavior.trackers.ScoreTracker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BeatSaviorPlayerScore implements Comparable<BeatSaviorPlayerScore> {

    String _id;
    int songDataType;
    String playerID;
    String songID;
    String songDifficulty;
    String songName;
    String songArtist;
    String songMapper;
    int songSpeed;
    int songStartTime;
    float songDuration;
    PlayerTrackers trackers;
    String timeSet;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getSongDataType() {
        return songDataType;
    }

    public void setSongDataType(int songDataType) {
        this.songDataType = songDataType;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public String getSongID() {
        return songID;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }

    public String getSongDifficulty() {
        return songDifficulty;
    }

    public void setSongDifficulty(String songDifficulty) {
        this.songDifficulty = songDifficulty;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongMapper() {
        return songMapper;
    }

    public void setSongMapper(String songMapper) {
        this.songMapper = songMapper;
    }

    public int getSongSpeed() {
        return songSpeed;
    }

    public void setSongSpeed(int songSpeed) {
        this.songSpeed = songSpeed;
    }

    public int getSongStartTime() {
        return songStartTime;
    }

    public void setSongStartTime(int songStartTime) {
        this.songStartTime = songStartTime;
    }

    public float getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(float songDuration) {
        this.songDuration = songDuration;
    }

    public PlayerTrackers getTrackers() {
        return trackers;
    }

    public void setTrackers(PlayerTrackers trackers) {
        this.trackers = trackers;
    }

    public String getTimeSet() {
        return timeSet;
    }

    public void setTimeSet(String timeSet) {
        this.timeSet = timeSet;
    }

    public LocalDateTime getTimeSetLocalDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return LocalDateTime.parse(timeSet, formatter);
    }

    @Override
    public int compareTo(BeatSaviorPlayerScore otherScore) {
        ScoreTracker otherTracker = otherScore.getTrackers().getScoreTracker();
        ScoreTracker thisTracker = getTrackers().getScoreTracker();
        if (otherTracker == null && thisTracker == null) {
            return 0;
        }
        if (otherTracker == null) {
            return -1;
        }
        if (thisTracker == null) {
            return 1;
        }
        return otherTracker.getScore() > thisTracker.getScore() ? 1 : -1;
        //return otherScore.getTimeSetLocalDateTime().compareTo(this.getTimeSetLocalDateTime());
    }
}