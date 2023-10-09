package com.roadpass.icecreamroll.activity;


import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

import android.app.ActivityOptions;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.roadpass.avid.util.postDefaultLauncherPixel;
import com.roadpass.icecreamroll.BuildConfig;
import com.roadpass.icecreamroll.R;
import com.roadpass.icecreamroll.activity.homeparts.HpAppDrawer;
import com.roadpass.icecreamroll.activity.homeparts.HpDesktopOption;
import com.roadpass.icecreamroll.activity.homeparts.HpDragOption;
import com.roadpass.icecreamroll.activity.homeparts.HpInitSetup;
import com.roadpass.icecreamroll.browser.BrowserActivity;
import com.roadpass.icecreamroll.manager.Setup;
import com.roadpass.icecreamroll.model.App;
import com.roadpass.icecreamroll.model.Item;
import com.roadpass.icecreamroll.notifications.NotificationListener;
import com.roadpass.icecreamroll.receivers.AppUpdateReceiver;
import com.roadpass.icecreamroll.receivers.ShortcutReceiver;
import com.roadpass.icecreamroll.util.AppManager;
import com.roadpass.icecreamroll.util.AppSettings;
import com.roadpass.icecreamroll.util.CustomViewPager;
import com.roadpass.icecreamroll.util.DatabaseHelper;
import com.roadpass.icecreamroll.util.Definitions.ItemPosition;
import com.roadpass.icecreamroll.util.LauncherAction;
import com.roadpass.icecreamroll.util.LauncherAction.Action;
import com.roadpass.icecreamroll.util.Tool;
import com.roadpass.icecreamroll.viewutil.DialogHelper;
import com.roadpass.icecreamroll.viewutil.WidgetHost;
import com.roadpass.icecreamroll.widget.AppDrawerController;
import com.roadpass.icecreamroll.widget.AppItemView;
import com.roadpass.icecreamroll.widget.Desktop;
import com.roadpass.icecreamroll.widget.Desktop.OnDesktopEditListener;
import com.roadpass.icecreamroll.widget.DesktopOptionView;
import com.roadpass.icecreamroll.widget.Dock;
import com.roadpass.icecreamroll.widget.GroupPopupView;
import com.roadpass.icecreamroll.widget.ItemOptionView;
import com.roadpass.icecreamroll.widget.PagerIndicator;
import com.jakewharton.threetenabp.AndroidThreeTen;

