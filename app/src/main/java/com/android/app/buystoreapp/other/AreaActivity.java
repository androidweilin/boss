/*
package com.android.app.buystoreapp.other;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.app.buystoreapp.R;
import com.android.app.buystoreapp.wallet.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

*/
/**
 * 省市区三级联动
 * Created by 尚帅波 on 2016/9/24.
 *//*

public class AreaActivity extends Activity implements View.OnClickListener {
    //定义上下文
    private Context context;
    //定义省市区数据列表
    private List<AreaBean.AreasListBean> provinceLists = new ArrayList<>();
    private List<AreaBean.AreasListBean> cityLists = new ArrayList<>();
    private List<AreaBean.AreasListBean> areaLists = new ArrayList<>();
    private TextView tv_area;
    private Button btn1_area, btn2_area;

    private AsyncHttpClient client;
    private RequestParams params;
    private AreaCmdBean provinceBeen, cityBeen, areaBeen, streetBeen;
    private Gson gson;
    private String request;
    private String provinceName, cityName, areaName;
    private int provinceId, cityId, areaId;

    private WheelView wheel_province, wheel_city, wheel_area;
    private TextView tv_exit, tv_confirm;
    private Dialog mDialog;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);
        context = this;
        initView();
    }

    */
/**
     * 初始化控件
     *//*

    private void initView() {
        tv_area = (TextView) findViewById(R.id.tv_area);
        btn1_area = (Button) findViewById(R.id.btn1_area);
        btn2_area = (Button) findViewById(R.id.btn2_area);
        btn1_area.setOnClickListener(this);
        btn2_area.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1_area:
                //当点击按钮以后设置按钮不可点击，防止重复点击加载数据
                btn1_area.setEnabled(false);
                getProvince();
                break;
            case R.id.btn2_area:
                break;
        }
    }

    */
/**
     * 获取省列表
     *//*

    private void getProvince() {
        //将三级列表布局加载上Dialog上
        mDialog = new Dialog(context, R.style.CustomDialog);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.wheel_view, null);
        wheel_province = (WheelView) view.findViewById(R.id.wheel_province);
        wheel_city = (WheelView) view.findViewById(R.id.wheel_city);
        wheel_area = (WheelView) view.findViewById(R.id.wheel_area);
        tv_exit = (TextView) view.findViewById(R.id.tv_exit);
        tv_confirm = (TextView) view.findViewById(R.id.tv_confirm);
        //设置显示行数为5行
        wheel_province.setVisibleItems(5);
        wheel_city.setVisibleItems(5);
        wheel_area.setVisibleItems(5);

        //初始化client
        client = new AsyncHttpClient();
        params = new RequestParams();
        gson = new Gson();

        //发送第一次请求，收到省级数据
        provinceBeen = new AreaCmdBean("selectAddress", 0, 1);
        request = gson.toJson(provinceBeen);
        params.put("json", request);
        client.get(Command.CONTEXT_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    AreaBean areaBean = gson.fromJson(new String(bytes), new TypeToken<AreaBean>() {
                    }.getType());
                    String result = areaBean.getResult();
                    if (result.equals("0")) {
                        provinceLists.clear();
                        cityLists.clear();
                        areaLists.clear();
                        provinceLists.addAll(areaBean.getAreasList());
                        String[] list01 = new String[provinceLists.size()];
                        for (int j = 0; j < provinceLists.size(); j++) {
                            list01[j] = provinceLists.get(j).getAreaname();
                        }
                        wheel_province.setViewAdapter(new ArrayWheelAdapter<>(context, list01));
                        wheel_province.setCurrentItem(0);

                        //给控件加滑动监听，每次滑动结束后执行
                        wheel_province.addChangingListener(new OnWheelChangedListener() {
                            @Override
                            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                                getCity();
                            }
                        });
                        getCity();
                    } else {

                    }
                } catch (Exception e) {
                    ToastUtil.showMessageDefault(context, "没有更多的数据了!!!");
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable
                    throwable) {
            }
        });

        tv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                tv_area.setText(provinceName + cityName + areaName + areaId);
            }
        });
    }

    */
