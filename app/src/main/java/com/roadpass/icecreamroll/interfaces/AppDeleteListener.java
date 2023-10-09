package com.roadpass.icecreamroll.interfaces;

import com.roadpass.icecreamroll.model.App;

import java.util.List;

public interface AppDeleteListener {
    boolean onAppDeleted(List<App> apps);
}
