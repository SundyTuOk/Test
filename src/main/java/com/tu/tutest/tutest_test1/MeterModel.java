package com.tu.tutest.tutest_test1;

public class MeterModel {
	private int ammeterID;
	private String ammeterName;
	public int getAmmeterID() {
		return ammeterID;
	}
	public void setAmmeterID(int ammeterID) {
		this.ammeterID = ammeterID;
	}
	public String getAmmeterName() {
		return ammeterName;
	}
	public void setAmmeterName(String ammeterName) {
		this.ammeterName = ammeterName;
	}
	public double getTerminalNum() {
		return terminalNum;
	}
	public void setTerminalNum(double terminalNum) {
		this.terminalNum = terminalNum;
	}
	public double getSYValue() {
		return SYValue;
	}
	public void setSYValue(double sYValue) {
		SYValue = sYValue;
	}
	public double getGross() {
		return gross;
	}
	public void setGross(double gross) {
		this.gross = gross;
	}
	private double terminalNum;
	private double SYValue;
	private double gross;
	private double oldSYValue;
	public double getOldSYValue() {
		return oldSYValue;
	}
	public void setOldSYValue(double oldSYValue) {
		this.oldSYValue = oldSYValue;
	}
}
