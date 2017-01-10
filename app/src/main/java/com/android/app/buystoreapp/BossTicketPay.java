package com.android.app.buystoreapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.pay.demo.AlipayKeys;
import com.alipay.sdk.pay.demo.PayResult;
import com.android.app.buystore.utils.SharedPreferenceUtils;
import com.android.app.buystoreapp.base.BaseAct;
import com.android.app.buystoreapp.setting.MyOrderActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.pingplusplus.android.PaymentActivity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class BossTicketPay extends BaseAct implements OnClickListener {
    private int alipay = 2;
    private int wechatpay = 1;
    private float price = 0;
    private String orderId = "";
    private String boday = "";
    private String userName = "";
    String count = "0";
    private String url = "";
    @ViewInject(R.id.id_custom_title_text)
    private TextView mTitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_select_pay);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_action_bar);
        ViewUtils.inject(this);
        mTitleText.setText("订单支付");
        BindClick();
        Init();
    }

    @OnClick(R.id.id_custom_back_image)
    public void onCustomBarBackClicked(View v) {
        switch (v.getId()) {
            case R.id.id_custom_back_image:
                this.finish();
            default:
                break;
        }
    }

    private void BindClick() {
        findViewById(R.id.ll_zhifubao).setOnClickListener(this);
        findViewById(R.id.ll5_weixin).setOnClickListener(this);
    }

    private void Init() {
        count = getIntent().getStringExtra("count");
        userName = SharedPreferenceUtils.getCurrentUserInfo(this)
                .getUserId();
        url = getString(R.string.web_url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_zhifubao:
                Log.e("支付宝","onClick");
                getOrderId(alipay);
                break;
            case R.id.ll5_weixin:
                Log.e("微信","onClick");
                getOrderId(wechatpay);
                break;
            default:
                break;
        }
    }

    private void getOrderId(final int channel) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("userName", userName);
            obj.put("Count", count);
            obj.put("PayFlag", channel);
            obj.put("cmd", "buyVouchers");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("json", obj.toString());
        //Toast.makeText(this, obj.toString(), Toast.LENGTH_SHORT).show();
        client.get(url, params, new com.loopj.android.http.AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                //Toast.makeText(BossTicketPay.this, new String(arg2), Toast.LENGTH_SHORT).show();
                try {
                    JSONObject obj = new JSONObject(new String(arg2));
                    if ("0".equals(obj.getString("result"))) {
                        orderId = obj.getString("orderId");
                        price = (float) obj.getDouble("price");
                        //Toast.makeText(getApplicationContext(), ""+price, Toast.LENGTH_SHORT).show();
                        if (channel == alipay) {
                            AliPay();
                        } else {
                            wechatPay();
                        }
                    } else {
                        Toast.makeText(BossTicketPay.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                Toast.makeText(BossTicketPay.this, "支付失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void wechatPay() {
        Toast.makeText(this,
                "请求支付中...请稍后", Toast.LENGTH_SHORT)
                .show();
        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        float totalPrice = price * 100;
        int amount = (int) totalPrice;
        //int amount = 1;
        String json = "{\"cmd\":\"getCharge\",\"amount\":" + amount + ",\"orderNo\":\"" + orderId + "\",\"channel\":\"wx\",\"body\":\"boss ticket\"}";
        params.put("json", json);
        Log.d("lulu", json);
        httpClient.post(url, params, new com.loopj.android.http.AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                try {
                    JSONObject jsonObject = new JSONObject(new String(arg2));
                    Log.e("-------------->", new String(arg2));
                    if ("0".equals(jsonObject.getString("result"))) {
                        Toast.makeText(BossTicketPay.this, "支付信息请求成功", 0).show();
                        String charge = jsonObject.getString("charge");
                        //JSONObject json = jsonObject.getJSONObject("charge");
                        Log.d("lulu", "charge" + charge);
                        //Log.d("lulu","json"+json);
                        Intent intent = new Intent(BossTicketPay.this, PaymentActivity.class);
                        intent.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
                        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                    } else {
                        Toast.makeText(BossTicketPay.this, jsonObject.getString("resultNote"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                Toast.makeText(BossTicketPay.this, "网络请求失败，请稍后重试", 0).show();
            }
        });

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AlipayKeys.SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();
                    Log.d(AlipayKeys.TAG, "resultStatus=" + resultStatus
                            + " ,AliPay result=" + resultInfo);

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        RequestTicketCount();
                        Toast.makeText(BossTicketPay.this, "支付成功",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BossTicketPay.this, MyOrderActivity.class));
                        BossTicketPay.this.finish();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(BossTicketPay.this, "支付结果确认中",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(BossTicketPay.this, "支付失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

    };

    private static final int REQUEST_CODE_PAYMENT = 1;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("lulu", "resultCode" + resultCode);
        //支付页面返回处理
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - 支付成功
	             * "fail"    - 支付失败
	             * "cancel"  - 取消支付
	             * "invalid" - 支付插件未安装（一般是微信客户端未安装的情况）
	             */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                //showMsg(result, errorMsg, extraMsg);
                Log.e("lulu", errorMsg + "   " + extraMsg);
                startActivity(new Intent(BossTicketPay.this, MyOrderActivity.class));
                BossTicketPay.this.finish();
            }
        }
    }

    private void AliPay() {
        AlipayKeys alipayKeys = new AlipayKeys();

        alipayKeys.pay(this, orderId, "boss ticket", price + "", mHandler);
        Toast.makeText(this, "请求支付中...请稍后", Toast.LENGTH_SHORT)
                .show();
    }

    private void RequestTicketCount() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("cmd", "getuserBossNum");
            obj.put("userName", userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams();
        params.put("json", obj.toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new com.loopj.android.http.AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                try {
                    JSONObject obj = new JSONObject(new String(arg2));
                    if ("0".equals(obj.getString("result"))) {
                        count = obj.getString("bossTicket");
                        ((TextView) findViewById(R.id.TicketCount)).setText("" + count + "张");
                        SharedPreferenceUtils.saveBossTicketCount(BossTicketPay.this, count);
                    } else {
                        Toast.makeText(BossTicketPay.this, "请求失败，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {

            }
        });
    }

}
