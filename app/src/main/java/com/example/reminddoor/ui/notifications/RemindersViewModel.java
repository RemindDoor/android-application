package com.example.reminddoor.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RemindersViewModel extends ViewModel {

	private MutableLiveData<String> mText;

	public RemindersViewModel() {
		mText = new MutableLiveData<>();
		mText.setValue("This is a notifications fragment");
	}

	public LiveData<String> getText() {
		return mText;
	}
}