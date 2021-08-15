package bot.dto.beatsaviour;

public class SongDifficulties {

	private boolean easy;
	private boolean normal;
	private boolean hard;
	private boolean expert;
	private boolean expertPlus;

	public boolean isEasy() {
		return easy;
	}

	public void setEasy(boolean easy) {
		this.easy = easy;
	}

	public boolean isNormal() {
		return normal;
	}

	public void setNormal(boolean normal) {
		this.normal = normal;
	}

	public boolean isHard() {
		return hard;
	}

	public void setHard(boolean hard) {
		this.hard = hard;
	}

	public boolean isExpert() {
		return expert;
	}

	public void setExpert(boolean expert) {
		this.expert = expert;
	}

	public boolean isExpertPlus() {
		return expertPlus;
	}

	public void setExpertPlus(boolean expertPlus) {
		this.expertPlus = expertPlus;
	}
}
