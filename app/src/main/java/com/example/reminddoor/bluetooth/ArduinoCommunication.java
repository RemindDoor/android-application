package com.example.reminddoor.bluetooth;

public class ArduinoCommunication {
	public static void openLock() {
		Connectivity.sendData("Open Lock\n".getBytes());
	}
	
	public static void closeLock() {
		Connectivity.sendData("Close Lock\n".getBytes());
	}
}
