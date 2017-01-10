package com.android.app.buystoreapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.app.buystore.utils.FileUtils;
import com.android.app.buystore.utils.SharedPreferenceUtils;
import com.android.app.buystoreapp.base.BaseAct;
import com.android.app.buystoreapp.bean.GsonBackOnlyResult;
import com.android.app.buystoreapp.bean.RelaseDataBean;
import com.android.app.buystoreapp.bean.RelaseGroupBean;
import com.android.app.buystoreapp.bean.RelaseImageBean;
import com.android.app.buystoreapp.crash.CrashApplication;
import com.android.app.buystoreapp.location.AddressActivity;
import com.android.app.buystoreapp.location.AddressDetailsActivity;
import com.android.app.buystoreapp.setting.LoginActivity;
import com.android.app.buystoreapp.wallet.ToastUtil;
import com.android.app.utils.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 发布第二步详情
 * Created by likaihang on 16/08/29.
 */
public class ReleaseStepTwoNextAct extends BaseAct implements View.OnClickListener, TextWatcher {
    @ViewInject(R.id.id_custom_title_text)
    private TextView mTitleText;
    @ViewInject(R.id.iv_release_line_1)
    private ImageView line1;
    @ViewInject(R.id.iv_release_step_2)
    private ImageView step2;
    @ViewInject(R.id.iv_release_line_2)
    private ImageView line2;
    @ViewInject(R.id.rg_release_service_transaction)
    private RadioGroup freight;
    @ViewInject(R.id.ll_release_freight)
    private LinearLayout llFreight;
    @ViewInject(R.id.tv_release_previous)
    private TextView previous;
    @ViewInject(R.id.tv_release_to)
    private TextView release;
    @ViewInject(R.id.et_service_week_from)
    private EditText mWeekfrom;
    @ViewInject(R.id.et_service_week_to)
    private EditText mWeekto;
    @ViewInject(R.id.et_service_day_from)
    private TextView mDayfrom;
    @ViewInject(R.id.et_service_day_to)
    private TextView mDayto;
    @ViewInject(R.id.et_release_service_freight_price)
    private EditText mFreprice;
    @ViewInject(R.id.rg_release_service_body)
    private RadioGroup service;
    @ViewInject(R.id.rg_release_service_freight)
    private RadioGroup trans;
    @ViewInject(R.id.ll_select_address)
    private LinearLayout address;
    @ViewInject(R.id.tv_dddress)
    private TextView res;
    @ViewInject(R.id.rl_details_address)
    private LinearLayout detail;
    @ViewInject(R.id.tv_street_details)
    private TextView mStreet;

