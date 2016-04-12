package com.example.haram.myapplication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import com.imanoweb.calendarview.CustomCalendarView;
//import com.example.haram.myapplication.EdgeDetector;

public class HotSpotService extends Service {
//    EdgeDetector edgeDetector;
    RelativeLayout rl;
    RelativeLayout rl2;
    WindowManager windowManager;
    WindowManager windowManager2;
    static int height;
    static int width;
    static TextView textView;
    static ListView taskList;
    static ListView newList;
    private IntentFilter receiveFilter;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        windowManager2 = (WindowManager)getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
//        this.edgeDetector = new EdgeDetector();

        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        rl = new RelativeLayout(this);
        rl2 = new RelativeLayout(this);
        textView = new TextView(this);

        textView.setSingleLine();
        textView.setTextSize(16.0f);
        textView.setText("  ");
        textView.setHeight((int) (0.6 * height));
        textView.setBackgroundResource(R.color.red);

        final Button button = new Button(this);
        final ImageView img = new ImageView(this);
//        final CustomCalendarView calendar = new CustomCalendarView(this);
//        calendar.setId(100000);
        img.setImageResource(R.drawable.al);

        LayoutInflater factory = (LayoutInflater) this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        final View textEntryView = factory.inflate(R.layout.activity_main, null);

        String[] summary = MainActivity.summary.toArray(new String[MainActivity.summary.size()]);

//        for (String s : MainActivity.summary){
//            summary.add( new String(s) );
//
//        }

        ArrayList<String> fds = new ArrayList<>();
        fds.add("fdsa");

        ArrayList<EventDateTime> start = new ArrayList<EventDateTime>();
        ArrayList<EventDateTime> end = new ArrayList<EventDateTime>();




        taskList =  (ListView) textEntryView.findViewById(R.id.list);
        newList = new ListView(this);
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, summary){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

            /*YOUR CHOICE OF COLOR*/
                textView.setTextColor(Color.BLACK);

                return view;
            }
        };
        newList.setAdapter(adapter);
        newList.setBackgroundColor(getResources().getColor(R.color.white));

//        newList.setText(MainActivity.mOutputText.getText());
//        newList.setBackgroundColor(getResources().getColor(R.color.white));
//        newList.setTextColor(getResources().getColor(R.color.black));

        RelativeLayout.LayoutParams layoutParams_calendar =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams_calendar.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        RelativeLayout.LayoutParams layoutParams_text =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams_text.addRule(RelativeLayout.BELOW, calendar.getId());

//        rl2.addView(calendar, layoutParams_calendar);
//        rl2.addView(newList, layoutParams_text);
          rl2.addView(newList, layoutParams_text);


        final WindowManager.LayoutParams layoutParams3 = new WindowManager.LayoutParams();
        layoutParams3.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams3.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams3.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams3.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams3.format = PixelFormat.TRANSLUCENT;

        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        textView.setBackgroundResource(R.color.white);
                        windowManager2.addView(rl2, layoutParams3);
                        break;

                    case MotionEvent.ACTION_UP:
                        textView.setBackgroundResource(R.color.red);
                        windowManager2.removeView(rl2);
                        break;
                }
                return true;
            }
        });

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rl.addView(textView, layoutParams);
        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams();
        layoutParams2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams2.width = WindowManager.LayoutParams.WRAP_CONTENT;;
        layoutParams2.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams2.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams2.format = PixelFormat.TRANSPARENT;
        layoutParams2.gravity = Gravity.RIGHT;
        windowManager.addView(rl, layoutParams2);



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        if(rl != null)
        {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(rl);
            rl = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
//        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

}

class Gestures extends GestureDetector.SimpleOnGestureListener {

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
        if (e1.getX() < HotSpotService.width) {
//            HotSpotService.textView.setBackgroundResource(R.color.black);
            return true;
        }
        return false;

    }

}