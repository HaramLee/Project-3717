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
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

public class HotSpotService extends Service {
    HUDView mView;

//    Bitmap hotspot = BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_up_blue);

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getBaseContext(), "Service onCreate", Toast.LENGTH_LONG).show();
//        mView = new HUDView(this, hotspot);
        mView = new HUDView(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                hotspot.getWidth(),
//                hotspot.getHeight(),
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        params.setTitle("Load Average");
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(mView, params);
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
        if(mView != null)
        {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mView);
            mView = null;
        }
    }

//    /** indicates how to behave if the service is killed */
//    int mStartMode;
//
//    /** interface for clients that bind */
//    IBinder mBinder;
//
//    /** indicates whether onRebind should be used */
//    boolean mAllowRebind;
//
//    /** Called when the service is being created. */
//    @Override
//    public void onCreate() {
//
//    }
//
//    /** The service is starting, due to a call to startService() */
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return mStartMode;
//    }
//
//    /** A client is binding to the service with bindService() */
//    @Override
//    public IBinder onBind(Intent intent) {
//        return mBinder;
//    }
//
//    /** Called when all clients have unbound with unbindService() */
//    @Override
//    public boolean onUnbind(Intent intent) {
//        return mAllowRebind;
//    }
//
//    /** Called when a client is binding to the service with bindService()*/
//    @Override
//    public void onRebind(Intent intent) {
//
//    }
//
//    /** Called when The service is no longer used and is being destroyed */
//    @Override
//    public void onDestroy() {
//
//    }
}

class HUDView extends ViewGroup {
    private Paint mLoadPaint;
    Bitmap hotspot;

//    public HUDView(Context context, Bitmap hotspot) {
    public HUDView(Context context) {
        super(context);
//        this.hotspot = hotspot;
//        Toast.makeText(getContext(),"HUDView", Toast.LENGTH_LONG).show();

        mLoadPaint = new Paint();
        mLoadPaint.setAntiAlias(true);
        mLoadPaint.setTextSize(100);
        mLoadPaint.setARGB(255, 255, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("l", 700, 305, mLoadPaint);
//        canvas.drawBitmap(hotspot,0,0,null);
    }

    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ( (event.getX() <= 700 && event.getX() >= 750) || (event.getY() >= 200 && event.getY() <= 300) ) {
            Log.d("coordsX", Float.toString(getX()));
            Log.d("coordsY", Float.toString(event.getY()));
            Toast.makeText(getContext(), "onTouchEvent", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
