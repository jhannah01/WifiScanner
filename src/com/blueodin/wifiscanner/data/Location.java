package com.blueodin.wifiscanner.data;

import android.annotation.SuppressLint;

public class Location {
	double longitude;
	double latitude;
	int altitude = -1;
	int accuracy = -1;
	
	public Location(double longitude, double latitude) {
		this(longitude, latitude, -1, -1);
	}
	
	public Location(double longitude, double latitude, int altitude) {
		this(longitude, latitude, altitude, -1);
	}
	
	public Location(double longitude, double latitude, int altitude, int accuracy) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
		this.accuracy = accuracy;
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public String toString() {
		if((longitude == 0) || (latitude == 0))
			return "None";
		
		String result = String.format("%f,%f", this.latitude, this.longitude);
		
		if(this.altitude > 0)
			result += " Altitude: " + this.altitude;
			
		if(this.accuracy > 0)
			result += " (Error: " + this.accuracy + ")";
		
		return result;
	}
}