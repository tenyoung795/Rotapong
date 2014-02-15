package com.penapps.rotapong;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

public class GameActivity extends Activity implements SensorEventListener{

	private static final String TAG = "TestRotationActivity";

	private SensorManager mSensorManager;
	private Sensor mAccelerometer, mMagneticField;
	// float[3]
	private float[] mGravity, mGeomagnetic, mOrientation;
	
	private static final int ROTATION_SIZE = 9;
	private float[] mTempRotation, mRotation;
	
	private TextView mTextView;
	private Button calibrate;
	private boolean calibrated = true;
	private float calibrationZ = 0.0f;
	
	private GLSurfaceView view;
	private GameRenderer renderer;
	
	private int prevZ, prevY = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = new GLSurfaceView(this);
		renderer = new GameRenderer(this);
		view.setRenderer(renderer);
		setContentView(view);
		
		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		
		mGravity = new float[3];
		mGeomagnetic = new float[3];
		// To mark the arrays as uninitialized
		mGravity[0] = mGeomagnetic[0] = Float.NaN;
		mOrientation = new float[3];
		
		mTempRotation = new float[ROTATION_SIZE];
		mRotation = new float[ROTATION_SIZE];
		
	}

	@Override
	protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_GAME);
    }
	
	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_rotation, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM){
			Log.d(TAG, "Inaccurate");
			return;
		}
		
		float[] array = null;
		switch (event.sensor.getType())
		{
		case Sensor.TYPE_ACCELEROMETER:
			array = mGravity;
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			array = mGeomagnetic;
			break;
        default:
        	return;
		}
		Log.i(TAG, event.values[0] + " " + event.values[1] + " " + event.values[2]);
		System.arraycopy(event.values, 0, array, 0, 3);
		if (!(Float.isNaN(mGravity[0]) || Float.isNaN(mGeomagnetic[0]))
			&& SensorManager.getRotationMatrix(mTempRotation, null, mGravity, mGeomagnetic))
        {	
			// Warp coordinate system to ease conversion to game world
			SensorManager.remapCoordinateSystem(mTempRotation, SensorManager.AXIS_Y, SensorManager.AXIS_Z, mRotation);
			SensorManager.getOrientation(mRotation, mOrientation);
			String msg = "Orientation vector in degrees: z: " + (int)Math.toDegrees(mOrientation[0] - calibrationZ)
				+ " x: " + (int)Math.toDegrees(mOrientation[1])
				+ " y: " + (int)Math.toDegrees(mOrientation[2]);
			//mTextView.setText(msg);
			Log.i(TAG, msg);
			
			if (!calibrated){
				calibrationZ = mOrientation[0];
				calibrated = true;
			}
			
			int newZ = (int)Math.toDegrees(mOrientation[0] - calibrationZ);
			int newY = (int)Math.toDegrees((mOrientation[1]));
			
			if (Math.abs(prevZ - newZ) > 5){
				renderer.updatePaddleZ(newZ);
			}
			if (Math.abs(prevY - newY) > 5){
				renderer.updatePaddleY(newY);
			}
        }
		
		
	}

}
