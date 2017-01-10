package com.android.app.buystoreapp.wallet;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.app.buystoreapp.R;

import java.util.HashMap;
import java.util.List;

/**
 * 另一种写单选按钮的方法(没有使用)
 *
 * Created by 尚帅波 on 2016/9/19.
 */
public class UnionpayListAdapter1 extends BaseAdapter {
    private Context context;
    private List<UnionpayInfoBeen> datas;
    // 用于记录每个RadioButton的状态，并保证只可选一个
    private HashMap<UnionpayInfoBeen, Boolean> states = new HashMap<UnionpayInfoBeen, Boolean>();

    public UnionpayListAdapter1(Context context, List<UnionpayInfoBeen> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return (datas == null) ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.unionpay_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.radioButton = (RadioButton) convertView.findViewById(R.id.radioBtn);
            viewHolder.tv_unionpayInfo = (TextView) convertView.findViewById(R.id.tv_unionpayInfo);
            viewHolder.tv_other = (TextView) convertView.findViewById(R.id.tv_other);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        UnionpayInfoBeen data = datas.get(position);
        viewHolder.tv_unionpayInfo.setText(data.getUnionpayName());
        viewHolder.tv_other.setText(data.getOther());

        final RadioButton radio = (RadioButton) convertView.findViewById(R.id.radioBtn);
        viewHolder.radioButton = radio;
        viewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 重置，确保最多只有一项被选中
                for (UnionpayInfoBeen key : states.keySet()) {
                    states.put(key, false);
                }
                states.put(datas.get(position), radio.isChecked());
                UnionpayListAdapter1.this.notifyDataSetChanged();
            }
        });
        boolean res = false;
        if (states.get(datas.get(position)) == null || states.get(datas.get(position)) == false) {
            res = false;
            states.put(datas.get(position), false);
        } else {
            res = true;
        }
        viewHolder.radioButton.setChecked(res);
        return convertView;
    }

    class ViewHolder {
        RadioButton radioButton;
        TextView tv_unionpayInfo, tv_other;
    }
}
