package com.netim;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements Camera.PreviewCallback, SurfaceHolder.Callback, LocationListener{

	private Camera m_camera;
	private DrawView view;
	private OverlayView overlay;

	private Object m_lock = new Object();

	private LocationManager mLocationManager;

	//画像データ格納
	int image_data_size;
	//速度
	int speed = 0;
	//座標
	static int num = 0;
	static int resolution = 0;
	static int points[];

	//時間計測用変数
	long start,end;
	long launchstart, launchend;

	long encordingTime = 0;

	long baseTime=0;

	LinkedList<byte[]> m_imageArray = new LinkedList<byte[]>();

	public CameraView(Context context, DrawView view){
		super(context);
		this.view = view;

		// setting of holder
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// setting of view's focus
		this.setFocusable(true);
		this.requestFocus();

	}
	// perform when there is a change in the view
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
		try
		{
			// setting of camera
			Camera.Parameters parameters = m_camera.getParameters();
			parameters.getSupportedPreviewSizes();
			
			parameters.setPreviewSize(CameraActivity.x_size, CameraActivity.y_size);

			// カメラにプレビューの設定情報を戻してプレビューを再開する;
			m_camera.setParameters(parameters);
			m_camera.setPreviewDisplay(holder);
			m_camera.setPreviewCallback(this);
			m_camera.startPreview();

		}
		catch (IOException e) {}
	}

	// perfome when view is created
	public void surfaceCreated(SurfaceHolder holder){
		m_camera = Camera.open();
		int thread_num = 1;
		int i =0;

		while(i <= 1){
			CameraNative.connectSock(CameraActivity.address, CameraActivity.port_number + i, i, CameraActivity.flag);
			try{
				Thread.sleep(10); //20ミリ秒Sleepする
				i++;
			}catch(InterruptedException e){}
		}

		CameraNative.createThread();
		CameraNative.sendInfo(CameraActivity.flag);

		if(CameraActivity.flag == 0)
			thread_num = 4;
		else if(CameraActivity.flag == 1 || CameraActivity.flag == 2)
			thread_num = 1;

		BackGroundThread[] backGroundThreadArray = new BackGroundThread[thread_num];
		for(BackGroundThread backGroundThread:backGroundThreadArray){
			backGroundThread = new BackGroundThread();
			backGroundThread.start();
		}
	}

	// perfome when view is destroyed
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		m_camera.setPreviewCallback(null);
		m_camera.stopPreview();
		m_camera.release();
		m_camera = null;
		CameraNative.closeSock();
	}
	//get preview image
	public void onPreviewFrame(byte[] data, Camera camera){
		camera.setPreviewCallback(null);

		//enqueue data
		bringData(data);

		camera.setPreviewCallback(this);
		invalidate();

	}

	private void bringData(byte[] data) {
		synchronized (m_lock) {
			if(m_imageArray.isEmpty()){
				m_imageArray.add(data);
				m_lock.notify();
			}
			else{
				while(m_imageArray.size() > 3){
					m_imageArray.removeFirst();
				}
				m_imageArray.add(data);
			}
		}
	}

	class BackGroundThread extends Thread{
		int threadID;
		long startTime, endTime;

		public void run(){
			byte[] image_data=null;
			while(true){
				startTime = System.currentTimeMillis();

				try {
					image_data = getImageData();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(image_data != null){
					YuvImage yuvimage = new YuvImage(image_data, ImageFormat.NV21, m_camera.getParameters().getPreviewSize().width, m_camera.getParameters().getPreviewSize().height, null);
					ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
					yuvimage.compressToJpeg(new Rect(0, 0, m_camera.getParameters().getPreviewSize().width, m_camera.getParameters().getPreviewSize().height),  80, baOutStream);
					endTime = System.currentTimeMillis();

					/*
					 * ローカル保存
					 * tmp.jpgにプレビュー画像を保存しサーバへ送る
					 * サーバから受け取った画像はrecieve.jpgファイルに
					 * 保存する
					 */

					FileOutputStream outStream = null;

					try {
						outStream = new FileOutputStream("/mnt/sdcard/tmp.jpg");
						outStream.write(baOutStream.toByteArray());
						outStream.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					String coordinate = CameraNative.sendPhoto(baOutStream.toByteArray().length, speed);
					int count=0;


					if(CameraActivity.flag == 1 || CameraActivity.flag == 2){
						Log.v("logggggggggggg", "" + coordinate);
						String[] buff = coordinate.split(",");

						num = Integer.parseInt(buff[0]);
						resolution = Integer.parseInt(buff[1]);
						int reserved = Integer.parseInt(buff[2]);
						int reserved2 = Integer.parseInt(buff[3]);

						points = new int[num*6];

						while(count < num*6){
							points[count] = Integer.parseInt(buff[4 + count]);
							Log.v("points[" + count + "]","" + points[count]);
							count++;
						}

						//synchronized
						if(CameraActivity.flag == 1){
							//CameraNative.getPhoto();
							view.drawBitmap();
						}
						//asynchronized
						else if(CameraActivity.flag == 2){
							CameraActivity.overlayview.drawBox();
						}
					}
				}
			}
		}

		private byte[] getImageData() throws InterruptedException {
			byte[] image_data = null;
			synchronized (m_lock) {
				if(m_imageArray.isEmpty()){
					m_lock.wait();
				}
				try{
					image_data = m_imageArray.getFirst();
					m_imageArray.removeFirst();
				}
				catch( NoSuchElementException e ){
					e.printStackTrace();
				}
			}
			return image_data;
		}
	}

	@Override
	public void onLocationChanged(Location location) {

		//速度取得
		speed = (int) location.getSpeed();

		Log.v("----------", "----------");
		Log.v("Latitude", String.valueOf(location.getLatitude()));
		Log.v("Longitude", String.valueOf(location.getLongitude()));
		Log.v("Accuracy", String.valueOf(location.getAccuracy()));
		Log.v("Altitude", String.valueOf(location.getAltitude()));
		Log.v("Time", String.valueOf(location.getTime()));
		Log.v("Speed", String.valueOf(speed));
		Log.v("Bearing", String.valueOf(location.getBearing()));

	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ

	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		switch (status) {
		case LocationProvider.AVAILABLE:
			Log.v("Status", "AVAILABLE");
			break;
		case LocationProvider.OUT_OF_SERVICE:
			Log.v("Status", "OUT_OF_SERVICE");
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			Log.v("Status", "TEMPORARILY_UNAVAILABLE");
			break;
		}
	}
}