package com.android.app.buystoreapp.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.android.app.buystore.utils.SharedPreferenceUtils;
import com.android.app.buystoreapp.R;
import com.android.app.buystoreapp.adapter.MyOrderAdapter;
import com.android.app.buystoreapp.adapter.MyOrderAdapter.OrderBottomListener;
import com.android.app.buystoreapp.bean.CommodityBean;
import com.android.app.buystoreapp.bean.GsonBackOnlyResult;
import com.android.app.buystoreapp.bean.GsonMyOrderBack;
import com.android.app.buystoreapp.bean.MyOrderBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

public class MyOrderActivity extends Activity implements OrderBottomListener {

    @ViewInject(R.id.id_my_order_list)
    private ListView mOrderListView;
    @ViewInject(R.id.id_empty_order)
    private View emptyView;
    @ViewInject(R.id.id_empty_fail_order)
    private View emptyFailureView;
    @ViewInject(R.id.id_empty_done_order)
    private View emptyDoneView;

    @ResInject(id = R.string.web_url, type = ResType.String)
    private String url;
    private MyOrderAdapter mOrderAdapter;
    private List<MyOrderBean> mOrderDatas = new ArrayList<MyOrderBean>();
    private String mCurrentUserName;

    @ViewInject(R.id.id_custom_title_text)
    private TextView mTitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.myorder_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.custom_action_bar);

        ViewUtils.inject(this);

        mTitleText.setText("我的订单");
        if(SharedPreferenceUtils.getCurrentCityInfo(this) != null){
        	  mCurrentUserName = SharedPreferenceUtils.getCurrentUserInfo(this).getUserName();
        }
      
        mOrderAdapter = new MyOrderAdapter(this, mOrderDatas);
        mOrderListView.setAdapter(mOrderAdapter);
        mOrderListView.setEmptyView(emptyView);
    }

    @OnClick(R.id.id_custom_back_image)
    public void onCustomBarBackClicked(View v) {
        switch (v.getId()) {
        case R.id.id_custom_back_image:
            this.finish();
            break;
        default:
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestOrder();
    }

    private void requestOrder() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        String param = "{\"cmd\":\"getMyOrder\",\"userName\":\"aaa\"}";
        param = param.replace("aaa", mCurrentUserName);
        requestParams.put("json", param);

        client.get(url, requestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                LogUtils.d("requestOrder,result= " + new String(arg2));
                try {
                    Gson gson = new Gson();
                    GsonMyOrderBack gsonMyOrderBack = gson.fromJson(new String(
                            arg2), new TypeToken<GsonMyOrderBack>() {
                    }.getType());
                    String result = gsonMyOrderBack.getResult();
                    if ("0".equals(result)) {
                        mOrderDatas.clear();
                        mOrderDatas.addAll(gsonMyOrderBack.getOrderLists());
                        mOrderAdapter.notifyDataSetChanged();
                    } else {
                        if ("您还没有订单".equals(gsonMyOrderBack.getResultNote())) {
                            emptyView.setVisibility(View.GONE);
                            mOrderListView.setEmptyView(emptyDoneView);
                        } else {
                            emptyView.setVisibility(View.GONE);
                            mOrderListView.setEmptyView(emptyFailureView);
                        }
                    }
                } catch (Exception e) {
                    LogUtils.e("requestOrder error:", e);
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                    Throwable arg3) {

            }
        });
    }

    @Override
    public void delOrder(final int position) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        String param = "{\"cmd\":\"deleteOrder\",\"userName\":\"aaa\",\"orderID\":\"bbb\"}";
        param = param.replace("aaa", mCurrentUserName);
        param = param.replace("bbb", mOrderDatas.get(position).getOrderID());
        requestParams.put("json", param);

        client.get(url, requestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                LogUtils.d("delOrder result="+new String(arg2));
                try {
                    Gson gson = new Gson();
                    GsonBackOnlyResult gsonBackOnlyResult = gson.fromJson(
                            new String(arg2),
                            new TypeToken<GsonBackOnlyResult>() {
                            }.getType());
                    String result = gsonBackOnlyResult.getResult();
                    if ("0".equals(result)) {
                        mOrderDatas.remove(position);
                        mOrderAdapter.notifyDataSetChanged();
                    } else {

                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                    Throwable arg3) {

            }
        });
    }

    private String[] payItems = new String[] { "支付宝" };

    @Override
    public void orderCharge(final int position) {
       /* AlertDialog dialog = new AlertDialog.Builder(this).setTitle("选择支付方式")
                .setItems(payItems, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int which) {
                        if (which == 0) {// alipay
                            MyOrderBean myOrder = mOrderDatas.get(position);
                            StringBuilder sb = new StringBuilder();
                            for (CommodityBean commodityBean : myOrder.getOrderList()) {
                                sb.append(commodityBean.getCommodityName()+",");
                            }
                            String orderId = myOrder.getOrderID();
                            String body = sb.toString();
                            String price = myOrder.getTotalMoney();
                            AlipayKeys alipayKeys = new AlipayKeys();
                            alipayKeys.pay(MyOrderActivity.this, subject, body, "0.01", mHandler);
                            Toast.makeText(MyOrderActivity.this, "请求支付中...请稍后",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).create();
        dialog.show();*/
    	MyOrderBean myOrder = mOrderDatas.get(position);
    	StringBuilder sb = new StringBuilder();
        for (CommodityBean commodityBean : myOrder.getOrderList()) {
            sb.append(commodityBean.getProName()+",");
        }
    	String orderId = myOrder.getOrderID();
        String body = sb.toString();
        String price = myOrder.getTotalMoney();
    	Intent intent = new Intent(MyOrderActivity.this,PayOrderActivity.class);
    	intent.putExtra("orderID", orderId);
    	intent.putExtra("price",price);
    	intent.putExtra("body",body);
    	startActivity(intent);
    	finish();
    	
    }	 
    
   /* private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case AlipayKeys.SDK_PAY_FLAG: {
                PayResult payResult = new PayResult((String) msg.obj);

                // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                String resultInfo = payResult.getResult();

                String resultStatus = payResult.getResultStatus();
                Log.d(AlipayKeys.TAG,  "resultStatus="+resultStatus+" ,AliPay result=" + resultInfo);

                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {
                    Toast.makeText(MyOrderActivity.this, "支付成功",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // 判断resultStatus 为非“9000”则代表可能支付失败
                    // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                    if (TextUtils.equals(resultStatus, "8000")) {
                        Toast.makeText(MyOrderActivity.this, "支付结果确认中",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                        Toast.makeText(MyOrderActivity.this, "支付失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            default:
                break;
            }
        };
    };*/
    
}
