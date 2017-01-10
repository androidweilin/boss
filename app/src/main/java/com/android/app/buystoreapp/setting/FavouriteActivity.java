package com.android.app.buystoreapp.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.app.buystoreapp.BaseWebActivity;
import com.android.app.buystoreapp.R;
import com.android.app.buystoreapp.adapter.BusinessAdapter;
import com.android.app.buystoreapp.adapter.HomeCommodityAdapter;
import com.android.app.buystoreapp.adapter.NewsAdapter;
import com.android.app.buystoreapp.bean.CommodityBean;
import com.android.app.buystoreapp.bean.GsonCommodityRightBack;
import com.android.app.buystoreapp.bean.GsonNewsBack;
import com.android.app.buystoreapp.bean.GsonNewsCmd;
import com.android.app.buystoreapp.bean.GsonShopback;
import com.android.app.buystoreapp.bean.NewsInfo;
import com.android.app.buystoreapp.bean.ShopBean;
import com.android.app.buystoreapp.business.ShopStoreActivity;
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

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends Activity {
    private String flag = "";
    @ViewInject(R.id.id_personal_favourite_list)
    private ListView mFavouriteListView;
    private int nowPage = 1;
    private int totalPage = 0;
    private String mCurrentUserName;

    private static final int HANDLE_NEWS = 0x51;
    private static final int HANDLE_SHOP = 0x52;
    private static final int HANDLE_COMMODITY = 0x53;
    private static final int HANDLE_MESSAGE = 0x54;

    @ViewInject(R.id.id_empty_fail)
    private View emptyFailureView;
    @ViewInject(R.id.id_search_empty)
    private View emptyView;

    // news favorite
    private NewsAdapter mNewsAdapter;
    private List<NewsInfo> mNewsDataBeans = new ArrayList<NewsInfo>();

    // shop favorite
    private BusinessAdapter mBusinessAdapter;
    private List<ShopBean> mShopBeans = new ArrayList<ShopBean>();

    // commodity favorite
    private HomeCommodityAdapter mCommodityAdapter;
    private List<CommodityBean> mCommodityBeans = new ArrayList<CommodityBean>();

    // message
    private NewsAdapter mMessageAdapter;
    private List<NewsInfo> mMessageDatas = new ArrayList<NewsInfo>();

    @ViewInject(R.id.id_custom_back_image)
    private ImageView mBackImage;
    @ViewInject(R.id.id_custom_title_text)
    private TextView mTitleText;
    @ViewInject(R.id.id_custom_delete_action)
    private ImageView mDeleteImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.favourite_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.custom_action_bar);

        ViewUtils.inject(this);
        mTitleText.setText(getIntent().getStringExtra("title"));
        flag = getIntent().getStringExtra("flag");
        mCurrentUserName = getIntent().getStringExtra("userName");
        mFavouriteListView.setEmptyView(emptyView);

        if ("commodity".equals(flag)) {
            requestFavouriteCommodity();
        } else if ("news".equals(flag)) {
            requestFavouriteNews();
        } else if ("shop".equals(flag)) {
            requestFavouriteShop();
        } else if ("message".equals(flag)) {
            requestUserMessage();
        }
    }

    @OnClick(R.id.id_custom_back_image)
    public void onCustomBarClicked(View v) {
        switch (v.getId()) {
        case R.id.id_custom_back_image:
            this.finish();
            break;
        default:
            break;
        }
    }

    private Handler favHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case HANDLE_NEWS:
                mNewsAdapter = new NewsAdapter(FavouriteActivity.this,
                        mNewsDataBeans);
                mFavouriteListView.setAdapter(mNewsAdapter);
                mFavouriteListView
                        .setOnItemClickListener(mNewsItemClickListener);
                break;
            case HANDLE_SHOP:
                mBusinessAdapter = new BusinessAdapter(FavouriteActivity.this,
                        mShopBeans, true);
                mFavouriteListView.setAdapter(mBusinessAdapter);
                mFavouriteListView
                        .setOnItemClickListener(mShopItemClickListener);
                break;
            case HANDLE_COMMODITY:
                mCommodityAdapter = new HomeCommodityAdapter(
                        FavouriteActivity.this, mCommodityBeans, true);
                mFavouriteListView.setAdapter(mCommodityAdapter);
                mFavouriteListView
                        .setOnItemClickListener(mCommodityItemClickListener);
                break;
            case HANDLE_MESSAGE:
                mMessageAdapter = new NewsAdapter(FavouriteActivity.this,
                        mMessageDatas);
                mFavouriteListView.setAdapter(mMessageAdapter);
                mFavouriteListView
                        .setOnItemClickListener(mMessageItemClickListener);
                break;
            default:
                break;
            }
        };
    };

    private OnItemClickListener mNewsItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                long arg3) {
           /* String url = mNewsDataBeans.get(position).getNewsUrl().toString();
            String newsID = mNewsDataBeans.get(position).getNewsID().toString();
            String newsState = mNewsDataBeans.get(position).getStateType()
                    .toString();*/
            Intent intent = new Intent(FavouriteActivity.this,
                    BaseWebActivity.class);
            /*intent.putExtra("url", url);
            intent.putExtra("newsID", newsID);
            intent.putExtra("state", newsState);*/
            Bundle bundle = new Bundle();
            bundle.putSerializable("newsInfo", mNewsDataBeans.get(position));
           intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };
    private void requestFavouriteNews() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        final Gson gson = new Gson();
        // 搜索资讯收藏
        GsonNewsCmd gsonNewsCmd = new GsonNewsCmd("getNews", mCurrentUserName,
                "2", "20", String.valueOf(nowPage), "0");
        String param = gson.toJson(gsonNewsCmd);
        params.put("json", param);
        LogUtils.d("requestFavouriteNews param=" + param);

        client.get(getResources().getString(R.string.web_url), params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        GsonNewsBack gsonNewsBack = gson.fromJson(new String(
                                arg2), new TypeToken<GsonNewsBack>() {
                        }.getType());
                        LogUtils.d("FavouriteActivity news ,json="+
                               new String(arg2));

                        try {
                            String result = gsonNewsBack.getResult();
                            totalPage = Integer.valueOf(gsonNewsBack
                                    .getTotalPage());

                            if ("1".equals(result)) {// failure
                                emptyView.setVisibility(View.GONE);
                                mFavouriteListView
                                        .setEmptyView(emptyFailureView);
                            } else {
                                mNewsDataBeans.clear();
                                mNewsDataBeans.addAll(gsonNewsBack
                                        .getNewsList());
                                favHandler.sendEmptyMessage(HANDLE_NEWS);
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

    private OnItemClickListener mShopItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                long arg3) {
            Intent intent = new Intent(FavouriteActivity.this,
                    ShopStoreActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("shop", mShopBeans.get(position));
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    private void requestFavouriteShop() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        final Gson gson = new Gson();
        // 商铺收藏
        String param = "{\"cmd\":\"getShopFavouriteList\",\"userName\":\"aaa\"}";
        param = param.replace("aaa", mCurrentUserName);
        params.put("json", param);
        LogUtils.d("requestFavouriteShop param=" + param);

        client.get(getResources().getString(R.string.web_url), params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        LogUtils.d(" FavouriteActivity requestFavouriteShop json="
                                + new String(arg2));
                        try {
                            GsonShopback gsonShopback = gson.fromJson(
                                    new String(arg2),
                                    new TypeToken<GsonShopback>() {
                                    }.getType());

                            String result = gsonShopback.getResult();
                            if ("0".equals(result)) {
                                mShopBeans.clear();
                                mShopBeans.addAll(gsonShopback.getShopList());
                                favHandler.sendEmptyMessage(HANDLE_SHOP);
                            } else {
                                emptyView.setVisibility(View.GONE);
                                mFavouriteListView
                                        .setEmptyView(emptyFailureView);
                            }
                        } catch (Exception e) {
                            LogUtils.e("requestFavouriteShop error:", e);
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                            Throwable arg3) {
                    }
                });
    }
    
    private OnItemClickListener mMessageItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                long arg3) {
            Intent intent = new Intent(FavouriteActivity.this,
                    BaseWebActivity.class);
            intent.putExtra("type", "message");
            intent.putExtra("url", mMessageDatas.get(position).getNewsUrl());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    };

    private OnItemClickListener mCommodityItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                long arg3) {
            Intent intent = new Intent(FavouriteActivity.this,
                    ShopDetailInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("commodity", mCommodityBeans.get(position));
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    };

    private void requestFavouriteCommodity() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        final Gson gson = new Gson();
        // 商品收藏
        String param = "{\"cmd\":\"getCommodityFavouriteList\",\"userName\":\"aaa\"}";
        param = param.replace("aaa", mCurrentUserName);
        params.put("json", param);
        LogUtils.d("requestFavouriteCommodity param=" + param);

        client.get(getResources().getString(R.string.web_url), params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        LogUtils.d("requestFavouriteCommodity--> result: "
                                + new String(arg2));
                        try {
                            GsonCommodityRightBack gsonRightBack = gson
                                    .fromJson(
                                            new String(arg2),
                                            new TypeToken<GsonCommodityRightBack>() {
                                            }.getType());

                            String result = gsonRightBack.getResult();
                            if ("0".equals(result)) {
                                mCommodityBeans.clear();
                                mCommodityBeans.addAll(gsonRightBack
                                        .getCommodityList());
                                favHandler.sendEmptyMessage(HANDLE_COMMODITY);
                            } else {
                                emptyView.setVisibility(View.GONE);
                                mFavouriteListView
                                        .setEmptyView(emptyFailureView);
                            }
                        } catch (Exception e) {
                            LogUtils.e("requestFavouriteCommodity error:", e);
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                            Throwable arg3) {
                    }
                });
    }

    private void requestUserMessage() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String param = "{\"cmd\":\"getUserMessage\",\"userName\":\"aaa\"}";
        param = param.replace("aaa", mCurrentUserName);
        params.put("json", param);
        LogUtils.d("requestUserMessage param=" + param);

        client.get(getResources().getString(R.string.web_url), params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        LogUtils.d(" FavouriteActivity requestUserMessage--> result: "
                                + new String(arg2));
                        try {
                            GsonNewsBack gsonNewsBack = new Gson().fromJson(
                                    new String(arg2),
                                    new TypeToken<GsonNewsBack>() {
                                    }.getType());
                            String result = gsonNewsBack.getResult();
                            if ("0".equals(result)) {
                                mMessageDatas.clear();
                                mMessageDatas.addAll(gsonNewsBack.getNewsList());
                                favHandler.sendEmptyMessage(HANDLE_MESSAGE);
                            } else {
                                emptyView.setVisibility(View.GONE);
                                mFavouriteListView
                                        .setEmptyView(emptyFailureView);
                            }
                        } catch (Exception e) {
                            LogUtils.e(
                                    " FavouriteActivity requestUserMessage--> error:",
                                    e);
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                            Throwable arg3) {
                    }
                });
    }
}