/**
     * 获取市级列表
     *//*

    private void getCity() {
        provinceName = provinceLists.get(wheel_province.getCurrentItem()).getAreaname(); //最后停留的省级名称
        provinceId = provinceLists.get(wheel_province.getCurrentItem()).getId();    //最后停留的省级ID
        cityBeen = new AreaCmdBean("selectAddress", provinceId, 2);
        request = gson.toJson(cityBeen);
        params.put("json", request);
        client.get(Command.CONTEXT_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    AreaBean areaBean = gson.fromJson(new String(bytes), new
                            TypeToken<AreaBean>() {
                            }.getType());
                    String result = areaBean.getResult();
                    if (result.equals("0")) {
                        cityLists.clear();
                        areaLists.clear();
                        cityLists.addAll(areaBean.getAreasList());
                        String[] list02 = new String[cityLists.size()];
                        for (int j = 0; j < cityLists.size(); j++) {
                            list02[j] = cityLists.get(j).getAreaname();
                        }
                        wheel_city.setViewAdapter(new ArrayWheelAdapter<>
                                (context, list02));
                        wheel_city.setCurrentItem(0);

                        wheel_city.addChangingListener(new OnWheelChangedListener() {
                            @Override
                            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                                getArea();
                            }
                        });
                        getArea();
                    } else {

                    }
                } catch (Exception e) {
                    ToastUtil.showMessageDefault(context, "没有更多的数据了!!!");
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes,
                                  Throwable throwable) {
            }
        });
    }

    */
/**
     * 获取区级列表
     *//*

    private void getArea() {
        cityName = cityLists.get(wheel_city.getCurrentItem()).getAreaname();
        cityId = cityLists.get(wheel_city.getCurrentItem()).getId();
        areaBeen = new AreaCmdBean("selectAddress", cityId, 3);
        request = gson.toJson(areaBeen);
        params.put("json", request);
        client.get(Command.CONTEXT_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    AreaBean areaBean = gson.fromJson(new String(bytes), new
                            TypeToken<AreaBean>() {
                            }.getType());
                    String result = areaBean.getResult();
                    if (result.equals("0")) {
                        areaLists.clear();
                        areaLists.addAll(areaBean.getAreasList());
                        String[] list03 = new String[areaLists.size()];
                        for (int j = 0; j < areaLists.size(); j++) {
                            list03[j] = areaLists.get(j).getAreaname();
                        }
                        wheel_area.setViewAdapter(new ArrayWheelAdapter<>
                                (context, list03));
                        wheel_area.setCurrentItem(0);

                        wheel_area.addChangingListener(new OnWheelChangedListener() {
                            @Override
                            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                                getStreet();
                            }
                        });
                        getStreet();

                    } else {

                    }
                } catch (Exception e) {
                    ToastUtil.showMessageDefault(context, "没有更多的数据了!!!");
                    wheel_area.setViewAdapter(new ArrayWheelAdapter<>
                            (context, new String[0]));
                    wheel_area.setCurrentItem(0);
                }

                //如果是联网请求，Dialog显示一定要在最后一个列表的数据请求完成后在show();
                mDialog.setContentView(view);
                WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
                WindowManager windowManager = ((Activity) context).getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                params.width = (int) (display.getWidth() * 0.8);
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                mDialog.getWindow().setAttributes(params);
                mDialog.show();
                //等dialog弹出以后设置按钮可点击
                btn1_area.setEnabled(true);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes,
                                  Throwable throwable) {
                //若请求失败，设置按钮可点击
                btn1_area.setEnabled(true);
            }
        });
    }

    */
/**
     * 获取最后停留时的区级名称及ID（为了后边获取街道列表）
     *//*

    private void getStreet() {
        areaName = areaLists.get(wheel_area.getCurrentItem()).getAreaname();
        areaId = areaLists.get(wheel_area.getCurrentItem()).getId();
    }


}
*/
