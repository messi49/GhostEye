package com.netim;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.os.Handler;
import android.view.View;


/**
 * 繧ｫ繝｡繝ｩ縺ｮ繝薙Η繝ｼ縺ｫ縺九�縺帙ｋ繧ｵ繝ｼ繝輔ぉ繧､繧ｹ繝薙Η繝ｼ
 *
 */
public class OverlayView extends View {
	private float x = -1;
	private float y = -1;

	/*
	 * 蠎ｧ讓�蟇ｾ隗偵�)
	 * (x1, y1) , (x2, y2)
	 */

	private Bitmap bitmap;
	Paint paint = new Paint();

	Handler  mHandler   = new Handler();

	public OverlayView(Context context) {
		super(context);
//		bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
		setFocusable(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int count = 0;

		if(CameraView.num == 0){
			canvas.drawColor(0,Mode.CLEAR);

		}
		else if (CameraView.num != 0) {
			canvas.drawColor(Color.TRANSPARENT);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLUE);
			paint.setStrokeWidth(10);

			//Log.v("逕ｻ髱｢繧ｵ繧､繧ｺ", CameraActivity.x_size + ", " + CameraActivity.y_size + ", " + CameraView.resolution);

			while(count < CameraView.num){
				if(CameraView.resolution == 2){
					switch(CameraView.points[4+count*6]){
					//Unknown
					case 0:
						paint.setColor(Color.MAGENTA);
						break;
					//Car
					case 1:
						paint.setColor(Color.BLUE);
						break;
					//sign
					case 2:
						paint.setColor(Color.YELLOW);
						break;
					//human
					case 3:
						paint.setColor(Color.GREEN);
						break;
					}
					canvas.drawRect(CameraActivity.display_x * CameraView.points[0+count*6 ] / CameraActivity.x_size, CameraActivity.display_y * CameraView.points[1+count*6] /480,
							CameraActivity.display_x * CameraView.points[2+count*6 ] / CameraActivity.x_size, CameraActivity.display_y * CameraView.points[3+count*6] / CameraActivity.y_size, paint);
//					Log.v("蠎ｧ讓�, "" + CameraActivity.x_size * CameraView.points[0+count*6 ] / 640 +","+ CameraActivity.y_size * CameraView.points[1+count*6] / 480 +","+
//							CameraActivity.x_size * CameraView.points[2+count*6 ] / 640 +"," + CameraActivity.y_size * CameraView.points[3+count*6] / 480);

				}
				count++;
				//Log.v("----------------------", "" + count);
			}

			//canvas.drawBitmap(bitmap, x, y, null);
		}

	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//
//		x = event.getX();
//		y = event.getY();
//
//		if(CameraActivity.flag == 2){
//			invalidate();
//		}
//		return true;
//	}

	public void drawBox(){
//		invalidate();

		mHandler.post(new Runnable(){

			@Override
			public void run() {
					invalidate();		//to update the display
			}
		});

		try {
		    Thread.sleep(300); //3000繝溘Μ遘担leep縺吶ｋ
		} catch (InterruptedException e) {
		}
	}
}