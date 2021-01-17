package bot.dto.beatsaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RankedMapCharacteristicsDifficulties {
	RankedMapCharacteristicsDifficulty easy;
	RankedMapCharacteristicsDifficulty normal;
	RankedMapCharacteristicsDifficulty hard;
	RankedMapCharacteristicsDifficulty expert;
	RankedMapCharacteristicsDifficulty expertplus;

	public RankedMapCharacteristicsDifficulty getEasy() {
		return easy;
	}

	public void setEasy(RankedMapCharacteristicsDifficulty easy) {
		this.easy = easy;
	}

	public RankedMapCharacteristicsDifficulty getNormal() {
		return normal;
	}

	public void setNormal(RankedMapCharacteristicsDifficulty normal) {
		this.normal = normal;
	}

	public RankedMapCharacteristicsDifficulty getHard() {
		return hard;
	}

	public void setHard(RankedMapCharacteristicsDifficulty hard) {
		this.hard = hard;
	}

	public RankedMapCharacteristicsDifficulty getExpert() {
		return expert;
	}

	public void setExpert(RankedMapCharacteristicsDifficulty expert) {
		this.expert = expert;
	}

	public RankedMapCharacteristicsDifficulty getExpertPlus() {
		return expertplus;
	}

	public void setExpertPlus(RankedMapCharacteristicsDifficulty expertPlus) {
		this.expertplus = expertPlus;
	}
	
	public List<RankedMapCharacteristicsDifficulty> getDifficultiesAsList() {
		List<RankedMapCharacteristicsDifficulty> diffs = new ArrayList<RankedMapCharacteristicsDifficulty>();
		diffs.add(easy);
		diffs.add(normal);
		diffs.add(hard);
		diffs.add(expert);
		diffs.add(expertplus);
		diffs.removeIf(Objects::isNull);
		return diffs;
	}
	
	public class RankedMapCharacteristicsDifficulty extends RankedMapDifficulty {
		double duration;
		int length;

		public double getDuration() {
			return duration;
		}

		public void setDuration(double duration) {
			this.duration = duration;
		}

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length;
		}
	}

}
