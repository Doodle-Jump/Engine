package game.engine;

import android.os.Bundle;
import android.app.Activity;
import java.math.*;

import android.content.SharedPreferences;
import android.content.pm.*;
import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.*;
import android.view.*;
import android.view.View.OnTouchListener;

public abstract class Engine extends Activity implements Runnable, OnTouchListener, SensorEventListener {
	private SurfaceView p_view;
	private Canvas p_canvas;
	private Thread p_thread;
	private boolean p_running,p_paused;
	private Paint p_paintDraw,p_paintFont;
	private Typeface p_typeface;
	private Point[] p_touchPoints;
	private int p_numPoints;
	private long p_preferredFrameRate,p_sleepTime;
	SensorManager sensorManager;
	Sensor sensor;
	public static final int FRAME_NO_DELAY = -1;
	SharedPreferences datas;
	SharedPreferences.Editor editor;
	
	public Engine() {
		Log.d("Engine", "Engine constructor");
		p_view=null;p_canvas=null;p_thread=null;
		p_running=p_paused=false;p_paintDraw=null;p_paintFont=null;
		p_numPoints=0;p_typeface=null;p_preferredFrameRate=40;
		p_sleepTime=1000/p_preferredFrameRate;
	}
	
	public abstract void init();
	public abstract void load();
	public abstract void draw();
	public abstract void update();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("Engine","Engine.onCreate start");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setScreenOrientation(ScreenModes.PORTRAIT);
		sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
		sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		//datas=getSharedPreferences("game.data",1);
		datas=getPreferences(0);
		editor=datas.edit();
		
		p_view=new SurfaceView(this);
		setContentView(p_view);
		p_view.setOnTouchListener(this);
		
		p_touchPoints=new Point[5];
		for (int n=0;n<5;n++) {
			p_touchPoints[n]=new Point(0,0);
		}

		/**
		 * call abstract init method in sub-class!
		 */
		init();
		
		p_paintDraw=new Paint();
		p_paintDraw.setColor(Color.WHITE);
		
		p_paintFont=new Paint();
		p_paintFont.setColor(Color.BLACK);
		p_paintFont.setTextSize(24);
		
		/**
		 * Call abstract load method in sub-class!
		 */
		load();
		
		p_running=true;
		p_thread=new Thread(this);
		p_thread.start();
		
