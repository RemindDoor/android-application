package com.example.reminddoor.bluetooth;

public class ArduinoCommunication {
	public static void openLock() {
		Connectivity.unlockDoor();
//		Connectivity.sendData(new byte[]{1});
	}
	
	public static void closeLock() {
		Connectivity.unlockDoor();
//		Connectivity.sendData(new byte[]{0});
	}
}
