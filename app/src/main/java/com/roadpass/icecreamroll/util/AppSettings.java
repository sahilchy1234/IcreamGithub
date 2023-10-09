package com.roadpass.icecreamroll.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.core.content.ContextCompat;

import com.roadpass.icecreamroll.AppObject;
import com.roadpass.icecreamroll.R;
import com.roadpass.icecreamroll.manager.Setup;
import com.roadpass.icecreamroll.widget.AppDrawerController;
import com.roadpass.icecreamroll.widget.PagerIndicator;

import com.roadpass.avid.preference.SharedPreferencesPropertyBackend;

import java.util.ArrayList;

public class AppSettings extends SharedPreferencesPropertyBackend {
    public AppSettings(Context context) {
        super(context, "app");
    }

    public static AppSettings get() {
        return new AppSettings(AppObject.get());
    }

    public int getDesktopColumnCount() {
        return getInt(R.string.pref_key__desktop_columns, 5);
    }

    public int getDesktopRowCount() {
        return getInt(R.string.pref_key__desktop_rows, 6);
    }

    public int getDesktopIndicatorMode() {
        return getIntOfStringPref(R.string.pref_key__desktop_indicator_style, PagerIndicator.Mode.DOTS);
    }

    public int getDesktopOrientationMode() {
        return getIntOfStringPref(R.string.pref_key__desktop_orientation, 0);
    }

    public Definitions.WallpaperScroll getDesktopWallpaperScroll() {
        int value = getIntOfStringPref(R.string.pref_key__desktop_wallpaper_scroll, 0);
        switch (value) {
            case 0:
            default:
                return Definitions.WallpaperScroll.Normal;
            case 1:
                return Definitions.WallpaperScroll.Inverse;
            case 2:
                return Definitions.WallpaperScroll.Off;
        }
    }

    public boolean getDesktopShowGrid() {
        return getBool(R.string.pref_key__desktop_show_grid, true);
    }

    public boolean getDesktopFullscreen() {
        return getBool(R.string.pref_key__desktop_fullscreen, false);
    }

    public boolean getDesktopShowIndicator() {
        return getBool(R.string.pref_key__desktop_show_position_indicator, true);
    }

    public boolean getDesktopShowLabel() {
        return getBool(R.string.pref_key__desktop_show_label, true);
    }

    public boolean getSearchBarEnable() {
        return getBool(R.string.pref_key__search_bar_enable, true);
    }

    public int getDesktopBackgroundColor() {
        return getInt(R.string.pref_key__desktop_background_color, Color.TRANSPARENT);
    }

    public int getDesktopInsetColor() {
        return getInt(R.string.pref_key__desktop_inset_color, Color.TRANSPARENT);
    }

    public int getDesktopFolderColor() {
        return getInt(R.string.pref_key__desktop_folder_color, Color.WHITE);
    }


    public int getDesktopIconSize() {
        return getIconSize();
    }

    public boolean getDockEnable() {
        return getBool(R.string.pref_key__dock_enable, true);
    }

    public int getDockColumnCount() {
        return getInt(R.string.pref_key__dock_columns, 5);
    }

    public int getDockRowCount() {
        return getInt(R.string.pref_key__dock_rows, 1);
    }

    public int getAppIconRowCount() {
        return getInt(R.string.pref_key__dock_rows, 3);
    }

    public boolean getDockShowLabel() {
        return getBool(R.string.pref_key__dock_show_label, false);
    }

    public int getDockColor() {
        return getInt(R.string.pref_key__dock_background_color, Color.TRANSPARENT);
    }

    public int getDockIconSize() {
        return getIconSize();
    }

    public int getDrawerColumnCount() {
        return getInt(R.string.pref_key__drawer_columns, 5);
    }

    public int getDrawerRowCount() {
        return getInt(R.string.pref_key__drawer_rows, 6);
    }

    public int getDrawerStyle() {
        return getIntOfStringPref(R.string.pref_key__drawer_style, AppDrawerController.Mode.GRID);
    }

    public boolean getDrawerShowCardView() {
        return getBool(R.string.pref_key__drawer_show_card_view, true);
    }

    public boolean getDrawerRememberPosition() {
        return getBool(R.string.pref_key__drawer_remember_position, true);
    }

