package bot.dto.beatleader.scores;

import com.google.gson.annotations.SerializedName;

public class ModifierValues{

	@SerializedName("ss")
	private double ss;

	@SerializedName("no")
	private double no;

	@SerializedName("gn")
	private double gn;

	@SerializedName("modifierId")
	private int modifierId;

	@SerializedName("fs")
	private double fs;

	@SerializedName("sa")
	private int sa;

	@SerializedName("sc")
	private int sc;

	@SerializedName("sf")
	private double sf;

	@SerializedName("na")
	private double na;

	@SerializedName("nb")
	private double nb;

	@SerializedName("nf")
	private double nf;

	@SerializedName("da")
	private int da;

	@SerializedName("pm")
	private int pm;

	public double getSs(){
		return ss;
	}

	public double getNo(){
		return no;
	}

	public double getGn(){
		return gn;
	}

	public int getModifierId(){
		return modifierId;
	}

	public double getFs(){
		return fs;
	}

	public int getSa(){
		return sa;
	}

	public int getSc(){
		return sc;
	}

	public double getSf(){
		return sf;
	}

	public double getNa(){
		return na;
	}

	public double getNb(){
		return nb;
	}

	public double getNf(){
		return nf;
	}

	public int getDa(){
		return da;
	}

	public int getPm(){
		return pm;
	}
}