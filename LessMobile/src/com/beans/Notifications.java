package com.beans;

public class Notifications {

	String premise_id;
	String notification_frequency;
	int reference;

	public int getReference() {
		return reference;
	}

	public void setReference(int reference) {
		this.reference = reference;
	}

	public String getPremise_id() {
		return premise_id;
	}

	public void setPremise_id(String premise_id) {
		this.premise_id = premise_id;
	}

	public String getNotification_frequency() {
		return notification_frequency;
	}

	public void setNotification_frequency(String notification_frequency) {
		this.notification_frequency = notification_frequency;
	}

}
