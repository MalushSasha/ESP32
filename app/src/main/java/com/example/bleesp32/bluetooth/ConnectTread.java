package com.example.bleesp32.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.bleesp32.MainActivity;

import java.io.IOException;
import java.util.UUID;

public class ConnectTread extends Thread{  /*extends Thread - Наследования от класса потоков Thread*/
    private BluetoothAdapter btAdapter;
    private BluetoothSocket mSoced;
    private ReadWriteTread readWriteTread;

    public static final String UUID = "00001101-0000-1000-8000-00805F9B34FB"; //c2ce4954-e260-11eb-ba80-0242ac130004
    public ConnectTread(BluetoothAdapter btAdapter, BluetoothDevice device){
        /*При передачи с помощью конструктора с BtConnection контекста, то его нужно записать будет в  contextVnut
         для доступа на уровне всего класса */

            this.btAdapter = btAdapter;

            try{
                mSoced = device.createRfcommSocketToServiceRecord(java.util.UUID.fromString(UUID));
            }catch (IOException e){

            }
    }

    @Override
    public void run() {
       // boolean success = false;
        btAdapter.cancelDiscovery();//Отменяем сканирование, поскольку оно тормозит соединение.
        try{
            mSoced.connect();
           // success = true;
            readWriteTread = new ReadWriteTread(mSoced);
            readWriteTread.start();
            Log.d("MyLog","соединения успешно");
        }catch (IOException e){
            Log.d("MyLog","Нет соединения");
            closeConnection();
        }

    }

    /*В случаи если пользователь хочет прервать соединение*/
    public void closeConnection(){
        try{
            mSoced.close();
            Log.d("MyLog","Нет соединения");
        }catch (IOException y){
        }
    }

    public ReadWriteTread getReadWriteTread() {
        return readWriteTread;
    }
}
