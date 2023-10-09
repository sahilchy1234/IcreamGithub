package com.roadpass.avid.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class PermissionChecker {
    protected static final int CODE_PERMISSION_EXTERNAL_STORAGE = 4000;

    protected Activity _activity;

    public PermissionChecker(Activity activity) {
        _activity = activity;
    }

    public boolean doIfExtStoragePermissionGranted(String... optionalToastMessageForKnowingWhyNeeded) {
        if (ContextCompat.checkSelfPermission(_activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (optionalToastMessageForKnowingWhyNeeded != null && optionalToastMessageForKnowingWhyNeeded.length > 0 && optionalToastMessageForKnowingWhyNeeded[0] != null) {
                new AlertDialog.Builder(_activity)
                        .setMessage(optionalToastMessageForKnowingWhyNeeded[0])
                        .setCancelable(false)
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            if (android.os.Build.VERSION.SDK_INT >= 23) {
                                ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_PERMISSION_EXTERNAL_STORAGE);
                            }
                        })
                        .show();
                return false;
            }
            ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_PERMISSION_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

}
