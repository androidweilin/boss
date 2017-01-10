package com.android.app.buystoreapp.managementservice;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.app.buystoreapp.R;
import com.android.app.buystoreapp.base.BaseAct;

public class ExplainWebViewActivity extends BaseAct implements View.OnClickListener{
    private ImageButton ib_explain_back;
    private WebView myWebView;
    private TextView tv_explain;
    private int flag;
    private String url = "http://baidu.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain_web_view);
        flag = getIntent().getExtras().getInt("flag");
        initView();
    }

    private void initView() {
        tv_explain = (TextView) findViewById(R.id.tv_explain);
        switch (flag)
        {
            case 1000:
                tv_explain.setText("Boss团注册协议");
                url = getResources().getString(R.string.boss_group_registration_protocol);
                break;
            case 2000:
                tv_explain.setText("提现说明");
                url = getResources().getString(R.string.description_of_the_present);
                break;
            case 3000:
                tv_explain.setText("软件许可使用协议");
                url = getResources().getString(R.string.software_license_agreement);
                break;
            case 4000:
                tv_explain.setText("使用条款和隐私协议");
                url = getResources().getString(R.string.terms_of_use_and_privacy_agreement);
                break;
        }

        ib_explain_back = (ImageButton) findViewById(R.id.ib_explain_back);
        myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl(url);
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view,url) ;
            }
        });
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        ib_explain_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.ib_explain_back:
                finish();
                break;
        }
    }
}
