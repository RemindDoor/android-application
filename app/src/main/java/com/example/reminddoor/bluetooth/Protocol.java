package com.example.reminddoor.bluetooth;

import androidx.core.util.Consumer;
import androidx.fragment.app.FragmentManager;

import com.example.reminddoor.MainActivity;
import com.example.reminddoor.assist.Util;
import com.example.reminddoor.ui.dashboard.DashboardFragment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Protocol {
	
	private static final byte[] empty = new byte[]{};
	
	public static void unlockDoor() {
		Connectivity.sendData(Type.UNLOCK.get(), empty, null);
	}
	
	public static void addUser(final FragmentManager fm, byte[] encryptedString) {
		Consumer<byte[]> byteEater = s -> {
			System.out.println("Received key.");
			System.out.println("The key is " + s.length + " bytes long");

			Util.setKey(s);
			
			if (MainActivity.currentFragment == MainActivity.BottomTab.DOOR) {
				DashboardFragment.updateUsersList(fm);
			}
		};
		
		Connectivity.sendData(encryptedString, byteEater);
	}
	
	public static void getUserList(final Consumer<ArrayList<String>> namesListConsumer) {
		Consumer<byte[]> byteEater = s -> {
			String[] splitNames = new String(s).split("\\|");
			
			ArrayList<String> names = new ArrayList<>();
			for (String name : splitNames) {
				if (name.length() > 0) {
					names.add(name);
				}
			}
			
			namesListConsumer.accept(names);
		};
		Connectivity.sendData(Type.GET_ALL_USERS.get(), empty, byteEater);
	}
	
	public static void generateUser() {
	
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
