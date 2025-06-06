package com.roadpass.icecreamroll.model;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;

import com.roadpass.icecreamroll.util.Definitions;
import com.roadpass.icecreamroll.util.Definitions.ItemPosition;
import com.roadpass.icecreamroll.util.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Item {

    public Drawable _icon;
    public String _label;
    public Type _type;
    public int _id;
    public ItemPosition _location;
    public int _x = 0;
    public int _y = 0;


    public Intent _intent;


    public List<ShortcutInfo> _shortcutInfo;


    public List<Item> _items;

    public int _actionValue;

    public int _widgetValue;
    public int _spanX = 1;
    public int _spanY = 1;

    public Item() {
        Random random = new Random();
        _id = random.nextInt();
        _label = "";
    }

    public static Item newAppItem(App app) {
        Item item = new Item();
        item._type = Type.APP;
        item._label = app.getLabel();
        item._icon = app.getIcon();
        item._intent = Tool.getIntentFromApp(app);
        item._shortcutInfo = app.getShortcutInfo();
        return item;
    }

    public static Item newShortcutItem(Intent intent, Drawable icon, String name) {
        Item item = new Item();
        item._type = Type.SHORTCUT;
        item._label = name;
        item._icon = icon;
        item._spanX = 1;
        item._spanY = 1;
        item._intent = intent;
        return item;
    }

    public static Item newGroupItem() {
        Item item = new Item();
        item._type = Type.GROUP;
        item._label = "";
        item._spanX = 1;
        item._spanY = 1;
        item._items = new ArrayList<>();
        return item;
    }

    public static Item newActionItem(int action) {
        Item item = new Item();
        item._type = Type.ACTION;
        item._spanX = 1;
        item._spanY = 1;
        item._actionValue = action;
        return item;
    }

    public static Item newWidgetItem(ComponentName componentName, int widgetValue) {
        Item item = new Item();
        item._type = Type.WIDGET;
        item._label = componentName.getPackageName() + Definitions.DELIMITER + componentName.getClassName();
        item._widgetValue = widgetValue;
        item._spanX = 1;
        item._spanY = 1;
        return item;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Item) {
            Item item = (Item) object;
            return _id == item._id;
        } else {
            return false;
        }
    }

    public void reset() {
        Random random = new Random();
        _id = random.nextInt();
    }

    public Integer getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public Intent getIntent() {
        return _intent;
    }

    public String getLabel() {
        return _label;
    }

    public void setLabel(String label) {
        _label = label;
    }

    public Type getType() {
        return _type;
    }

    public List<ShortcutInfo> getShortcutInfo() {
        return _shortcutInfo;
    }

    public void setShortcutInfo(List<ShortcutInfo> shortcutInfo) {
        _shortcutInfo = shortcutInfo;
    }

    public List<Item> getGroupItems() {
        return _items;
    }

    public int getX() {
        return _x;
    }

    public void setX(int x) {
        _x = x;
    }

    public int getY() {
        return _y;
    }

    public void setY(int y) {
        _y = y;
    }

    public int getSpanX() {
        return _spanX;
    }

    public void setSpanX(int x) {
        _spanX = x;
    }

    public int getSpanY() {
        return _spanY;
    }

    public void setSpanY(int y) {
        _spanY = y;
    }

    public Drawable getIcon() {
        return _icon;
    }

    public enum Type {
        APP,
        SHORTCUT,
        GROUP,
        ACTION,
        WIDGET
    }

    public void setType(Type type) {
        _type = type;
    }

    public void setIcon(Drawable icon) {
        _icon = icon;
    }

    public void setIntent(Intent intent) {
        _intent = intent;
    }

    public List<Item> getItems() {
        return _items;
    }

    public void setItems(List<Item> items) {
        _items = items;
    }

    public int getActionValue() {
        return _actionValue;
    }

    public void setActionValue(int actionValue) {
        _actionValue = actionValue;
    }

    public int getWidgetValue() {
        return _widgetValue;
    }

    public void setWidgetValue(int widgetValue) {
        _widgetValue = widgetValue;
    }

    @Override
    public String toString() {
        return "Item{" +
                "_icon=" + _icon +
                ", _label='" + _label + '\'' +
                ", _type=" + _type +
                ", _id=" + _id +
                ", _location=" + _location +
                ", _x=" + _x +
                ", _y=" + _y +
                ", _intent=" + _intent +
                ", _shortcutInfo=" + _shortcutInfo +
                ", _items=" + _items +
                ", _actionValue=" + _actionValue +
                ", _widgetValue=" + _widgetValue +
                ", _spanX=" + _spanX +
                ", _spanY=" + _spanY +
                '}';
    }
    public Item(Drawable _icon, String _label, Type _type, int _id, ItemPosition _location, int _x, int _y, Intent _intent, List<ShortcutInfo> _shortcutInfo, List<Item> _items, int _actionValue, int _widgetValue, int _spanX, int _spanY) {
        this._icon = _icon;
        this._label = _label;
        this._type = _type;
        this._id = _id;
        this._location = _location;
        this._x = _x;
        this._y = _y;
        this._intent = _intent;
        this._shortcutInfo = _shortcutInfo;
        this._items = _items;
        this._actionValue = _actionValue;
        this._widgetValue = _widgetValue;
        this._spanX = _spanX;
        this._spanY = _spanY;
    }
}
