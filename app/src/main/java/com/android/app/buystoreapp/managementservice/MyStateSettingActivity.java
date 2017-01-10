package com.android.app.buystoreapp.managementservice;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.app.buystore.utils.SharedPreferenceUtils;
import com.android.app.buystoreapp.R;
import com.android.app.buystoreapp.base.BaseAct;
import com.android.app.buystoreapp.other.ImageUtil;
import com.android.app.buystoreapp.wallet.ToastUtil;
import com.android.app.utils.HttpUtils;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 急速聊选择状态页面
 * <p/>
 * weilin
 */
public class MyStateSettingActivity extends BaseAct implements View.OnClickListener {

    private CheckBox cb_state_setting_charge;//选择收费

    private CheckBox cb_state_setting_free;//选择免费

    private CheckBox cb_state_setting_close;//选择关闭

    private RelativeLayout rl_state_setting_true;//选择关闭

    private ImageButton iv_back;

    private AddOrUpFastBean addOrUpFastBean = new AddOrUpFastBean();


    private int flog = 1;

    public static final int HANDLE_LOADMORE = 100;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_LOADMORE:
                    ToastUtil.showMessageDefault(MyStateSettingActivity.this, "极速聊添加成功");
                    finish();
                    MyRapidlyChatSetUpActivity.myRapidlyChatSetUpActivity.finish();
                    break;
            }
        }
    };
    private String userId;

    @Override
    protected void load() {
        super.load();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_state_setting);
        initView();
        setListener();

        flog = getIntent().getExtras().getInt("flog");
        initErrorPage();
        addIncludeLoading(true);
        ischecked(flog);
        Log.e("Intentflog", "Intentflogflog==============" + getIntent().getExtras().get("flog"));


    }

    @Override
    protected void onResume() {
        super.onResume();

        userId = SharedPreferenceUtils.getCurrentUserInfo(this).getUserId();
    }

    public void initView() {
        ((TextView)findViewById(R.id.tv_title)).setText(getResources().getString(R.string.state_set));
        cb_state_setting_charge = (CheckBox) findViewById(R.id.cb_state_setting_charge);
        cb_state_setting_free = (CheckBox) findViewById(R.id.cb_state_setting_free);
        cb_state_setting_close = (CheckBox) findViewById(R.id.cb_state_setting_close);
        rl_state_setting_true = (RelativeLayout) findViewById(R.id.rl_state_setting_true);
        iv_back = (ImageButton) findViewById(R.id.iv_back);
    }

    public void setListener() {
        cb_state_setting_charge.setOnClickListener(this);
        cb_state_setting_free.setOnClickListener(this);
        cb_state_setting_close.setOnClickListener(this);
        rl_state_setting_true.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.cb_state_setting_charge:
                cb_state_setting_charge.setChecked(true);
                cb_state_setting_free.setChecked(false);
                cb_state_setting_close.setChecked(false);
                flog = 1;
                break;
            case R.id.cb_state_setting_free:
                cb_state_setting_charge.setChecked(false);
                cb_state_setting_free.setChecked(true);
                cb_state_setting_close.setChecked(false);
                flog = 2;
                break;
            case R.id.cb_state_setting_close:
                cb_state_setting_charge.setChecked(false);
                cb_state_setting_free.setChecked(false);
                cb_state_setting_close.setChecked(true);
                flog = 3;
                break;
            case R.id.rl_state_setting_true:
                if (cb_state_setting_charge.isChecked() == true || cb_state_setting_free.isChecked() == true || cb_state_setting_close.isChecked() == true) {

                    startWhiteLoadingAnim();
                    addFastChat();
                }
                break;
            case R.id.iv_back:
                this.finish();
                break;
        }
    }

    /**
     * 添加极速聊
     */
    private void addFastChat() {
       List<RapidlyBean.FastChatListBean> list = (List<RapidlyBean.FastChatListBean>) getIntent().getSerializableExtra("list");
        AddOrUpFastBean.ImagepathBean imgListBean1 = new AddOrUpFastBean.ImagepathBean();
        if (!TextUtils.isEmpty(getIntent().getExtras().getString("positive"))){
            try {
                imgListBean1.setWebrootpath(ImageUtil.bitMapToString(ImageUtil.revitionImageSize(getIntent().getExtras().getString("positive"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (list.size()!=0){
                imgListBean1.setId(list.get(0).getImgList().get(0).getId());
            }else {
                imgListBean1.setId("");
            }
        }else {
            if (list.size()!=0){
                imgListBean1.setWebrootpath("");
                imgListBean1.setId(list.get(0).getImgList().get(0).getId());
            }else {
                imgListBean1.setWebrootpath("");
                imgListBean1.setId("");
            }
        }
        imgListBean1.setImageType("1");

        AddOrUpFastBean.ImagepathBean imgListBean2 = new AddOrUpFastBean.ImagepathBean();
        if (!TextUtils.isEmpty(getIntent().getExtras().getString("unpositive"))){
            try {
                imgListBean2.setWebrootpath(ImageUtil.bitMapToString(ImageUtil.revitionImageSize(getIntent().getExtras().getString("unpositive"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (list.size()!=0){
                imgListBean2.setId(list.get(0).getImgList().get(1).getId());
            }else {
                imgListBean2.setId("");
            }
        }else {
            if (list.size()!=0){
                imgListBean2.setWebrootpath("");
                imgListBean2.setId(list.get(0).getImgList().get(1).getId());
            }else {
                imgListBean2.setWebrootpath("");
                imgListBean2.setId("");
            }
        }
        imgListBean2.setImageType("0");
//        if (!TextUtils.isEmpty(getIntent().getExtras().getString("positive"))) {
//            try {
//                imgListBean1.setWebrootpath(ImageUtil.bitMapToString(ImageUtil.revitionImageSize(getIntent().getExtras().getString("positive"))));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else if (list.size()!=0){
//           imgListBean1.setWebrootpath(list.get(0).getImgList().get(0).getWebrootpath());
//        }else {
//            imgListBean1.setWebrootpath("");
//        }
//        imgListBean1.setImageType("1");
//        if (getIntent().getExtras().getString("positiveId")!=null) {
//            imgListBean1.setId(getIntent().getExtras().getString("positiveId"));
//        }
//        AddOrUpFastBean.ImagepathBean imgListBean2 = new AddOrUpFastBean.ImagepathBean();
//        if (!TextUtils.isEmpty(getIntent().getExtras().getString("unpositive"))) {
//            try {
//                imgListBean2.setWebrootpath(ImageUtil.bitMapToString(ImageUtil.revitionImageSize(getIntent().getExtras().getString("unpositive"))));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else if (list.size()!=0){
//           imgListBean2.setWebrootpath(list.get(0).getImgList().get(1).getWebrootpath());
//        }else {
//            imgListBean2.setWebrootpath("");
//        }
//        imgListBean2.setImageType("0");
//        if (getIntent().getExtras().getString("unpositiveId")!=null) {
//            imgListBean2.setId(getIntent().getExtras().getString("unpositiveId"));
//        }
        addOrUpFastBean.imagepath = new ArrayList<AddOrUpFastBean.ImagepathBean>();
        addOrUpFastBean.imagepath.add(imgListBean1);
        addOrUpFastBean.imagepath.add(imgListBean2);
//  ( String note, String we_chat, String tell, String cmd, String phone, String QQ, String email, int status, String userid,List<ImagepathBean> imagepath)
        addOrUpFastBean = new AddOrUpFastBean(
                getIntent().getExtras().getString("note"),
                getIntent().getExtras().getString("we_chat"),
                getIntent().getExtras().getString("tell"),
                "addFastChat",
                getIntent().getExtras().getString("phone"),
                getIntent().getExtras().getString("qq"),
                getIntent().getExtras().getString("email"),
                flog,
                userId,
                addOrUpFastBean.imagepath);
        JSONObject obj = null;
        try {
            obj = new JSONObject(new Gson().toJson(addOrUpFastBean));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("obj========", obj.toString());
        HttpUtils.post(mContext, obj, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                stopLoadingAnim();
                hideErrorPageState();
                try {
                    String str = new String(bytes);
                    JSONObject object = new JSONObject(str);
                    String result = (String) object.get("result");
                    if ("0".equals(result)) {
                        mHandler.obtainMessage(HANDLE_LOADMORE).sendToTarget();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                stopLoadingAnim();
                ToastUtil.showMessageDefault(MyStateSettingActivity.this, getResources().getString(R.string.service_error_hint));
            }
        }, new HttpUtils.RequestNetworkError() {
            @Override
            public void networkError() {
                stopLoadingAnim();
                ToastUtil.showMessageDefault(MyStateSettingActivity.this, getResources().getString(R.string.service_error_hint));
            }
        });

    }

//    /**
//     * 修改极速聊
//     */
//    private void UpFastChat() {
//        initErrorPage();
//        addIncludeLoading(true);
//        startWhiteLoadingAnim();
//
//        AddOrUpFastBean.ImagepathBean imagepathBean1 = new AddOrUpFastBean.ImagepathBean();
//        if (!TextUtils.isEmpty(getIntent().getExtras().getString("positive"))) {
//            imagepathBean1.setWebrootpath(ImageUtil.bitMapToString(getIntent().getExtras().getString("positive")));
//        } else {
//            imagepathBean1.setWebrootpath("");
//        }
//        imagepathBean1.setImageType("1");
//        AddOrUpFastBean.ImagepathBean imagepathBean2 = new AddOrUpFastBean.ImagepathBean();
//        if (!TextUtils.isEmpty(getIntent().getExtras().getString("unpositive"))) {
//            imagepathBean2.setWebrootpath(ImageUtil.bitMapToString(getIntent().getExtras().getString("unpositive")));
//        } else {
//            imagepathBean2.setWebrootpath("");
//        }
//        imagepathBean2.setImageType("2");
//        addOrUpFastBean.imagepath = new ArrayList<>();
//        addOrUpFastBean.imagepath.add(imagepathBean1);
//        addOrUpFastBean.imagepath.add(imagepathBean2);
//
//        addOrUpFastBean = new AddOrUpFastBean(
//                "UpFastChat",
//                getIntent().getExtras().getString("chatId"),
//                getIntent().getExtras().getString("phone"),
//                getIntent().getExtras().getString("tell"),
//                getIntent().getExtras().getString("qq"),
//                getIntent().getExtras().getString("we_chat"),
//                getIntent().getExtras().getString("email"),
//                getIntent().getExtras().getString("note"),
//                flog,
//                userId,
//                addOrUpFastBean.imagepath);
//        JSONObject obj = null;
//        try {
//            obj = new JSONObject(new Gson().toJson(addOrUpFastBean));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Log.e("obj========", obj.toString());
//        HttpUtils.post(mContext, obj, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                LogUtils.e("MyStateSettingActivity-----------------------------\n" +
//                        "UpFastChat============ bytes=" + new String(bytes));
//                stopLoadingAnim();
//                hideErrorPageState();
//                try {
//                    String str = new String(bytes);
//                    JSONObject object = new JSONObject(str);
//                    String result = (String) object.get("result");
//                    LogUtils.e("MyStateSettingActivity-----------------------------\n" +
//                            "UpFastChat============ result=" + new String(bytes));
//                    if ("0".equals(result)) {
//                        mHandler.obtainMessage(UPFASTCHAT_SUCCESS).sendToTarget();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//                showErrorPageState(SERVEICE_ERR_FLAG);
//            }
//        }, new HttpUtils.RequestNetworkError() {
//            @Override
//            public void networkError() {
//                showErrorPageState(SERVEICE_ERR_FLAG);
//            }
//        });
//    }

    public void ischecked(int flog) {
        Log.e("ischeked\nflog=", String.valueOf(flog));
        switch (flog) {
            case 1:
                cb_state_setting_charge.setChecked(true);
                cb_state_setting_free.setChecked(false);
                cb_state_setting_close.setChecked(false);
                break;
            case 2:
                cb_state_setting_charge.setChecked(false);
                cb_state_setting_free.setChecked(true);
                cb_state_setting_close.setChecked(false);
                break;
            case 3:
                cb_state_setting_charge.setChecked(false);
                cb_state_setting_free.setChecked(false);
                cb_state_setting_close.setChecked(true);
                break;
        }

    }


}
