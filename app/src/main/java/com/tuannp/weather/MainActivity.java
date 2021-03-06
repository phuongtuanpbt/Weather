package com.tuannp.weather;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.tuannp.weather.adapter.PagerAdapter;
import com.tuannp.weather.databinding.ActivityMainBinding;

/**
 * Created by Nguyễn Phương Tuấn on 23-Jul-17.
 */

public class MainActivity extends AppCompatActivity {

    //key to save location to SharePreferences
    private final String LOCATION_KEY = "LOCATION_KEY";
    ActivityMainBinding bindings;

    //default location is Tokyo
    public static String location = "Tokyo";
    private PagerAdapter pagerAdapter;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindings = DataBindingUtil.setContentView(this, R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        location = sharedPreferences.getString(LOCATION_KEY,"Tokyo");
        setSupportActionBar(bindings.toolbar);
        getSupportActionBar().show();

        setUpTabLayout();
    }

    /**
     * setUp TabLayout, 2 fragment: DAILY and WEEKLY
     * use PagerAdapter to setup ViewPager
     */
    private void setUpTabLayout() {
        bindings.tabLayout.addTab(bindings.tabLayout.newTab().setText("Daily"));
        bindings.tabLayout.addTab(bindings.tabLayout.newTab().setText("Weekly"));
        bindings.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        pagerAdapter = new PagerAdapter
                (getSupportFragmentManager(), bindings.tabLayout.getTabCount());
        bindings.viewPager.setAdapter(pagerAdapter);
        bindings.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(bindings.tabLayout));
        bindings.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                bindings.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * setup Option Menu with 3 action: refresh button, location button and about option.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.location: {
                showLocationDialog();
                return true;
            }
            case R.id.about: {
                showAboutDialog();
                return true;
            }
            default: {
                //refresh weather data
                pagerAdapter.refreshData();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * show alert dialog, user can change location to get weather information
     * when user click set location button, location value will stored to SharePreference, and app will call request to update weather data
     */
    private void showLocationDialog() {
        final EditText editTextLocation = new EditText(MainActivity.this);
        editTextLocation.setMaxLines(2);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(10, 0, 10, 0);
        editTextLocation.setLayoutParams(lp);

        AlertDialog.Builder alertDialog =
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Location")
                        .setMessage("Set your current location")
                        .setIcon(R.drawable.ic_location_on_white_24dp)
                        .setPositiveButton("Set Location", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                location = editTextLocation.getText().toString();
                                location = location.replace(" ", "-");
                                location = location.trim();
                                sharedPreferences.edit().putString(LOCATION_KEY, location).apply();
                                pagerAdapter.refreshData();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setView(editTextLocation);
        alertDialog.create().show();
    }

    /**
     * show about dialog to introduce app
     */
    private void showAboutDialog() {
        AlertDialog.Builder aboutAlertDialog = new AlertDialog.Builder(MainActivity.this);
        aboutAlertDialog.setTitle("About");
        aboutAlertDialog.setMessage("Weather app v0.1 by tuannp");
        aboutAlertDialog.setNegativeButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        aboutAlertDialog.create().show();
    }
}
