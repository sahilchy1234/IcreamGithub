package com.roadpass.avid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class postOpenLauncherPixel {

    public postOpenLauncherPixel(String key, String event, String uuid) throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .add("app_name", "launcher")
                .add("idfv", uuid)
                .add("pixel_name", "launcher_pixel")
                .add("pixel", "opened")
                .build();
        Request request = new Request.Builder()
                .url("https://7ogim9mqkh.execute-api.us-east-1.amazonaws.com/default/app_central_pixels_save")
                .post(formBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("[postOpenLauncherPixel]Unexpected code " + response);
            else if (response.isSuccessful())
                Log.d("[postOpenLauncherPixel]", "postOpenLauncherPixel success");

            Log.d("[postOpenLauncherPixel]", "request:"+request);
            Log.d("[postOpenLauncherPixel]", "response:"+response.body());
            Log.d("[GUID123]", "guid:"+uuid);

            System.out.println(Objects.requireNonNull(response.body()).string());
        }
    }
}