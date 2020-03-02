package com.example.reminddoor.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.reminddoor.MainActivity;
import com.example.reminddoor.R;
import com.example.reminddoor.bluetooth.Protocol;
import com.example.reminddoor.ui.notifications.list.RemindersCalendarContainer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

	private static FragmentManager fm;
	
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		MainActivity.currentFragment = MainActivity.BottomTab.DOOR;
		
		View root = inflater.inflate(R.layout.fragment_my_door, container, false);
		
		fm = getChildFragmentManager();
		
		FloatingActionButton floatingActionButton = root.findViewById(R.id.floatingActionButton);
		RemindersCalendarContainer.updateDate(0L);
		floatingActionButton.setOnClickListener(v -> ((MainActivity) getContext()).createQRCode());
		
		updateUsersList();
		return root;
	}
	
	public static Consumer<ArrayList<String>> updateList() {
		final UsersList list = (UsersList) fm.findFragmentById(R.id.fragment3);
		
		Consumer<ArrayList<String>> listLoader = names -> {
			RemindersCalendarContainer.removeAll();
			
			for (String name : names) {
				RemindersCalendarContainer.addItem();
				int newSize = RemindersCalendarContainer.getSize();
				RemindersCalendarContainer.RemindersListItem item = RemindersCalendarContainer.getItem(newSize-1);
				item.content = name.trim();
			}
			if (list != null && list.getActivity() != null) {
				list.getActivity().runOnUiThread(() -> list.adapter.notifyDataSetChanged());
			}
		};
		return listLoader;
	}
	
	public static void updateUsersList() {
		Protocol.getUserList(updateList());
	}
}