    public boolean getDrawerShowIndicator() {
        return getBool(R.string.pref_key__drawer_show_position_indicator, true);
    }

    public boolean getDrawerShowLabel() {
        return getBool(R.string.pref_key__drawer_show_label, true);
    }

    public int getDrawerBackgroundColor() {
        return getInt(R.string.pref_key__drawer_background_color, rcolor(R.color.shade));
    }

    public int getDrawerCardColor() {
        return getInt(R.string.pref_key__drawer_card_color, Color.WHITE);
    }

    public int getDrawerLabelColor() {
        return getInt(R.string.pref_key__drawer_label_color, Color.WHITE);
    }

    public int getDrawerFastScrollColor() {
        return getInt(R.string.pref_key__drawer_fast_scroll_color, ContextCompat.getColor(Setup.appContext(), R.color.materialRed));
    }

    public boolean getGestureFeedback() {
        return getBool(R.string.pref_key__gesture_feedback, false);
    }

    public boolean getGestureDockSwipeUp() {
        return getBool(R.string.pref_key__gesture_quick_swipe, true);
    }

    public Object getGestureDoubleTap() {
        return getGesture(R.string.pref_key__gesture_double_tap);
    }

    public Object getGestureSwipeUp() {
        return getGesture(R.string.pref_key__gesture_swipe_up);
    }

    public Object getGestureSwipeDown() {
        return getGesture(R.string.pref_key__gesture_swipe_down);
    }

    public Object getGesturePinch() {
        return getGesture(R.string.pref_key__gesture_pinch_in);
    }

    public Object getGestureUnpinch() {
        return getGesture(R.string.pref_key__gesture_pinch_out);
    }

    public Object getGesture(int key) {
        String result = getString(key, "");
        Object gesture = LauncherAction.getActionItem(result);
        if (gesture == null) {
            gesture = Tool.getIntentFromString(result);
            if (AppManager.getInstance(_context).findApp((Intent) gesture) == null) gesture = null;
        }
        if (gesture == null) {
            setString(key, null);
        }
        return gesture;
    }


    public int getIconSize() {
        return getInt(R.string.pref_key__icon_size, 52);
    }

    public String getIconPack() {
        return getString(R.string.pref_key__icon_pack, "");
    }

    public boolean getNotificationStatus() {
        return getBool(R.string.pref_key__gesture_notifications, false);
    }
    public int getAnimationSpeed() {
        return 100 - getInt(R.string.pref_key__animation_speed, 80);
    }

    public String getLanguage() {
        return getString(R.string.pref_key__language, "");
    }

    public ArrayList<String> getHiddenAppsList() {
        return getStringList(R.string.pref_key__hidden_apps);
    }

    public void setHiddenAppsList(ArrayList<String> value) {
        setStringList(R.string.pref_key__hidden_apps, value);
    }

    public int getDesktopPageCurrent() {
        return getInt(R.string.pref_key__desktop_current_position, 0);
    }

    public void setDesktopPageCurrent(int value) {
        setInt(R.string.pref_key__desktop_current_position, value);
    }

    public boolean getDesktopLock() {
        return getBool(R.string.pref_key__desktop_lock, false);
    }

    public void setDesktopLock(boolean value) {
        setBool(R.string.pref_key__desktop_lock, value);
    }

    public boolean getAppRestartRequired() {
        return getBool(R.string.pref_key__queue_restart, false);
    }

    @SuppressLint("ApplySharedPref")
    public void setAppRestartRequired(boolean value) {
        _prefApp.edit().putBoolean(_context.getString(R.string.pref_key__queue_restart), value).commit();
    }

    @SuppressLint("ApplySharedPref")
    public void setAppShowIntro(boolean value) {
        _prefApp.edit().putBoolean(_context.getString(R.string.pref_key__show_intro), value).commit();
    }

    public boolean getAppFirstLaunch() {
        return getBool(R.string.pref_key__first_start, true);
    }

    @SuppressLint("ApplySharedPref")
    public void setAppFirstLaunch(boolean value) {
        _prefApp.edit().putBoolean(_context.getString(R.string.pref_key__first_start), value).commit();
    }
}
