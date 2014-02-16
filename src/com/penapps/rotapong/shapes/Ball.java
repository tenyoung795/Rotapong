package com.penapps.rotapong.shapes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.penapps.rotapong.R;
import com.penapps.rotapong.util.Buffers;

/*
 * As of now, a ball is just a scaled sprite.
 */
public class Ball implements Shape {
	
	private int[] mTexturePointer;
	private int[] mCropWorkspace;
	public float x, y, z;
	public boolean zDir;
	
	private static final FloatBuffer VERTICES = Buffers.wrap(new float[] {
        -1.0f, -1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
        -1.0f, 1.0f, 0.0f,
        1.0f, 1.0f, 0.0f
	});
	
	private static final FloatBuffer TEXTURE = Buffers.wrap(new float[]{
		0.0f, 0.0f, 
		0.0f, 1.0f, 
		1.0f, 0.0f, 
		1.0f, 1.0f
	});
	
	public Ball(GL10 gl, Context context, boolean zDir, float x, float y, float z)
	{	
		this.x = x;
		this.y = y;
		this.z = z;
		this.zDir = zDir;
		InputStream is = context.getResources().openRawResource(R.drawable.ball);
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		mTexturePointer = new int[1];
		gl.glGenTextures(1, mTexturePointer, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexturePointer[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		
		mCropWorkspace = new int[4];
		mCropWorkspace[0] = 0;
		mCropWorkspace[1] = bitmap.getHeight();
		mCropWorkspace[2] = bitmap.getWidth();
		mCropWorkspace[3] = -bitmap.getHeight();
		
		bitmap.recycle();

		((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0);	
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
