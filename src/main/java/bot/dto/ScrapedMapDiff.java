package bot.dto;

public class ScrapedMapDiff {
//    {
//    "pp":"0",
//    "star":"0",
//    "scores":"0",
//    "diff":"Expert",
//    "type":1,
//    "len":165,
//    "njs":0,
//    "njt":0,
//    "bmb":0,
//    "nts":750,
//    "obs":13
// }
	private String pp;
	private String star;
	private String scores;
	private String diff;
	private int type;
	private int len;
	private int njs;
	private int njt;
	private int bmb;
	private int nts;
	private int obs;

	public String getPp() {
		return pp;
	}

	public void setPp(String pp) {
		this.pp = pp;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}

	public String getScores() {
		return scores;
	}

	public void setScores(String scores) {
		this.scores = scores;
	}

	public String getDiff() {
		return diff;
	}

	public void setDiff(String diff) {
		this.diff = diff;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public int getNjs() {
		return njs;
	}

	public void setNjs(int njs) {
		this.njs = njs;
	}

	public int getNjt() {
		return njt;
	}

	public void setNjt(int njt) {
		this.njt = njt;
	}

	public int getBmb() {
		return bmb;
	}

	public void setBmb(int bmb) {
		this.bmb = bmb;
	}

	public int getNts() {
		return nts;
	}

	public void setNts(int nts) {
		this.nts = nts;
	}

	public int getObs() {
		return obs;
	}

	public void setObs(int obs) {
		this.obs = obs;
	}
}
