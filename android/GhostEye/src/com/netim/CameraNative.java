package com.netim;
public class CameraNative {
	static{
		System.loadLibrary("cameranative");
	}
	public static native void connectSock(String address, int port_number, int sock_num, int flag);
	public static native void closeSock();
	public static native void createThread();
	//public static native String sendPhoto(int data_size, byte[] data, int speed);
	public static native String sendPhoto(int data_size, int speed);
	public static native void sendInfo(int flag);
	public static native void getPhoto();
}
