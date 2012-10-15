package eu.uniek.wwy.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import eu.uniek.wwy.location.GPSLocation;

public class DataWrapper implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<GPSLocation> breadcrumbs = new ArrayList<GPSLocation>();
	private List<GPSLocation> pointsOfInterest = new ArrayList<GPSLocation>();
	
	public DataWrapper() {
	}

	public List<GPSLocation> getBreadcrumbs() {
		return breadcrumbs;
	}
	
	public void setBreadcrumbs(List<GPSLocation> breadcrumbs) {
		this.breadcrumbs = breadcrumbs;
	}
	
	public List<GPSLocation> getPointsOfInterest() {
		return pointsOfInterest;
	}
	
	public void setPointsOfInterest(List<GPSLocation> pointsOfInterest) {
		this.pointsOfInterest = pointsOfInterest;
	}
}