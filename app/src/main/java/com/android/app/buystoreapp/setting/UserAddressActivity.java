package com.android.app.buystoreapp.setting;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.app.buystore.utils.SharedPreferenceUtils;
import com.android.app.buystoreapp.R;
import com.android.app.buystoreapp.adapter.AddressAdapter;
import com.android.app.buystoreapp.bean.AddressBean;
import com.android.app.buystoreapp.bean.GsonAddressBack;
import com.android.app.buystoreapp.bean.GsonAddressCmd;
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

public class UserAddressActivity extends Activity {
    @ViewInject(R.id.id_address_list)
    private ListView mListView;
    private AddressAdapter addressAdapter;
    private List<AddressBean> mDatas = new ArrayList<AddressBean>();

    @ViewInject(R.id.id_address_add)
    private ImageButton mAddAddress;

    @ResInject(id = R.string.web_url, type = ResType.String)
    private String url;
    private String mCurrentUserName;

    @ViewInject(R.id.id_custom_title_text)
    private TextView mTitleText;
    
    @ViewInject(R.id.id_empty)
    private View emptyView;
    @ViewInject(R.id.id_empty_fail)
    private View emptyFailureView;
    @ViewInject(R.id.id_empty_no_address)
    private View emptyAddress;
    
    /**
     * 0 查看个人信息时
     * 1 确认订单时
     */
    private int fromWhere = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.user_address_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.custom_action_bar);

        ViewUtils.inject(this);
        mTitleText.setText("收货地址管理");
        
        fromWhere = getIntent().getIntExtra("where", 0);
        if (fromWhere == 0) {
            mListView.setOnItemClickListener(fromZeroItemListener);
            mListView.setOnItemLongClickListener(null);
        } else if (fromWhere == 1){
            mListView.setOnItemClickListener(fromOneItemListener);
            mListView.setOnItemLongClickListener(fromOneItemLongClickListener);
        }

        mCurrentUserName = SharedPreferenceUtils.getCurrentUserInfo(this)
                .getUserName();
        addressAdapter = new AddressAdapter(this, mDatas);
        mListView.setAdapter(addressAdapter);
        mListView.setEmptyView(emptyView);
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
        requestAddressList();
    }

    @OnClick(R.id.id_address_add)
    public void onAddClick(View v) {
        Intent intent = new Intent(this, UserAddressItemActivity.class);
        intent.putExtra("type", "add");
        startActivity(intent);
    }

    private void requestAddressList() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        final Gson gson = new Gson();
        GsonAddressCmd gsonAddressCmd = new GsonAddressCmd("getAdress",
                mCurrentUserName);
        requestParams.put("json", gson.toJson(gsonAddressCmd));

        client.get(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                try {
                    LogUtils.d("requestAddressList result=" + new String(arg2));
                    GsonAddressBack gsonAddressBack = gson.fromJson(
                            new String(arg2), new TypeToken<GsonAddressBack>() {
                            }.getType());
                    String result = gsonAddressBack.getResult();
                    if ("0".equals(result)) {
                        mDatas.clear();
                        mDatas.addAll(gsonAddressBack.getAdressList());
                        addressAdapter.notifyDataSetChanged();
                    } else {
                        mDatas.clear();
                        addressAdapter.notifyDataSetChanged();
                        if (gsonAddressBack.getAdressList().size() == 0) {
                            emptyView.setVisibility(View.GONE);
                            mListView.setEmptyView(emptyAddress);
                        } else {
                            emptyView.setVisibility(View.GONE);
                            mListView.setEmptyView(emptyFailureView);
                        }
                    }
                } catch (Exception e) {
                    LogUtils.e("requestAddressList error:", e);
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                    Throwable arg3) {
                emptyView.setVisibility(View.GONE);
                mListView.setEmptyView(emptyFailureView);
            }
        });
    }

    private OnItemClickListener fromZeroItemListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                long arg3) {
            Intent intent = new Intent(UserAddressActivity.this, UserAddressItemActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("addressBean", mDatas.get(position));
            bundle.putString("type", "more");
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };
    
    private OnItemClickListener fromOneItemListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                long arg3) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("address", mDatas.get(position));
            intent.putExtras(bundle);
            setResult(UserAddressActivity.RESULT_OK,intent);
            UserAddressActivity.this.finish();
        }
    };
    
    private OnItemLongClickListener fromOneItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                int position, long arg3) {
            Intent intent = new Intent(UserAddressActivity.this, UserAddressItemActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("addressBean", mDatas.get(position));
            bundle.putString("type", "more");
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }
    };
}
