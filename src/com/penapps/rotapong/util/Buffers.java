package com.penapps.rotapong.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public final class Buffers {
	
	private Buffers() {
	}
	
	public static FloatBuffer wrap(float[] floats)
	{
		FloatBuffer floatBuffer = ByteBuffer
			.allocateDirect(floats.length * 4)
			.order(ByteOrder.nativeOrder())
			.asFloatBuffer();
		floatBuffer.put(floats);
		floatBuffer.position(0);
		return floatBuffer;
	}
	
	public static ByteBuffer wrap(byte[] bytes)
	{
		return ByteBuffer.wrap(bytes).order(ByteOrder.nativeOrder());
	}

}
