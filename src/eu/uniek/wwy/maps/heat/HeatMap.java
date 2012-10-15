package eu.uniek.wwy.maps.heat;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import eu.uniek.wwy.maps.KMLMetaData;

public class HeatMap extends Overlay {
	private Bitmap layer;
	public Bitmap getLayer() {
		return layer;
	}

	private float radius;
	private MapView mapView;
	private ReentrantLock lock;
	private Point point;

	ReentrantLock getLock() {
		return lock;
	}

	public HeatMap(float radius, MapView mapview){
		this.radius = radius;
		this.mapView = mapview;
		this.lock = new ReentrantLock();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		if(layer != null){
			canvas.drawBitmap(layer, 0,0,null);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {	
		if(e.getAction() == MotionEvent.ACTION_DOWN){
			lock.lock();
			this.layer = null;	
			lock.unlock();
		}
		return super.onTouchEvent(e, mapView);
	}

	void update(List<HeatPoint> points){
		float pxRadius = (float) (mapView.getProjection().metersToEquatorPixels(radius) * 1/Math.cos(Math.toRadians(mapView.getMapCenter().getLatitudeE6()/1E6)));
		HeatTask task = new HeatTask(mapView.getWidth(), mapView.getHeight(), pxRadius, points,this);
		new Thread(task).start();
	}

	public MapView getMapView() {
		return mapView;
	}

	void setLayer(Bitmap backbuffer) {
		this.layer = backbuffer;
	}

	public Bitmap getWholeLayer(KMLMetaData layerValues, List<HeatPoint> points) {
		int south = (int) (((layerValues.getNorth() - layerValues.getSouth()) / 2 + layerValues.getSouth()) * 1000000);
		int west = (int) (((layerValues.getEast() - layerValues.getWest()) / 2 + layerValues.getWest())* 1000000);
		GeoPoint geoPoint = new GeoPoint(south, west);
		MapController mapController = mapView.getController();
		mapController.setCenter(geoPoint);
		mapController.setZoom(13);
		update(points);
		setProjection(points);
		return getLayer();
	}

	private void setProjection(List<HeatPoint> points) {
		GeoPoint topLeft = mapView.getProjection().fromPixels(mapView.getLeft(), mapView.getTop());
		GeoPoint bottomRight = mapView.getProjection().fromPixels(mapView.getRight(), mapView.getBottom());
		KMLMetaData kmlMetaData = new KMLMetaData();
		kmlMetaData.setBoundaries(topLeft.getLatitudeE6(), bottomRight.getLatitudeE6(), bottomRight.getLongitudeE6(), topLeft.getLongitudeE6());
	}

	public Point getPoint() {
		return point;
	}
}