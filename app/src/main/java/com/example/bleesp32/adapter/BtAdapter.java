package com.example.bleesp32.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bleesp32.R;

import java.util.ArrayList;
import java.util.List;

/*Заполняет ListView списком*/
public class BtAdapter extends ArrayAdapter<ListItem> {
    public static final String DEF_ITEM_TYPE = "normal";
    public static final String TITLE_ITEM_TYPE = "title";
    public static final String DISCOVERY_ITEM_TYPE = "discovery";
    private List<ListItem> mainList;
    private List<ViewHolder> ListVievHolders; // Лист со списком устройств
    private SharedPreferences pref; //Переменная для сохранение мак адресов устройств что выбрали
    private boolean isDiscoveryType = false;

    public BtAdapter(@NonNull Context context, int resource, List<ListItem> btList) {
        super(context, resource, btList);
        mainList = btList;
        ListVievHolders = new ArrayList<>();
        pref = context.getSharedPreferences(BtConsts.MY_PREF, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        switch (mainList.get(position).getItemType()){
            case TITLE_ITEM_TYPE: convertView = titleItem(convertView, parent);
                break;
            default: convertView = defaultItem(convertView, position, parent);
        break;}

        return convertView;
    }

    private void savePref(int pos){
        SharedPreferences.Editor editor= pref.edit();//доступ к класу для записи. У преференс нет доступа для записи
        editor.putString(BtConsts.MAC_KEY, mainList.get(pos).getBtDevice().getAddress());//запись
        editor.apply();//функция для сохранения
    }
/*Клас для запоминания списка устройств. Когда большой перечень и нужно прокручивать то
* те елементы которые скрылись при прокручивании обратно будут наново есть ресурс устройства что бы прописаться
* етот клас их запомнит и при прокручивании не будет тормозить лист*/
static class ViewHolder{
        TextView tvBtName;
        CheckBox chBtSelected;
    }
    private View defaultItem(View convertView, int position, ViewGroup parent){
        ViewHolder viewHolder;

        boolean hasViewHolder = false;
        if(convertView != null) hasViewHolder=(convertView.getTag() instanceof ViewHolder);
        if (convertView == null || !hasViewHolder){
            viewHolder= new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bt_list_item,null, false);
            viewHolder.tvBtName = convertView.findViewById(R.id.tvBtName1);
            viewHolder.chBtSelected = convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
            ListVievHolders.add(viewHolder);// помещаем в лист каждое устройство
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.chBtSelected.setChecked(false);
        }
        if(mainList.get(position).getItemType().equals(BtAdapter.DISCOVERY_ITEM_TYPE)){ //проверяем, если это список ново найденых то спрячем чек бокс
          viewHolder.chBtSelected.setVisibility(View.GONE);
            isDiscoveryType = true;
        }else {
            viewHolder.chBtSelected.setVisibility(View.VISIBLE); // нужно для блокировки слушателя нажатий
            isDiscoveryType = false;
        }
        viewHolder.tvBtName.setText(mainList.get(position).getBtDevice().getName());
        /*для сброса чек боксов в листе. Один только будет выбран(часть кода подсвечена серым. Это включена лямбда, сокращение кода)*/
        viewHolder.chBtSelected.setOnClickListener(v -> {
            if(!isDiscoveryType){
            for (ViewHolder spisok : ListVievHolders){
                spisok.chBtSelected.setChecked(false);
            }
            viewHolder.chBtSelected.setChecked(true);//оставляет только один чек бокс выбраным
            savePref(position);
            }
        });
        if(pref.getString(BtConsts.MAC_KEY, "блютуз устройство не выбрано").equals(mainList.get(position)
                .getBtDevice().getAddress()))viewHolder.chBtSelected.setChecked(true);
        isDiscoveryType = false;
        return convertView;
    }


    private View titleItem(View convertView, ViewGroup parent){
        //ViewHolder viewHolder;
        boolean hasViewHolder = false;
        if (convertView != null) hasViewHolder = (convertView.getTag() instanceof ViewHolder);
        if (convertView == null || hasViewHolder){

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bt_list_item_title,null, false);

            }
        return convertView;
    }
}
