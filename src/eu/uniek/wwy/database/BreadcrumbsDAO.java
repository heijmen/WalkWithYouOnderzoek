package eu.uniek.wwy.database;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import eu.uniek.wwy.location.GPSLocation;

public class BreadcrumbsDAO {

	public void saveBreadcrumbs(List<GPSLocation> locations, String file) throws Exception {
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(locations);
		fos.close();
	}

	@SuppressWarnings("unchecked")
	public List<GPSLocation> getBreadcrumbs(String file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		List<GPSLocation> locations = (List<GPSLocation>) ois.readObject();
		fis.close();
		return locations;
	}


	
}
