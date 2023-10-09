package com.roadpass.icecreamroll.activity.homeparts;

import com.roadpass.icecreamroll.activity.HomeActivity;
import com.roadpass.icecreamroll.manager.Setup;
import com.roadpass.icecreamroll.util.Tool;
import com.roadpass.icecreamroll.widget.AppDrawerController;
import com.roadpass.icecreamroll.widget.PagerIndicator;

import com.roadpass.avid.util.Callback;

public class HpAppDrawer implements Callback.a2<Boolean, Boolean> {
    private HomeActivity _homeActivity;
    private PagerIndicator _appDrawerIndicator;

    public HpAppDrawer(HomeActivity homeActivity, PagerIndicator appDrawerIndicator) {
        _homeActivity = homeActivity;
        _appDrawerIndicator = appDrawerIndicator;
    }

    public void initAppDrawer(AppDrawerController appDrawerController) {
        appDrawerController.setCallBack(this);
    }

    @Override
    public void callback(Boolean openingOrClosing, Boolean startOrEnd) {
        if (openingOrClosing) {
            if (startOrEnd) {
                _homeActivity.getAppDrawerController().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Tool.visibleViews(200, _appDrawerIndicator);
                        Tool.invisibleViews(200, _homeActivity.getDesktop());
                        _homeActivity.updateDesktopIndicator(false);
                        _homeActivity.updateDock(false);
                        _homeActivity.updateSearchBar(false);
                        _homeActivity.updateTime(false);
                    }
                }, 100);
            }
        } else {
            if (startOrEnd) {
                Tool.invisibleViews(200, _appDrawerIndicator);
                Tool.visibleViews(200, _homeActivity.getDesktop());
                _homeActivity.updateDesktopIndicator(true);
                _homeActivity.updateDock(true);
                _homeActivity.updateSearchBar(true);
                _homeActivity.updateTime(true);
            } else {
                if (!Setup.appSettings().getDrawerRememberPosition()) {
                    _homeActivity.getAppDrawerController().reset();
                }
            }
        }
    }
}
