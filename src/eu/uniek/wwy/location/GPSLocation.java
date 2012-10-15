package eu.uniek.wwy.location;

import java.io.Serializable;

public class GPSLocation  implements Serializable{
	private static final long serialVersionUID = 1L;
	private double latitude, longitude;
	private int intensity;

	public GPSLocation(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		intensity = 0;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public int getIntensity() {
		return intensity;
	}
}