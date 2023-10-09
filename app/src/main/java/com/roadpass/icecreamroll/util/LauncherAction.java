package com.roadpass.icecreamroll.util;

import static android.util.Log.println;
import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.roadpass.icecreamroll.R;
import com.roadpass.icecreamroll.activity.HomeActivity;
import com.roadpass.icecreamroll.browser.BrowserActivity;
import com.roadpass.icecreamroll.iceCreamGameLaunch.IceCreamGameEntryActivity;
import com.roadpass.icecreamroll.viewutil.DialogHelper;

import java.lang.reflect.Method;

public class LauncherAction {

    public enum Action {
        EditMinibar, SetWallpaper, LockScreen, LauncherSettings, VolumeDialog, DeviceSettings, AppDrawer, SearchBar, MobileNetworkSettings, ShowNotifications, TurnOffScreen, Camera, Browser, IceCreamGame
    }

    public static ActionDisplayItem[] actionDisplayItems = new ActionDisplayItem[]{
    };


    public static void RunAction(Action action, final Context context) {
        LauncherAction.RunAction(getActionItem(action), context);
    }

    @SuppressWarnings("WrongConstant")
    public static void RunAction(ActionDisplayItem action, final Context context) {
        Toast.makeText(context, "action: "+action._action+" label: "+action._label, Toast.LENGTH_SHORT).show();
        Log.d("action123", "[action123] action: "+action._action);
        Log.d("action123", "[action123] label: "+action._label);
        Log.d("action123", "[action123] id: "+action._id);
        Log.d("action123", "[action123] desc: "+action._description);
        Log.d("action123", "[action123] icon: "+action._icon);
        switch (action._action) {
            case SetWallpaper:
                context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_SET_WALLPAPER), context.getString(R.string.select_wallpaper)));
                break;
            case LockScreen:
                try {
                    ((DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE)).lockNow();
                } catch (Exception e) {
                    DialogHelper.alertDialog(context, context.getString(R.string.device_admin_title), context.getString(R.string.device_admin_summary), context.getString(R.string.enable), new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Tool.toast(context, context.getString(R.string.toast_device_admin_required));
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.DeviceAdminSettings"));
                            context.startActivity(intent);
                        }
                    });
                }
                break;
            case DeviceSettings:
                context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                break;
            case VolumeDialog:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    try {
                        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamVolume(AudioManager.STREAM_RING), AudioManager.FLAG_SHOW_UI);
                    } catch (Exception e) {
                        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                            context.startActivity(intent);
                        }
                    }
                } else {
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamVolume(AudioManager.STREAM_RING), AudioManager.FLAG_SHOW_UI);
                }
                break;
            case AppDrawer:
                HomeActivity._launcher.openAppDrawer();
                break;
            case SearchBar:
                break;
            case MobileNetworkSettings:
                context.startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                break;
            case ShowNotifications:
                try {
                    Object statusBarService = context.getSystemService("statusbar");
                    Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
                    Method statusBarExpand = statusBarManager.getMethod("expandNotificationsPanel");
                    statusBarExpand.invoke(statusBarService);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case TurnOffScreen:
                try {
                    int defaultTurnOffTime = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 60000);
                    Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 1000);
                    Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, defaultTurnOffTime);
                } catch (Exception e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                }
                break;
            case Camera:
                context.startActivity(new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA));
                break;
            case Browser:
                context.startActivity(new Intent(context, BrowserActivity.class));
                break;
            case IceCreamGame:
                context.startActivity(new Intent(context, IceCreamGameEntryActivity.class));
                break;
        }
    }

    public static ActionDisplayItem getActionItem(int position) {
        return getActionItem(Action.values()[position]);
    }

    public static ActionDisplayItem getActionItem(Action action) {
        return getActionItem(action.toString());
    }

    public static ActionDisplayItem getActionItem(String action) {
        if (action.equalsIgnoreCase("Browser")) {
            return new ActionDisplayItem(Action.Browser, "Browser", "browser", 0, 0);
        }
        for (ActionDisplayItem item : actionDisplayItems) {
            if (item._action.toString().equals(action)) {
                return item;
            }
        }
        return null;
    }

    public static class ActionDisplayItem {
        public Action _action;
        public String _label;
        public String _description;
        public int _icon;
        public int _id;

        public ActionDisplayItem(Action action, String label, String description, int icon, int id) {
            _action = action;
            _label = label;
            _description = description;
            _icon = icon;
            _id = id;
        }
    }
}
