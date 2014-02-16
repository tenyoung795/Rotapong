package com.penapps.rotapong;

import java.net.InetAddress;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;
import android.widget.Toast;

import com.penapps.rotapong.util.Buffers;

public class GameRenderer implements Renderer {

	private static final FloatBuffer LIGHT_POSITION = Buffers.wrap(new float[] {
			0.0f, 0.0f, 1.0f, 0.0f });

	private Context mContext;
	private Game mGame;
	
	public GameRenderer(Context context, Game game) {
		mContext = context;
		mGame = game;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		mGame.step();
	    
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		gl.glPushMatrix();
		gl.glTranslatef(mGame.otherPaddle.x, mGame.otherPaddle.y, -10.0f);
		gl.glScalef(0.75f, 0.75f, 0.75f);
		mGame.otherPaddle.draw(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(mGame.paddle.x, mGame.paddle.y, -5.0f);
		gl.glScalef(0.75f, 0.75f, 0.75f);
		mGame.paddle.draw(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(mGame.ball.x, mGame.ball.y, mGame.ball.z);
		gl.glScalef(0.125f, 0.125f, 0.125f);
		mGame.ball.draw(gl);
		gl.glPopMatrix();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// gl.glEnable(GL10.GL_LIGHTING);
		gl.glViewport(0, 0, width, height); // Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); // Select The Projection Matrix
		gl.glLoadIdentity(); // Reset The Projection Matrix

		// Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,
				100.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); // Select The Modelview Matrix
		gl.glLoadIdentity(); // Reset The Modelview Matrix
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, LIGHT_POSITION);
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0); // Enable Light 0 ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		mGame.createResources(gl, mContext);
	}

}
