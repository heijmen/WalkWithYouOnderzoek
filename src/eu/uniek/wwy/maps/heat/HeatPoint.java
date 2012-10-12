package eu.uniek.wwy.maps.heat;

public class HeatPoint {
	private float latitude;
	private float longitude;
	private int intensity;

	public HeatPoint(){
		this(0f,0f,0);
	}

	public HeatPoint(float latitude, float longitude){
		this(latitude,longitude,1);
	}

	public HeatPoint(float latitude, float longitude, int intensity) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.intensity = intensity;
	}

	public float getLatitude() {
		return latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public int getIntensity() {
		return intensity;
	}
}