/*
package com.android.app.buystoreapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.app.buystoreapp.R;
import com.android.app.buystoreapp.bean.MyAddress;

import java.util.HashMap;
import java.util.List;


*/
/**
 * Created by hanamingming on 16/3/7.
 *//*

public class MyAddressAdapter extends BaseAdapter {
    private List<MyAddress> addresses;
    private Context context;
    private OnClickInterface onClickInterface;
    HashMap<String, Boolean> states = new HashMap<String, Boolean>();//用于记录每个RadioButton的状态，并保证只可选一个

    public MyAddressAdapter(List<MyAddress> addresses, Context context) {
        this.addresses = addresses;
        this.context = context;
    }

    public void setOnClickInterface(OnClickInterface onClickInterface) {
        this.onClickInterface = onClickInterface;
    }

    @Override
    public int getCount() {
        return addresses.size();
    }

    @Override
    public MyAddress getItem(int i) {
        return addresses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return addresses.get(i).getId();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = View.inflate(context, R.layout.item_address_list, null);
            holder = new ViewHolder();
            holder.tvReceiveName = (TextView) view.findViewById(R.id.tv_item_address_recipients_name);
            holder.tvNumber = (TextView) view.findViewById(R.id.tv_item_address_phone_number);
            holder.tvReceiveAddress = (TextView) view.findViewById(R.id.tv_item_address_harvest_address);
            holder.radioEdit = (ImageButton) view.findViewById(R.id.ib_item_address_editor);
            view.setTag(holder);
        }

        holder = (ViewHolder) view.getTag();
        MyAddress address = getItem(i);
        final RadioButton radio = (RadioButton) view.findViewById(R.id.iv_address_checked);
        holder.rdBtn = radio;
        holder.tvReceiveName.setText(address.getUsername());
        holder.tvNumber.setText(address.getUserphone());
        holder.tvReceiveAddress.setText(address.getUserarea() + address.getUserstreet() + address.getUserdetailed());
        holder.radioEdit.setTag("radioEdit" + i);
        holder.radioEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/9/14
                onClickInterface.doClic(i);
            }
        });

//当RadioButton被选中时，将其状态记录进States中，并更新其他RadioButton的状态使它们不被选中
        holder.rdBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //重置，确保最多只有一项被选中
                for (String key : states.keySet()) {
                    states.put(key, false);

                }
                states.put(String.valueOf(i), radio.isChecked());
                MyAddressAdapter.this.notifyDataSetChanged();
            }
        });

        boolean res = false;
        if (states.get(String.valueOf(i)) == null || states.get(String.valueOf(i)) == false) {
            res = false;
            states.put(String.valueOf(i), false);
        } else
            res = true;

        holder.rdBtn.setChecked(res);

        return view;
    }


    class ViewHolder {
        TextView tvReceiveName;
        TextView tvNumber;
        TextView tvReceiveAddress;
        ImageButton radioEdit;
        RadioButton rdBtn;
    }


    public interface OnClickInterface
    {
        */
/**
         *
         *//*

        public void doClic(int i);
    }


}
*/
