package com.example.bleesp32.adapter;

import android.bluetooth.BluetoothDevice;

/*Клас который содержит в себе два streng. Один для мак адреса устройства, второй для имени*/
public class ListItem {
    private BluetoothDevice btDevice;
    private String itemType = BtAdapter.DEF_ITEM_TYPE;

    public BluetoothDevice getBtDevice() {
        return btDevice;
    }

    public void setBtDevice(BluetoothDevice btDevice) {
        this.btDevice = btDevice;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
}
