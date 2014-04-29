package com.example.BoundSerDemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.haarman.supertooltips.ToolTip;
import com.haarman.supertooltips.ToolTipRelativeLayout;
import com.haarman.supertooltips.ToolTipView;

public class MyActivity extends Activity implements View.OnClickListener, ToolTipView.OnToolTipViewClickedListener{

    //----------------------constants
    private static final String TAG = "MyActivity" ;
    //判断service绑定状态
    private static boolean mBound  = false ;

    //-------------------------view
    @InjectView(R.id.btnBind)
    Button mBtnBind;
    @InjectView(R.id.btnshow)
    Button mBtnshow;
    @InjectView(R.id.btnOpen)
    Button mBtnOpen ;
    @InjectView(R.id.btnClose)
    Button mBtnClose ;
    @InjectView(R.id.layouttoolTip)
    ToolTipRelativeLayout mLayoutToolTip ;


    //-------------------------variate
    private ServiceConnection mServiceConnection;
    private Serv mServ ;
    private ToolTipView mToolTipView ;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.inject(this);
        init();
    }

    /**
     * 初始化
     */
    void init() {
        initVariate();
        bindListener();
    }

    /**
     * 为显示信息用的按钮添加事件
     * <p>
     *     在没有绑定service时，显示“没有绑定service”
     * </p>
     */
    void addUnbindToolTip(View view){
        Log.d(TAG,"添加了tooltipview") ;
        ToolTip toolTip= new ToolTip().withText("没有绑定service")
                .withColor(Color.RED).withAnimationType(ToolTip.ANIMATIONTYPE_FROMTOP);
        //mBtnshow与相应的ToolTip绑定
        mToolTipView = mLayoutToolTip.showToolTipForView(toolTip, view);
        mToolTipView.setOnToolTipViewClickedListener(this);
    }

    /**
     * 此处不能以参数的形式传入，并将参数.remove()，以及设置为null，不会起作用
     * 即
     * void removeToolTipView(ToolTipView toolTipView){
     *     toolTipView.remove();
     *     toolTipView = null ;
     * }
     * 这样是不会起到任何作用的
     */
    void removeToolTipView(){
        mToolTipView.remove();
        mToolTipView = null ;
    }

    void bindListener(){
        mBtnBind.setOnClickListener(this);
        mBtnshow.setOnClickListener(this);
        mBtnOpen.setOnClickListener(this);
        mBtnClose.setOnClickListener(this);
    }

    void initVariate(){
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d(TAG,"连接服务成功") ;
                //Note: 此处是重点,将iBinder强转为LocalBinder并获取Serv的引用
                Serv.LocalBinder localBinder = (Serv.LocalBinder) iBinder;
                mServ = localBinder.getService() ;
                mServ.setCallback(new ICallback() {
                    @Override
                    public void infoOnChanged(String oldInfo, String newInfo) {
                        Log.d(TAG,"old:" + oldInfo+ "new：" +newInfo);
                        Toast.makeText(getApplication(),"old" + oldInfo,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onMinTimeChanged(int oldMinTime, int newMinTime) {
                        Toast.makeText(getApplicationContext(),"oldMinTime：" +oldMinTime+ "newMinTime:" + newMinTime,Toast.LENGTH_LONG).show();
                    }
                });
                mBound = true ;
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mBound = false ;
                mServ = null ;
                Log.d(TAG,"service  disconnected") ;
            }
        } ;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBind:
                if (!mBound) {
                    //未绑定时
                    Log.i(TAG, "btn绑定service");
                    Intent service = new Intent(this, Serv.class) ;
                    bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
                    mBtnBind.setText("unbind");
                } else {
                    //已绑定时
                    Log.i(TAG,"btn取消绑定service");
                    unbindService(mServiceConnection);
                    mBtnBind.setText("bind");
                    mBound = false ;
                    mServ = null ;
                }
                break;
            case R.id.btnshow:
                if (mBound) {
                    //只有绑定了service才能执行此方法
                    //Note:  注意点:客户端调用服务器端的方法
//                    mServ.methodForClient(this, "你好，客户端调用了服务器的方法");
                    mServ.setMinTime(1000000);
                    if (mToolTipView != null) {
                        removeToolTipView();
                    }
                } else {
                    //未绑定service时提示
                    if (mToolTipView == null) {
                        addUnbindToolTip(mBtnshow);
                    }
                    Log.w(TAG, "没有绑定service");
                }
                break;
            case R.id.btnOpen:
                if (mServ != null) {
                    mServ.openNoti();
                } else {
                    if (mToolTipView == null)
                        addUnbindToolTip(mBtnOpen);
                }
                break;
            case R.id.btnClose:
                if (mServ != null) {
                    mServ.closeNoti();
                } else {
                    if (mToolTipView == null)
                        addUnbindToolTip(mBtnClose);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onToolTipViewClicked(ToolTipView toolTipView) {
        if (toolTipView == mToolTipView) {
            removeToolTipView();
            Log.w(TAG,"移除了mToolTipView") ;
        }
    }
}