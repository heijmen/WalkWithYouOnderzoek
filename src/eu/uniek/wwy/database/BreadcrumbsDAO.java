package eu.uniek.wwy.database;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BreadcrumbsDAO {
	
	public void saveData(DataWrapper data, String file) throws Exception {
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(data);
		fos.close();
	}
	public DataWrapper getData(String file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		DataWrapper data = ((DataWrapper) ois.readObject());
		fis.close();
		return data;
	}

	
}
