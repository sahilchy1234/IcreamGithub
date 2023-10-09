package com.roadpass.icecreamroll.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.viewpager.widget.ViewPager;
import com.roadpass.icecreamroll.activity.HomeActivity;


public class CustomViewPager extends ViewPager {

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(HomeActivity._launcher.getAppDrawerController()._isOpen||HomeActivity._launcher.getDesktop()._inEditMode){
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(HomeActivity._launcher.getAppDrawerController()._isOpen||HomeActivity._launcher.getDesktop()._inEditMode){
            return false;
        }
        return super.onTouchEvent(event);
    }
    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if(v != this && v instanceof ViewPager) {
            int currentItem = ((ViewPager) v).getCurrentItem();
            int countItem = ((ViewPager) v).getAdapter().getCount();
            if((currentItem==(countItem-1) && dx<0) || (currentItem==0 && dx>0)){
                return false;
            }
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }



}