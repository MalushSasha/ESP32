package com.example.bleesp32;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.bleesp32.adapter.BtConsts;
import com.example.bleesp32.bluetooth.BtConnection;
import com.example.bleesp32.bluetooth.ReadWriteTread;


public class MainActivity extends AppCompatActivity  {
    private MenuItem menuItem;
    private BluetoothAdapter btAdapter;
    private final int ENEBLE_REQUEST = 15;
    private SharedPreferences pref;
    private BtConnection btConnection;

    private EditText inText;
    private EditText ouText;
    private Button btRead, btWrite;
    private ToggleButton tb1;
    Handler h;





    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;


/*Вызов екрана. в этом же вызове и открываем доступ к блютуз адаптеру*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inText = (EditText) findViewById(R.id.stRead);
        ouText = (EditText) findViewById(R.id.stWrite);
        btRead = (Button) findViewById(R.id.btRead);
        btWrite = (Button)findViewById(R.id.btWrite);
        tb1 =  findViewById(R.id.toggleButton);
        bt_zapusk();

        tb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(tb1.isChecked()){
                    ouText.setText("sgd");
                }else {
                    ouText.setText("23");
                }
            }
        });

        btWrite.setOnClickListener(v -> {
            btConnection.sendMessage(ouText.getText().toString());
        });
        h = new Handler(Looper.getMainLooper()) {
            public void handleMessages(android.os.Message msg) {
                // обновляем TextView
                inText.setText("получили " + msg);
            };
            };
    }




    /* Вызов кнопки меню вкл/выкл блюту*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.osnov_menu, menu);
        menuItem = menu.findItem(R.id.id_bt_button);
        bt_icon();
        return super.onCreateOptionsMenu(menu);
    }
/*перезапуск иконки блютуза, что бы меняло ее вид. Слушает нажание кнопок и производит действие*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.id_bt_button) {
            if (!btAdapter.isEnabled()) {
                bt_vkl();
            } else {
                btAdapter.disable();
                menuItem.setIcon(R.drawable.ic_bt_enable);
            }
        }else if (item.getItemId() == R.id.id_menu){
            if(btAdapter.isEnabled()){
            Intent i = new Intent(MainActivity.this, BtListActivity.class);
            startActivity(i);
            }else{
                Toast.makeText(this, "Включите блютуз", Toast.LENGTH_SHORT).show();
            }
        }else if (item.getItemId() == R.id.id_connect){
            btConnection.connect();
        }
        return super.onOptionsItemSelected(item);
    }
/*после того как функция bt_vkl запустила окно разрешения запуска блютуза. результат ответа смотрим*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ENEBLE_REQUEST){
            if(resultCode==RESULT_OK){
              bt_icon();}
                }
    }

    /* Инициализация функции для доступа через переменную к блютуз адаптеру*/
    private void bt_zapusk(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        pref = getSharedPreferences(BtConsts.MY_PREF, Context.MODE_PRIVATE);
        /* нижняя строчка для вывода сообщений в Logcat с фильтром MyLog*/
       // Log.d("MyLog",  "Bt name : " + pref.getString(BtConsts.MAC_KEY, "no Bt Selected"));
        btConnection = new BtConnection(this); //Запустили нашу функцию подключения и иконку

    }
    /*Функция переключения иконки блютуза*/
    private void bt_icon(){
        if(btAdapter.isEnabled()){
            menuItem.setIcon(R.drawable.ic_bt_disable);
        }else {
            menuItem.setIcon(R.drawable.ic_bt_enable);
        }
    }
    /*Функция запуска блютуз*/
    private void bt_vkl(){
        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(i, ENEBLE_REQUEST);
    }

    
  /*btWrite.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            write.write();
        }
    });  */

    /**
     * Обработчик, который получает информацию от BluetoothChatService
     */




}