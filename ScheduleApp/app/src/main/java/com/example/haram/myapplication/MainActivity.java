package com.example.haram.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CalendarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                Intent i = new Intent(MainActivity.this, setting_display.class);

                startActivity(i);
                break;
            default:
                break;
        }
        return true;
    }


    public void calendar_init(){
        CalendarView calendar = (CalendarView) findViewById(R.id.calendar);

//        calendar.setDateTextAppearance();
    }

}
