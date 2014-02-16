package com.penapps.rotapong.shapes;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.penapps.rotapong.util.Buffers;

/*
 * As of now, a ball is just a scaled sprite.
 */
public class Paddle implements Shape {
	
	private int[] mTexturePointer;
	public float x, y, z;
	public boolean dir;
	
	private static final FloatBuffer VERTICES = Buffers.wrap(new float[] {
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f,
        -0.5f, 0.5f, 0.0f,
        0.5f, 0.5f, 0.0f
	});
	
	private static final FloatBuffer TEXTURE = Buffers.wrap(new float[]{
		0.0f, 0.0f, 
		0.0f, 1.0f, 
		1.0f, 0.0f, 
		1.0f, 1.0f
	});
	
	public Paddle(GL10 gl, Context context, boolean dir, float x, float y, float z)
	{	
		this.x = x;
		this.y = y;
		this.z = z;
		this.dir = dir;

		mTexturePointer = new int[1];
		gl.glGenTextures(1, mTexturePointer, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexturePointer[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
		//GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		
		int error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR)
		{
			throw new RuntimeException("GL Error " + error);
		}
		
	}

	@Override
	public void draw(GL10 gl) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexturePointer[0]);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glFrontFace(GL10.GL_CCW);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, VERTICES);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, TEXTURE);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		//gl.glDrawElements(GL10.GL_TRIANGLES, INDICES.capacity(), GL10.GL_UNSIGNED_BYTE, INDICES);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

}
