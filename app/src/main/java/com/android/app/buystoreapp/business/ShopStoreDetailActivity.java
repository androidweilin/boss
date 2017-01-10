package com.android.app.buystoreapp.business;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.buystore.utils.SharedPreferenceUtils;
import com.android.app.buystore.utils.expandtab.ExpandTabView;
import com.android.app.buystore.utils.expandtab.ViewRightCommodity;
import com.android.app.buystoreapp.R;
import com.android.app.buystoreapp.adapter.HomeCommodityAdapter;
import com.android.app.buystoreapp.bean.CityInfoBean;
import com.android.app.buystoreapp.bean.CommodityBean;
import com.android.app.buystoreapp.bean.GsonCommodityRightBack;
import com.android.app.buystoreapp.bean.GsonShopDetailCmd;
import com.android.app.buystoreapp.bean.GsonShopDetailback;
import com.android.app.buystoreapp.crash.CrashApplication;
import com.android.app.buystoreapp.goods.ShopDetailInfoActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.view.SwipeRefreshLayoutUpDown;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * 店铺详情下的某一分类商品下的所有商品
 * 
 * @author Mikes Lee
 * 
 */
public class ShopStoreDetailActivity extends Activity implements
        OnItemClickListener, SwipeRefreshLayoutUpDown.OnRefreshListener, SwipeRefreshLayoutUpDown.OnLoadListener {
   // private static final String PAGE_SIZE = "20";
	
	private static final String PAGE_SIZE = "20";
    private Context mContext;
    private int nowPage = 1;
    private int totalPage;
    /**
     * 0 由点击店铺页面下的商品分类进来 1 由点击商品页面的右侧商品分类进来
     */
    private int flag = 0;

    private String shopID;
    private String categoryID;
    private String cityID;
    
    /**
     * 用户经纬度
     */
    private String userLat;
    private String userLong;
    
    /**
     * 排序方式 0综合，1 销量由高到低 2销量由低到高 3价格由低到高 4价格由高到低
     */
    private String mOrderBy = "0";

    @ViewInject(R.id.id_shop_store_detail_list)
    private ListView mshopDetailLV;
    private HomeCommodityAdapter mshopDetailAdapter;
    private List<CommodityBean> mshopDetailList = new ArrayList<CommodityBean>();

    SwipeRefreshLayoutUpDown mSwipeLayout;

    @ViewInject(R.id.id_shop_store_expandtab_view)
    private ExpandTabView expandTabView;
    private ArrayList<View> mViewArray;
    private ViewRightCommodity viewRight;

    @ViewInject(R.id.id_custom_title_text)
    private TextView mTitleText;

    @ViewInject(R.id.id_shop_store_empty)
    private View emptyView;
    @ViewInject(R.id.id_shop_store_empty_fail)
    private View emptyFailView;
    @ViewInject(R.id.id_shop_store_empty_done)
    private View emptyDoneView;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.shop_store_detail_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.custom_action_bar);
        ViewUtils.inject(this);
        mContext = this.getBaseContext();
        Intent dataIntent = getIntent();
        mTitleText.setText(dataIntent.getStringExtra("shopName"));
        
        CityInfoBean cityInfoBean = SharedPreferenceUtils.getCurrentCityInfo(this);
        //userLat = cityInfoBean.getCityLat();
        //userLong = cityInfoBean.getCityLong();
        userLat = ""+CrashApplication.latitude;
        userLong = ""+CrashApplication.longitude;

        mshopDetailLV.setEmptyView(emptyView);

        mSwipeLayout = (SwipeRefreshLayoutUpDown) findViewById(R.id.id_shop_store_detail_list_container);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setOnLoadListener(this);
        mSwipeLayout.setColor(R.color.holo_blue_bright,
                R.color.holo_green_light, R.color.holo_orange_light,
                R.color.holo_red_light);
        mSwipeLayout.setMode(SwipeRefreshLayoutUpDown.Mode.BOTH);
        mSwipeLayout.setLoadNoFull(false);

        flag = dataIntent.getIntExtra("where", 0);
        if (flag == 1) {// from goods
            mshopDetailAdapter = new HomeCommodityAdapter(this, mshopDetailList);
            mshopDetailLV.setOnItemClickListener(this);
            View headerView = new View(this);
            headerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    110));
            mshopDetailLV.addHeaderView(headerView);
            
            cityID = dataIntent.getStringExtra("cityID");
            categoryID = dataIntent.getStringExtra("categoryID");
            refreshRightCommodity();
        } else {// from business
            mshopDetailAdapter = new HomeCommodityAdapter(this, mshopDetailList,true);
            mshopDetailLV.setOnItemClickListener(shopItemClickListener);
            shopID = dataIntent.getStringExtra("shopID");
            categoryID = dataIntent.getStringExtra("categoryID");
            requestShopDetailFromHttp();
        }
        mshopDetailLV.setAdapter(mshopDetailAdapter);
        
        if (flag == 1) {
            expandTabView.setVisibility(View.VISIBLE);
            viewRight = new ViewRightCommodity(mContext);
            mViewArray = new ArrayList<View>();
            mViewArray.add(viewRight);
            ArrayList<String> mTextArray = new ArrayList<String>();
            mTextArray.add("1");
            expandTabView.setValue(mTextArray, mViewArray,0);
            expandTabView.setTitle(viewRight.getShowText(), 0);
            viewRight.setOnSelectListener(new ViewRightCommodity.OnSelectListener() {
                @Override
                public void getValue(String distance, String showText) {
                    onRefreshTabTitle(viewRight, showText);
                    nowPage = 1;
                    mOrderBy = distance;
                    refreshRightCommodity();
                }
            });
        } else {
            expandTabView.setVisibility(View.GONE);
        }
    }
    
    private void onRefreshTabTitle(View view, String showText) {
        expandTabView.onPressBack();
        int position = getPositon(view);
        if (position >= 0 && !expandTabView.getTitle(position).equals(showText)) {
            expandTabView.setTitle(showText, position);
        }
    }
    
    private int getPositon(View tView) {
        for (int i = 0; i < mViewArray.size(); i++) {
            if (mViewArray.get(i) == tView) {
                return i;
            }
        }
        return -1;
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
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long arg3) {
        Intent intent = new Intent(this, ShopDetailInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("commodity", mshopDetailList.get(position - 1));
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    
    private OnItemClickListener shopItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                long arg3) {
            Intent intent = new Intent(ShopStoreDetailActivity.this, ShopDetailInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("commodity", mshopDetailList.get(position));
            bundle.putString("des", mshopDetailList.get(position).getProDes());
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    };

    /**
     * 请求商铺详细
     * 
     */
    private void requestShopDetailFromHttp() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        final Gson gson = new Gson();
        GsonShopDetailCmd gsonShopDetailCmd = new GsonShopDetailCmd(
                "getShopDetailComList", categoryID, shopID, PAGE_SIZE,
                String.valueOf(nowPage));
        requestParams.put("json", gson.toJson(gsonShopDetailCmd));
        LogUtils.d("requestShopDetailFromHttp param="
                + gson.toJson(gsonShopDetailCmd));

        client.get(getString(R.string.web_url), requestParams,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        LogUtils.d("requestShopDetailFromHttp json="
                                + new String(arg2));
                        try {
                            GsonShopDetailback gsonShopDetailback = gson
                                    .fromJson(
                                            new String(arg2),
                                            new TypeToken<GsonShopDetailback>() {
                                            }.getType());

                            String result = gsonShopDetailback.getResult();
                            if ("0".equals(result)) {
                                totalPage = Integer.valueOf(gsonShopDetailback
                                        .getTotalPage());
                                if (gsonShopDetailback.getCommodityList()
                                        .size() == 0) {
                                    emptyView.setVisibility(View.GONE);
                                    mshopDetailLV.setEmptyView(emptyDoneView);
                                } else {
                                    mshopDetailList.clear();
                                    mshopDetailList.addAll(gsonShopDetailback
                                            .getCommodityList());
                                    mshopDetailAdapter.notifyDataSetChanged();
                                }
                            } else {
                                emptyView.setVisibility(View.GONE);
                                mshopDetailLV.setEmptyView(emptyFailView);
                            }
                            mSwipeLayout.setRefreshing(false);
                        } catch (Exception e) {
                            LogUtils.e("requestShopDetailFromHttp error:", e);
                            mSwipeLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                            Throwable arg3) {
                        mSwipeLayout.setRefreshing(false);
                    }
                });
    }

    private void loadmoreShopDetailFromHttp() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        final Gson gson = new Gson();
        GsonShopDetailCmd gsonShopDetailCmd = new GsonShopDetailCmd(
                "getShopDetailComList", categoryID, shopID, PAGE_SIZE,
                String.valueOf(nowPage));
        requestParams.put("json", gson.toJson(gsonShopDetailCmd));
        LogUtils.d("requestShopDetailFromHttp param="
                + gson.toJson(gsonShopDetailCmd));

        client.get(getString(R.string.web_url), requestParams,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        LogUtils.d("requestShopDetailFromHttp json="
                                + new String(arg2));
                        try {
                            GsonShopDetailback gsonShopDetailback = gson
                                    .fromJson(
                                            new String(arg2),
                                            new TypeToken<GsonShopDetailback>() {
                                            }.getType());

                            String result = gsonShopDetailback.getResult();
                            if ("0".equals(result)) {
                                totalPage = Integer.valueOf(gsonShopDetailback
                                        .getTotalPage());
                                mshopDetailList.addAll(gsonShopDetailback
                                        .getCommodityList());
                                mshopDetailAdapter.notifyDataSetChanged();
                            } else {
                                emptyView.setVisibility(View.GONE);
                                mshopDetailLV.setEmptyView(emptyFailView);
                            }
                            mSwipeLayout.setLoading(false);
                        } catch (Exception e) {
                            LogUtils.e("requestShopDetailFromHttp error:", e);
                            mSwipeLayout.setLoading(false);
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                            Throwable arg3) {
                        mSwipeLayout.setLoading(false);
                    }
                });
    }

    /**
     * 
     * categoryID
     *            二级分类ID subCategoryID
     * cityID
     *            城市ID
     * orderBy
     *            排序方式 0综合，1 销量由高到低 2销量由低到高 3价格由低到高 4价格由高到低
     * nowPage
     *            1为第一页
     * 
     *            排序功能未实现
     */
    private void refreshRightCommodity() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        final Gson gson = new Gson();
        String param = "{\"cmd\":\"getCommodity\",\"cityID\":\"aaa\",\"categoryID\":\"bbb\",\"orderBy\":\"ccc\",\"pageSize\":\"ddd\",\"nowPage\":\"eee\",\"userLat\":\"fff\",\"userLong\":\"ggg\"}";
        param = param.replace("aaa", cityID);
        param = param.replace("bbb", categoryID);
        param = param.replace("ccc", mOrderBy);
        param = param.replace("ddd", PAGE_SIZE);
        param = param.replace("eee", String.valueOf(nowPage));
        param = param.replace("fff", userLat);
        param = param.replace("ggg", userLong);

        requestParams.put("json", param);
        LogUtils.d("refreshRightCommodity,param=" + param);

        client.get(getString(R.string.web_url), requestParams,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        LogUtils.d("ReleaseChoiceClassActivity refreshRightCommodity--> result: "
                                + new String(arg2));
                        try {
                            GsonCommodityRightBack gsonRightBack = gson
                                    .fromJson(
                                            new String(arg2),
                                            new TypeToken<GsonCommodityRightBack>() {
                                            }.getType());

                            String result = gsonRightBack.getResult();
                            if ("0".equals(result)) {
                                totalPage = Integer.valueOf(gsonRightBack
                                        .getTotalPage());
                                if (gsonRightBack.getCommodityList().size() == 0) {
                                    emptyView.setVisibility(View.GONE);
                                    mshopDetailLV.setEmptyView(emptyDoneView);
                                } else {
                                    mshopDetailList.clear();
                                    mshopDetailList.addAll(gsonRightBack
                                            .getCommodityList());
                                    mshopDetailAdapter.notifyDataSetChanged();
                                }
                            } else {
                                emptyView.setVisibility(View.GONE);
                                mshopDetailLV.setEmptyView(emptyFailView);
                            }
                            mSwipeLayout.setRefreshing(false);
                        } catch (Exception e) {
                            LogUtils.e("refreshRightCommodity error:", e);
                            mSwipeLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                            Throwable arg3) {
                        mSwipeLayout.setRefreshing(false);
                    }
                });
    }

    private void loadmoreRightCommodity() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        final Gson gson = new Gson();
        String param = "{\"cmd\":\"getCommodity\",\"cityID\":\"aaa\",\"categoryID\":\"bbb\",\"orderBy\":\"ccc\",\"pageSize\":\"ddd\",\"nowPage\":\"eee\"}";
        param = param.replace("aaa", cityID);
        param = param.replace("bbb", categoryID);
        param = param.replace("ccc", mOrderBy);
        param = param.replace("ddd", PAGE_SIZE);
        param = param.replace("eee", String.valueOf(nowPage));

        requestParams.put("json", param);
        LogUtils.d("loadmoreRightCommodity,param=" + param);

        client.get(getString(R.string.web_url), requestParams,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        LogUtils.d("ReleaseChoiceClassActivity loadmoreRightCommodity--> result: "
                                + new String(arg2));
                        try {
                            GsonCommodityRightBack gsonRightBack = gson
                                    .fromJson(
                                            new String(arg2),
                                            new TypeToken<GsonCommodityRightBack>() {
                                            }.getType());

                            String result = gsonRightBack.getResult();
                            if ("0".equals(result)) {
                                totalPage = Integer.valueOf(gsonRightBack
                                        .getTotalPage());
                                mshopDetailList.addAll(gsonRightBack
                                        .getCommodityList());
                                mshopDetailAdapter.notifyDataSetChanged();
                            } else {
                                emptyView.setVisibility(View.GONE);
                                mshopDetailLV.setEmptyView(emptyFailView);
                            }
                            mSwipeLayout.setLoading(false);
                        } catch (Exception e) {
                            LogUtils.e("loadmoreRightCommodity error:", e);
                            mSwipeLayout.setLoading(false);
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                            Throwable arg3) {
                        mSwipeLayout.setLoading(false);
                    }
                });
    }

    @Override
    public void onLoad() {
        if (nowPage < totalPage) {
            nowPage++;
            if (flag == 1) {
                loadmoreRightCommodity();
            } else {
                loadmoreShopDetailFromHttp();
            }
        } else {
            LogUtils.d("onLoad  no more datas");
            Toast.makeText(this, getString(R.string.no_more_data),
                    Toast.LENGTH_SHORT).show();
            mSwipeLayout.setLoading(false);
        }
    }

    @Override
    public void onRefresh() {
        nowPage = 1;
        if (flag == 1) {
            refreshRightCommodity();
        } else {
            requestShopDetailFromHttp();
        }
    }
}
