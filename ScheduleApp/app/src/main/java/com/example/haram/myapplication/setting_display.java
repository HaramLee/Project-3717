package com.example.haram.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;


/**
 * Created by Haram on 2016-02-01.
 */
public class setting_display extends PreferenceActivity {

    private LinearLayout background;
    static int layoutId = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.setting_page);

//        background = (LinearLayout) findViewById(R.id.backgroundLayout);

//        Switch switch1 = (Switch) findViewById(R.id.switch1);
        final CheckBoxPreference sw_switch = (CheckBoxPreference)findPreference("sw_switch");

        sw_switch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if (sw_switch.isChecked()) {
                    startService(new Intent(getBaseContext(), HotSpotService.class));
                    return true;
                } else {
                    stopService(new Intent(getBaseContext(), HotSpotService.class));
                    return false;
                }

            }
        });

        final ListPreference listChange = (ListPreference)findPreference("pref_type");
        String currentValue = listChange.getValue();
        Log.d("***********", currentValue);
        if(currentValue.equals("1")){
            layoutId = 1;
            startService(new Intent(getBaseContext(), MainActivity.class));
        } else if (currentValue.equals("2")){
            layoutId = 2;
            startService(new Intent(getBaseContext(), MainActivity.class));
        }
    }

    // String
    public static String Read(Context context, final String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(key, "");
    }

    public static void Write(Context context, final String key, final String value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    // Boolean
    public static boolean ReadBoolean(Context context, final String key, final boolean defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(key, defaultValue);
    }

    public static void WriteBoolean(Context context, final String key, final boolean value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_menu, menu);
        inflater.inflate(R.menu.add_event, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.settings_id:
                Intent i = new Intent(setting_display.this, MainActivity.class);

                startActivity(i);
                break;
            case R.id.add_id:
                Calendar cal = Calendar.getInstance();
                Intent j = new Intent(Intent.ACTION_EDIT);
                j.setType("vnd.android.cursor.item/event");
                j.putExtra("BeginTime", cal.getTimeInMillis());
                j.putExtra("allDay",true);
                j.putExtra("rrule","FREQ=YEARLY");
                j.putExtra("EndTime",cal.getTimeInMillis()+60*60*1000);
                j.putExtra("title","Test Event");
                j.putExtra("description"," ");

                startActivity(j);
                break;
            default:
                break;
        }
        return true;
    }

}
