package eu.uniek.wwy.maps;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import eu.uniek.wwy.maps.listeners.PanChangeListener;
import eu.uniek.wwy.maps.listeners.ZoomChangeListener;

public class BasicMapView extends MapView {
	private int currentZoomLevel = -1;
	private GeoPoint currentCenter;
	private List<ZoomChangeListener> zoomEvents = new ArrayList<ZoomChangeListener>();
	private List<PanChangeListener> panEvents = new ArrayList<PanChangeListener>();

	public BasicMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BasicMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	public BasicMapView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	public int[][] getBounds() {
		GeoPoint center = getMapCenter();
		int latitudeSpan = getLatitudeSpan();
		int longtitudeSpan = getLongitudeSpan();
		int[][] bounds = new int[2][2];

		bounds[0][0] = center.getLatitudeE6() - (latitudeSpan / 2);
		bounds[0][1] = center.getLongitudeE6() - (longtitudeSpan / 2);

		bounds[1][0] = center.getLatitudeE6() + (latitudeSpan / 2);
		bounds[1][1] = center.getLongitudeE6() + (longtitudeSpan / 2);
		return bounds;
	}

	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			GeoPoint centerGeoPoint = this.getMapCenter();
			if (currentCenter == null || 
					(currentCenter.getLatitudeE6() != centerGeoPoint.getLatitudeE6()) ||
					(currentCenter.getLongitudeE6() != centerGeoPoint.getLongitudeE6()) ) {
				firePanEvent(currentCenter, this.getMapCenter());
			}
			currentCenter = this.getMapCenter();
		}
		return super.onTouchEvent(ev);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if(getZoomLevel() != currentZoomLevel){
			fireZoomLevel(currentZoomLevel, getZoomLevel());
			currentZoomLevel = getZoomLevel();
		}
	}

	private void fireZoomLevel(int old, int current){
		for(ZoomChangeListener event : zoomEvents){
			event.onZoom(old, current);
		}
	}

	private void firePanEvent(GeoPoint old, GeoPoint current){
		for(PanChangeListener event : panEvents){
			event.onPan(old, current);
		}
	}

	public void addZoomChangeListener(ZoomChangeListener listener){
		this.zoomEvents.add(listener);
	}

	public void addPanChangeListener(PanChangeListener listener){
		this.panEvents.add(listener);
	}


}