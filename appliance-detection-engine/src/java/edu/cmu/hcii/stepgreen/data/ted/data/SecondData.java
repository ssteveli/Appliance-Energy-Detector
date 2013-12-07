package edu.cmu.hcii.stepgreen.data.ted.data;

import java.util.Calendar;

public class SecondData {

	private long cal;
	private float v;
	private int p;
	
	public long getCalLong() {
		return cal;
	}

	public float getVoltage() {
		return v;
	}

	public int getPower() {
		return p;
	}

	public void setCalLong(long timestamp) {
		this.cal = timestamp;
	}

	public void setVoltage(float float1) {
		this.v = float1;
	}

	public void setPower(int int1) {
		this.p = int1;
	}

	public Calendar getCal() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(cal);
		return c;
	}

	@Override
	public String toString() {
		return "SecondData [cal=" + cal + ", v=" + v + ", p=" + p + "]";
	}

}
