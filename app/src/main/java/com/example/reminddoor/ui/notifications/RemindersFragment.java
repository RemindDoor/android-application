package com.example.reminddoor.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.example.reminddoor.R;
import com.example.reminddoor.ui.notifications.list.RemindersCalendarContainer;
import com.example.reminddoor.ui.notifications.list.RemindersList;
import com.example.reminddoor.ui.notifications.list.RemindersListViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RemindersFragment extends Fragment {

	private RemindersViewModel remindersViewModel;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		remindersViewModel = ViewModelProviders.of(this).get(RemindersViewModel.class);
		final View root = inflater.inflate(R.layout.fragment_reminders, container, false);
		
		FloatingActionButton floatingActionButton = root.findViewById(R.id.floatingActionButton3);
		floatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RemindersCalendarContainer.addItem();
				FragmentManager fm = getChildFragmentManager();
				RemindersList list = (RemindersList) fm.findFragmentById(R.id.fragment2);
				list.adapter.notifyItemInserted(RemindersCalendarContainer.getSize());
			}
		});
		
		return root;
	}
}