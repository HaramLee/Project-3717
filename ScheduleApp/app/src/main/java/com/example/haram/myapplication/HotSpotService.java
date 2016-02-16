package com.example.haram.myapplication;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
//import com.example.haram.myapplication.EdgeDetector;

public class HotSpotService extends Service {
//    EdgeDetector edgeDetector;
    RelativeLayout rl;
    WindowManager windowManager;
    WindowManager windowManager2;
    static int height;
    static int width;
    static TextView textView;

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
        textView = new TextView(this);

        textView.setSingleLine();
        textView.setTextSize(16.0f);
        textView.setText("  ");
        textView.setHeight((int) (0.6 * height));
        textView.setBackgroundResource(R.color.red);

        final Button button = new Button(this);
        final ImageView img = new ImageView(this);
        img.setImageResource(R.drawable.al);


        final WindowManager.LayoutParams layoutParams3 = new WindowManager.LayoutParams();
        layoutParams3.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams3.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams3.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams3.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams3.format = PixelFormat.TRANSPARENT;

        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        textView.setBackgroundResource(R.color.white);
                        windowManager2.addView(img, layoutParams3);

                        break;
                    case MotionEvent.ACTION_UP:
                        textView.setBackgroundResource(R.color.red);
                        windowManager2.removeView(img);
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
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }
}

class Gestures extends GestureDetector.SimpleOnGestureListener {

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
        if (e1.getX() < HotSpotService.width) {
            HotSpotService.textView.setBackgroundResource(R.color.black);
            return true;
        }
        return false;

    }

}