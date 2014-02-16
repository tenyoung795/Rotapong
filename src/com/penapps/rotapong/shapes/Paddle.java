package com.penapps.rotapong.shapes;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.penapps.rotapong.util.Buffers;

public class Paddle implements Shape {
	public float x, y ,z;
	public boolean dir;
	
	private static final FloatBuffer VERTICES = Buffers.wrap(new float[] {
        -1.0f, -1.0f, 0.25f,
        1.0f, -1.0f, 0.25f,
        -1.0f, 1.0f, 0.25f,
        1.0f, 1.0f, 0.25f,

        1.0f, -1.0f, 0.25f,
        1.0f, -1.0f, -0.25f,
        1.0f, 1.0f, 0.25f,
        1.0f, 1.0f, -0.25f,

        1.0f, -1.0f, -0.25f,
        -1.0f, -1.0f, -0.25f,
        1.0f, 1.0f, -0.25f,
        -1.0f, 1.0f, -0.25f,

        -1.0f, -1.0f, -0.25f,
        -1.0f, -1.0f, 0.25f,
        -1.0f, 1.0f, -0.25f,
        -1.0f, 1.0f, 0.25f,

        -1.0f, -1.0f, -0.25f,
        1.0f, -1.0f, -0.25f,
        -1.0f, -1.0f, 0.25f,
        1.0f, -1.0f, 0.25f,

        -1.0f, 1.0f, 0.25f,
        1.0f, 1.0f, 0.25f,
        -1.0f, 1.0f, -0.25f,
        1.0f, 1.0f, -0.25f,
    });

	private static final FloatBuffer NORMAL = Buffers.wrap(new float[] {
        0.0f, 0.0f, 1.0f, 						
        0.0f, 0.0f, -1.0f, 
        0.0f, 1.0f, 0.0f, 
        0.0f, -1.0f, 0.0f, 

        0.0f, 0.0f, 1.0f, 
        0.0f, 0.0f, -1.0f, 
        0.0f, 1.0f, 0.0f, 
        0.0f, -1.0f, 0.0f,

        0.0f, 0.0f, 1.0f, 
        0.0f, 0.0f, -1.0f, 
        0.0f, 1.0f, 0.0f, 
        0.0f, -1.0f, 0.0f,

        0.0f, 0.0f, 1.0f, 
        0.0f, 0.0f, -1.0f, 
        0.0f, 1.0f, 0.0f, 
        0.0f, -1.0f, 0.0f,

        0.0f, 0.0f, 1.0f, 
        0.0f, 0.0f, -1.0f, 
        0.0f, 1.0f, 0.0f, 
        0.0f, -1.0f, 0.0f,

        0.0f, 0.0f, 1.0f, 
        0.0f, 0.0f, -1.0f, 
        0.0f, 1.0f, 0.0f, 
        0.0f, -1.0f, 0.0f,
    });

	private static final ByteBuffer INDICES = Buffers.wrap(new byte[] {
        0, 1, 3, 0, 3, 2,
        4, 5, 7, 4, 7, 6,
        8, 9, 11, 8, 11, 10,
        12, 13, 15, 12, 15, 14, 
        16, 17, 19, 16, 19, 18, 
        20, 21, 23, 20, 23, 22, 
    });
	
	public Paddle(boolean dir, float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = y;
		this.dir = dir;
	}

	@Override
	public void draw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

		gl.glFrontFace(GL10.GL_CCW);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, VERTICES);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, NORMAL);
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
		gl.glDrawElements(GL10.GL_TRIANGLES, INDICES.capacity(), GL10.GL_UNSIGNED_BYTE, INDICES);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
	}

}
