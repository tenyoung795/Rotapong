package com.penapps.rotapong;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import com.penapps.rotapong.shapes.Ball;
import com.penapps.rotapong.shapes.OtherPaddle;

public class GameRenderer implements Renderer {
	
	private Context mContext;
	private Ball mBall;
	private OtherPaddle mOtherPaddle;
	
	private float mBallZ;
	private boolean mBallDir;

	private float mOtherPaddleX;
	private boolean mOtherPaddleDir;

	public GameRenderer(Context context)
	{
		mContext = context;
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
		
		gl.glLoadIdentity();
		gl.glTranslatef(mOtherPaddleX, 0.0f, -25.0f);
		gl.glScalef(5.0f, 5.0f, 5.0f);
		mOtherPaddle.draw(gl);
		if (mOtherPaddleDir)
		{
			mOtherPaddleX += 0.125f;
			if (mOtherPaddleX == 5.0f)
				mOtherPaddleDir = false;
		}
		else
		{
			mOtherPaddleX -= 0.125f;
			if (mOtherPaddleX == -5.0f)
				mOtherPaddleDir = true;
		}
		
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, mBallZ);
		mBall.draw(gl);
		if (mBallDir)
		{
			mBallZ++;
			if (mBallZ == -10.0f)
				mBallDir = false;
		}
		else
		{
			mBallZ--;
			if (mBallZ == -20.0f)
				mBallDir = true;
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
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
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0.125f, 0.125f, 0.5f, 1.0f);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
		
        mBall = new Ball(gl, mContext);
        mBallZ = -20.0f;
        mBallDir = true;
        
        mOtherPaddle = new OtherPaddle();
        mOtherPaddleX = 0.0f;
        mOtherPaddleDir = true;
	}

}
