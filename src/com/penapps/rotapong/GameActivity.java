package com.penapps.rotapong;

import java.net.InetAddress;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

public class GameActivity extends Activity implements SensorEventListener{

	public static final String TAG = GameActivity.class.getSimpleName();
	public static final String SERVER = "com.penapps.rotapong.SERVER";
	public static final String IS_SERVER = "com.penapps.rotapong.IS_SERVER";

	private SensorManager mSensorManager;
	private Sensor mRotationVector;

	private float[] mOrientation = new float[3];
	private float[] mTempRotation = new float[3 * 3], mRotation = new float[3 * 3];
	
	private Button calibrate;
	private boolean calibrated = true;
	private float calibrationZ = 0.0f;
	
	private GLSurfaceView view;
	private Game mGame;
	private GameRenderer renderer;
	
	private int prevZ, prevY = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = new GLSurfaceView(this);
		//view.setEGLContextClientVersion(2);
		view.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		view.getHolder().setFormat(PixelFormat.RGBA_8888);
		
		calibrate = new Button(this);
		calibrate.setText("Calibrate me!");
		
		Intent intent = getIntent();
		InetAddress address = (InetAddress)intent.getSerializableExtra(SERVER);
		if (address == null)
			mGame = new Game();
		else
		{
			boolean isServer = intent.getBooleanExtra(IS_SERVER, false);
			mGame = new Game(address, isServer);
		}
		renderer = new GameRenderer(this, mGame);
		view.setRenderer(renderer);
		
		setContentView(view);
		
		addContentView(calibrate, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		
		calibrate.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				calibrated = false;
			}
		});
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	protected void onResume() {
        super.onResume();
        view.onResume();
        mSensorManager.registerListener(this, mRotationVector, SensorManager.SENSOR_DELAY_GAME);
    }
	
	@Override
	protected void onPause() {
		super.onPause();
		view.onPause();
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
		
		SensorManager.getRotationMatrixFromVector(mTempRotation, event.values);
		// Warp coordinate system to ease conversion to game world
		SensorManager.remapCoordinateSystem(mTempRotation, SensorManager.AXIS_Y, SensorManager.AXIS_Z, mRotation);
		SensorManager.getOrientation(mRotation, mOrientation);
		//String msg = "Orientation vector in degrees: z: " + (int)Math.toDegrees(mOrientation[0] - calibrationZ)
		//	+ " x: " + (int)Math.toDegrees(mOrientation[1])
		//	+ " y: " + (int)Math.toDegrees(mOrientation[2]);
		//mTextView.setText(msg);
		//Log.i(TAG, msg);
		
		if (!calibrated){
			calibrationZ = mOrientation[0];
			calibrated = true;
		}
		
		int newZ = (int)Math.toDegrees(mOrientation[0] - calibrationZ);
		int newY = (int)Math.toDegrees((mOrientation[2]));
		
		//Log.d(TAG, newZ + " " + newY);
		
		if (Math.abs(prevZ - newZ) > 0){
			mGame.updatePaddleZ(newZ);
		}
		if (Math.abs(prevY - newY) > 0){
			mGame.updatePaddleY(newY);
		}
	}

}
