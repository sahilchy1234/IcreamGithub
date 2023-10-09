package com.roadpass.icecreamroll.viewutil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.afollestad.materialdialogs.MaterialDialog;
import com.roadpass.icecreamroll.R;
import com.roadpass.icecreamroll.model.Item;

public class DialogHelper {
    public static void editItemDialog(String title, String defaultText, Context c, final OnItemEditListener listener) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(c);
        builder.title(title)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .input(null, defaultText, (dialog, input) -> listener.itemLabel(input.toString())).show();
    }

    public static void alertDialog(Context context, String title, String msg, MaterialDialog.SingleButtonCallback onPositive) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title)
                .onPositive(onPositive)
                .content(msg)
                .negativeText(android.R.string.cancel)
                .positiveText(android.R.string.ok)
                .show();
    }

    public static void alertDialog(Context context, String title, String message, String positive, MaterialDialog.SingleButtonCallback onPositive) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title)
                .onPositive(onPositive)
                .content(message)
                .negativeText(android.R.string.cancel)
                .positiveText(positive)
                .show();
    }

    public static void selectDesktopActionDialog(final Context context, MaterialDialog.ListCallback callback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(R.string.action)
                .items(R.array.entries__desktop_actions)
                .itemsCallback(callback)
                .show();
    }

    public static void deletePackageDialog(Context context, Item item) {
        if (item.getType() == Item.Type.APP) {
            try {
                Uri packageURI = Uri.parse("package:" + item.getIntent().getComponent().getPackageName());
                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                context.startActivity(uninstallIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public interface OnItemEditListener {
        void itemLabel(String label);
    }
}
