package bot.dto.rankedmaps;

import com.google.gson.annotations.SerializedName;

public class ParitySummary{

	@SerializedName("warns")
	private int warns;

	@SerializedName("resets")
	private int resets;

	@SerializedName("errors")
	private int errors;

	public void setWarns(int warns){
		this.warns = warns;
	}

	public int getWarns(){
		return warns;
	}

	public void setResets(int resets){
		this.resets = resets;
	}

	public int getResets(){
		return resets;
	}

	public void setErrors(int errors){
		this.errors = errors;
	}

	public int getErrors(){
		return errors;
	}
}