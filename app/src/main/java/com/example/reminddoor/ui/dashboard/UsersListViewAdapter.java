package com.example.reminddoor.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminddoor.R;
import com.example.reminddoor.ui.notifications.list.RemindersCalendarContainer;

public class UsersListViewAdapter extends RecyclerView.Adapter<UsersListViewAdapter.ViewHolder> {
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_entry_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		RemindersCalendarContainer.RemindersListItem reminder = RemindersCalendarContainer.getItem(position);
		reminder.textBox = holder.mContentView;
		String text = reminder.getDisplayedText();
		
		if (text != null && !text.equals("")) {
			holder.mContentView.setText(text);
		} else {
			holder.mContentView.setText("");
		}
		
		holder.mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RemindersCalendarContainer.removeItem(position);
				notifyItemRemoved(position);
				notifyItemRangeChanged(position, getItemCount());
			}
		});
	}
	
	@Override
	public int getItemCount() {
		return RemindersCalendarContainer.getSize();
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		final View mView;
		final EditText mContentView;
		final ImageButton mButton;
		
		ViewHolder(View view) {
			super(view);
			mView = view;
			mContentView = view.findViewById(R.id.editText);
			mButton = view.findViewById(R.id.imageButton);
		}
	}
}
