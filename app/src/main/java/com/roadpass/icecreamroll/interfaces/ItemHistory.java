package com.roadpass.icecreamroll.interfaces;

import android.view.View;

import com.roadpass.icecreamroll.model.Item;

public interface ItemHistory {
    void setLastItem(Item item, View view);

    void revertLastItem();

    void consumeLastItem();
}
