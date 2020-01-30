package com.example.reminddoor.ui.notifications.list;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.reminddoor.R;
import com.example.reminddoor.ui.notifications.list.NotificationList.OnNotificationListInteractionListener;
import com.example.reminddoor.ui.notifications.list.dummy.DummyContent.NotificationListItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link NotificationListItem} and makes a call to the
 * specified {@link OnNotificationListInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class NotificationListViewAdapter extends RecyclerView.Adapter<NotificationListViewAdapter.ViewHolder> {
	
	private final List<NotificationListItem> mValues;
	private final OnNotificationListInteractionListener mListener;
	
	public NotificationListViewAdapter(List<NotificationListItem> items, OnNotificationListInteractionListener listener) {
		mValues = items;
		mListener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.notification_item, parent, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		holder.mItem = mValues.get(position);
		holder.mContentView.setText(mValues.get(position).content);
		
		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mListener) {
					// Notify the active callbacks interface (the activity, if the
					// fragment is attached to one) that an item has been selected.
					mListener.onNotificationListSelected(holder.mItem);
				}
			}
		});
	}
	
	@Override
	public int getItemCount() {
		return mValues.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		public final View mView;
		public final TextView mContentView;
		public NotificationListItem mItem;
		
		public ViewHolder(View view) {
			super(view);
			mView = view;
			mContentView = (TextView) view.findViewById(R.id.content);
		}
		
		@Override
		public String toString() {
			return super.toString() + " '" + mContentView.getText() + "'";
		}
	}
}
