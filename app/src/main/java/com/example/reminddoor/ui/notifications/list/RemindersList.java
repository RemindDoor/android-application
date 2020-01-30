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

public class NotificationList extends Fragment {

	public NotificationList() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.notification_list, container, false);
		Context context = view.getContext();
		
		RecyclerView recyclerView = (RecyclerView) view;
		recyclerView.setLayoutManager(new LinearLayoutManager(context));
		recyclerView.setAdapter(new NotificationListViewAdapter());
		return view;
	}
}
