package com.example.haram.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by Haram on 2016-02-01.
 */
public class setting_display extends AppCompatActivity {

    private LinearLayout background;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_page);

//        background = (LinearLayout) findViewById(R.id.backgroundLayout);

        Switch switch1 = (Switch) findViewById(R.id.switch1);
        Switch sw_switch = (Switch) findViewById(R.id.sw_service);

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    background.setBackgroundColor(Color.BLUE);
                } else {
                    background.setBackgroundColor(Color.WHITE);
                }
            }
        });

        sw_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(new Intent(getBaseContext(), HotSpotService.class));
                } else {
                    stopService(new Intent(getBaseContext(), HotSpotService.class));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.settings_id:
                Intent i = new Intent(setting_display.this, MainActivity.class);

                startActivity(i);
                break;
            default:
                break;
        }
        return true;
    }

//
//    public void onGetNameClick(View view) {
//
//        Intent getNameScreenIntent = new Intent(this, setting_displayTwo.class);
//
//        final int result = 1;
//
//        getNameScreenIntent.putExtra("callingActivity", "setting_display");
//
//        startActivityForResult(getNameScreenIntent, result);
//
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        TextView usersNameMessage = (TextView)
//                findViewById(R.id.users_name_message);
//
//        String nameSentBack = data.getStringExtra("UsersName");
//
//        usersNameMessage.append(" " + nameSentBack);
//    }

}
