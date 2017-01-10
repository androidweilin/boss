package com.android.app.buystoreapp.setting;

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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.pay.demo.AlipayKeys;
import com.alipay.sdk.pay.demo.PayResult;
import com.android.app.buystore.utils.SharedPreferenceUtils;
import com.android.app.buystoreapp.R;
import com.android.app.buystoreapp.base.BaseAct;
import com.android.app.buystoreapp.managementservice.PaySuccessActivity;
import com.android.app.buystoreapp.wallet.ToastUtil;
import com.android.app.utils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pingplusplus.android.PaymentActivity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class PayOrderActivity extends BaseAct implements OnClickListener {
    @ViewInject(R.id.id_custom_title_text)
	private TextView mTitleText;
    @ViewInject(R.id.id_custom_back_image) ImageButton back;
    @ViewInject(R.id.ll_zhifubao) LinearLayout alipayLayout;
    @ViewInject(R.id.ll5_weixin) LinearLayout weiXinLayout;
	//@ResInject(id= R.string.web_url,type=ResType.String)
	private String webUrl;
	private String orderId;
	private String price;
	private String body;
	public final static int PAY_ALIPY = 0x10;
	public final static int PAY_WEIXIN = 0x11;
	public final static int PAY_RECHARGE = 2;
	public final static int PAY_GOODS = 4;
	private static final int PAY_WALLET = 0x12;
	private String flag ="0";
	private String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_select_pay);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_action_bar);
     	ViewUtils.inject(this);
     	mTitleText.setText("订单支付");
        orderId = getIntent().getStringExtra("orderID");
        price = getIntent().getStringExtra("price");
     	body = getIntent().getStringExtra("body");
		flag = getIntent().getStringExtra("flag");
     	webUrl = getResources().getString(R.string.web_url);
     	back.setOnClickListener(this);
     	alipayLayout.setOnClickListener(this);
     	weiXinLayout.setOnClickListener(this);
		userId = SharedPreferenceUtils.getCurrentUserInfo(mContext).getUserId();
		initErrorPage();
		addIncludeLoading(true);
     	
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_zhifubao:
			AlipayKeys alipayKeys = new AlipayKeys();
	        alipayKeys.pay(this,
	                orderId,body,price,
	                mHandler);
	        Toast.makeText(this,
	                "请求支付中...请稍后", Toast.LENGTH_SHORT)
	                .show();
			break;
		case R.id.ll5_weixin:
			 Toast.makeText(this,
		                "请求支付中...请稍后", Toast.LENGTH_SHORT)
		                .show();
			sendWeiXinPay();
			break;
		case R.id.id_custom_back_image:
			finish();
			break;
		}
	}
	
	 public void sendWeiXinPay(){
	    	AsyncHttpClient httpClient = new AsyncHttpClient();
	    	RequestParams params = new RequestParams();
	    	float totalPrice = Float.parseFloat(price) * 100;
	    	int amount =(int)totalPrice;
	    	//int amount = 1;
	    	String json = "{\"cmd\":\"getCharge\",\"amount\":"+amount+",\"orderNo\":\""+orderId+"\",\"channel\":\"wx\",\"body\":\""+body+"\"}";
			params.put("json",json);
			Log.d("lulu",json);
			httpClient.post(webUrl,params,new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					Log.d("lulu",new String(arg2));
					try {
						JSONObject jsonObject = new JSONObject(new String(arg2));
						if("0".equals(jsonObject.getString("result"))){
							ToastUtil.showMessageDefault(PayOrderActivity.this,"支付信息请求成功");
							 String charge = jsonObject.getString("charge");
							//JSONObject json = jsonObject.getJSONObject("charge");
							Log.d("lulu","charge"+charge);
							//Log.d("lulu","json"+json);
							Intent intent = new Intent(PayOrderActivity.this, PaymentActivity.class);
							intent.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
							startActivityForResult(intent, REQUEST_CODE_PAYMENT);
						}else{
							ToastUtil.showMessageDefault(PayOrderActivity.this,jsonObject.getString("resultNote"));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					ToastUtil.showMessageDefault(PayOrderActivity.this,"网络请求失败，请稍后重试");
					
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
	                    Toast.makeText(PayOrderActivity.this, "支付成功",
	                            Toast.LENGTH_SHORT).show();
//	                    startActivity(new Intent(PayOrderActivity.this,MyOrderActivity.class));
						if ("1".equals(flag)) {//VIP购买
							updateOpenVip(orderId);
						}else{
							updateOrder(orderId, 1, 2, 2);
						}
	                } else {
	                    // 判断resultStatus 为非“9000”则代表可能支付失败
	                    // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
	                    if (TextUtils.equals(resultStatus, "8000")) {
	                        Toast.makeText(PayOrderActivity.this, "支付结果确认中",
	                                Toast.LENGTH_SHORT).show();
	                    } else {
	                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
	                        Toast.makeText(PayOrderActivity.this, "支付失败",
	                                Toast.LENGTH_SHORT).show();
	                    }
	                }
	                break;
	            }
	            default:
	                break;
	            }
	        };
	    };
	    
	    private static final int REQUEST_CODE_PAYMENT = 1;
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		    Log.d("lulu","resultCode"+resultCode);
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
		           Log.e("lulu", errorMsg +"   "+extraMsg);
					if (result.equals("success")){//跳转支付成功页面
						if ("1".equals(flag)){
							updateOpenVip(orderId);
						}else {
							updateOrder(orderId,1,2,1);
						}
					}else {//跳转我的订单
//						startActivity(new Intent(PayOrderActivity.this, com.android.app.buystoreapp.order.MyOrderActivity.class));
					}
                   PayOrderActivity.this.finish();
		        }
		    }
		}

	private void updateOpenVip(String orderId) {
		startWhiteLoadingAnim();
		JSONObject obj = new JSONObject();
		try {
			obj.put("cmd", "updateOpenVip");
			obj.put("userId", userId);
			obj.put("vipOrderNum", orderId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.e("修改Vip状态提交数据 obj==", obj.toString());
		HttpUtils.post(mContext, obj, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int i, Header[] headers, byte[] bytes) {
				Log.e("修改Vip状态返回数据 bytes==", new String(bytes));
				stopLoadingAnim();
				hideErrorPageState();
				try {
					JSONObject object = new JSONObject(new String(bytes));
					String result = object.getString("result");
					if ("0".equals(result)) {
						ToastUtil.showMessageDefault(PayOrderActivity.this, "Vip兑换成功");
						finish();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
				stopLoadingAnim();
			}
		}, new HttpUtils.RequestNetworkError() {
			@Override
			public void networkError() {
				stopLoadingAnim();
			}
		});

	}

	/**
	 * 改变订单状态
	 *
	 * @author likaihang
	 * creat at @time 16/11/07 13:49
	 */
	private void updateOrder(String orderId, int style, int status,int isFinished) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("cmd", "updateOrder");
			obj.put("style", style);
			obj.put("status", status);
			obj.put("payOrderId", orderId);
			obj.put("userStatus", 0);//买家
			obj.put("isFinished",isFinished);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d("改变状态---",obj.toString());
		HttpUtils.post(this, obj, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int i, Header[] headers, byte[] bytes) {
				JSONObject obj = null;
				try {
					obj = new JSONObject(new String(bytes));
					if (obj.getString("result").equals("0")) {
						startActivity(new Intent(PayOrderActivity.this,PaySuccessActivity.class));
						PayOrderActivity.this.finish();
					} else {
						ToastUtil.showMessageDefault(PayOrderActivity.this, obj.getString("resultNote"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

			}
		}, new HttpUtils.RequestNetworkError() {
			@Override
			public void networkError() {

			}
		});
	}

}
