package eu.uniek.wwy.walkwithyouonderzoek;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;
import eu.uniek.wwy.R;
import eu.uniek.wwy.database.BreadcrumbsDAO;
import eu.uniek.wwy.database.DataWrapper;
import eu.uniek.wwy.location.GPSLocationListener;
import eu.uniek.wwy.maps.heat.HeatMapActivity;
import eu.uniek.wwy.utils.ToastUtil;

public class WalkWithYouOnderzoek extends Activity {

	private Handler updateHandler;
	private Runnable updateRunnable;
	private LocationManager locationManager;
	private GPSLocationListener gpsLocationListener = new GPSLocationListener();
	private BreadcrumbsDAO dao = new BreadcrumbsDAO();
	private Context context = this;
	private DataWrapper dataWrapper = new DataWrapper();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_walk_with_you_onderzoek);
		if(!sdWritable()) {
			ToastUtil.showToast(this, "sdcard is not writable");
		}
		checkEmailIsSet();
	
		try {
			File file = new File(getFile());
			if(file.exists()) {
				dataWrapper = dao.getData(getFile());
			} else {
				dataWrapper = new DataWrapper();
			}
		} catch (Exception e) {
			ToastUtil.showToast(context, e.getMessage());
		}
		ToggleButton button = (ToggleButton) findViewById(R.id.AanUitKnop);
		updateHandler = new Handler();
		updateRunnable = new Runnable () {
			public void run() {
				if(gpsLocationListener.getCurrentLocation() != null) {
					try {
						dataWrapper.getBreadcrumbs().add(gpsLocationListener.getCurrentLocation());
						dao.saveData(dataWrapper, getFile());
					} catch (Exception e) {
						ToastUtil.showToast(context, e.getMessage());
					}
				}
				updateHandler.postDelayed(this, 500);
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
		final Context c = this;
		Button mapButton = (Button) findViewById(R.id.map);
		mapButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(c, HeatMapActivity.class);
				startActivity(i);
				
			}
		});
		
		Button herkingButton = (Button) findViewById(R.id.herkenningspunt_button);
		herkingButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					dataWrapper.getPointsOfInterest().add(gpsLocationListener.getCurrentLocation());
					dao.saveData(dataWrapper, getFile());
				} catch (Exception e) {
					ToastUtil.showToast(v.getContext(), e.getMessage());
				}
			}
		});
	}

	private void checkEmailIsSet() {
		SharedPreferences settings = getSharedPreferences(AskEmail.PREFS_NAME, 0);
		String email = settings.getString("email", null);
		if(email == null || email.equals("")) {
			Intent i = new Intent(this, AskEmail.class);
			startActivity(i);
		}
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
		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_FINE);
		String provider =locationManager.getBestProvider(c, true);
		ToastUtil.showToast(this, provider);
		locationManager.requestLocationUpdates(provider, 500, 1, gpsLocationListener);
	}
	private boolean sdWritable() {
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    mExternalStorageWriteable = false;
		} else {
		    mExternalStorageWriteable = false;
		}
		return mExternalStorageWriteable;
	}
	public String getFile() {
		File root = getExternalFilesDir(null);
		File wwydaba = new File(root + "/wwydaba.obj");
		return "" + wwydaba;
	}
	
}
