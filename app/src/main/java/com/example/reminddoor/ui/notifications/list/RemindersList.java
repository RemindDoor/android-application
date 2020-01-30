package com.example.reminddoor.ui.notifications.list;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.reminddoor.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RemindersList extends Fragment {

	public RemindersList() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public RemindersListViewAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.notification_list, container, false);
		Context context = root.getContext();
		
		final RecyclerView recyclerView = (RecyclerView) root;
		recyclerView.setLayoutManager(new LinearLayoutManager(context));
		
		adapter = new RemindersListViewAdapter();
		recyclerView.setAdapter(adapter);
		return root;
	}
}
