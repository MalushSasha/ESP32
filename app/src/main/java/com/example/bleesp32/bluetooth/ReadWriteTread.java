package com.example.bleesp32.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.bleesp32.adapter.BtConsts;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReadWriteTread extends Thread {
    private  BluetoothSocket socket;
    private  InputStream mInStream;
    private  OutputStream mOutStream;
    public Handler mHandler;
    private byte[] perBuffer;// буферный массив



    public ReadWriteTread(BluetoothSocket socket) {
       this.socket = socket;
        Log.d("MyLog", "create ConnectedThread: " + socket);

        try {
            mInStream = socket.getInputStream();
            Log.d("MyLog", "Входной поток создан");
            } catch (IOException e) {
            Log.d("MyLog", "Произошла ошибка при создании входного потока");
        }
        try {
            mOutStream = socket.getOutputStream();
            Log.d("MyLog", "Выходной поток создан");
        }catch (IOException y){
            Log.d("MyLog", "Произошла ошибка при создании выходного потока 2");
        }

    }

    @Override
    public void run() {
        perBuffer = new byte[2];// буферный массив
       // int bytes;// bytes returned from read()
// Прослушиваем InputStream пока не произойдет исключение
        while (true) {//Прием данных если соединены
            try {
// читаем из InputStream
              int size = mInStream.read(perBuffer);
// посылаем прочитанные байты главной деятельности
                Log.d("MyLog", "Bt name :" + perBuffer);
                String message = new String(perBuffer,0, size);// переменная в формате стринг с переменной perBuffer читается
                // с нулевой позиции длиной переданного слова
                Log.d("MyLog", "Сообщение" + message);

               // Отправьте полученные байты в UI Activity
                } catch (IOException e) {
                Log.d("MyLog", "Входной поток был отключен, подключитесь повторно", e);
                break;
            }
        }
    }
    /* Вызываем этот метод из главной деятельности, чтобы отправить данные
    удаленному устройству */
    public void sendMessage (byte[] bayteMessage) {

        try {
            mOutStream.write(bayteMessage);
           // Поделиться отправленным сообщением с действием пользовательского интерфейса

            Log.d("MyLog", "Отправлено !?");

        } catch (IOException e) {
            Log.d("MyLog", "Ошибка при отправке данных", e);
        }
    }



}
