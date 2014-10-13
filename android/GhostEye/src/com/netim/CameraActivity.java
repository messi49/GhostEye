package com.netim;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class CameraActivity extends Activity {
	public static String address;
	public static int port;
	public static int port_number;
	
	//preview size
	static String resolution;
	public static int x_size, y_size;
	
	//display size
	public static int display_x, display_y;

	public static OverlayView overlayview;

	/*
	 * flag: 0 is only send
	 * 		 1 is synchronous
	 * 		 2 is recieve points of rectangle(asynchronous)
	 */
	public static int flag;

	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Fixed in the horizontal direction of the screen
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// Full screen
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Disable the sleep
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// To hide the title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Layout
		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		// initialization and additional view
		DrawView dView = new DrawView(this);
		CameraView camView = new CameraView(this, dView);
		overlayview = new OverlayView(this);
		// add view
		setContentView(dView, layoutParams);
		addContentView(camView, layoutParams);
		addContentView(overlayview, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		// get parameters
		address = getIntent().getExtras().getString("address");
		port = getIntent().getExtras().getInt("port");
		if(address.length() == 0){
			address = "192.168.2.227";
		}

		if(port == 0){
			port_number = 12345;
		}
		else{
			port_number = port;
		}

		flag = getIntent().getExtras().getInt("p_type");
		resolution = getIntent().getExtras().getString("resolution");
		
		String size[] = resolution.split("x");
		x_size = Integer.parseInt(size[0]);
		y_size = Integer.parseInt(size[1]);
		
	    Log.v("address", address);
	    Log.v("port", "" + port);
	    Log.v("flag", "flag = ;;;;;;;;" + flag);
	    Log.v("resolution", x_size + "x" + y_size);
	    
	    /* ディスプレイサイズ取得 */
	    WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
	    // ディスプレイのインスタンス生成
	    Display disp = wm.getDefaultDisplay();
	    Point display_size = new Point();
	    //API level 13
	    disp.getSize(display_size);
	    display_x = display_size.x;
	    display_y = display_size.y;


	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.v("ProgramPause", "Pause!!!!!!!!!!!!!!!!!");

	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.v("ProgramStop", "STOP!!!!!!!!!!!!!!!!!");
//		CameraNative.closeSock();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("ProgramDetroy", "Finish!!!!!!!!!!!!!!!!!");
//		CameraNative.closeSock();  
		}
}