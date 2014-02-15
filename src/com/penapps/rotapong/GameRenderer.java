package com.penapps.rotapong;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import com.penapps.rotapong.shapes.Ball;
import com.penapps.rotapong.shapes.OtherPaddle;
import com.penapps.rotapong.util.Buffers;

public class GameRenderer implements Renderer {
	
	private static final FloatBuffer LIGHT_POSITION = Buffers.wrap(new float[] {
		0.0f, 0.0f, 1.0f, 0.0f
	});
	
	private Context mContext;
	private Ball mBall;
	private OtherPaddle mOtherPaddle;
	
	private float mBallZ;
	private boolean mBallDir;

	private float mOtherPaddleX;
	private boolean mOtherPaddleDir;
	
	private float mCameraY;
	private boolean mCameraDir;

	public GameRenderer(Context context)
	{
		mContext = context;
		mCameraY = 0.0f;
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
		
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, mCameraY, 0.0f);
		if (mCameraDir)
		{
			mCameraY += 0.03125f;
			if (mCameraY == 1.0f)
				mCameraDir = false;
		}
		else
		{
			mCameraY -= 0.03125f;
			if (mCameraY == -1.0f)
				mCameraDir = true;
		}
		
		gl.glPushMatrix();
		gl.glTranslatef(mOtherPaddleX, 0.0f, -5.0f);
		gl.glScalef(0.75f, 0.75f, 0.75f);
		mOtherPaddle.draw(gl);
		gl.glPopMatrix();
		if (mOtherPaddleDir)
		{
			mOtherPaddleX += 0.0625f;
			
			if (mOtherPaddleX == 1.0f)
				mOtherPaddleDir = false;
		}
		else
		{
			mOtherPaddleX -= 0.0625f;
			if (mOtherPaddleX == -1.0f)
				mOtherPaddleDir = true;
		}
		
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, mBallZ);
		gl.glScalef(0.125f, 0.125f, 0.125f);
		mBall.draw(gl);
		gl.glPopMatrix();
		if (mBallDir)
		{
			mBallZ += 0.0625f;
			if (mBallZ == -2.0f)
				mBallDir = false;
		}
		else
		{
			mBallZ -= 0.0625f;
			if (mBallZ == -4.0f)
				mBallDir = true;
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		//gl.glEnable(GL10.GL_LIGHTING);
		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		//Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, LIGHT_POSITION);
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);											//Enable Light 0 ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
		
        mBall = new Ball(gl, mContext);
        mBallZ = -3.0f;
        mBallDir = true;
        
        mOtherPaddle = new OtherPaddle();
        mOtherPaddleX = 0.0f;
        mOtherPaddleDir = true;
	}

}
