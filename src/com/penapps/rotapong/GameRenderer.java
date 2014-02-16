package com.penapps.rotapong;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

import com.penapps.rotapong.shapes.Camera;
import com.penapps.rotapong.util.Buffers;

public class GameRenderer implements Renderer {

	private static final FloatBuffer LIGHT_POSITION = Buffers.wrap(new float[] {
			0.0f, 0.0f, 1.0f, 0.0f });

	private Context mContext;
	private Camera mCamera;
	private Game game;
	
	public GameRenderer(Context context) {
		mContext = context;
		mCamera = new Camera(false, 0.0f, 0.0f, 0.0f);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
	    
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glLoadIdentity();
		/*
		 * gl.glTranslatef(0.0f, mCamera.y, 0.0f); if (mCamera.dir) { mCamera.y
		 * += 0.03125f; if (mCamera.y == 1.0f) mCamera.dir = false; } else {
		 * mCamera.y -= 0.03125f; if (mCamera.y == -1.0f) mCamera.dir = true; }
		 */

		gl.glPushMatrix();
		gl.glTranslatef(game.otherPaddle.x, game.otherPaddle.y, -10.0f);
		gl.glScalef(0.75f, 0.75f, 0.75f);
		game.otherPaddle.draw(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(game.paddle.x, game.paddle.y, -5.0f);
		game.paddle.draw(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(game.ball.x, game.ball.y, game.ball.z);
		gl.glScalef(0.125f, 0.125f, 0.125f);
		game.ball.draw(gl);
		gl.glPopMatrix();

		moveBall();

	}

	public void moveBall() {
		if (game.ball.zDir) {
			game.ball.z += game.ball.zSpeed;
			if (game.ball.z >= -6f) {
				checkLose();
				game.bounceCount++;
				game.ball.zDir = false;
			}
		} else {
			game.ball.z -= game.ball.zSpeed;
			if (game.ball.z <= -10.0f) {
				game.bounceCount++;
				game.ball.zDir = true;
			}
		}
		if (game.ball.xDir){
			game.ball.x += game.ball.xSpeed;
			if (game.ball.x >= 2f)
				game.ball.xDir = false;
		} else {
			game.ball.x -= game.ball.xSpeed;
			if (game.ball.x <= -2f)
				game.ball.xDir = true;
		}
		if (game.ball.yDir){
			game.ball.y += game.ball.ySpeed;
			if (game.ball.y >= 2f)
				game.ball.yDir = false;
		} else {
			game.ball.y -= game.ball.ySpeed;
			if (game.ball.y <= -2f)
				game.ball.yDir = true;
		}
		
		if (game.bounceCount % 5 == 0 && game.bounceCount != 0){
			game.ball.xSpeed += (float) Math.pow(2, ((int)(Math.random() * ((8 - 5) + 1) + 5)) * -1);
			game.ball.ySpeed += (float) Math.pow(2, ((int)(Math.random() * ((8 - 5) + 1) + 5)) * -1);
			Log.i("TAG", game.ball.xSpeed + " " + game.ball.ySpeed);
		}
		
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

		game = new Game(gl, mContext);
	}

	public void checkLose() {
		if (game.ball.zDir) {
			// broadcast ball direction to other device
		}
		return;
	}

	public void updatePaddleZ(int newZ) {
		game.paddle.x = ((int) (newZ / 7)) * 0.1f;
	}

	public void updatePaddleY(int newY) {
		game.paddle.y = (float) ((newY / 7) * 0.1f);
	}

	public void updateOtherPaddle(float newZ, float newY) {
		game.otherPaddle.x = newZ;
		game.otherPaddle.y = newY;
	}

}