    private RelaseDataBean data;
    private String weekfrom = "周一";
    private String weekto = "周五";
    private String dayfeom = "9:00";
    private String dayto = "17:00";
    private String freprice = "";
    private String proLat = String.valueOf(CrashApplication.latitude);
    private String proLong = String.valueOf(CrashApplication.longitude);
    private String street = CrashApplication.street;
    private String city = CrashApplication.cityName;
    private String adname = CrashApplication.adname;//定位到的区
    public static ReleaseStepTwoNextAct releaseStepTwoNextAct;
    private String cityid = "110100";
    private String thisUser;
    private boolean islogin;
    private ScrollView scroll;
    private String cityinfo;
    private String lable = "A";
    private List<RelaseImageBean> imagelist = new ArrayList<RelaseImageBean>();
    private List<RelaseGroupBean> grouplist = new ArrayList<RelaseGroupBean>();
    String weeks[] = {"周一", "周二", "周三", "周四", "周五", "周六", "周日",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.release_step_tow_next_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.custom_action_bar);
        ViewUtils.inject(this);
        releaseStepTwoNextAct = this;
        data = (RelaseDataBean) getIntent().getSerializableExtra("data");
        cityid = data.getAreasId2();
        initErrorPage();
        addIncludeLoading(true);
        init();
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
        thisUser = SharedPreferenceUtils.getCurrentUserInfo(this).getUserId();
        islogin = SharedPreferenceUtils.getCurrentUserInfo(this).isLogin();
    }

    public void init() {
        mTitleText.setText("发布资源・服务");
        mStreet.setText(street);
        res.setText(city);
        mWeekfrom.setOnClickListener(this);
        mWeekto.setOnClickListener(this);
        mDayfrom.setOnClickListener(this);
        mDayto.setOnClickListener(this);
        scroll = (ScrollView) findViewById(R.id.sl_release_next);
        line1.setBackgroundResource(R.drawable.release_step_line_lit);
        step2.setBackgroundResource(R.drawable.release_step_lit_2);
        line2.setBackgroundResource(R.drawable.release_step_line_lit);
        previous.setOnClickListener(this);
        release.setOnClickListener(this);
        freight.setOnCheckedChangeListener(freightOnCheckedChangeListener);
        detail.setOnClickListener(this);
        service.setOnCheckedChangeListener(serviceOnCheckedLister);
        trans.setOnCheckedChangeListener(transOnCheckLinster);
        address.setOnClickListener(this);
        mFreprice.addTextChangedListener(this);
    }

    private int servicebody;//服务主体
    private RadioGroup.OnCheckedChangeListener serviceOnCheckedLister = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id = group.getCheckedRadioButtonId();
            if (id == R.id.rb_release_service_body_boss) {
                servicebody = 0;
            } else if (id == R.id.rb_release_service_body_company) {
                servicebody = 1;
            } else {
                servicebody = 2;
            }
        }
    };

    private int frieght;//运费
    private RadioGroup.OnCheckedChangeListener transOnCheckLinster = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id = group.getCheckedRadioButtonId();
            if (id == R.id.rb_release_service_freight_free) {
                mFreprice.setText("");
                frieght = 0;
            } else {
                mFreprice.setText("");
                frieght = 1;
            }
        }
    };
    private int transbody;//服务方式
    private RadioGroup.OnCheckedChangeListener freightOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int radioButtonId = group.getCheckedRadioButtonId();
            if (radioButtonId == R.id.rb_release_service_transaction_freight) {
                llFreight.setVisibility(View.VISIBLE);
                scroll.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
                transbody = 2;
            } else if (radioButtonId == R.id.rb_release_service_transaction_off) {
                transbody = 1;
                llFreight.setVisibility(View.GONE);
            } else {
                transbody = 0;
                llFreight.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onClick(View v) {
        View view = View.inflate(this, R.layout.timepicker_item, null);
        final TimePicker picker = (TimePicker) view.findViewById(R.id.tp_time_select);
        imagelist = data.getListimg();
        grouplist = data.getMgList();
        weekfrom = mWeekfrom.getText().toString().trim();
        weekto = mWeekto.getText().toString().trim();
        dayfeom = mDayfrom.getText().toString().trim();
        dayto = mDayto.getText().toString().trim();
        Calendar c = Calendar.getInstance();
//        proLat = streetLat;
//        proLong =streetLng;
        switch (v.getId()) {
            case R.id.tv_release_previous://上一步
                finish();
                break;
            case R.id.tv_release_to:
                if (islogin) {
                    if (checkEmpty()) {
                        startWhiteLoadingAnim();
                        relase();
                    }
                } else {
                    Toast.makeText(this,
                            getString(R.string.personal_no_login_toast),
                            Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(this, LoginActivity.class);
                    loginIntent.putExtra("type", TabPersonal.FLAG_LOGIN);
                    startActivityForResult(loginIntent, Activity.RESULT_FIRST_USER);
                }
                break;
            case R.id.et_service_week_from:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("");
                builder.setItems(weeks, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mWeekfrom.setText(weeks[which]);
                    }
                });
                builder.create().show();
                break;
            case R.id.et_service_week_to:
                final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("");
                builder1.setItems(weeks, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mWeekto.setText(weeks[which]);
                    }
                });
                builder1.create().show();
                break;
            case R.id.et_service_day_from:
               /* new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mDayfrom.setText(hourOfDay + ":" + minute);
                    }
                }, c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE),
                        true).show();*/
                AlertDialog.Builder aler = new AlertDialog.Builder(this);
                aler.setView(view);
                aler.setTitle("选择时间");
                aler.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDayfrom.setText(picker.getCurrentHour() + ":" + picker.getCurrentMinute());
                    }
                });
                aler.show();
                break;
            case R.id.et_service_day_to:
               /* new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mDayto.setText(hourOfDay + ":" + minute);
                    }
                }, c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE),
                        true).show();*/
                AlertDialog.Builder aler1 = new AlertDialog.Builder(this);
                aler1.setView(view);
                aler1.setTitle("选择时间");
                aler1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDayto.setText(picker.getCurrentHour() + ":" + picker.getCurrentMinute());
                    }
                });
                aler1.show();
                break;
            case R.id.ll_select_address:
                Intent i = new Intent(this, AddressActivity.class);
                startActivityForResult(i, 1);
