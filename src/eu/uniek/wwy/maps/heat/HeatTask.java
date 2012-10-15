package eu.uniek.wwy.maps.heat;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Projection;

public class HeatTask implements Runnable{
	private Canvas myCanvas;
	private Bitmap backbuffer;
	private int width;
	private int height;
	private float radius;
	private List<HeatPoint> points;
	private HeatMap heatMap;

	public HeatTask(int width, int height, float radius, List<HeatPoint> points, HeatMap heatMap){
		this.heatMap = heatMap;
		backbuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		myCanvas = new Canvas(backbuffer);
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.TRANSPARENT);
		this.width = width;
		this.height = height;
		this.points = points;
		myCanvas.drawRect(0, 0, width, height, paint);
		this.radius = radius;
	}

	public void run() {
		Projection projection = heatMap.getMapView().getProjection();
		Point outPoint = new Point(1, 1);
		for(HeatPoint p : points){
			GeoPoint in = new GeoPoint((int)(p.getLatitude()*1E6),(int)(p.getLongitude()*1E6));
			projection.toPixels(in, outPoint);
			addPoint(outPoint.x, outPoint.y, p.getIntensity());
		}
		colorize(0, 0);
		heatMap.getLock().lock();
		heatMap.setLayer(backbuffer);
		heatMap.getLock().unlock();
		heatMap.getMapView().postInvalidate();
	}

	private void addPoint(float x, float y, int times) {
		RadialGradient g = new RadialGradient(x, y, radius, Color.argb(
				Math.max(1 * times, 150), 0, 0, 0), Color.TRANSPARENT,
				TileMode.CLAMP);
		Paint gradientPaint = new Paint();
		gradientPaint.setShader(g);
		myCanvas.drawCircle(x, y, radius, gradientPaint);
	}

	private void colorize(float x, float y) {
		int[] pixels = new int[(int) (this.width * this.height)];
		backbuffer.getPixels(pixels, 0, this.width, 0, 0, this.width, this.height);

		for (int i = 0; i < pixels.length; i++) {
			int red = 0, green = 0, blue = 0, temp = 0;
			int alpha = pixels[i] >>> 24;
			if (alpha == 0) {
				continue;
			}
			if (alpha <= 255 && alpha >= 235) {
				temp = 255 - alpha;
				red = 255 - temp;
				green = temp * 12;
			} else if (alpha <= 234 && alpha >= 200) {
				temp = 234 - alpha;
				red = 255 - (temp * 8);
				green = 255;
			} else if (alpha <= 199 && alpha >= 150) {
				temp = 199 - alpha;
				green = 255;
				blue = temp * 5;
			} else if (alpha <= 149 && alpha >= 100) {
				temp = 149 - alpha;
				green = 255 - (temp * 5);
				blue = 255;
			} else
				blue = 255;
			pixels[i] = Color.argb((int) alpha / 2, red, green, blue);
		}
		backbuffer.setPixels(pixels, 0, this.width, 0, 0, this.width, this.height);
	}
}
