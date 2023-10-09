package com.roadpass.avid.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SharedPreferencesPropertyBackend implements PropertyBackend<String, SharedPreferencesPropertyBackend> {
    protected static final String ARRAY_SEPARATOR = "%%%";
    protected static final String ARRAY_SEPARATOR_SUBSTITUTE = "§§§";
    private static String _debugLog = "";


    protected final SharedPreferences _prefApp;
    protected final String _prefAppName;
    protected final Context _context;


    public SharedPreferencesPropertyBackend(final Context context, final String prefAppName) {
        _context = context.getApplicationContext();
        _prefAppName = TextUtils.isEmpty(prefAppName) ?
                _context.getPackageName() + "_preferences" : prefAppName;
        _prefApp = _context.getSharedPreferences(_prefAppName, Context.MODE_PRIVATE);
    }


    public Context getContext() {
        return _context;
    }



    public void resetSettings() {
        resetSettings(_prefApp);
    }

    @SuppressLint("ApplySharedPref")
    public void resetSettings(final SharedPreferences pref) {
        pref.edit().clear().commit();
    }

    public SharedPreferences getDefaultPreferences() {
        return _prefApp;
    }

    private SharedPreferences gp(final SharedPreferences... pref) {
        return (pref != null && pref.length > 0 ? pref[0] : _prefApp);
    }


    public String rstr(@StringRes int stringKeyResourceId) {
        return _context.getString(stringKeyResourceId);
    }

    public int rcolor(@ColorRes int resColorId) {
        return ContextCompat.getColor(_context, resColorId);
    }

    public void setString(@StringRes int keyResourceId, String value, final SharedPreferences... pref) {
        gp(pref).edit().putString(rstr(keyResourceId), value).apply();
    }

    public void setString(String key, String value, final SharedPreferences... pref) {
        gp(pref).edit().putString(key, value).apply();
    }

    public String getString(@StringRes int keyResourceId, String defaultValue, final SharedPreferences... pref) {
        return gp(pref).getString(rstr(keyResourceId), defaultValue);
    }

    public String getString(@StringRes int keyResourceId, @StringRes int defaultValueResourceId, final SharedPreferences... pref) {
        return gp(pref).getString(rstr(keyResourceId), rstr(defaultValueResourceId));
    }

    public String getString(String key, String defaultValue, final SharedPreferences... pref) {
        try {
            return gp(pref).getString(key, defaultValue);
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    public String getString(@StringRes int keyResourceId, String defaultValue, @StringRes int keyResourceIdDefaultValue, final SharedPreferences... pref) {
        return getString(rstr(keyResourceId), rstr(keyResourceIdDefaultValue), pref);
    }

    private void setStringListOne(String key, List<String> values, final SharedPreferences pref) {
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            sb.append(ARRAY_SEPARATOR);
            sb.append(value.replace(ARRAY_SEPARATOR, ARRAY_SEPARATOR_SUBSTITUTE));
        }
        setString(key, sb.toString().replaceFirst(ARRAY_SEPARATOR, ""), pref);
    }

    private ArrayList<String> getStringListOne(String key, final SharedPreferences pref) {
        ArrayList<String> ret = new ArrayList<>();
        String value = getString(key, ARRAY_SEPARATOR).replace(ARRAY_SEPARATOR_SUBSTITUTE, ARRAY_SEPARATOR);
        if (value.equals(ARRAY_SEPARATOR) || TextUtils.isEmpty(value)) {
            return ret;
        }
        ret.addAll(Arrays.asList(value.split(ARRAY_SEPARATOR)));
        return ret;
    }

    public void setStringArray(String key, String[] values, final SharedPreferences... pref) {
        setStringListOne(key, Arrays.asList(values), gp(pref));
    }

    public void setStringList(@StringRes int keyResourceId, List<String> values, final SharedPreferences... pref) {
        setStringArray(rstr(keyResourceId), values.toArray(new String[values.size()]), pref);
    }
    public ArrayList<String> getStringList(@StringRes int keyResourceId, final SharedPreferences... pref) {
        return getStringListOne(rstr(keyResourceId), gp(pref));
    }

    public ArrayList<String> getStringList(String key, final SharedPreferences... pref) {
        return getStringListOne(key, gp(pref));
    }

    public void setInt(@StringRes int keyResourceId, int value, final SharedPreferences... pref) {
        gp(pref).edit().putInt(rstr(keyResourceId), value).apply();
    }

    public void setInt(String key, int value, final SharedPreferences... pref) {
        gp(pref).edit().putInt(key, value).apply();
    }

    public int getInt(@StringRes int keyResourceId, int defaultValue, final SharedPreferences... pref) {
        return getInt(rstr(keyResourceId), defaultValue, pref);
    }

    public int getInt(String key, int defaultValue, final SharedPreferences... pref) {
        try {
            return gp(pref).getInt(key, defaultValue);
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    public int getIntOfStringPref(@StringRes int keyResId, int defaultValue, final SharedPreferences... pref) {
        return getIntOfStringPref(rstr(keyResId), defaultValue, gp(pref));
    }

    public int getIntOfStringPref(String key, int defaultValue, final SharedPreferences... pref) {
        String strNum = getString(key, Integer.toString(defaultValue), gp(pref));
        return Integer.valueOf(strNum);
    }

    private void setIntListOne(String key, List<Integer> values, final SharedPreferences pref) {
        StringBuilder sb = new StringBuilder();
        for (Integer value : values) {
            sb.append(ARRAY_SEPARATOR);
            sb.append(value.toString());
        }
        setString(key, sb.toString().replaceFirst(ARRAY_SEPARATOR, ""), pref);
    }

    private ArrayList<Integer> getIntListOne(String key, final SharedPreferences pref) {
        ArrayList<Integer> ret = new ArrayList<>();
        String value = getString(key, ARRAY_SEPARATOR);
        if (value.equals(ARRAY_SEPARATOR)) {
            return ret;
        }
        for (String s : value.split(ARRAY_SEPARATOR)) {
            ret.add(Integer.parseInt(s));
        }
        return ret;
    }

    public ArrayList<Integer> getIntList(String key, final SharedPreferences... pref) {
        return getIntListOne(key, gp(pref));
    }


    public void setLong(String key, long value, final SharedPreferences... pref) {
        gp(pref).edit().putLong(key, value).apply();
    }
    public long getLong(String key, long defaultValue, final SharedPreferences... pref) {
        try {
            return gp(pref).getLong(key, defaultValue);
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }
    public void setFloat(String key, float value, final SharedPreferences... pref) {
        gp(pref).edit().putFloat(key, value).apply();
    }

    public float getFloat(String key, float defaultValue, final SharedPreferences... pref) {
        try {
            return gp(pref).getFloat(key, defaultValue);
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }
    public void setDouble(String key, double value, final SharedPreferences... pref) {
        setLong(key, Double.doubleToRawLongBits(value));
    }

    public double getDouble(String key, double defaultValue, final SharedPreferences... pref) {
        return Double.longBitsToDouble(getLong(key, Double.doubleToRawLongBits(defaultValue), gp(pref)));
    }

    public void setBool(@StringRes int keyResourceId, boolean value, final SharedPreferences... pref) {
        gp(pref).edit().putBoolean(rstr(keyResourceId), value).apply();
    }

    public void setBool(String key, boolean value, final SharedPreferences... pref) {
        gp(pref).edit().putBoolean(key, value).apply();
    }

    public boolean getBool(@StringRes int keyResourceId, boolean defaultValue, final SharedPreferences... pref) {
        return getBool(rstr(keyResourceId), defaultValue);
    }

    public boolean getBool(String key, boolean defaultValue, final SharedPreferences... pref) {
        try {
            return gp(pref).getBoolean(key, defaultValue);
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    @Override
    public String getString(String key, String defaultValue) {
        return getString(key, defaultValue, _prefApp);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return getInt(key, defaultValue, _prefApp);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return getLong(key, defaultValue, _prefApp);
    }

    @Override
    public boolean getBool(String key, boolean defaultValue) {
        return getBool(key, defaultValue, _prefApp);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return getFloat(key, defaultValue, _prefApp);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return getDouble(key, defaultValue, _prefApp);
    }

    @Override
    public ArrayList<Integer> getIntList(String key) {
        return getIntList(key, _prefApp);
    }

    @Override
    public ArrayList<String> getStringList(String key) {
        return getStringList(key, _prefApp);
    }

    @Override
    public SharedPreferencesPropertyBackend setString(String key, String value) {
        setString(key, value, _prefApp);
        return this;
    }

    @Override
    public SharedPreferencesPropertyBackend setInt(String key, int value) {
        setInt(key, value, _prefApp);
        return this;
    }

    @Override
    public SharedPreferencesPropertyBackend setLong(String key, long value) {
        setLong(key, value, _prefApp);
        return this;
    }

    @Override
    public SharedPreferencesPropertyBackend setBool(String key, boolean value) {
        setBool(key, value, _prefApp);
        return this;
    }

    @Override
    public SharedPreferencesPropertyBackend setFloat(String key, float value) {
        setFloat(key, value, _prefApp);
        return this;
    }

    @Override
    public SharedPreferencesPropertyBackend setDouble(String key, double value) {
        setDouble(key, value, _prefApp);
        return this;
    }

    @Override
    public SharedPreferencesPropertyBackend setIntList(String key, List<Integer> value) {
        setIntListOne(key, value, _prefApp);
        return this;
    }

    @Override
    public SharedPreferencesPropertyBackend setStringList(String key, List<String> value) {
        setStringListOne(key, value, _prefApp);
        return this;
    }

    public boolean contains(String key, final SharedPreferences... pref) {
        return gp(pref).contains(key);
    }


}
