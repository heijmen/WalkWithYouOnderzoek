package eu.uniek.wwy.maps;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import eu.uniek.wwy.maps.heat.HeatMapActivity;
import eu.uniek.wwy.maps.heat.HeatPoint;
import eu.uniek.wwy.utils.ZipUtil;
import eu.uniek.wwy.walkwithyouonderzoek.AskEmail;

public class KMZExport {
	private final String fileName = "walkwithyou.png";
	private final String kmlTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"\r\n<kml xmlns=\"http://www.opengis.net/kml/2.2\" \r\n" + 
			"xmlns:gx=\"http://www.google.com/kml/ext/2.2\" \r\n" + 
			"xmlns:kml=\"http://www.opengis.net/kml/2.2\" \r\n" + 
			"xmlns:atom=\"http://www.w3.org/2005/Atom\">\r\n" + 
			"\r\n<GroundOverlay>\r\n" + 
			"\r\n		<name>Heatmap WalkWithYou</name>\r\n" + 
			"\r\n		<Icon>\r\n" + 
			"\r\n			<href>%s</href>\r\n" + 
			"\r\n			<viewBoundScale>0.75</viewBoundScale>\r\n" + 
			"\r\n		</Icon>\r\n" + 
			"\r\n		<LatLonBox>\r\n" + 
			"\r\n			<north>%s</north>\r\n" + 
			"\r\n			<south>%s</south>\r\n" + 
			"\r\n			<east>%s</east>\r\n" + 
			"\r\n			<west>%s</west>\r\n" + 
			"\r\n		</LatLonBox>\r\n" + 
			"\r\n	</GroundOverlay>\r\n" + 
			"\r\n</kml>\r\n";
	private String zipPath;

	public String exportToKMZ(HeatMapActivity heatmapActivity, List<HeatPoint> points, Bitmap bitmap) {
		KMLMetaData kmlMetaData = generateKMLMetaData(points);
		OutputStream kmlFile = getKMLFile(kmlMetaData);
		OutputStream mapFileOutputStream = getMapFile(bitmap);
		setZipPath();
		ZipOutputStream createZipFile = ZipUtil.createZipFile(zipPath, kmlFile, mapFileOutputStream);
		sendToEmail(heatmapActivity, createZipFile, points);
		return "Send export to emailadress";
	}

	private void setZipPath() {
		zipPath = Environment.getExternalStorageDirectory().getPath()+"/wwy/heatmap.kmz";
	}
	private void sendToEmail(HeatMapActivity h, ZipOutputStream createZipFile, List<HeatPoint> points) {
		Intent i = createMailIntent(h, points);
		startActivity(h, i);
	}
	
	private Intent createMailIntent(HeatMapActivity h, List<HeatPoint> points) {
		SharedPreferences settings = h.getSharedPreferences(AskEmail.PREFS_NAME, 0);
		String email = settings.getString("email", null);
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , email);
		i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
		StringBuffer text = new StringBuffer();
		for (HeatPoint heat : points) {
			text.append("\nLatitude:\n");
			text.append(heat.getLatitude());
			text.append("\nLongitude:\n");
			text.append(heat.getLongitude());
			text.append("\nIntensity:\n");
			text.append(heat.getIntensity());
		}
		i.putExtra(Intent.EXTRA_TEXT   , text.toString()); 
		i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + zipPath));
		return i;
	}

	private void startActivity(HeatMapActivity h, Intent i) {
		try {
			h.startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(h, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}

	private OutputStream getMapFile(Bitmap bitmap) {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
		return outStream;
	}

	private KMLMetaData generateKMLMetaData(List<HeatPoint> points) {
		KMLMetaData kmlMetaData = new KMLMetaData();
		kmlMetaData.setHeatmapFileName(fileName);
		kmlMetaData = calculateExtremesValues(points, kmlMetaData);
		return kmlMetaData;
	}

	private KMLMetaData calculateExtremesValues(List<HeatPoint> points, KMLMetaData kmlMetaData) {
		float northest = -180f, southest = 180f, eastest = -180f, westest = 180f;
		for (HeatPoint heatPoint : points) {
			northest = getNorth(heatPoint, northest);
			southest = getSouth(heatPoint, southest);
			eastest = getEast(heatPoint, eastest);
			westest = getWest(heatPoint, westest);
		}
		kmlMetaData.setBoundaries(northest,southest,eastest,westest);
		return kmlMetaData;
	}

	private float getNorth(HeatPoint heatPoint, float northest) {		
		if (heatPoint.getLatitude() > northest) {
			northest = heatPoint.getLatitude();
		}
		return northest;
	}

	private float getSouth(HeatPoint heatPoint, float southest) {		
		if (heatPoint.getLatitude() < southest) {
			southest = heatPoint.getLatitude();
		}
		return southest;
	}

	private float getEast(HeatPoint heatPoint, float eastest) {		
		if (heatPoint.getLongitude() > eastest) {
			eastest = heatPoint.getLongitude();
		}
		return eastest;
	}

	private float getWest(HeatPoint heatPoint, float westest) {		
		if (heatPoint.getLongitude() < westest) {
			westest = heatPoint.getLongitude();
		}
		return westest;
	}

	private OutputStream getKMLFile(KMLMetaData kmlMetaData) {
		String kmlString = getKMLString(kmlMetaData);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			stream.write(kmlString.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stream;
	}

	private String getKMLString(KMLMetaData kmlMetaData) {
		return String.format(kmlTemplate,
				kmlMetaData.getHeatmapFileName(),
				kmlMetaData.getNorth(),
				kmlMetaData.getSouth(),
				kmlMetaData.getEast(),
				kmlMetaData.getWest());
	}
}
