package com.example.reminddoor.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminddoor.R;
import com.example.reminddoor.ui.notifications.list.RemindersListViewAdapter;

public class UsersList extends Fragment {

	public UsersList() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public RemindersListViewAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.users_list, container, false);
		Context context = root.getContext();
		
		final RecyclerView recyclerView = (RecyclerView) root;
		recyclerView.setLayoutManager(new LinearLayoutManager(context));
		
		adapter = new RemindersListViewAdapter();
		recyclerView.setAdapter(adapter);
		return root;
	}
}
