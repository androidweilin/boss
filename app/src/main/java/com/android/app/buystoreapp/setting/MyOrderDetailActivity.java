package com.android.app.buystoreapp.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.app.buystoreapp.BaseWebActivity;
import com.android.app.buystoreapp.R;
import com.android.app.buystoreapp.bean.CommodityBean;
import com.android.app.buystoreapp.bean.MyOrderBean;
import com.android.app.buystoreapp.goods.ShopDetailInfoActivity;
import com.android.app.buystoreapp.wallet.ToastUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class MyOrderDetailActivity extends Activity {
    
    private static final String DEFAULE_WL_NAME = "圆通速递";
    private static final String DEFAULE_WL_NUMBER = "500199715365";
    private static final String DEFAULE_WL_CODE = "yuantong";

    private String type = "";
    private MyOrderBean mOrderBean;
    private CommodityBean mCommodityBean;

    @ViewInject(R.id.id_myorder_detail_order_container)
    private View orderContainer;
    @ViewInject(R.id.id_myorder_detail_orderid)
    private TextView mOrderId;
    @ViewInject(R.id.id_myorder_detail_state)
    private TextView mOrderState;
    @ViewInject(R.id.id_myorder_detail_receivename)
    private TextView mOrderReceiveName;
    @ViewInject(R.id.id_myorder_detail_receivephone)
    private TextView mOrderReceivePhone;
    @ViewInject(R.id.id_myorder_detail_phone)
    private TextView mOrderPhone;
    @ViewInject(R.id.id_myorder_detail_receiveaddress)
    private TextView mOrderReceiveAddress;
    @ViewInject(R.id.id_myorder_detail_ordertotalprice)
    private TextView mOrderTotalPrice;
    @ViewInject(R.id.id_myorder_detail_orderwlname)
    private TextView mOrderWlName;
    @ViewInject(R.id.id_myorder_detail_orderwlnumber)
    private TextView mOrderWlNumber;

    @ViewInject(R.id.id_myorder_detail_commodity_container)
    private View commodityContainer;
    @ViewInject(R.id.id_myorder_detail_commodityname)
    private TextView mCommodityName;
    @ViewInject(R.id.id_myorder_detail_commodityintro)
    private TextView mCommodityIntro;
    @ViewInject(R.id.id_myorder_detail_commodityprice)
    private TextView mCommodityPrice;
    @ViewInject(R.id.id_myorder_detail_commoditynum)
    private TextView mCommodityBuyNum;
    @ViewInject(R.id.id_myorder_detail_commoditytotalprice)
    private TextView mCommodityTotalPrice;
    @ViewInject(R.id.id_myorder_detail_commodityToStore)
    private Button mCommodityGoStore;
    
    @ViewInject(R.id.id_custom_title_text)
    private TextView mTitleText;
    @ViewInject(R.id.id_myorder_detail_wl)
    private Button wlDetails;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.myorder_detail_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.custom_action_bar);
        ViewUtils.inject(this);

        type = getIntent().getStringExtra("type");
        if ("order".equals(type)) {
            mTitleText.setText("订单详情");
            mOrderBean = (MyOrderBean) getIntent().getSerializableExtra(
                    "orderBean");
            commodityContainer.setVisibility(View.GONE);
            orderContainer.setVisibility(View.VISIBLE);
            initOrderLayout();
        } else if ("commodity".equals(type)) {
            mTitleText.setText("已购买商品详情");
            mCommodityBean = (CommodityBean) getIntent().getSerializableExtra(
                    "commodityBean");
            commodityContainer.setVisibility(View.VISIBLE);
            orderContainer.setVisibility(View.GONE);
//            initCommodityLayout();
        }
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

    private void initOrderLayout() {
        mOrderId.setText(mOrderBean.getOrderID());
        String state = mOrderBean.getOrderState();
        switch (state) {
       /* case "0":// 已经处理
            mOrderState.setText("已经处理");
            break;
        case "1":// 未处理，可以取消
            mOrderState.setText("未付款");
            break;*/
        case "2":// 待收货
            mOrderState.setText("待收货");
            wlDetails.setEnabled(true);
            wlDetails.setBackgroundResource(R.drawable.app_btn_enabled_shape);
            break;
        case "3":// 已完成
            mOrderState.setText("已完成");
            wlDetails.setEnabled(true);
            wlDetails.setBackgroundResource(R.drawable.app_btn_enabled_shape);
            break;
        case "4":// 待发货
            mOrderState.setText("待发货");
            wlDetails.setEnabled(false);
            wlDetails.setBackgroundResource(R.drawable.app_btn_disabled_shape);
            break;
        case "5":// 待付款
            mOrderState.setText("未付款");
            wlDetails.setEnabled(false);
            wlDetails.setBackgroundResource(R.drawable.app_btn_disabled_shape);
            break;
        case "6":// 待评价
            mOrderState.setText("待评价");
            wlDetails.setEnabled(true);
            wlDetails.setBackgroundResource(R.drawable.app_btn_enabled_shape);
            break;
        default:
            break;
        }

        mOrderReceiveName.setText(mOrderBean.getReceiveName());
        mOrderReceivePhone.setText(mOrderBean.getReceivePhone());
        mOrderReceiveAddress.setText(mOrderBean.getReceiveAdress());
        mOrderPhone.setText(TextUtils.isEmpty(mOrderBean.getPhone()) ? "" : mOrderBean.getPhone());
        mOrderTotalPrice.setText(mOrderBean.getTotalMoney());
        mOrderWlName.setText(TextUtils.isEmpty(mOrderBean.getWlName()) ? "" : mOrderBean.getWlName());
        mOrderWlNumber.setText(TextUtils.isEmpty(mOrderBean.getWlNumber()) ? "" : mOrderBean.getWlNumber());
    }
    
    @OnClick(R.id.id_myorder_detail_wl)
    public void onLookWlInfo(View v) {
        if (TextUtils.isEmpty(mOrderBean.getWlName()) || TextUtils.isEmpty(mOrderBean.getWlNumber())) {
            ToastUtil.showMessageDefault(this,"暂无物流信息");
        	return;//正式上线打开
        }
        	
       // String wlUrl = "http://wap.kuaidi100.com/wap_result.jsp?rand=20120517&id=[快递公司编码]&fromWeb=null&&postid=[快递单号]";
        //http://m.kuaidi100.com/index_all.html?type=quanfengkuaidi&postid=123456
        StringBuilder wlUrl = new StringBuilder();
       /* wlUrl.append("http://wap.kuaidi100.com/wap_result.jsp?rand=20120517&id=")
            .append(mOrderBean.getWlCode())
            .append("&fromWeb=null&&postid=")
            .append(mOrderBean.getWlNumber());*/
        wlUrl.append("http://m.kuaidi100.com/index_all.html?type=")
        .append(mOrderBean.getWlCode())
        .append("&postid=").append(mOrderBean.getWlNumber());
       /* wlUrl.append("http://m.kuaidi100.com/index_all.html?type=")
        .append("yuantong")
        .append("&postid=").append("500199715365");*/
        LogUtils.d("wlurl="+wlUrl.toString());
        
        Intent webIntent = new Intent(this,BaseWebActivity.class);
        webIntent.putExtra("url", wlUrl.toString());
        webIntent.putExtra("type", "order");
        startActivity(webIntent);
    }

    /*private void initCommodityLayout() {
        mCommodityName.setText(mCommodityBean.getCommodityName());
        mCommodityIntro.setText(mCommodityBean.getCommodityIntro());
        mCommodityPrice.setText(TextUtils.isEmpty(mCommodityBean.getCommodityPrice()) ? "0" : mCommodityBean.getCommodityPrice());
        mCommodityBuyNum.setText(TextUtils.isEmpty(mCommodityBean.getCommodityBuyNum()) ? "0" : mCommodityBean.getCommodityBuyNum());
        
        float totalPrice = Float.valueOf(TextUtils.isEmpty(mCommodityBean.getCommodityPrice()) ? "0" : mCommodityBean.getCommodityPrice())
                * Float.valueOf(TextUtils.isEmpty(mCommodityBean.getCommodityBuyNum()) ? "0" : mCommodityBean.getCommodityBuyNum());
        mCommodityTotalPrice.setText(String.format("%1$.2f", totalPrice));
    }*/
    
    @OnClick(R.id.id_myorder_detail_commodityToStore)
    public void onGoStoreClicked(View v) {
        Intent intent = new Intent(MyOrderDetailActivity.this, ShopDetailInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("commodity", mCommodityBean);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
