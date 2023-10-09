package com.roadpass.avid.util;

import android.util.Log;

import java.io.IOException;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class postPopUpLauncherPixel {

    public postPopUpLauncherPixel(String key, String event, String uuid) throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .add("app_name", "launcher")
                .add("idfv", uuid)
                .add("pixel_name", "launcher_pixel")
                .add("pixel", "default_selector_popup")
                .build();
        Request request = new Request.Builder()
                .url("https://7ogim9mqkh.execute-api.us-east-1.amazonaws.com/default/app_central_pixels_save")
                .post(formBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("[postDefaultLauncherPixel]Unexpected code " + response);
            else if (response.isSuccessful())
                Log.d("[postDefaultLauncherPixel]", "postOpenLauncherPixel success");

            Log.d("[postDefaultLauncherPixel]", "request:"+request);
            Log.d("[postDefaultLauncherPixel]", "response:"+response.body());
            Log.d("[GUID123]", "guid:"+uuid);

            System.out.println(Objects.requireNonNull(response.body()).string());
        }
    }
}