//                getProvince();
                break;
            case R.id.rl_details_address:
                if (city != null) {
                    Intent l = new Intent(this, AddressDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("city", city);
                    bundle.putString("street", street);
                    l.putExtras(bundle);
                    startActivityForResult(l, 2);
                } else {
                    ToastUtil.showMessageCenter(this, "请先选择地区！");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 1:
                String result = data.getExtras().getString("result");
                city = data.getExtras().getString("cityName");
                res.setText(result);
                mStreet.setText("");
                break;
            case 2:
                street = data.getExtras().getString("details");
                proLat = data.getExtras().getString("lat");
                proLong = data.getExtras().getString("lon");
                cityinfo = data.getExtras().getString("cityinfo");
                adname = data.getExtras().getString("adname");
                mStreet.setText(street);
                res.setText(cityinfo);
                break;
        }
    }

    private boolean checkEmpty() {
        if (TextUtils.isEmpty(weekfrom)) {
            ToastUtil.showMessageDefault(this, "");
            return false;
        } else if (TextUtils.isEmpty(weekto)) {
            ToastUtil.showMessageDefault(this, "");
            return false;
        } else if (TextUtils.isEmpty(dayfeom)) {
            ToastUtil.showMessageDefault(this, "");
            return false;
        } else if (TextUtils.isEmpty(dayto)) {
            ToastUtil.showMessageDefault(this, "");
            return false;
        } else if (transbody == 2 && TextUtils.isEmpty(freprice)) {
            ToastUtil.showMessageDefault(this, "");
            return false;
        }
        return true;
    }

    private void relase() {
        data = new RelaseDataBean("addProduct", data.getServeLabel(), data.getServeLabelName(),
                thisUser, cityid, data.getProCatId(), data.getProName(), data.getProDes(), data.listimg,
                data.mgList, weekto, weekfrom, dayfeom, dayto,
                adname, street,
                proLat, proLong, servicebody, transbody, frieght, freprice, 1, 0, 3);
        JSONObject obj = null;
        try {
            obj = new JSONObject(new Gson().toJson(data));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*
        JSONObject obj = new JSONObject();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        final Gson gson = new Gson();
        requestParams.put("json", gson.toJson(data));
        Log.e("ReleaseStepTwoNextAct", "--" + requestParams);
        client.post(getString(R.string.web_url), requestParams,*/
        System.out.println("ReleaseStepTwoNextAct" + obj.toString());
//        Log.d("ReleaseStepTwoNextAct", obj.toString());
        HttpUtils.post(mContext, obj, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                stopLoadingAnim();
                Gson gson = new Gson();
                GsonBackOnlyResult gsonBack = gson.fromJson(
                        new String(bytes),
                        new TypeToken<GsonBackOnlyResult>() {
                        }.getType());
                String result = gsonBack.getResult();
                String resultNote = gsonBack
                        .getResultNote();
                if (result.equals("0")) {
                    Intent intent = new Intent(getApplication(), ReleaseFinishActivity.class);
                    startActivity(intent);
                    FileUtils.deleteDir();
                    finish();
                } else {
                    ToastUtil.showMessageDefault(ReleaseStepTwoNextAct.this, resultNote);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                stopLoadingAnim();
                ToastUtil.showMessageDefault(mContext, "网络不给力");
            }
        }, new HttpUtils.RequestNetworkError() {
            @Override
            public void networkError() {

            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(s.toString().trim())) {
            freprice = mFreprice.getText().toString().trim();
            ((RadioButton) findViewById(R.id.rb_release_service_freight_free)).setChecked(false);
            ((RadioButton) findViewById(R.id.rb_release_service_freight_pay)).setChecked(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
