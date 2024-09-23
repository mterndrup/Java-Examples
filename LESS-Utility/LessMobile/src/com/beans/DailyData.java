package com.beans;

import java.util.Vector;

public class DailyData {

	private float cityAverage;
	private Vector<Intervals> intervals;
	private float totalCost;
	private float totalkWh;
	private Vector<Float> temps;

	public float getCityAverage() {
		return cityAverage;
	}

	public void setCityAverage(float cityAverage) {
		this.cityAverage = cityAverage;
	}

	public Vector<Intervals> getIntervals() {
		return intervals;
	}

	public void setIntervals(Vector<Intervals> intervals) {
		this.intervals = intervals;
	}

	public float getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(float totalCost) {
		this.totalCost = totalCost;
	}

	public float getTotalkWh() {
		return totalkWh;
	}

	public void setTotalkWh(float totalkWh) {
		this.totalkWh = totalkWh;
	}

	public Vector<Float> getTemps() {
		return temps;
	}

	public void setTemps(Vector<Float> temps) {
		this.temps = temps;
	}

}
