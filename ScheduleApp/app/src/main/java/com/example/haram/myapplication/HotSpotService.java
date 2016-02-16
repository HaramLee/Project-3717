package com.example.haram.myapplication;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HotSpotService extends Service {
//    HUDView mView;
    RelativeLayout rl;
    TextView tv;
    WindowManager wm;


//    Bitmap hotspot = BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_up_blue);

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Toast.makeText(getBaseContext(), "Service onCreate", Toast.LENGTH_LONG).show();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;

        rl = new RelativeLayout(this);
        tv = new TextView(this);

        tv.setText("FEEEEEEEEEEEEEEEED");
        tv.setRotation(90);
//        tv.setWidth((int) (height * 0.6));
        tv.setSingleLine();
        tv.setTextSize(16f);
//        tv.setBackgroundResource(R.color.red);

        RelativeLayout.LayoutParams RLParams = new RelativeLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                                                                               WindowManager.LayoutParams.WRAP_CONTENT);

//        RLParams.addRule(RelativeLayout.CENTER_VERTICAL);
//        RLParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        rl.addView(tv, RLParams);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//        params.gravity = Gravity.RIGHT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                     | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                     | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        params.format = PixelFormat.TRANSPARENT;
//        params.gravity = Gravity.RIGHT;



        wm.addView(rl, params);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        if(rl != null)
        {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(rl);
            rl = null;
        }
    }
}


