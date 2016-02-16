package com.example.haram.myapplication;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
//import com.example.haram.myapplication.EdgeDetector;

public class HotSpotService extends Service {
//    EdgeDetector edgeDetector;
    RelativeLayout rl;
    WindowManager windowManager;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
//        this.edgeDetector = new EdgeDetector();

        int height = displayMetrics.heightPixels;
        rl = new RelativeLayout(this);
        TextView textView = new TextView(this);

        textView.setSingleLine();
        textView.setTextSize(16.0f);
        textView.setText((CharSequence) " ");
        textView.setHeight((int) (0.6 * height));
        textView.setBackgroundResource(R.color.red);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);;
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

