package com.example.reminddoor;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.reminddoor.ui.dashboard.DashboardFragment;
import com.example.reminddoor.ui.home.HomeFragment;
import com.example.reminddoor.ui.notifications.RemindersFragment;
import com.example.reminddoor.ui.notifications.list.RemindersCalendarContainer;
import com.example.reminddoor.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity {

	BottomNavigationView bottomNavigationView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RemindersCalendarContainer.load(getDir("data", MODE_PRIVATE));
		
		setContentView(R.layout.activity_main);
		BottomNavigationView navView = findViewById(R.id.nav_view);
		navView.setOnNavigationItemSelectedListener(navListener);
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
				R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_profile)
				.build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
		NavigationUI.setupWithNavController(navView, navController);

	}

	private BottomNavigationView.OnNavigationItemSelectedListener navListener =
			new BottomNavigationView.OnNavigationItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(@NonNull MenuItem item) {

					Fragment selectedFragment = null;

					switch(item.getItemId()) {
						case R.id.navigation_home:
							selectedFragment = new HomeFragment();
							break;
						case R.id.navigation_notifications:
							selectedFragment = new RemindersFragment();
							break;
						case R.id.navigation_dashboard:
							selectedFragment = new DashboardFragment();
							break;
						case R.id.navigation_profile:
							selectedFragment = new ProfileFragment();
							break;

					}

					getSupportFragmentManager().beginTransaction().replace(R.id.navigation_home, selectedFragment).commit();
					return true;
				}
			};

	@Override
	protected void onStop() {
		super.onStop();
		
		RemindersCalendarContainer.save(getDir("data", MODE_PRIVATE));
	}
}
