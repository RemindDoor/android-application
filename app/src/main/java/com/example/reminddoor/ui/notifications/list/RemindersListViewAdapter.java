package com.example.reminddoor.ui.notifications.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.reminddoor.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationListViewAdapter extends RecyclerView.Adapter<NotificationListViewAdapter.ViewHolder> {
	
	private static final List<NotificationListItem> mValues = new ArrayList<>();
	
	static {
		for (int i = 0; i < 10; i++) {
			mValues.add(new NotificationListItem("Item " + i));
		}
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		holder.mContentView.setText(mValues.get(position).content);
		
		holder.mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mValues.remove(position);
				notifyItemRemoved(position);
				notifyItemRangeChanged(position,getItemCount());
			}
		});
	}
	
	@Override
	public int getItemCount() {
		return mValues.size();
	}
	
	public static class NotificationListItem {
		final String content;
		
		NotificationListItem(String content) {
			this.content = content;
		}
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		final View mView;
		final TextView mContentView;
		final ImageButton mButton;
		
		ViewHolder(View view) {
			super(view);
			mView = view;
			mContentView = view.findViewById(R.id.content);
			mButton = view.findViewById(R.id.imageButton);
		}
	}
}
