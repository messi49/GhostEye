package com.netim;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class MainActivity extends Activity implements OnClickListener, OnItemSelectedListener{

	protected SurfaceHolder holder;
	
	/* recognized target */
	boolean car_flag, human_flag, sign_flag, lane_flag;

	CheckBox checkbox_car;
	CheckBox checkbox_human;
	CheckBox checkbox_sign;
	CheckBox checkbox_lane;

	/* In order to get preview size */
	protected Camera camera;
	String previewSize;

	/* perfome type */
	String address;
	int p_type;


	/* IP-Address and port number */
	int port;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		/* target checkbox */
		checkbox_car = (CheckBox)findViewById(R.id.checkBox_car);
		checkbox_human = (CheckBox)findViewById(R.id.checkBox_human);
		checkbox_sign = (CheckBox)findViewById(R.id.checkBox_sign);
		checkbox_lane = (CheckBox)findViewById(R.id.checkBox_lane);

		// set listener
		checkbox_car.setOnClickListener(this);
		checkbox_human.setOnClickListener(this);
		checkbox_sign.setOnClickListener(this);
		checkbox_lane.setOnClickListener(this);
		
		/* select resolution of preview */
		// get preview size
		camera=Camera.open();
		try{
			camera.setPreviewDisplay(holder);
		}catch(IOException e){
			Log.d("StartActivity","setPreviewDisplay exception");
		}
		
        Camera.Parameters parameters = camera.getParameters();
        List<Size> sizes = parameters.getSupportedPreviewSizes();
        
        ArrayAdapter<String> resolution_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        resolution_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // add items
        int count = 0, num = -1;
        for (Size currSize : sizes) {
        	resolution_adapter.add(currSize.width + "x" + currSize.height);
        	if(currSize.width == 320 && currSize.height == 240){
        		num = count;
        	}
        	count++;
        }
        
        Spinner resolution_spinner = (Spinner) findViewById(R.id.resolution);
        // set adapter
        resolution_spinner.setAdapter(resolution_adapter);
        // set default value
        if(num != -1){
        	resolution_spinner.setSelection(num);
        }
        // set listener
        resolution_spinner.setOnItemSelectedListener(this);

		/* select box of type */
        ArrayAdapter<String> type_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // add items
        type_adapter.add("Camera mode");
        type_adapter.add("Synchronous mode");
        type_adapter.add("Asynchronous mode");
        Spinner type_spinner = (Spinner) findViewById(R.id.type);
        // set adapter
        type_spinner.setAdapter(type_adapter);
        // set listener
        type_spinner.setOnItemSelectedListener(this);

        /* start */
        Button btn = (Button)findViewById(R.id.start_button);
        btn.setOnClickListener(this);

        /* demo */
        address = "192.168.2.227";
        port = 12345;
//        address = "192.168.11.5";
//        port = 12345;


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_start, menu);
		return true;
	}

	@Override
	public void onClick(View view){

		switch (view.getId()) {
		case R.id.checkBox_car:
			if(checkbox_car.isChecked()) {
				// car
				Toast.makeText(this, "Car checked", Toast.LENGTH_LONG).show();
				car_flag = true;
			}
			else{
				car_flag = false;
			}
			break;
		case R.id.checkBox_human:
			if(checkbox_human.isChecked()) {
				// human
				Toast.makeText(this, "Human checked", Toast.LENGTH_LONG).show();
				human_flag = true;
			}
			else{
				human_flag = false;
			}
			break;
		case R.id.checkBox_sign:
			if(checkbox_sign.isChecked()) {
				// sign
				Toast.makeText(this, "Traffic sign checked", Toast.LENGTH_LONG).show();
			}
			else{
				sign_flag = false;
			}
			break;
		case R.id.checkBox_lane:
			if(checkbox_lane.isChecked()) {
				// lane
				Toast.makeText(this, "Traffic lane checked", Toast.LENGTH_LONG).show();
			}
			else{
				lane_flag = false;
			}
			break;
			/* stat button pushed */
		case R.id.start_button:
			/* connect RelayServer and get infomation of image recognition */
			//String buff = GhostEyeNative.getServerInfo();


			Toast.makeText(this, "Start Push!", Toast.LENGTH_LONG).show();
			// pass intent to GhostEyeActivity
			Intent intent = new Intent(getApplication(), CameraActivity.class);
			// infomation of recognized object 
			intent.putExtra("car_flag", car_flag);
			intent.putExtra("human_flag", human_flag);
			intent.putExtra("sign_flag", sign_flag);
			intent.putExtra("lane_flag", lane_flag);
			// infomation of resolution
			if(previewSize.length() != 0){
				intent.putExtra("resolution", previewSize);
				Log.v("resolution", previewSize);
			}
			else{
				intent.putExtra("resolution", "320x240");
			}
			// type infomation	
			intent.putExtra("p_type", p_type);
			// address and port
			intent.putExtra("address", address);
			intent.putExtra("port", port);
			
			/* Camera release */
			camera.release();

			startActivity(intent);

			break;
		default:
			Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
			break;

		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Spinner spinner = (Spinner) arg0;
		// get selected item
		String item = (String) spinner.getSelectedItem();
		Toast.makeText(this, item, Toast.LENGTH_LONG).show();

		if(item == "Camera mode"){
			p_type = 0;
		}
		else if(item == "Synchronous mode"){
			p_type = 1;
		}
		else if(item == "Asynchronous mode"){
			p_type = 2;
			Log.v("typeeee", "asynchronous");
		}
		else{
			previewSize = item;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}
}
	
