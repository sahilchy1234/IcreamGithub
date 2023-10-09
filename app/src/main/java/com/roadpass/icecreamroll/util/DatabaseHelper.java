package com.roadpass.icecreamroll.util;

import static com.roadpass.icecreamroll.util.Definitions.ItemPosition.Desktop;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.roadpass.icecreamroll.R;
import com.roadpass.icecreamroll.iceCreamGameLaunch.IceCreamGameEntryActivity;
import com.roadpass.icecreamroll.manager.Setup;
import com.roadpass.icecreamroll.model.App;
import com.roadpass.icecreamroll.model.Item;
import com.roadpass.icecreamroll.util.Definitions.ItemState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_HOME = "home.db";
    private static final String TABLE_HOME = "home";

    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_LABEL = "label";
    private static final String COLUMN_X_POS = "x";
    private static final String COLUMN_Y_POS = "y";
    private static final String COLUMN_DATA = "data";
    private static final String COLUMN_PAGE = "page";
    private static final String COLUMN_DESKTOP = "desktop";
    private static final String COLUMN_STATE = "state";

    private static final String SQL_DELETE = "DROP TABLE IF EXISTS ";
    private static final String SQL_QUERY = "SELECT * FROM ";
    private static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_HOME + " ("
                    + COLUMN_TIME + " INTEGER PRIMARY KEY,"
                    + COLUMN_TYPE + " VARCHAR,"
                    + COLUMN_LABEL + " VARCHAR,"
                    + COLUMN_X_POS + " INTEGER,"
                    + COLUMN_Y_POS + " INTEGER,"
                    + COLUMN_DATA + " VARCHAR,"
                    + COLUMN_PAGE + " INTEGER,"
                    + COLUMN_DESKTOP + " INTEGER,"
                    + COLUMN_STATE + " INTEGER)";

    protected SQLiteDatabase _db;
    protected Context _context;

    public DatabaseHelper(Context c) {
        super(c, DATABASE_HOME, null, 1);
        _db = getWritableDatabase();
        _context = c;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // discard the data and start over
        db.execSQL(SQL_DELETE + TABLE_HOME);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void createItem(Item item, int page, Definitions.ItemPosition itemPosition) {
        Log.i(this.getClass().getName(), String.format("createItem: %s (ID: %d)", item.getLabel(), item.getId()));
        ContentValues itemValues = new ContentValues();
        itemValues.put(COLUMN_TIME, item.getId());
        itemValues.put(COLUMN_TYPE, item.getType().toString());
        itemValues.put(COLUMN_LABEL, item.getLabel());
        itemValues.put(COLUMN_X_POS, item.getX());
        itemValues.put(COLUMN_Y_POS, item.getY());

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.setClass(_context, IceCreamGameEntryActivity.class);
        Item itemData = new Item((ContextCompat.getDrawable(_context, R.drawable.ic_launch)), "Ice Cream Roll", Item.Type.APP, getApplicationUid(_context, _context.getPackageName())
                , Desktop, 4, 5, intent, new ArrayList<>(), new ArrayList<>(), 0, 0, 1, 1);
        createAppIconItem(itemData, 0, Desktop);
        String concat = "";
        switch (item.getType()) {
            case APP:
            case SHORTCUT:
                Tool.saveIcon(_context, Tool.drawableToBitmap(item.getIcon()), Integer.toString(item.getId()));
                itemValues.put(COLUMN_DATA, Tool.getIntentAsString(item.getIntent()));
                break;
            case GROUP:
                for (Item tmp : item.getItems()) {
                    if (tmp != null) {
                        concat += tmp.getId() + Definitions.DELIMITER;
                    }
                }
                itemValues.put(COLUMN_DATA, concat);
                break;
            case ACTION:
                itemValues.put(COLUMN_DATA, item.getActionValue());
                break;
            case WIDGET:
                concat = item.getWidgetValue()
                        + Definitions.DELIMITER
                        + item.getSpanX()
                        + Definitions.DELIMITER
                        + item.getSpanY();
                itemValues.put(COLUMN_DATA, concat);
                break;
        }
        itemValues.put(COLUMN_PAGE, page);
        itemValues.put(COLUMN_DESKTOP, itemPosition.ordinal());
        // item will always be visible when first added
        itemValues.put(COLUMN_STATE, 1);
        _db.insert(TABLE_HOME, null, itemValues);
    }
    public void createAppIconItem(Item item, int page, Definitions.ItemPosition itemPosition) {
        Log.i(this.getClass().getName(), String.format("createItem: %s (ID: %d)", item.getLabel(), item.getId()));
        ContentValues itemValues = new ContentValues();
        itemValues.put(COLUMN_TIME, item.getId());
        itemValues.put(COLUMN_TYPE, item.getType().toString());
        itemValues.put(COLUMN_LABEL, item.getLabel());
        itemValues.put(COLUMN_X_POS, item.getX());
        itemValues.put(COLUMN_Y_POS, item.getY());

        String concat = "";
        switch (item.getType()) {
            case APP:
            case SHORTCUT:
                Tool.saveIcon(_context, Tool.drawableToBitmap(item.getIcon()), Integer.toString(item.getId()));
                itemValues.put(COLUMN_DATA, Tool.getIntentAsString(item.getIntent()));
                break;
            case GROUP:
                for (Item tmp : item.getItems()) {
                    if (tmp != null) {
                        concat += tmp.getId() + Definitions.DELIMITER;
                    }
                }
                itemValues.put(COLUMN_DATA, concat);
                break;
            case ACTION:
                itemValues.put(COLUMN_DATA, item.getActionValue());
                break;
            case WIDGET:
                concat = item.getWidgetValue()
                        + Definitions.DELIMITER
                        + item.getSpanX()
                        + Definitions.DELIMITER
                        + item.getSpanY();
                itemValues.put(COLUMN_DATA, concat);
                break;
        }
        itemValues.put(COLUMN_PAGE, page);
        itemValues.put(COLUMN_DESKTOP, itemPosition.ordinal());

        // item will always be visible when first added
        itemValues.put(COLUMN_STATE, 1);
        _db.insert(TABLE_HOME, null, itemValues);
    }

    public void saveItem(Item item) {
        updateItem(item);
    }

    public void saveItem(Item item, Definitions.ItemState state) {
        updateItem(item, state);
    }

    public void saveItem(Item item, int page, Definitions.ItemPosition itemPosition) {
        String SQL_QUERY_SPECIFIC = SQL_QUERY + TABLE_HOME + " WHERE " + COLUMN_TIME + " = " + item.getId();
        Cursor cursor = _db.rawQuery(SQL_QUERY_SPECIFIC, null);
        if (cursor.getCount() == 0) {
            createItem(item, page, itemPosition);
        } else if (cursor.getCount() == 1) {
            updateItem(item, page, itemPosition);
        }
    }

    public void deleteItem(Item item, boolean deleteSubItems) {

        if (deleteSubItems && item.getType() == Item.Type.GROUP) {
            for (Item i : item.getGroupItems()) {
                deleteItem(i, deleteSubItems);
            }
        }


        _db.delete(TABLE_HOME, COLUMN_TIME + " = ?", new String[]{String.valueOf(item.getId())});
    }

    public List<List<Item>> getDesktop() {
        String SQL_QUERY_DESKTOP = SQL_QUERY + TABLE_HOME;
        Cursor cursor = _db.rawQuery(SQL_QUERY_DESKTOP, null);
        List<List<Item>> desktop = new ArrayList<>();
        if (cursor.moveToFirst()) {
            int pageColumnIndex = cursor.getColumnIndex(COLUMN_PAGE);
            int desktopColumnIndex = cursor.getColumnIndex(COLUMN_DESKTOP);
            int stateColumnIndex = cursor.getColumnIndex(COLUMN_STATE);
            do {
                int page = Integer.parseInt(cursor.getString(pageColumnIndex));
                int desktopVar = Integer.parseInt(cursor.getString(desktopColumnIndex));
                int stateVar = Integer.parseInt(cursor.getString(stateColumnIndex));
                while (page >= desktop.size()) {
                    desktop.add(new ArrayList<>());
                }
                if (desktopVar == Desktop.ordinal() && stateVar == ItemState.Visible.ordinal()) {
                    desktop.get(page).add(getSelection(cursor));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return desktop;
    }

    public static int getApplicationUid(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return appInfo.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // Handle the case where the app is not installed.
            return -1; // Return a value indicating that the app was not found.
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Item> getDock() {
        AtomicInteger x = new AtomicInteger();
        List<Item> docks = AppManager.getInstance(_context).getAllApps(_context, true).stream().map(Item::newAppItem).collect(Collectors.toList());
        List<Item> filtered = docks.stream().filter(item -> item._label.equals("Phone") || item._label.equals("Messages") || item._label.equals("Camera") || item._label.equals("Browser")).collect(Collectors.toList());
        Item appDrawerBtnItem = Item.newActionItem(8);
        try{
            filtered.add(2,appDrawerBtnItem);

        }catch (Exception e){
            e.printStackTrace();
        }
        return filtered.stream().peek(item -> {
            item._x = x.get();
            x.getAndIncrement();

        }).collect(Collectors.toList());

    }

    public Item getItem(int id) {
        String SQL_QUERY_SPECIFIC = SQL_QUERY + TABLE_HOME + " WHERE " + COLUMN_TIME + " = " + id;
        Cursor cursor = _db.rawQuery(SQL_QUERY_SPECIFIC, null);
        Item item = null;
        if (cursor.moveToFirst()) {
            item = getSelection(cursor);
        }
        cursor.close();
        return item;
    }


    public void updateItem(Item item) {
        Log.i(this.getClass().getName(), String.format("updateItem: %s %d", item.getLabel(), item.getId()));

        ContentValues itemValues = new ContentValues();
        itemValues.put(COLUMN_LABEL, item.getLabel());
        itemValues.put(COLUMN_X_POS, item.getX());
        itemValues.put(COLUMN_Y_POS, item.getY());

        String concat = "";
        switch (item.getType()) {
            case APP:
            case SHORTCUT:
                Tool.saveIcon(_context, Tool.drawableToBitmap(item.getIcon()), Integer.toString(item.getId()));
                itemValues.put(COLUMN_DATA, Tool.getIntentAsString(item.getIntent()));
                break;
            case GROUP:
                for (Item tmp : item.getItems()) {
                    concat += tmp.getId() + Definitions.DELIMITER;
                }
                itemValues.put(COLUMN_DATA, concat);
                break;
            case ACTION:
                itemValues.put(COLUMN_DATA, item.getActionValue());
                break;
            case WIDGET:
                concat = item.getWidgetValue()
                        + Definitions.DELIMITER
                        + item.getSpanX()
                        + Definitions.DELIMITER
                        + item.getSpanY();
                itemValues.put(COLUMN_DATA, concat);
                break;
        }
        _db.update(TABLE_HOME, itemValues, COLUMN_TIME + " = " + item.getId(), null);
    }

    public void updateItem(Item item, Definitions.ItemState state) {
        Log.i(this.getClass().getName(), String.format("updateItem: %s %d", item.getLabel(), item.getId()));

        ContentValues itemValues = new ContentValues();
        itemValues.put(COLUMN_STATE, state.ordinal());

        _db.update(TABLE_HOME, itemValues, COLUMN_TIME + " = " + item.getId(), null);
    }

    public void updateItem(Item item, int page, Definitions.ItemPosition itemPosition) {
        Log.i(this.getClass().getName(), String.format("updateItem: %s %d", item.getLabel(), item.getId()));

        deleteItem(item, false);
        createItem(item, page, itemPosition);
    }

    private Item getSelection(Cursor cursor) {
        Item item = new Item();
        int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
        Item.Type type = Item.Type.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
        String label = cursor.getString(cursor.getColumnIndex(COLUMN_LABEL));
        int x = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_X_POS)));
        int y = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_Y_POS)));
        String data = cursor.getString(cursor.getColumnIndex(COLUMN_DATA));

        item.setId(id);
        item.setLabel(label);
        item.setX(x);
        item.setY(y);
        item.setType(type);

        String[] dataSplit;
        switch (type) {
            case APP: {
                item.setIntent(Tool.getIntentFromString(data));
                item.setShortcutInfo(Tool.getShortcutInfo(_context, item.getIntent().getComponent().getPackageName()));
                App app = Setup.get().getAppLoader().findItemApp(item);
                item.setIcon(app != null ? app.getIcon() : null);
                break;
            }
            case SHORTCUT: {
                item.setIntent(Tool.getIntentFromString(data));
                App app = Setup.get().getAppLoader().findItemApp(item);
                item.setIcon(app != null ? app.getIcon() : null);
                break;
            }
            case GROUP: {
                item.setItems(new ArrayList<>());
                dataSplit = data.split(Definitions.DELIMITER);
                for (String string : dataSplit) {
                    if (string.isEmpty()) continue;
                    Item groupItem = getItem(Integer.parseInt(string));
                    if (groupItem != null) {
                        item.getItems().add(groupItem);
                    }
                }
                break;
            }
            case ACTION: {
                item.setActionValue(Integer.parseInt(data));
                break;
            }
            case WIDGET: {
                dataSplit = data.split(Definitions.DELIMITER);
                item.setWidgetValue(Integer.parseInt(dataSplit[0]));
                item.setSpanX(Integer.parseInt(dataSplit[1]));
                item.setSpanY(Integer.parseInt(dataSplit[2]));
                break;
            }
        }

        return item;
    }


}
