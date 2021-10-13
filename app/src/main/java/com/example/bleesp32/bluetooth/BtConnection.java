package com.example.bleesp32.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import com.example.bleesp32.adapter.BtConsts;

public class BtConnection {
    private Context context;
    private SharedPreferences pref;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice device;
    private ConnectTread connectTread;



    public BtConnection(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(BtConsts.MY_PREF, Context.MODE_PRIVATE);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }
    public void connect(){
        String mac = pref.getString(BtConsts.MAC_KEY, ""); //В BtConst  настроена переменная в которой хранится выбраный в чек бокс мак.
        if(!btAdapter.isEnabled() || mac.isEmpty()) return; // Если адаптер не включен то функция дальше не выполняется. Можно сделать всплывающее сообщение что он не включен
        /*mac.isEmpty() проверка что мак не пустой*/
        device = btAdapter.getRemoteDevice(mac);
        if(device == null) return; // если устройство выключено то дальше не пойдет. сделать сообщение !!!
        /*выполнели все проверки. Само подключение производим на второстепенном потоке. Что бы не выбивало приложение*/
    connectTread = new ConnectTread(btAdapter, device);
    connectTread.start();
    }
/*Закрытие соединения в случаи выхода с приложения
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(connectTread!=null) connectTread.closeConnection();
    }
    /*добавить проверки на наличие соединения*/
    public void sendMessage (String message){
        connectTread.getReadWriteTread().sendMessage(message.getBytes());
    }
    public void run (EditText message){
        connectTread.getReadWriteTread().run();
    }
}
