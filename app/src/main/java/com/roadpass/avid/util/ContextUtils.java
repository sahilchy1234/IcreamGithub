package com.roadpass.avid.util;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import androidx.annotation.ColorInt;
import java.util.Locale;

public class ContextUtils {

    protected Context _context;

    public ContextUtils(Context context) {
        _context = context;
    }

    public Context context() {
        return _context;
    }

    public void freeContextRef() {
        _context = null;
    }


    public enum ResType {
        ID, BOOL, INTEGER, COLOR, STRING, ARRAY, DRAWABLE, PLURALS,
        ANIM, ATTR, DIMEN, LAYOUT, MENU, RAW, STYLE, XML,
    }

    public int getResId(final ResType resType, final String name) {
        try {
            return _context.getResources().getIdentifier(name, resType.name().toLowerCase(), _context.getPackageName());
        } catch (Exception e) {
            return 0;
        }
    }


    public Locale getLocaleByAndroidCode(String androidLC) {
        if (!TextUtils.isEmpty(androidLC)) {
            return androidLC.contains("-r")
                    ? new Locale(androidLC.substring(0, 2), androidLC.substring(4, 6)) // de-rAt
                    : new Locale(androidLC); // de
        }
        return Resources.getSystem().getConfiguration().locale;
    }

    public void setAppLanguage(final String androidLC) {
        Locale locale = getLocaleByAndroidCode(androidLC);
        locale = (locale != null && !androidLC.isEmpty()) ? locale : Resources.getSystem().getConfiguration().locale;
        setLocale(locale);
    }

    public ContextUtils setLocale(final Locale locale) {
        Configuration config = _context.getResources().getConfiguration();
        config.locale = (locale != null ? locale : Resources.getSystem().getConfiguration().locale);
        _context.getResources().updateConfiguration(config, null);
        Locale.setDefault(locale);
        return this;
    }

    public boolean shouldColorOnTopBeLight(@ColorInt final int colorOnBottomInt) {
        return 186 > (((0.299 * Color.red(colorOnBottomInt))
                + ((0.587 * Color.green(colorOnBottomInt))
                + (0.114 * Color.blue(colorOnBottomInt)))));
    }

    public static final InputFilter INPUTFILTER_FILENAME = new InputFilter() {
        public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
            if (src.length() < 1) return null;
            char last = src.charAt(src.length() - 1);
            String illegal = "|\\?*<\":>[]/'";
            if (illegal.indexOf(last) > -1) return src.subSequence(0, src.length() - 1);
            return null;
        }
    };


}


