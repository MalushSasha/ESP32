package com.example.bleesp32;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bleesp32.adapter.BtAdapter;
import com.example.bleesp32.adapter.ListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BtListActivity extends AppCompatActivity {
    private final int BT_REQUEST_PERM = 12;//Переменная для запроса, разрешить доступ кместонахождению
    private ListView listView;
    private BtAdapter adapet;
    private BluetoothAdapter btAdapter;
    private List<ListItem> list;
    private boolean isBtPermisionGranted = false;
    private boolean isDiscovery = false;
    private ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_list);
        getBtPermision();
        init();
    }
/* Фильтр что бы понимать на что именно мы получаем запрос. Используем при поиске новых устройств*/
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter f1= new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter f2= new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        IntentFilter f3= new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bReceiver, f1);
        registerReceiver(bReceiver, f2);
        registerReceiver(bReceiver, f3);
    }
/*Что бы фильтр не работал когда пользователь сваричивает окно, применяем паузу*/
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bt_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            if(isDiscovery){

                btAdapter.cancelDiscovery();//останавливаем поиск
                isDiscovery = false;
                getPairedDeviced();// обновляем список подключенных устройств

            }else {

                finish();

            }

        }else if(item.getItemId() == R.id.id_search){
                if(isDiscovery) return true;
                ab.setTitle(R.string.discoveribg);
                list.clear();//при нажатии поис очищается список устройств которые уже подключались к смартфону
            ListItem itemTitle = new ListItem();
            itemTitle.setItemType(BtAdapter.TITLE_ITEM_TYPE);
            list.add(itemTitle);
            adapet.notifyDataSetChanged();
            btAdapter.startDiscovery();//Запуск поиска устройств
            isDiscovery = true;
        }

        return true;
    }
/*Функция вызова стрелки возврата назад*/
    private void init() {
        ab = getSupportActionBar();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        list = new ArrayList<>();
        ActionBar ab = getSupportActionBar();
        if (ab==null)return; //если не равно нулю тогда запуститься
        ab.setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.ListView1);
        adapet = new BtAdapter(this, R.layout.bt_list_item, list);
        listView.setAdapter(adapet);
        getPairedDeviced();
        onItemClickListener();
    }
    /*Функция отслеживает выбрано ли какое то устройство из списка*/
    private void onItemClickListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // Log.d(tag:"MyLog", msg:"Item click");
                ListItem item = (ListItem)parent.getItemAtPosition(position);
                if(item.getItemType().equals(BtAdapter.DISCOVERY_ITEM_TYPE)) item.getBtDevice().createBond();//эсли устройство рание небыло подключено то подключиться к нему
            }
        });
    }
    private void getPairedDeviced(){
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            list.clear();
            for (BluetoothDevice device : pairedDevices) {
                ListItem item = new ListItem();
                item.setBtDevice(device);
                list.add(item);
            }
            adapet.notifyDataSetChanged();
        }
    }
/*onRequestPermissionsResult - функция для для просмотра выбора пользователя на запрос к системе(у нас местоположение)*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
        if (requestCode == BT_REQUEST_PERM){if(grantResults[0]==PackageManager.PERMISSION_GRANTED){isBtPermisionGranted = true;
            Toast.makeText(this, "Пользователь Красавчик", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Нет разрешения на поиск блютуз устройств", Toast.LENGTH_SHORT).show();
        }
        }
        else {super.onRequestPermissionsResult(requestCode, permissions, grantResults);}

    }

    /*Функция запроса проверки разрешения на доступ к сестонахождению. нужно для доступа к поиску блютуз устройств*/
    private void getBtPermision(){
         if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                  != PackageManager.PERMISSION_GRANTED){
             ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                BT_REQUEST_PERM);
         }else {
        isBtPermisionGranted=true;
        }
    }

    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                  ListItem item = new ListItem();
                  item.setBtDevice(device);
                  item.setItemType(BtAdapter.DISCOVERY_ITEM_TYPE);
                    list.add(item);
                adapet.notifyDataSetChanged();
                //Toast.makeText(context, "Ух я нашел: " +device.getName(), Toast.LENGTH_SHORT).show();
            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
                isDiscovery=false;
                getPairedDeviced();
                ab.setTitle(R.string.app_name);

            }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() == BluetoothDevice.BOND_BONDED){       //если устройство подключено
                    getPairedDeviced();
                }
            }

        }
    };
}