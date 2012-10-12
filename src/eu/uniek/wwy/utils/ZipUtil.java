package eu.uniek.wwy.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

public class ZipUtil {
	public static ZipOutputStream createZipFile(String zipPath,OutputStream... streams) {
		try {
			return makeZipFile(zipPath, streams);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static ZipOutputStream makeZipFile(String zipPath, OutputStream... streams) throws IOException {
		ZipOutputStream initialzipFile = initialiseZipFile(zipPath);
		try {
			int i = 0;
			for(OutputStream stream : streams) {
				String fileName;
				if (i == 0)	{
					fileName = "heatmap.kml";
				} else {
					fileName = "walkwithyou.png";
				}
				i++;
				insertFileIn(fileName, stream, initialzipFile);
			}
		}
		finally {
			initialzipFile.close();
		}
		return initialzipFile;
	}


	private static void insertFileIn(String path, OutputStream stream, ZipOutputStream initialzipFile) throws IOException {
		ZipEntry zipEntry = new ZipEntry(path);
		InputStream in = new ByteArrayInputStream(((ByteArrayOutputStream) stream).toByteArray());
		initialzipFile.putNextEntry(zipEntry);
		byte[] b = IOUtils.toByteArray(in);
		initialzipFile.write(b);
		initialzipFile.closeEntry();
	}

	private static ZipOutputStream initialiseZipFile(String zipPath) {
		try {
			FileOutputStream destination = new FileOutputStream(zipPath);
			OutputStream outputStream = new BufferedOutputStream(destination);
			return new ZipOutputStream(outputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} 

	}
}