import com.roadpass.avid.util.ContextUtils;
import com.unity3d.player.UnityPlayerActivity;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class HomeActivity extends FragmentActivity implements OnDesktopEditListener, MaxAdListener {
    public static final Companion Companion = new Companion();
    public static final int REQUEST_CREATE_APPWIDGET = 0x6475;
    public static final int REQUEST_PERMISSION_STORAGE = 0x3648;
    public static final int REQUEST_PICK_APPWIDGET = 0x2678;
    public static WidgetHost _appWidgetHost;
    public static AppWidgetManager _appWidgetManager;
    public static boolean ignoreResume;
    public static float _itemTouchX;
    public static float _itemTouchY;

    public static HomeActivity _launcher;
    public static DatabaseHelper _db;
    public static HpDesktopOption _desktopOption;

    private static final IntentFilter _appUpdateIntentFilter = new IntentFilter();
    private static final IntentFilter _shortcutIntentFilter = new IntentFilter();
    private static final IntentFilter _timeChangedIntentFilter = new IntentFilter();
    private AppUpdateReceiver _appUpdateReceiver;
    private ShortcutReceiver _shortcutReceiver;
    private BroadcastReceiver _timeChangedReceiver;
    CustomViewPager pager;

    private int cx;
    private int cy;

    FirebaseAnalytics analytics;

    public static final class Companion {
        private Companion() {
        }

        public final HomeActivity getLauncher() {
            return _launcher;
        }

        public final void setLauncher(@Nullable HomeActivity v) {
            _launcher = v;
        }
    }

    static {
        _timeChangedIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        _timeChangedIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        _timeChangedIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        _appUpdateIntentFilter.addDataScheme("package");
        _appUpdateIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        _appUpdateIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        _appUpdateIntentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        _shortcutIntentFilter.addAction("com.android.launcher.action.INSTALL_SHORTCUT");
    }


    public final Desktop getDesktop() {
        return findViewById(R.id.desktop);
    }

    public final Dock getDock() {
        return findViewById(R.id.dock);
    }

    public final AppDrawerController getAppDrawerController() {
        return findViewById(R.id.appDrawerController);
    }
    @Override
    public void onAdLoaded(MaxAd ad) {
        // Your implementation for when an ad is loaded
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {
        // Your implementation for when an ad is displayed
    }

    @Override
    public void onAdHidden(MaxAd ad) {
        // Your implementation for when an ad is hidden
    }

    @Override
    public void onAdClicked(MaxAd ad) {
        // Your implementation for when an ad is clicked
    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        // Your implementation for when an ad load fails
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        // Your implementation for when an ad display fails
    }
    public final GroupPopupView getGroupPopup() {
        return findViewById(R.id.groupPopup);
    }

    public final EditText getSearchBar() {
        return findViewById(R.id.search_text);
    }

    public final View getBackground() {
        return findViewById(R.id.background_frame);
    }

    public final PagerIndicator getDesktopIndicator() {
        return findViewById(R.id.desktopIndicator);
    }

    public final DesktopOptionView getDesktopOptionView() {
        return findViewById(R.id.desktop_option);
    }

    public final ItemOptionView getItemOptionView() {
        return findViewById(R.id.item_option);
    }


    public final View getStatusView() {
        return findViewById(R.id.status_frame);
    }

    public final View getNavigationView() {
        return findViewById(R.id.navigation_frame);
    }
    private static View.OnClickListener globalClickListener;

    public static void setOnGlobalClickListener(View.OnClickListener listener) {
        globalClickListener = listener;
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Companion.setLauncher(this);
        AndroidThreeTen.init(this);

        AppSettings appSettings = AppSettings.get();

        ContextUtils contextUtils = new ContextUtils(getApplicationContext());
        contextUtils.setAppLanguage(appSettings.getLanguage());
        super.onCreate(savedInstanceState);
        if (!Setup.wasInitialised()) {
            Setup.init(new HpInitSetup(this));
        }

        Companion.setLauncher(this);
        _db = Setup.dataManager();

        setContentView(getLayoutInflater().inflate(R.layout.activity_home, null));

        if (VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(1536);
        }

        init();

        checkIfDefaultLauncher();

    }

    private void checkIfDefaultLauncher() {
        PackageManager localPackageManager = getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        String str = localPackageManager
                .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .activityInfo
                .packageName;

        Bundle params = new Bundle();
        if (Objects.equals(str, "com.roadpass.icecreamroll")){
            params.putString("default_set", str);
            analytics.logEvent("default_app", params);
            sendPixelEventForDefault();
        } else {
            params.putString("no_default_set", str);
            analytics.logEvent("non_default_app", params);
        }

    }

    private String guid() {
        return getDefaultSharedPreferences(this).getString("uuid","error");
    }

    private void sendPixelEventForDefault() {
        try {
            new postDefaultLauncherPixel("IceCreamRoll", "idfv", guid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private MaxAdView adView;

    private void init() {

        AppLovinSdk.getInstance( HomeActivity.this ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( HomeActivity.this  , new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {
                // AppLovin SDK is initialized, start loading ads
            }
        } );


        adView= findViewById(R.id.adView);
        adView.loadAd();

        _appWidgetManager = AppWidgetManager.getInstance(this);
        _appWidgetHost = new WidgetHost(getApplicationContext(), R.id.app_widget_host);
        _appWidgetHost.startListening();

        CardView iceCreamRoll = (CardView) findViewById(R.id.icecreamroll);
        CardView y8 = (CardView) findViewById(R.id.y8);
        CardView poki = (CardView) findViewById(R.id.poki);
        CardView games10001 = (CardView) findViewById(R.id.games10001);
        CardView agame = (CardView) findViewById(R.id.agame);
        CardView arkadium = (CardView) findViewById(R.id.arkadium);
        CardView crazygames = (CardView) findViewById(R.id.crazygames);
        CardView freearcade = (CardView) findViewById(R.id.freearcade);
        CardView freeonlinegames = (CardView) findViewById(R.id.freeonlinegames);
        CardView gamehouse = (CardView) findViewById(R.id.gamehouse);
        CardView pogo = (CardView) findViewById(R.id.pogo);

        TextView terms = (TextView)findViewById(R.id.TnC);
        TextView privacy = (TextView)findViewById(R.id.privacy);

        HpDragOption hpDragOption = new HpDragOption();
        View findViewById = findViewById(R.id.leftDragHandle);
        View findViewById2 = findViewById(R.id.rightDragHandle);
        hpDragOption.initDragNDrop(this, findViewById, findViewById2, getItemOptionView());

        analytics = FirebaseAnalytics.getInstance(this);

        registerBroadcastReceiver();

        initAppManager();
        initSettings();
        initViews();
        WizardPagerAdapter adapter = new WizardPagerAdapter();
        pager = findViewById(R.id.pager1);
        pager.setAdapter(adapter);
        pager.setCurrentItem(1);

        privacy.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/1chJhdAQSWDi9YIRbo30nobi2VikFHGtEJA6YYJ8Ml0c/view"));
                startActivity(browserIntent);
            }
        });

        terms.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/1tSUJi1WmxOomSRhZGkGK5Fi5HW1JjVd1wfoKI_FvnTw/view"));
                startActivity(browserIntent);
            }
        });

        iceCreamRoll.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this, UnityPlayerActivity.class);
                startActivity(intent);
            }
        });

        y8.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://he.y8.com/";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(HomeActivity.this, Uri.parse(url));
            }
        });

        poki.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.poki.com";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(HomeActivity.this, Uri.parse(url));
            }
        });

        games10001.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.1001games.com";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(HomeActivity.this, Uri.parse(url));
            }
        });

        agame.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.agame.com";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(HomeActivity.this, Uri.parse(url));
            }
        });

        arkadium.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.arkadium.com";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(HomeActivity.this, Uri.parse(url));
            }
        });

        crazygames.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.crazygames.com";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(HomeActivity.this, Uri.parse(url));
            }
        });

        freearcade.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.freearcade.com";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(HomeActivity.this, Uri.parse(url));
            }
        });

        freeonlinegames.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.freeonlinegames.com";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(HomeActivity.this, Uri.parse(url));
            }
        });

        gamehouse.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.gamehouse.com";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(HomeActivity.this, Uri.parse(url));
            }
        });

        pogo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.pogo.com";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(HomeActivity.this, Uri.parse(url));
            }
        });

    }

    protected void initAppManager() {
        if (Setup.appSettings().getAppFirstLaunch()) {
            Setup.appSettings().setAppFirstLaunch(false);
            Setup.appSettings().setAppShowIntro(false);
            Item appDrawerBtnItem = Item.newActionItem(8);
            appDrawerBtnItem._x = 2;
            _db.saveItem(appDrawerBtnItem, 0, ItemPosition.Dock);
        }
        Setup.appLoader().addUpdateListener(it -> {
            getDesktop().initDesktop();
            getDock().initDock();
            return false;
        });
        Setup.appLoader().addDeleteListener(apps -> {
            getDesktop().initDesktop();
            getDock().initDock();
            return false;
        });
        AppManager.getInstance(this).init();

    }


    class WizardPagerAdapter extends PagerAdapter {

        public Object instantiateItem(ViewGroup collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.container2;
                    break;

                case 1:
                    resId = R.id.container1;
                    View view = findViewById(resId);
                    return view;

            }
            return findViewById(resId);
        }


        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

    protected void initViews() {
        getAppDrawerController().init();
        getDock().setHome(this);

        getDesktop().setDesktopEditListener(this);
        getDesktop().setPageIndicator(getDesktopIndicator());
        getDesktopIndicator().setMode(Setup.appSettings().getDesktopIndicatorMode());

        AppSettings appSettings = Setup.appSettings();

        _desktopOption = new HpDesktopOption(this);

        getDesktopOptionView().setDesktopOptionViewListener(_desktopOption);
        getDesktopOptionView().postDelayed(new Runnable() {
            @Override
            public void run() {

                getDesktopOptionView().updateLockIcon(appSettings.getDesktopLock());
            }
        }, 100);

        getSearchBar().setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BrowserActivity.class);
            startActivity(intent);
        });

        getDesktop().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            public void onPageSelected(int position) {
                getDesktopOptionView().updateHomeIcon(appSettings.getDesktopPageCurrent() == position);

            }

            public void onPageScrollStateChanged(int state) {

            }
        });

        new HpAppDrawer(this, findViewById(R.id.appDrawerIndicator)).initAppDrawer(getAppDrawerController());

    }

    public final void initSettings() {
        updateHomeLayout();

        AppSettings appSettings = Setup.appSettings();
        if (appSettings.getDesktopFullscreen()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        getDesktop().setBackgroundColor(appSettings.getDesktopBackgroundColor());
        getDock().setBackgroundColor(appSettings.getDockColor());

        getStatusView().setBackgroundColor(appSettings.getDesktopInsetColor());
        getNavigationView().setBackgroundColor(appSettings.getDesktopInsetColor());


    }

    private void registerBroadcastReceiver() {
        _appUpdateReceiver = new AppUpdateReceiver();
        _shortcutReceiver = new ShortcutReceiver();
        _timeChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_TIME_TICK)
                        || intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)
                        || intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) {
                }
            }
        };

        registerReceiver(_appUpdateReceiver, _appUpdateIntentFilter);
        registerReceiver(_shortcutReceiver, _shortcutIntentFilter);
        registerReceiver(_timeChangedReceiver, _timeChangedIntentFilter);
    }

    public final void onStartApp(@NonNull Context context, @NonNull App app, @Nullable View view) {

        if (BuildConfig.APPLICATION_ID.equals(app._packageName) && app._className.equals("com.roadpass.icecreamroll.IceCreamGameLaunch.iceCreamGameLaunch.IceCreamGameEntryActivity")) {
            LauncherAction.RunAction(Action.IceCreamGame, context);
            return;
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && app._userHandle != null) {
                LauncherApps launcherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
                List<LauncherActivityInfo> activities = launcherApps.getActivityList(app.getPackageName(), app._userHandle);
                for (int intent = 0; intent < activities.size(); intent++) {
                    if (app.getComponentName().equals(activities.get(intent).getComponentName().toString()))
                    launcherApps.startMainActivity(activities.get(intent).getComponentName(), app._userHandle, null, getActivityAnimationOpts(view));
                }
            } else {
                Intent intent = Tool.getIntentFromApp(app);
                context.startActivity(intent, getActivityAnimationOpts(view));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Tool.toast(context, R.string.toast_app_uninstalled);
        }
    }

    private Bundle getActivityAnimationOpts(View view) {
        Bundle bundle = null;
        if (view == null) {
            return null;
        }

        ActivityOptions options = null;
        if (VERSION.SDK_INT >= 23) {
            int left = 0;
            int top = 0;
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            if (view instanceof AppItemView) {
                width = (int) ((AppItemView) view).getIconSize();
                left = (int) ((AppItemView) view).getDrawIconLeft();
                top = (int) ((AppItemView) view).getDrawIconTop();
            }
            options = ActivityOptions.makeClipRevealAnimation(view, left, top, width, height);
        } else if (VERSION.SDK_INT < 21) {
            options = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        if (options != null) {
            bundle = options.toBundle();
        }

        return bundle;
    }

    public void onStartDesktopEdit() {
        Tool.visibleViews(100, getDesktopOptionView());
        updateDesktopIndicator(false);
        updateDock(false);
        updateSearchBar(false);
        Tool.invisibleViews(100, findViewById(R.id.textclock));
        Tool.invisibleViews(100, findViewById(R.id.textclock_dt));
    }

    public void onFinishDesktopEdit() {
        Tool.invisibleViews(100, getDesktopOptionView());
        updateDesktopIndicator(true);
        updateDock(true);
        updateSearchBar(true);
        Tool.visibleViews(100, findViewById(R.id.textclock));
        Tool.visibleViews(100, findViewById(R.id.textclock_dt));
    }


    public final void updateDock(boolean show) {
        AppSettings appSettings = Setup.appSettings();
        if (appSettings.getDockEnable() && show) {
            Tool.visibleViews(100, getDock());
        } else {
            if (appSettings.getDockEnable()) {
                Tool.invisibleViews(100, getDock());
            } else {
                Tool.goneViews(100, getDock());
            }
        }
    }

    public final void updateTime(boolean show) {
        if (show) {
            Tool.visibleViews(100, findViewById(R.id.textclock));
            Tool.visibleViews(100, findViewById(R.id.textclock_dt));
        } else {
            Tool.invisibleViews(100, findViewById(R.id.textclock));
            Tool.invisibleViews(100, findViewById(R.id.textclock_dt));
        }
    }

    public final void updateSearchBar(boolean show) {
        AppSettings appSettings = Setup.appSettings();

        if (appSettings.getSearchBarEnable() && show) {
            Tool.visibleViews(100, getSearchBar());
        } else {
            if (appSettings.getSearchBarEnable()) {
                Tool.invisibleViews(100, getSearchBar());
            } else {
                Tool.goneViews(100, getSearchBar());
            }
        }
    }

    public final void updateDesktopIndicator(boolean show) {
        AppSettings appSettings = Setup.appSettings();
        if (appSettings.getDesktopShowIndicator() && show) {
            Tool.visibleViews(100, getDesktopIndicator());
        } else {
            Tool.goneViews(100, getDesktopIndicator());
        }
    }


    public final void updateHomeLayout() {
        updateSearchBar(true);
        updateDock(true);
        updateDesktopIndicator(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK_APPWIDGET) {

                _desktopOption.configureWidget(data);
            } else if (requestCode == REQUEST_CREATE_APPWIDGET) {
                _desktopOption.createWidget(data);
            }
        } else if (resultCode == RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra("appWidgetId", -1);
            if (appWidgetId != -1) {
                _appWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            pager.setCurrentItem(1, true);
        }
        handleLauncherResume();
    }

    @Override
    protected void onStart() {
        _appWidgetHost.startListening();
        _launcher = this;
        super.onStart();
    }

    private void checkNotificationPermissions() {
        Set<String> appList = NotificationManagerCompat.getEnabledListenerPackages(this);
        for (String app : appList) {
            if (app.equals(getPackageName())) {
                Intent i = new Intent(NotificationListener.UPDATE_NOTIFICATIONS_ACTION);
                i.setPackage(getPackageName());
                i.putExtra(NotificationListener.UPDATE_NOTIFICATIONS_COMMAND, NotificationListener.UPDATE_NOTIFICATIONS_UPDATE);
                sendBroadcast(i);
                return;
            }
        }

        DialogHelper.alertDialog(this, getString(R.string.notification_title), getString(R.string.notification_summary), getString(R.string.enable), new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Tool.toast(HomeActivity.this, getString(R.string.toast_notification_permission_required));
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        _appWidgetHost.startListening();
        _launcher = this;
        AppSettings appSettings = Setup.appSettings();
        if (appSettings.getAppRestartRequired()) {
            appSettings.setAppRestartRequired(false);
            recreate();
            return;
        }

        if (appSettings.getNotificationStatus()) {
            checkNotificationPermissions();
        }
        if (appSettings.getDesktopOrientationMode() == 2) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (appSettings.getDesktopOrientationMode() == 1) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        handleLauncherResume();


        View rootView = findViewById(android.R.id.content);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (globalClickListener != null) {
                    globalClickListener.onClick(view);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        _appWidgetHost.stopListening();
        _launcher = null;

        unregisterReceiver(_appUpdateReceiver);
        unregisterReceiver(_shortcutReceiver);
        unregisterReceiver(_timeChangedReceiver);
        super.onDestroy();
    }
    protected void onPause() {
        super.onPause();
        View rootView = findViewById(android.R.id.content);
        rootView.setOnClickListener(null);
    }

    private void handleLauncherResume() {
        if (ignoreResume) {
            ignoreResume = false;
        } else {
            getGroupPopup().collapse();
            getItemOptionView().collapse();
            if (getDesktop().getInEditMode()) {
                getDesktop().getCurrentPage().performClick();
            } else if (getAppDrawerController().getDrawer().getVisibility() == View.VISIBLE) {
                closeAppDrawer();
            }
            if (getDesktop().getCurrentItem() != 0) {
                AppSettings appSettings = Setup.appSettings();
                getDesktop().setCurrentItem(appSettings.getDesktopPageCurrent());
            }
        }
    }

    public final void openAppDrawer() {
        openAppDrawer(null, 0, 0);
    }

    public final void openAppDrawer(View view, int x, int y) {
        if (!(x > 0 && y > 0) && view != null) {
            int[] pos = new int[2];
            view.getLocationInWindow(pos);
            cx = pos[0];
            cy = pos[1];

            cx += view.getWidth() / 2f;
            cy += view.getHeight() / 2f;
            if (view instanceof AppItemView) {
                AppItemView appItemView = (AppItemView) view;
                if (appItemView != null && appItemView.getShowLabel()) {
                    cy -= Tool.dp2px(14) / 2f;
                }
            }
            cy -= getAppDrawerController().getPaddingTop();
        } else {
            cx = x;
            cy = y;
        }
        getAppDrawerController().open(cx, cy);
    }

    public final void closeAppDrawer() {
        getAppDrawerController().close(cx, cy);
    }
}
