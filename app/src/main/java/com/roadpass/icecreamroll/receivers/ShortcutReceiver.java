package com.roadpass.icecreamroll.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.Log;

import com.roadpass.icecreamroll.R;
import com.roadpass.icecreamroll.activity.HomeActivity;
import com.roadpass.icecreamroll.manager.Setup;
import com.roadpass.icecreamroll.model.App;
import com.roadpass.icecreamroll.model.Item;
import com.roadpass.icecreamroll.util.Definitions;
import com.roadpass.icecreamroll.util.Tool;

public class ShortcutReceiver extends BroadcastReceiver {

    public ShortcutReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() == null) return;

        String shortcutLabel = intent.getExtras().getString(Intent.EXTRA_SHORTCUT_NAME);
        Intent shortcutIntent = (Intent) intent.getExtras().get(Intent.EXTRA_SHORTCUT_INTENT);
        Drawable shortcutIcon = null;

        try {
            Parcelable parcelable = intent.getExtras().getParcelable(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
            if (parcelable instanceof Intent.ShortcutIconResource) {
                Intent.ShortcutIconResource iconResource = (Intent.ShortcutIconResource) parcelable;
                Resources resources = context.getPackageManager().getResourcesForApplication(iconResource.packageName);
                if (resources != null) {
                    int id = resources.getIdentifier(iconResource.resourceName, null, null);
                    shortcutIcon = resources.getDrawable(id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (shortcutIcon == null)
                shortcutIcon = new BitmapDrawable(context.getResources(), (Bitmap) intent.getExtras().getParcelable(Intent.EXTRA_SHORTCUT_ICON));
        }

        App app = Setup.appLoader().createApp(shortcutIntent);
        Item item;
        if (app != null) {
            item = Item.newAppItem(app);
        } else {
            item = Item.newShortcutItem(shortcutIntent, shortcutIcon, shortcutLabel);
        }
        Point preferredPos = HomeActivity.Companion.getLauncher().getDesktop().getPages().get(HomeActivity.Companion.getLauncher().getDesktop().getCurrentItem()).findFreeSpace();
        if (preferredPos == null) {
            Tool.toast(HomeActivity.Companion.getLauncher(), R.string.toast_not_enough_space);
        } else {
            item.setX(preferredPos.x);
            item.setY(preferredPos.y);
            HomeActivity._db.saveItem(item, HomeActivity.Companion.getLauncher().getDesktop().getCurrentItem(), Definitions.ItemPosition.Desktop);
            HomeActivity.Companion.getLauncher().getDesktop().addItemToPage(item, HomeActivity.Companion.getLauncher().getDesktop().getCurrentItem());
            Log.d(this.getClass().toString(), "shortcut installed");
        }
    }
}
