package com.android.app.buystoreapp.business;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.buystore.utils.SharedPreferenceUtils;
import com.android.app.buystoreapp.R;
import com.android.app.buystoreapp.TabPersonal;
import com.android.app.buystoreapp.adapter.ShopCategoryAdapter;
import com.android.app.buystoreapp.bean.GsonBackOnlyResult;
import com.android.app.buystoreapp.bean.GsonShopStoreBack;
import com.android.app.buystoreapp.bean.GsonShopStoreCmd;
import com.android.app.buystoreapp.bean.GsonStarShopCmd;
import com.android.app.buystoreapp.bean.ShopBean;
import com.android.app.buystoreapp.bean.ShopCategoryBean;
import com.android.app.buystoreapp.setting.LoginActivity;
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
import com.squareup.picasso.Picasso;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * 店铺详细界面。包括店铺地址，联系电话以及商品分类 提供店铺收藏功能
 * 
 * @author Mikes Lee
 * 
 */
public class ShopStoreActivity extends Activity {
    @ResInject(id = R.string.web_url, type = ResType.String)
    private String url;

    private String shopID;
    private String shopIcon;
    private String shopName;
    private String shopLon;
    private String shopLat;
    private String mCurrentUserName;

    private ImageView mshopIconImage;
    // private TextView mshopPhoneText;
    private TextView mshopAddressText;
    private TextView mshopPhoneText;
    private String mShopPhoneString;

    private ListView mShopCategoryLV;
    private ShopCategoryAdapter mShopCategoryAdapter;
    private List<ShopCategoryBean> mShopCategoryListData = new ArrayList<ShopCategoryBean>();

    private boolean isLogin;
    private boolean isStared;

    @ViewInject(R.id.id_custom_title_text)
    private TextView mTitleText;
    @ViewInject(R.id.id_custom_favourite_action)
    private ImageView mFavouriteImage;

    @ViewInject(R.id.id_shop_store_empty)
    private View emptyView;
    @ViewInject(R.id.id_shop_store_empty_fail)
    private View emptyFailureView;
    @ViewInject(R.id.id_shop_store_empty_done)
    private View emptyDoneView;

