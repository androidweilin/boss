package com.android.app.buystoreapp.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.app.buystoreapp.R;
import com.android.app.buystoreapp.bean.AddressBean;

import java.util.List;

public class AddressAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<AddressBean> mDatas;

    public AddressAdapter(Context context, List<AddressBean> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.address_item, null);
            holder.itemName = (TextView) convertView
                    .findViewById(R.id.id_address_item_name);
            holder.itemAddress = (TextView) convertView
                    .findViewById(R.id.id_address_item_address);
            holder.itemDefault = (TextView) convertView
                    .findViewById(R.id.id_address_item_default);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        boolean isDefault = "1".equals(mDatas.get(position).getIsDefault()) ? true
                : false;

        holder.itemDefault.setVisibility(isDefault ? View.VISIBLE : View.GONE);

        String name = String.format(mContext.getString(R.string.confirm_order_receive_name), mDatas.get(position).getName(), mDatas.get(position)
                                .getPhone());
        SpannableString spanText = new SpannableString(name);
        holder.itemName.setText(spanText);

        String address = String.format(
                mContext.getString(R.string.confirm_order_receive_address),
                mDatas.get(position).getAdress());
        holder.itemAddress.setText(address);
        return convertView;
    }

    static class ViewHolder {
        TextView itemName;
        TextView itemAddress;
        TextView itemDefault;
    }

}
