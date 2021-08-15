package bot.dto.beatsaviour.trackers;

import java.util.List;

public class AccuracyTracker {
	float accRight;
	float accLeft;
	float averageAcc;
	float leftSpeed;
	float rightSpeed;
	float averageSpeed;
	float leftPreswing;
	float rightPreswing;
	float averagePreswing;
	float leftPostswing;
	float rightPostswing;
	float averagePostswing;
	List<Float> leftAverageCut;
	List<Float> rightAverageCut;
	List<Float> averageCut;
	List<Float> gridAcc;
	List<Integer> gridCut;

	public float getLeftPreswing() {
		return leftPreswing;
	}

	public void setLeftPreswing(float leftPreswing) {
		this.leftPreswing = leftPreswing;
	}

	public float getRightPreswing() {
		return rightPreswing;
	}

	public void setRightPreswing(float rightPreswing) {
		this.rightPreswing = rightPreswing;
	}

	public float getAveragePreswing() {
		return averagePreswing;
	}

	public void setAveragePreswing(float averagePreswing) {
		this.averagePreswing = averagePreswing;
	}

	public float getLeftPostswing() {
		return leftPostswing;
	}

	public void setLeftPostswing(float leftPostswing) {
		this.leftPostswing = leftPostswing;
	}

	public float getRightPostswing() {
		return rightPostswing;
	}

	public void setRightPostswing(float rightPostswing) {
		this.rightPostswing = rightPostswing;
	}

	public float getAveragePostswing() {
		return averagePostswing;
	}

	public void setAveragePostswing(float averagePostswing) {
		this.averagePostswing = averagePostswing;
	}

	public float getAccRight() {
		return accRight;
	}

	public void setAccRight(float accRight) {
		this.accRight = accRight;
	}

	public float getAccLeft() {
		return accLeft;
	}

	public void setAccLeft(float accLeft) {
		this.accLeft = accLeft;
	}

	public float getAverageAcc() {
		return averageAcc;
	}

	public void setAverageAcc(float averageAcc) {
		this.averageAcc = averageAcc;
	}

	public float getLeftSpeed() {
		return leftSpeed;
	}

	public void setLeftSpeed(float leftSpeed) {
		this.leftSpeed = leftSpeed;
	}

	public float getRightSpeed() {
		return rightSpeed;
	}

	public void setRightSpeed(float rightSpeed) {
		this.rightSpeed = rightSpeed;
	}

	public float getAverageSpeed() {
		return averageSpeed;
	}

	public void setAverageSpeed(float averageSpeed) {
		this.averageSpeed = averageSpeed;
	}

	public List<Float> getLeftAverageCut() {
		return leftAverageCut;
	}

	public void setLeftAverageCut(List<Float> leftAverageCut) {
		this.leftAverageCut = leftAverageCut;
	}

	public List<Float> getRightAverageCut() {
		return rightAverageCut;
	}

	public void setRightAverageCut(List<Float> rightAverageCut) {
		this.rightAverageCut = rightAverageCut;
	}

	public List<Float> getAverageCut() {
		return averageCut;
	}

	public void setAverageCut(List<Float> averageCut) {
		this.averageCut = averageCut;
	}

	public List<Float> getGridAcc() {
		return gridAcc;
	}

	public void setGridAcc(List<Float> gridAcc) {
		this.gridAcc = gridAcc;
	}

	public List<Integer> getGridCut() {
		return gridCut;
	}

	public void setGridCut(List<Integer> gridCut) {
		this.gridCut = gridCut;
	}
}
