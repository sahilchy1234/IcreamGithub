package com.roadpass.icecreamroll.interfaces;

import com.roadpass.icecreamroll.model.App;

import java.util.List;

public interface AppUpdateListener {
    boolean onAppUpdated(List<App> apps);
}
