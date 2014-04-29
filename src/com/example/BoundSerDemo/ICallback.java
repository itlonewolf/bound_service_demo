package com.example.BoundSerDemo;

/**
 * Created by yee on 4/25/14.
 */
public interface ICallback {
    /**
     *
     */
    public void infoOnChanged(String oldInfo,String newInfo) ;


    public void onMinTimeChanged(int oldMinTime, int newMinTime) ;

}
