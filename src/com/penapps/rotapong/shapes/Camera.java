package com.penapps.rotapong.shapes;

import com.penapps.rotapong.GameRenderer;

public class Camera {
	public float x, y, z;
	public boolean dir;
	
	public Camera(boolean dir, float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
		this.dir = dir;
	}

}
