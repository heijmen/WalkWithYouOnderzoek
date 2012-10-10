package com.example.walkwithyouonderzoek;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;
import eu.uniek.wwy.database.BreadcrumbsDAO;
import eu.uniek.wwy.location.GPSLocation;
import eu.uniek.wwy.location.GPSLocationListener;
import eu.uniek.wwy.utils.ToastUtil;

public class WalkWithYouOnderzoek extends Activity {

	private Handler updateHandler;
	private Runnable updateRunnable;
	private LocationManager locationManager;
	private GPSLocationListener gpsLocationListener = new GPSLocationListener();
	private BreadcrumbsDAO dao = new BreadcrumbsDAO();
	private Context context = this;
	private List<GPSLocation> breadcrumbs = new ArrayList<GPSLocation>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_walk_with_you_onderzoek);
		try {
			breadcrumbs = dao.getBreadcrumbs(getFile());
		} catch (Exception e) {
			ToastUtil.showToast(context, e.getMessage());
		}
		ToggleButton button = (ToggleButton) findViewById(R.id.AanUitKnop);
		updateHandler = new Handler();
		updateRunnable = new Runnable () {
			public void run() {
				if(gpsLocationListener.getCurrentLocation() != null) {
					try {
						breadcrumbs.add(gpsLocationListener.getCurrentLocation());
						dao.saveBreadcrumbs(breadcrumbs, getFile());
					} catch (Exception e) {
						Log.e("sex", e.getMessage(), e.getCause());
						e.printStackTrace();
					}
				}
				updateHandler.postDelayed(this, 5000);
			}
		};
		button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					on();
				} else {
					stop();
				}
			}
		});
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		

		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void stop() {
		updateHandler.removeCallbacks(updateRunnable);
		locationManager.removeUpdates(gpsLocationListener);
	}

	public void on() {
		updateRunnable.run();
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, gpsLocationListener);
	}
	private boolean sdWritable() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		return mExternalStorageWriteable;
	}
	public String getFile() {
		File root = getExternalFilesDir(null);
		File wwydaba = new File(root + "/wwydaba.obj");
		return "" + wwydaba;
	}
	public void thisworks() {
		File root = getExternalFilesDir(null);
		File testfile = new File(root + "/testwwy.txt");
		try {
			FileOutputStream fos = new FileOutputStream(testfile);
			fos.write("blabla".getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
