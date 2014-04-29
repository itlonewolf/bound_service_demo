package com.example.BoundSerDemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Created by yee on 4/24/14.
 */
public class Serv extends Service {
    private static final String TAG = "Serv";
    private static final int mId = 1988 ;

    private int minTime = 100 ;


    public void setMinTime(int minTime) {
        this.minTime = minTime;
        mCallback.onMinTimeChanged(123,minTime);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg == null) {
                return;
            }
            String data = (String) msg.obj;
            if (mCallback != null) {
                mCallback.infoOnChanged(data, "info");
            } else {
                Log.w(TAG,"mCallback为空") ;
            }
        }
    };

    public void setCallback(ICallback iCallback) {
        this.mCallback = iCallback;
    }

    private ICallback mCallback;

    private LocalBinder mLocalBinder = new LocalBinder();

    private NotificationManager mNotificationManager ;
    private Notification mNotification ;

    public IBinder onBind(Intent intent) {
        Log.wtf(TAG, "onBind") ;
        return mLocalBinder;
    }

    @Override
    public void onCreate() {
        Log.wtf(TAG, "onCreate") ;
        super.onCreate();
        init();
    }

    void init(){
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE) ;
        mNotification = new NotificationCompat.Builder(this)
                .setContentTitle("title")
                .setContentText("text")
                .setTicker("ticker")
                .setSmallIcon(R.drawable.ic_launcher)
                .build() ;
    }

    public void openNoti(){
        mNotificationManager.notify(mId,mNotification);
        Message msg = mHandler.obtainMessage() ;
        msg.obj = "这是下载获取到的数据!@##$%^^&&**())_+" ;
        mHandler.sendMessageDelayed(msg,3* 1000);
    }
    public void closeNoti(){
        mNotificationManager.cancel(mId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.wtf(TAG, "onStartCommand") ;
        return START_STICKY;
    }

    public class LocalBinder extends  Binder{
        //Note:此处返回Serv的引用
        Serv getService(){
            return  Serv.this ;
        }
    }

    /**
     * 服务器端供client调用的方法
     * @param context
     * @param info
     */
    public void methodForClient(Context context,String info){
        //Note:此处可以处理服务器端的数据
        Toast.makeText(context,"这是服务器的方法\r\n"+info,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLowMemory() {
        Log.wtf(TAG,"onDestroy") ;
        super.onLowMemory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.wtf(TAG,"onConfigurationChanged") ;
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        Log.wtf(TAG,"dump") ;
        super.dump(fd, writer, args);
    }

    @Override
    public void onDestroy() {
        Log.wtf(TAG,"onDestroy") ;
        closeNoti();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.wtf(TAG,"onUnbind") ;
//        closeNoti();
        return super.onUnbind(intent);
    }

}
