package com.example.reminddoor.ui.notifications.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.reminddoor.R;

public class RemindersListViewAdapter extends RecyclerView.Adapter<RemindersListViewAdapter.ViewHolder> {
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		String text = RemindersCalendarContainer.getItem(position).getDisplayedText();
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
		
		holder.mContentView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (position < RemindersCalendarContainer.getSize() && !RemindersCalendarContainer.ignoreTextChanges) {
					RemindersCalendarContainer.getItem(position).content = s.toString();
				}
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			
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
