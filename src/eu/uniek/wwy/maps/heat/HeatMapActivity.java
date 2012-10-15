package eu.uniek.wwy.maps.heat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;

import eu.uniek.wwy.R;
import eu.uniek.wwy.database.BreadcrumbsDAO;
import eu.uniek.wwy.database.DataWrapper;
import eu.uniek.wwy.location.GPSLocation;
import eu.uniek.wwy.maps.BasicMapView;
import eu.uniek.wwy.maps.KMZExport;
import eu.uniek.wwy.maps.listeners.PanChangeListener;
import eu.uniek.wwy.utils.ToastUtil;

public class HeatMapActivity extends MapActivity {
	private HeatMap overlay;
	private List<HeatPoint> points; //TODO Performance!
	private DataWrapper dataWrapper = new DataWrapper();
	private BreadcrumbsDAO dao = new BreadcrumbsDAO();

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_heat_map); 
		try {
			File file = new File(getFile());
			if(file.exists()) {
				dataWrapper = dao.getData(getFile());
			} else {
				dataWrapper = new DataWrapper();
			}
		} catch (Exception e) {
			ToastUtil.showToast(this, e.getMessage());
		}
		final BasicMapView mapview = (BasicMapView)findViewById(R.id.mapview);
		this.overlay = new HeatMap(2000, mapview);
		points = new ArrayList<HeatPoint>();
		for(GPSLocation location : dataWrapper.getBreadcrumbs()) {
			points.add(new HeatPoint((float)location.getLatitude(), (float)location.getLongitude()));
		}
		
		mapview.getOverlays().add(overlay);
		mapview.addPanChangeListener(new PanChangeListener() {		
			public void onPan(GeoPoint old, GeoPoint current) {
				update();
			}
		});
	}

	protected void update() {
		if(points.size() > 0){
			overlay.update(points);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_map, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) { 
		switch (item.getItemId()) {
		case R.id.exportToKMZ:
			KMZExport kmzExporter = new KMZExport();
			String result = kmzExporter.exportToKMZ(this, points, overlay.getLayer());
			ToastUtil.showToast(getApplicationContext(), result);
			return true;
		default:
			return false;
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	public String getFile() {
		File root = getExternalFilesDir(null);
		File wwydaba = new File(root + "/wwydaba.obj");
		return "" + wwydaba;
	}

}