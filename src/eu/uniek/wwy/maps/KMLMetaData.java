package eu.uniek.wwy.maps;

public class KMLMetaData {
	private String heatmapFileName;
	private float north, south, east, west;
	public String getHeatmapFileName() {
		return heatmapFileName;
	}
	
	public void setHeatmapFileName(String heatmapFileName) {
		this.heatmapFileName = heatmapFileName;
	}
	
	public float getNorth() {
		return north;
	}
	
	public float getSouth() {
		return south;
	}
	
	public float getEast() {
		return east;
	}
	
	public float getWest() {
		return west;
	}
	
	public void setBoundaries(float northest, float southest, float eastest, float westest) {
		this.north = northest;
		this.south = southest;
		this.east = eastest;
		this.west = westest;		
	}
}
