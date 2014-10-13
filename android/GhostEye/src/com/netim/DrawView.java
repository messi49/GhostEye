package com.netim;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class DrawView extends SurfaceView implements SurfaceHolder.Callback
{
	private SurfaceHolder holder;
//	Handler  mHandler   = new Handler();

	private	Bitmap	_bmpImageFile;		//image file

	public DrawView(Context context)
	{
		super(context);
		holder = getHolder();
		holder.addCallback(this);

		//load image file
		LoadImageFile("/mnt/sdcard/tmp.jpg",false);
		
	}

	private boolean LoadImageFile(String strFile,final boolean bInvalidate) {
		//load image
		_bmpImageFile = BitmapFactory.decodeFile(strFile);

		if(_bmpImageFile == null)
			return false;

		return	(_bmpImageFile != null) ? true : false;
	}

	public void surfaceDestroyed(SurfaceHolder holder)
	{
		holder.removeCallback(this);
		holder=null;
	}

	public void drawBitmap()
	{
		Canvas canvas = holder.lockCanvas();
		if(LoadImageFile("/mnt/sdcard/tmp.jpg",true) == false){
			holder.unlockCanvasAndPost(canvas);
		}
		else{
			//preview image
			_bmpImageFile = Bitmap.createScaledBitmap(_bmpImageFile, CameraActivity.display_x, CameraActivity.display_y , true);
			canvas.drawBitmap(_bmpImageFile, 0, 0, null);
			
			// preview rect
			CameraActivity.overlayview.drawBox();
			holder.unlockCanvasAndPost(canvas);
		}
	}


	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
	public void surfaceCreated(SurfaceHolder holder) {}
}