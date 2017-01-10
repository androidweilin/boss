package com.android.app.buystoreapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.app.buystoreapp.managementservice.ManagementServiceActivity;

/**
 * 发布成功页面
 * Created by likaihang on 16/08/30.
 */
public class ReleaseFinishActivity extends Activity implements View.OnClickListener {
    private ImageView line1;
    private ImageView step2;
    private ImageView line2;
    private ImageView step3;
    private TextView look;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.release_step_three_layout);
        init();
    }


    public void init() {
        line1 = (ImageView) findViewById(R.id.iv_release_line_1);
        step2 = (ImageView) findViewById(R.id.iv_release_step_2);
        line2 = (ImageView) findViewById(R.id.iv_release_line_2);
        step3 = (ImageView) findViewById(R.id.iv_release_step_3);
        look = (TextView) findViewById(R.id.tv_look_it);


        line1.setBackgroundResource(R.drawable.release_step_line_lit);
        step2.setBackgroundResource(R.drawable.release_step_lit_2);
        line2.setBackgroundResource(R.drawable.release_step_line_lit);
        step3.setBackgroundResource(R.drawable.release_step_lit_3);
        look.setOnClickListener(this);

        ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(R.string.fb_zy_fw));
        findViewById(R.id.iv_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_look_it:
                Intent intentmanagement = new Intent(this, ManagementServiceActivity.class);
                startActivity(intentmanagement);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
