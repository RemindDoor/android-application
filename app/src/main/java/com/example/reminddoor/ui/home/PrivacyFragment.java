package com.example.reminddoor.ui.home;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.example.reminddoor.MainActivity;
import com.example.reminddoor.R;

import org.w3c.dom.Text;

public class PrivacyFragment extends DialogFragment {
	public static Runnable dismiss = null;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog);
		dismiss = this::dismiss;
		this.setCancelable(false);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_privacy, container);
		Spanned sp = Html.fromHtml( getString(R.string.some_text));
		TextView text = v.findViewById(R.id.privacyText);
		text.setText(sp);
		Button button = v.findViewById(R.id.accept_button);
		button.setEnabled(false);
		button.setOnClickListener(v1 -> {
			dismiss();
			MainActivity.prefs.edit().putBoolean("accepted_privacy", false).commit();
		});
		CheckBox checkBox = v.findViewById(R.id.accept_checkbox);
		checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
			button.setEnabled(isChecked);
		});
		
		return v;
	}
}
