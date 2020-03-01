package com.example.reminddoor.bluetooth;

import java.io.ByteArrayOutputStream;

public class Protocol {
	
	public static void unlockDoor() {
		Connectivity.sendData(Type.GET_ALL_USERS.get(), "Please unlock the door.".getBytes());
	}
	
	
	/*
	 * Case list:
	 * 0: ADMIN - Unlocking door request admin
	 * 1: Unlocking door request guest
	 * 2: ADMIN - Generate new user request (Generates new key and adds user name)
	 * 3: ADMIN - Remove user request (You provide a name).
	 *      | --- 32 bytes of name --- |
	 * 4: ADMIN - Get all authenticated users.
	 *      | --- nothing --- |
	 * 5: My name change request.
	 *      | --- 32 bytes of new name --- |
	 * 6: Others change name request.
	 *      | --- 32 bytes of old name --- | | --- 32 bytes of new name --- |
	 */
	
	public enum Type {
		UNLOCK,
		UNLOCK_GUEST,
		NEW_USER,
		REMOVE_USER,
		GET_ALL_USERS,
		CHANGE_MY_NAME,
		CHANGE_OTHER_NAME;
		
		public byte get() {
			return (byte) this.ordinal();
		}
	}
}