    private OnItemClickListener mShopCategoryAdapterListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position,
                long arg3) {
            String categoryID = mShopCategoryListData.get(position)
                    .getCategoryID();
            Intent intent = new Intent(ShopStoreActivity.this,
                    ShopStoreDetailActivity.class);
            intent.putExtra("categoryID", categoryID);
            intent.putExtra("shopID", shopID);
            intent.putExtra("shopName", shopName);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.shop_store_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.custom_action_bar);
        ViewUtils.inject(this);
        mFavouriteImage.setVisibility(View.VISIBLE);

        mCurrentUserName = SharedPreferenceUtils.getCurrentUserInfo(this)
                .getUserName();

        initShopTitle();

        mShopCategoryLV = (ListView) findViewById(R.id.id_business_store_list);
        mShopCategoryAdapter = new ShopCategoryAdapter(this,
                mShopCategoryListData);
        mShopCategoryLV.setAdapter(mShopCategoryAdapter);
        mShopCategoryLV.setOnItemClickListener(mShopCategoryAdapterListener);
        mShopCategoryLV.setEmptyView(emptyView);
        requestShopTitleFromHttp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isLogin = SharedPreferenceUtils.getCurrentUserInfo(this).isLogin();
    }

    private void initShopTitle() {
        ShopBean shopBean = (ShopBean) getIntent().getSerializableExtra("shop");
        shopName = shopBean.getShopName();
        mTitleText.setText(shopName);
        shopID = shopBean.getShopID();
        mshopIconImage = (ImageView) findViewById(R.id.id_business_store_title_icon);
        mshopAddressText = (TextView) findViewById(R.id.id_business_store_address_name);
        mshopPhoneText = (TextView) findViewById(R.id.id_business_store_phone);
    }

    public void ClickPhone(View v) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                + mShopPhoneString));
        startActivity(callIntent);
    }

   /* public void ClickAddress(View v) {
        Intent locationIntent = new Intent(this, BaiduMapLocationActivity.class);
        locationIntent.putExtra("type", "shop");
        locationIntent.putExtra("shopName", shopName);
        locationIntent.putExtra("shopLon", shopLon);
        locationIntent.putExtra("shopLat", shopLat);
        startActivity(locationIntent);
        LogUtils.d("click address,shopName=" + shopName + ",shopLon=" + shopLon
                + ",shopLat" + shopLat);
    }*/

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

    @OnClick(R.id.id_custom_favourite_action)
    public void onFavouriteClicked(View v) {
        if (isLogin) {
            if (isStared) {
                isStared = false;
                mFavouriteImage
                        .setImageResource(R.drawable.ic_custom_favourite_unselected);
            } else {
                isStared = true;
                mFavouriteImage
                        .setImageResource(R.drawable.ic_custom_favourite_selected);
            }
            startFavoriteShop(isStared);
        } else {
            Toast.makeText(this, getString(R.string.personal_no_login_toast),
                    Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.putExtra("type", TabPersonal.FLAG_LOGIN);
            startActivityForResult(loginIntent, Activity.RESULT_FIRST_USER);
        }
    }

    private void startFavoriteShop(final boolean isStar) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        final Gson gson = new Gson();
        GsonStarShopCmd gsonStarShopCmd = new GsonStarShopCmd(
                "saveShopFavourite", shopID, mCurrentUserName, isStar ? "0"
                        : "1");
        requestParams.put("json", gson.toJson(gsonStarShopCmd));
        LogUtils.d("startFavoriteShop param=" + gson.toJson(gsonStarShopCmd));
        client.get(url, requestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                LogUtils.d("startFavoriteShop result=" + new String(arg2));
                try {
                    GsonBackOnlyResult gsonBackOnlyResult = gson.fromJson(
                            new String(arg2),
                            new TypeToken<GsonBackOnlyResult>() {
                            }.getType());

                    String result = gsonBackOnlyResult.getResult();
                    String resultNote = gsonBackOnlyResult.getResultNote();
                    if ("0".equals(result)) {
                        if (isStar) {
                            Toast.makeText(
                                    ShopStoreActivity.this,
                                    getResources().getString(
                                            R.string.news_star_save_success),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(
                                    ShopStoreActivity.this,
                                    getResources().getString(
                                            R.string.news_star_cancel_success),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ShopStoreActivity.this, resultNote,
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    LogUtils.e("startFavoriteShop error:", e);
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                    Throwable arg3) {
                LogUtils.e("startFavoriteShop failure");
            }
        });
    }

    private void updateFavouriteState(boolean stared) {
        isStared = stared;
        if (stared) {
            mFavouriteImage
                    .setImageResource(R.drawable.ic_custom_favourite_selected);
        } else {
            mFavouriteImage
                    .setImageResource(R.drawable.ic_custom_favourite_unselected);
        }
    }

    private void requestShopTitleFromHttp() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        final Gson gson = new Gson();
        GsonShopStoreCmd gsonShopDetailCmd = new GsonShopStoreCmd(
                "getShopDetail", mCurrentUserName, shopID);
        requestParams.put("json", gson.toJson(gsonShopDetailCmd));
        LogUtils.d("requestShopTitleFromHttp param="
                + gson.toJson(gsonShopDetailCmd));

        client.get(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                LogUtils.d("requestShopTitleFromHttp json=" + new String(arg2));
                try {
                    GsonShopStoreBack gsonShopDetailBack = gson.fromJson(
                            new String(arg2),
                            new TypeToken<GsonShopStoreBack>() {
                            }.getType());

                    String result = gsonShopDetailBack.getResult();
                    if ("0".equals(result)) {
                        mshopAddressText.setText(gsonShopDetailBack
                                .getShopAdress());
                        mShopPhoneString = gsonShopDetailBack.getShopPhone();
                        String first = mShopPhoneString.substring(0, 3);
                        String middle = mShopPhoneString.substring(3, 7);
                        String last = mShopPhoneString.substring(7,
                                mShopPhoneString.length());
                        middle = middle.replaceAll("[0-9]", "*");
                        mshopPhoneText.setText(first + middle + last);
                        shopLon = gsonShopDetailBack.getShopLon();
                        shopLat = gsonShopDetailBack.getShopLat();
                        shopIcon = gsonShopDetailBack.getShopIcon();
                        shopName = gsonShopDetailBack.getShopName();
                        mTitleText.setText(shopName);
                        if (!TextUtils.isEmpty(shopIcon)) {
                            Picasso.with(ShopStoreActivity.this).load(shopIcon)
                                    .error(R.drawable.ic_default)
                                    .placeholder(R.drawable.ic_default)
                                    .into(mshopIconImage);
                        } else {
                            Picasso.with(ShopStoreActivity.this)
                                    .load(R.drawable.ic_default)
                                    .into(mshopIconImage);
                        }
                        String shopIsFavourite = gsonShopDetailBack
                                .getShopIsFavourite();
                        updateFavouriteState("0".equals(shopIsFavourite));

                        if (gsonShopDetailBack.getShopCategoryList().size() == 0) {
                            emptyView.setVisibility(View.GONE);
                            mShopCategoryLV.setEmptyView(emptyDoneView);
                        } else {
                            mShopCategoryListData.addAll(gsonShopDetailBack
                                    .getShopCategoryList());
                            mShopCategoryAdapter.notifyDataSetChanged();
                        }
                    } else {
                        emptyView.setVisibility(View.GONE);
                        mShopCategoryLV.setEmptyView(emptyFailureView);
                    }
                } catch (Exception e) {
                    LogUtils.e("requestShopTitleFromHttp error:", e);
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                    Throwable arg3) {
                LogUtils.d("requestShopTitleFromHttp failure");
            }
        });
    }

}
