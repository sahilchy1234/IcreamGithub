package com.roadpass.icecreamroll.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.roadpass.icecreamroll.manager.Setup;

public class AppUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Setup.appLoader().onAppUpdated(context, intent);
    }
}
