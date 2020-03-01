package com.example.reminddoor.ui.notifications.list;

import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class RemindersCalendarContainer {
	// It's a public static class!
	// (Only one of these exists per app so we're all good)
	
	// The Java Date class is notorious for being annoying to work with
	// So String is used as a key instead, to create usable map keys.
	private static Map<String, List<RemindersListItem>> mValues = new HashMap<>();
	
	private static String currentSelectedDate;
	
	public static void save(File f) {
		File file = new File(f, "calendarValues");
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
			outputStream.writeObject(mValues);
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void load(File f) {
		File file = new File(f, "calendarValues");
		if (!file.exists()) return;
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
			mValues = (Map<String, List<RemindersListItem>>) inputStream.readObject();
			inputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateDate(long time) {
		Date date = new Date(time);
		// Deprecated, but still works. Alternatives might be better, but this works so
		// There's no reason to go searching for more up to date versions.
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		currentSelectedDate = date.toString();
		
		if (!mValues.containsKey(currentSelectedDate)) {
			mValues.put(currentSelectedDate, new ArrayList<RemindersListItem>());
		}
	}
	
	public static void updateDate(int year, int month, int dayOfMonth) {
		currentSelectedDate = new GregorianCalendar(year, month, dayOfMonth).getTime().toString();
		
		if (!mValues.containsKey(currentSelectedDate)) {
			mValues.put(currentSelectedDate, new ArrayList<RemindersListItem>());
		}
	}
	
	public static void removeItem(int position) {
		mValues.get(currentSelectedDate).remove(position);
	}
	
	public static void removeAll() {
		mValues.get(currentSelectedDate).clear();
	}
	
	public static RemindersListItem getItem(int position) {
		return mValues.get(currentSelectedDate).get(position);
	}
	
	public static void addItem() {
		mValues.get(currentSelectedDate).add(new RemindersListItem());
	}
	
	public static void syncText() {
		for (RemindersListItem item : mValues.get(currentSelectedDate)) {
			item.sync();
		}
	}
	
	public static class RemindersListItem implements Serializable {
		// Changing this may not immediately change the displayed string
		public String content;
		transient public EditText textBox;
		
		public String getDisplayedText() {
			return content;
		}
		public void sync() {
			content = textBox.getText().toString();
		}
	}
	
	public static int getSize() {
		return mValues.get(currentSelectedDate).size();
	}
	
	
}