		Log.d("Engine", "Engine.onCreate end");
	}
	
	public void onResume() {
		Log.d("Engine", "Engine.onResume");
		sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		super.onResume();
		p_paused=false;
		/*
		p_running=true;
		p_thread=new Thread(this);
		p_thread.start();
		*/
	}

	public void onPause() {
		Log.d("Engine", "Engine.onPause");
		sensorManager.unregisterListener(this);
		super.onPause();
		p_paused=true;
		/*
		p_running=false;
		while (true) {
			try {
				p_thread.join();
			} catch (InterruptedException e) {}
		}
		*/
	}
	
	public void onSensorChanged(SensorEvent sensorEvent) {
		
	}
	
	public void onAccuracyChanged(Sensor arg0,int arg1) {
		
	}
	
	public boolean onTouch(View v,MotionEvent event) {
		p_numPoints=event.getPointerCount();
		if (p_numPoints>5) p_numPoints=5;
		
		for (int n=0;n<p_numPoints;n++) {
			p_touchPoints[n].x=(int)event.getX(n);
			p_touchPoints[n].y=(int)event.getY(n);
		}
		return true;
	}
	
	
	
	public void run() {
		Log.d("Engine", "Engine.run start");
		
		Timer frameTimer=new Timer();
		long startTime=0,timdDiff=0;
		
		while (p_running) {
			if (p_paused) continue;
			
			/**
			 * Calculate frame rate
			 */
			startTime=frameTimer.getElapsed();
			
			/**
			 * Rendering section, lock the canvas
			 * Only proceed if the SurfaceView is valid
			 */
			if (beginDrawing()) {
				//p_canvas.drawColor(Color.BLUE);
				
				/**
				 * Call abstract draw method in sub-class!
				 */
				draw();
				
				/**
				 * Complete the rendering process by unlocking the canvas
				 */
				endDrawing();
			}

			/**
			 * Call abstract update method in sub-class!
			 */
			update();
			
			/**
			 * Calculate frame update time and sleep if necessary
			 */
			if (p_sleepTime!=-1)
			{
				timdDiff=frameTimer.getElapsed()-startTime;
				long updatePeriod=p_sleepTime-timdDiff;
				if (updatePeriod>0) {
					try {
						Thread.sleep(updatePeriod);
					} catch (InterruptedException e) {}
				}
			}
		}
		Log.d("Engine", "Engine.run end");
		System.exit(RESULT_OK);
	}
	
	public void fatalError(String msg) {
		Log.d("FATAL EOORO",msg);
		System.exit(0);
	}
	
	/**
	 * BEGIN RENDERING
	 * Verify that the surface is valid, and then lock the canvas
	 * @return true if locked
	 */
	public boolean beginDrawing() {
		if (!p_view.getHolder().getSurface().isValid()) {
			return false;
		}
		p_canvas=p_view.getHolder().lockCanvas();
		return true;
	}
	
	/**
	 * END RENDERING
	 * Unlock the canvas
	 */
	public void endDrawing() {
		p_view.getHolder().unlockCanvasAndPost(p_canvas);
	}
	
	public void drawText(String text,int x,int y) {
		p_canvas.drawText(text, x, y, p_paintFont);
	}
	public SurfaceView getView() {
		return p_view;
	}
	public Canvas getCavas() {
		return p_canvas;
	}
	public void setFrameRate(int rate) {
		if (rate==FRAME_NO_DELAY) {
			p_sleepTime=-1;
		}
		p_preferredFrameRate=rate;
		p_sleepTime=1000/p_preferredFrameRate;
	}
	public int getTouchInputs() {
		return p_numPoints;
	}
	public Point getTouchPoint(int index) {
		if (index>p_numPoints) index=p_numPoints;
		if (index<0) index=0;
		return p_touchPoints[index];
	}
	public void setDrawColor(int color) {
		p_paintDraw.setColor(color);
	}
	public void setTextColor(int color) {
		p_paintFont.setColor(color);
	}
	public void setTextSize(int size) {
		p_paintFont.setTextSize((float)size);
	}
	public void setTextSize(float size) {
		p_paintFont.setTextSize(size);
	}
	
	public enum FontStyles {
		NORMAL (Typeface.NORMAL), BOLD (Typeface.BOLD), ITALIC (Typeface.ITALIC),
		BOLD_ITALIC (Typeface.BOLD_ITALIC);
		int value;
		FontStyles (int type) {
			this.value=type;
		}
	}
	public void setTextStyle(FontStyles styles) {
		p_typeface=Typeface.create(Typeface.DEFAULT, styles.value);
		p_paintFont.setTypeface(p_typeface);
	}
	
	public enum ScreenModes {
		LANDSCAPE (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE),
		PORTRAIT (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		int value;
		ScreenModes(int mode) {
			this.value=mode;
		}
	}
	public void setScreenOrientation(ScreenModes mode) {
		setRequestedOrientation(mode.value);
	}
	
	/**
	 * Round to any precision
	 * @param value
	 * @param precision
	 * @return answer, 0 if error
	 */
	public double round(double value, int precision) {
		try {
			return new BigDecimal(value).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
		} catch (Exception e) {
			Log.e("Engine","round: error rounding number");
		}
		return 0;
	}
	
	public int getData(String dataname, int defaultValue) {
		return datas.getInt(dataname, defaultValue);
	}
	
	public void saveData(String dataname,int value) {
		editor.putInt(dataname, value);
	}
	
	public void saveCommitted() {
		editor.commit();
	}
}
