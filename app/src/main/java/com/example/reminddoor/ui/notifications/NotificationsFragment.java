package com.example.reminddoor.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminddoor.R;

public class NotificationsFragment extends Fragment {

	private NotificationsViewModel notificationsViewModel;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
		View root = inflater.inflate(R.layout.fragment_reminders, container, false);
		
		return root;
	}
}