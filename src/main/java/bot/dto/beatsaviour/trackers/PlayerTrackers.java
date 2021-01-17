package bot.dto.beatsaviour.trackers;

public class PlayerTrackers {
	HitTracker hitTracker;
	AccuracyTracker accuracyTracker;
	ScoreTracker scoreTracker;
	WinTracker winTracker;
	DistanceTracker distanceTracker;
	ScoreGraphTracker scoreGraphTracker;

	public HitTracker getHitTracker() {
		return hitTracker;
	}

	public void setHitTracker(HitTracker hitTracker) {
		this.hitTracker = hitTracker;
	}

	public AccuracyTracker getAccuracyTracker() {
		return accuracyTracker;
	}

	public void setAccuracyTracker(AccuracyTracker accuracyTracker) {
		this.accuracyTracker = accuracyTracker;
	}

	public ScoreTracker getScoreTracker() {
		return scoreTracker;
	}

	public void setScoreTracker(ScoreTracker scoreTracker) {
		this.scoreTracker = scoreTracker;
	}

	public WinTracker getWinTracker() {
		return winTracker;
	}

	public void setWinTracker(WinTracker winTracker) {
		this.winTracker = winTracker;
	}

	public DistanceTracker getDistanceTracker() {
		return distanceTracker;
	}

	public void setDistanceTracker(DistanceTracker distanceTracker) {
		this.distanceTracker = distanceTracker;
	}

	public ScoreGraphTracker getScoreGraphTracker() {
		return scoreGraphTracker;
	}

	public void setScoreGraphTracker(ScoreGraphTracker scoreGraphTracker) {
		this.scoreGraphTracker = scoreGraphTracker;
	}
}
