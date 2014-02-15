package com.penapps.rotapong;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class GameView extends GLSurfaceView {

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setRenderer(new GameRenderer());
	}

}
