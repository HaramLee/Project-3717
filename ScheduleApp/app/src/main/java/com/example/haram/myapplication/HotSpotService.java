package com.example.haram.myapplication;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HotSpotService extends Service {

    RelativeLayout hotspotContainer;
    RelativeLayout listViewContainer;
    WindowManager hotspotWindowManager;
    WindowManager listViewWindowManager;
    static int height;
    static int width;
    static TextView textView;
    static ListView newList;
    private static ArrayList<String> summary;
    private IntentFilter receiveFilter;

    private boolean inHotSpot = false;

    GoogleAccountCredential credential = MainActivity.mCredential;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getData();
    }

    private void getData(){
        RequestTask makeRequestTask = new RequestTask(credential, getApplicationContext());
        makeRequestTask.setListener(new RequestTask.RequestTaskListener() {
            @Override
            public void onPreExecuteConcluded() {

            }

            @Override
            public void onPostExecuteConcluded(List<Event> result) {
                parseOutput(result);
            }
        });
        makeRequestTask.execute();
    }

    private void createHotSpot(ArrayList<HashMap<String, String>> datalist){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        hotspotWindowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        listViewWindowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        hotspotWindowManager.getDefaultDisplay().getMetrics(displayMetrics);

        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        hotspotContainer = new RelativeLayout(this);
        listViewContainer = new RelativeLayout(this);

        //hotspot
        textView = new TextView(this);

        textView.setSingleLine();
        textView.setTextSize(16.0f);
        textView.setText("  ");
        textView.setHeight((int) (0.6 * height));
        textView.setBackgroundResource(R.color.red);

        newList = new ListView(this);
        setSummary(datalist);

        final WindowManager.LayoutParams layoutParams3 = new WindowManager.LayoutParams();
        layoutParams3.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams3.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams3.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams3.flags =   WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |

                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams3.format = PixelFormat.TRANSLUCENT;

        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        inHotSpot = true;
                        getData();
                        textView.setBackgroundResource(R.color.white);
                        listViewWindowManager.addView(listViewContainer, layoutParams3);
                        break;

                    case MotionEvent.ACTION_UP:
                        inHotSpot = false;
                        textView.setBackgroundResource(R.color.red);
                        listViewWindowManager.removeView(listViewContainer);
                        break;

                }
                return true;
            }
        });

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        hotspotContainer.addView(textView, layoutParams);
        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams();
        layoutParams2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams2.width = WindowManager.LayoutParams.WRAP_CONTENT;;
        layoutParams2.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams2.flags =   WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |

                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams2.format = PixelFormat.TRANSPARENT;
        layoutParams2.gravity = Gravity.RIGHT;
        hotspotWindowManager.addView(hotspotContainer, layoutParams2);

    }

    private void setSummary(ArrayList<HashMap<String, String>> datalist){

        final ListAdapterHot adapter = new ListAdapterHot(this, datalist){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);


                return view;
            }
        };
        newList.setBackgroundColor(getResources().getColor(R.color.white));

        newList.setAdapter(adapter);



        RelativeLayout.LayoutParams layoutParams_text =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (!inHotSpot) {
            listViewContainer.addView(newList, layoutParams_text);
        } else {
            listViewContainer.removeView(newList);
            listViewContainer.addView(newList, layoutParams_text);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        if(hotspotContainer != null)
        {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(hotspotContainer);
            hotspotContainer = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    static final String KEY_DATE = "date";
    static final String KEY_DAY = "day";
    static final String KEY_START = "start";
    static final String KEY_END = "end";
    static final String KEY_SUMMARY = "summary";
    static final String KEY_COLOR = "color";

    private void parseOutput(List<Event> output) {
        summary = new ArrayList<String>();
        ArrayList<HashMap<String, String>> datalist = new ArrayList<HashMap<String, String>>();

        String init="",fin="",last="",startHour="",endHour="";
        Date dates = null;
        DateTime startTime = null, endTime = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Event e : output) {
            summary.add(e.getSummary());

            if (e.getStatus().equals("cancelled")){
                continue;
            } else if (e.getStart().containsKey("date")) {

                startTime = (DateTime) e.getStart().get("date");
                endTime = (DateTime) e.getEnd().get("date");


                //String color = e.getColorId();
                String Ymd = startTime.toString();
                String initTime = endTime.toString();

                try {
                    dates = dateFormat.parse(Ymd);
                } catch (ParseException j) {
                    j.printStackTrace();
                }

                init = dates.toString();
                fin = init.substring(0, 4);
                last = init.substring(8,11);

                startHour = "00:00";
                endHour = "23:59";
            } else if (e.getStart().containsKey("dateTime")) {
                startTime = (DateTime) e.getStart().get("dateTime");
                endTime = (DateTime) e.getEnd().get("dateTime");

                //String color = e.getColorId();
                String Ymd = startTime.toString();
                String initTime = endTime.toString();

                try {
                    dates = dateFormat.parse(Ymd);
                } catch (ParseException j) {
                    j.printStackTrace();
                }

                init = dates.toString();
                fin = init.substring(0, 4);
                last = init.substring(8,11);

                startHour = Ymd.substring(11,16);
                endHour = initTime.substring(11,16);

            }


            HashMap<String, String> map = new HashMap<String, String>();
            map.put(KEY_SUMMARY, e.getSummary());
            map.put(KEY_START, "Start: " + startHour);
            map.put(KEY_END, "End:  " + endHour);
            map.put(KEY_DATE,fin);//word
            map.put(KEY_DAY,last);//number
            //map.put(KEY_COLOR,color);//number
            datalist.add(map);

        }

        if (!inHotSpot) {
            createHotSpot(datalist);
        } else {
            setSummary(datalist);
        }

    }
}

class Gestures extends GestureDetector.SimpleOnGestureListener {

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
        if (e1.getX() < HotSpotService.width) {
            return true;
        }
        return false;

    }

}