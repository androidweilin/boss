package com.android.app.buystoreapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.app.buystore.utils.SharedPreferenceUtils;
import com.android.app.buystoreapp.R;
import com.android.app.buystoreapp.bean.CommodityBean;
import com.android.app.buystoreapp.bean.MyOrderBean;
import com.android.app.buystoreapp.setting.MyOrderAssessActivity;
import com.android.app.buystoreapp.setting.MyOrderDetailActivity;
import com.android.app.buystoreapp.wallet.ToastUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyOrderAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<MyOrderBean> mDatas;
    private List<CommodityBean> mOrderCommoditys = new ArrayList<CommodityBean>();
    OrderBottomListener mListener;
    private Context mContext;

    public interface OrderBottomListener {
        void delOrder(final int position);

        void orderCharge(int position);
    }

    public MyOrderAdapter(Context context, List<MyOrderBean> order) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = order;
        mListener = (OrderBottomListener) context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.myorder_item, null);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ViewUtils.inject(holder, convertView);

        final MyOrderBean orderBean = mDatas.get(position);
        holder.orderId
                .setText(String.format("订单号：%1$s", orderBean.getOrderID()));
        String state = orderBean.getOrderState();
        switch (state) {
        /*case "0":// 已经处理
            holder.orderBottomLayout.setVisibility(View.GONE);
            holder.orderStateText.setText("已经处理");
            break;
        case "1":// 未处理，可以取消
*/        case "5":// 未付款 可以取消,待付款
            holder.orderBottomLayout.setVisibility(View.VISIBLE);
            holder.orderDel.setVisibility(View.VISIBLE);
            holder.orderCharge.setVisibility(View.VISIBLE);
            holder.orderStateText.setText("待付款");
            break;
        case "2":// 待收货
            holder.orderBottomLayout.setVisibility(View.GONE);
            holder.orderStateText.setText("待收货");
            break;
        case "3":// 已完成
            holder.orderBottomLayout.setVisibility(View.VISIBLE);
            holder.orderDel.setVisibility(View.VISIBLE);
            holder.orderCharge.setVisibility(View.GONE);
            holder.orderStateText.setText("已完成");
            break;
        case "4":// 待发货
            holder.orderBottomLayout.setVisibility(View.GONE);
            holder.orderStateText.setText("待发货");
            break;
        case "6":// 待评价
            holder.orderBottomLayout.setVisibility(View.GONE);
            holder.orderStateText.setText("待评价");
            break;
        default:
            break;
        }
        holder.orderItemHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(mContext,
                        MyOrderDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "order");
                bundle.putSerializable("orderBean", orderBean);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        mOrderCommoditys = orderBean.getOrderList();
        if (mOrderCommoditys == null)
            mOrderCommoditys = new ArrayList<CommodityBean>();
        final ContentAdapter contentAdapter = new ContentAdapter(
                mOrderCommoditys, state,orderBean.getOrderID());
        holder.orderGridView.setAdapter(contentAdapter);
        holder.orderGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    int position, long arg3) {
                LogUtils.d("order grid view position=" + position
                        + ",commodity:"
                        + contentAdapter.getItem(position).toString());
                Intent intent = new Intent(mContext,
                        MyOrderDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "commodity");
                bundle.putSerializable("commodityBean",
                        contentAdapter.getItem(position));
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        holder.orderTotalMoney.setText(String.format("总花费：￥%1$s",
                orderBean.getTotalMoney()));
        holder.orderDel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mListener.delOrder(position);
            }
        });

        holder.orderCharge.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mListener.orderCharge(position);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        @ViewInject(R.id.id_myorder_id)
        TextView orderId;
        @ViewInject(R.id.id_myorder_state)
        TextView orderStateText;
        @ViewInject(R.id.id_myorder_totalMoney)
        TextView orderTotalMoney;
        @ViewInject(R.id.id_myorder_item_bottom)
        View orderBottomLayout;
        @ViewInject(R.id.id_myorder_del)
        Button orderDel;
        @ViewInject(R.id.id_myorder_charge)
        Button orderCharge;
        @ViewInject(R.id.id_myorder_item_header)
        View orderItemHeader;
        @ViewInject(R.id.id_myorder_content_gridview)
        GridView orderGridView;
    }

    class ContentAdapter extends BaseAdapter {
        List<CommodityBean> mDatas;
        String orderState;
        private String orderId;

        public ContentAdapter(List<CommodityBean> data, String state,String orderID) {
            mDatas = data;
            orderState = state;
            orderId = orderID;
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public CommodityBean getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Content_ViewHolder holder = null;
            if (view == null) {
                holder = new Content_ViewHolder();
                view = mInflater.inflate(R.layout.myorder_item_content_layout,
                        null);
                view.setTag(holder);
            } else {
                holder = (Content_ViewHolder) view.getTag();
            }

            ViewUtils.inject(holder, view);
            final CommodityBean commodityBean = mDatas.get(position);
           /* String icon = commodityBean.getCommodityIcon();
            if (!TextUtils.isEmpty(icon)) {
                Picasso.with(mContext).load(icon)
                        .placeholder(R.drawable.ic_default)
                        .error(R.drawable.ic_default).into(holder.contentIcon);
            }
            holder.contentName.setText(commodityBean.getCommodityName());
            holder.contentPrice.setText(String.format("%1$s元", TextUtils
                    .isEmpty(commodityBean.getCommodityPrice()) ? "0"
                    : commodityBean.getCommodityPrice()));
            holder.contentMarketPrice.setText(String.format("%1$s元", TextUtils
                    .isEmpty(commodityBean.getCommodityMarketPrice()) ? "0"
                    : commodityBean.getCommodityMarketPrice()));
            holder.contentMarketPrice.getPaint().setFlags(
                    Paint.STRIKE_THRU_TEXT_FLAG);
            holder.contentIntro.setText(commodityBean.getCommodityIntro());
*/
            switch (orderState) {
            case "2":// 待收货
                holder.contentAssess.setVisibility(View.VISIBLE);
                holder.contentAssess.setText("确认收货");
                break;
            case "3":// 已完成
                holder.contentAssess.setVisibility(View.GONE);
                break;
            case "4"://待发货
                holder.contentAssess.setVisibility(View.GONE);
            case "5":// 待付款
                holder.contentAssess.setVisibility(View.GONE);
                break;
            case "6":// 待评价
                holder.contentAssess.setVisibility(View.VISIBLE);
                break;
            default:
                break;
            }
            holder.contentAssess.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                   if("2".equals(orderState)){
                	   sendConfirmGetGoods(commodityBean,orderId);
                   }else if("6".equals(orderState)){
                	   goGoodsAssess(commodityBean,orderId);
                   }
                }

				
            });
            return view;
        }
        private void goGoodsAssess(final CommodityBean commodityBean,String orderId) {
			Intent intent = new Intent(mContext,
                       MyOrderAssessActivity.class);
               Bundle bundle = new Bundle();
               bundle.putSerializable("commodity", commodityBean);
               intent.putExtra("orderId", orderId);
               intent.putExtras(bundle);
               mContext.startActivity(intent);
		}
		protected void sendConfirmGetGoods(final CommodityBean commodityBean,final String orderId) {
			AsyncHttpClient httpClient = new AsyncHttpClient();
			RequestParams params = new RequestParams();
			String userName= SharedPreferenceUtils.getCurrentUserInfo(mContext).getUserName();
			String json = "{\"cmd\":\"checkGetGoods\",\"userName\":\""+userName+"\",\"userOrderID\":\""+orderId+"\"}";
			params.put("json",json);
			Log.d("lulu","确认收货"+json);
			String webUrl = mContext.getResources().getString(R.string.web_url);
			httpClient.get(webUrl, params, new AsyncHttpResponseHandler(){

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2,
						Throwable arg3) {
                    ToastUtil.showMessageDefault(mContext,"网络繁忙");
				}

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					Log.d("lulu",new String(arg2));
					try {
						JSONObject jsonObject = new JSONObject(new String(arg2));
						if("0".equals(jsonObject.getString("result"))){
							goGoodsAssess(commodityBean,orderId);
						}else{
                            ToastUtil.showMessageDefault(mContext,"网络繁忙");
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			});
		}
    }

    static class Content_ViewHolder {
        @ViewInject(R.id.id_order_item_content_icon)
        ImageView contentIcon;
        @ViewInject(R.id.id_order_item_content_name)
        TextView contentName;
        @ViewInject(R.id.id_order_item_content_price)
        TextView contentPrice;
        @ViewInject(R.id.id_order_item_content_marketprice)
        TextView contentMarketPrice;
        @ViewInject(R.id.id_order_item_content_intro)
        TextView contentIntro;
        @ViewInject(R.id.id_order_item_content_assess)
        Button contentAssess;
    }
}
