package com.example.reminddoor.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.reminddoor.R;
import com.example.reminddoor.ui.notifications.list.RemindersCalendarContainer;
import com.example.reminddoor.ui.notifications.list.RemindersList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;

public class DashboardFragment extends Fragment {

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_my_door, container, false);
		FragmentManager fm = getChildFragmentManager();
		final UsersList list = (UsersList) fm.findFragmentById(R.id.fragment3);
		
		FloatingActionButton floatingActionButton = root.findViewById(R.id.floatingActionButton);
		RemindersCalendarContainer.updateDate(0L);
		floatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RemindersCalendarContainer.addItem();
				list.adapter.notifyItemInserted(RemindersCalendarContainer.getSize());
			}
		});
		
		return root;
	}
}