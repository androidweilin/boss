package com.android.app.buystoreapp.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.buystore.utils.CameraUtils;
import com.android.app.buystore.utils.ImageUtils;
import com.android.app.buystore.utils.SharedPreferenceUtils;
import com.android.app.buystoreapp.BossBuyActivity;
import com.android.app.buystoreapp.R;
import com.android.app.buystoreapp.bean.EditUserInfoBean;
import com.android.app.buystoreapp.bean.GsonEditUserCmd;
import com.android.app.buystoreapp.bean.GsonLoginBack;
import com.android.app.buystoreapp.bean.UserInfoBean;
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

import java.io.ByteArrayOutputStream;

public class UpdateUserInfoActivity extends Activity implements OnClickListener {
    @ResInject(id=R.string.web_url,type=ResType.String)
    private String webUrl;
    private static final int CODE_GALLERY_REQUEST = 0xa1;
    private static final int CODE_CAMERA_REQUEST = 0xa2;
    private static final int CODE_RESIZE_REQUEST = 0xa3;
    private static final int PICK_PIC = 0xa4;
    
    private static final int DIALOG_USERICON = 0xb1;
    private static final int DIALOG_NICKNAME = 0xb2;

    private ImageView muserIcon;
    private EditText muserName;
    private TextView mnickName;
    private EditText muserScore;
    private View muserSafe;
    private View muserAddress;

    private Button exitBtn;
    private Bitmap userPhoto;
    private ProgressDialog progressDialog;
    private static final int ICON_WIDTH_AND_HEIGHT = 200;

    @ViewInject(R.id.id_custom_title_text)
    private TextView mTitleText;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.update_userinfo);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.custom_action_bar);

        ViewUtils.inject(this);
        mTitleText.setText("个人中心");

        initViews();
        initUserInfo();
    };

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
    protected void onDestroy() {
        super.onDestroy();
        if (userPhoto != null) {
            userPhoto.recycle();
            userPhoto = null;
        }
    }
    
    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
        case DIALOG_USERICON:
            builder
            .setTitle(R.string.modify_icon_dialog_title)
            .setItems(R.array.modify_icon_dialog_choices,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0,
                                int which) {
                            if (which == 0) {
                                CameraUtils.openCamera(
                                        UpdateUserInfoActivity.this,
                                        CODE_CAMERA_REQUEST);
                            } else if (which == 1) {
                                CameraUtils.openPhotos(
                                        UpdateUserInfoActivity.this,
                                        CODE_GALLERY_REQUEST);
                            }
                        }
                    });
            break;
        case DIALOG_NICKNAME:
            builder.setTitle("修改昵称");
            final View contentView = LayoutInflater.from(this).inflate(R.layout.update_userinfo_nickname, null);
            EditText nickEdit = (EditText) contentView.findViewById(R.id.id_update_userinfo_nickname_dialog);
            if (nickEdit != null) {
                nickEdit.setHint(mnickName.getText());
            }
            builder.setView(contentView);
            builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    EditText edit = (EditText) contentView.findViewById(R.id.id_update_userinfo_nickname_dialog);
                    mnickName.setText(edit.getText());
                    sendEditUserinfo();
                }
            })
            .setNegativeButton("取消", null);
            break;
        default:
            break;
        }
        return builder.create();
    }
    

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
        case PICK_PIC:
            Log.d("mikes", "resultCode="+resultCode);
            
            break;
        case CODE_CAMERA_REQUEST:
            userPhoto = (Bitmap) data.getExtras().get("data");
            muserIcon.setImageBitmap(userPhoto);
            sendEditUserinfo();
            break;
        case CODE_GALLERY_REQUEST:
            String path = CameraUtils.getPhotoPathByLocalUri(this, data);
            Log.d("mikes", "path=" + path);
            if (path != null
                    && (path.endsWith(".jpg") || path.endsWith(".png")
                            || path.endsWith(".PNG") || path.endsWith(".jpeg") || path.endsWith(".JPEG")|| path.endsWith(".JPG"))) {
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, option);
                option.inSampleSize = ImageUtils.calculateInSampleSize(option,
                        ICON_WIDTH_AND_HEIGHT, ICON_WIDTH_AND_HEIGHT);
                option.inJustDecodeBounds = false;
                userPhoto = BitmapFactory.decodeFile(path, option);
                muserIcon.setImageBitmap(userPhoto);
                sendEditUserinfo();
            }
            break;
        case CODE_RESIZE_REQUEST:

            break;
        default:
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void resizeImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CODE_RESIZE_REQUEST);
    }

    private void initUserInfo() {
        UserInfoBean userInfo = SharedPreferenceUtils.getCurrentUserInfo(this);
        String userIconUrl = userInfo.getUserIcon();
        Picasso.with(this).load(userIconUrl).into(muserIcon);
        // getBitmapFromDrawable(muserIcon.getDrawable());
        muserName.setText(userInfo.getUserName());
        mnickName.setText(userInfo.getNickName());
        muserScore.setText(userInfo.getScore());
    }

    private void getBitmapFromDrawable(Drawable drawable) {
        Log.d("mikes",
                "initUserinfo  drawable == null :"
                        + String.valueOf(drawable == null));
        try {
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                    : Bitmap.Config.RGB_565;
            userPhoto = Bitmap.createBitmap(w, h, config);
            Canvas canvas = new Canvas(userPhoto);
            drawable.setBounds(0, 0, w, h);
            drawable.draw(canvas);
        } catch (ClassCastException e) {
            Log.e("mikes", "transfer drawable error:", e);
        } catch (Exception e) {
            Log.e("mikes", "error:", e);
        }
    }

    private void initViews() {
        muserIcon = (ImageView) findViewById(R.id.id_updateuserinfo_userinfo_usericon);
        muserName = (EditText) findViewById(R.id.id_updateuserinfo_userinfo_username);
        mnickName = (TextView) findViewById(R.id.id_updateuserinfo_userinfo_nickname);
//        muserScore = (EditText) findViewById(R.id.id_updateuserinfo_userinfo_score);
        
        muserIcon.setOnClickListener(this);
        mnickName.setOnClickListener(this);

        exitBtn = (Button) findViewById(R.id.id_updateuserinfo_userinfo_exit);
        exitBtn.setOnClickListener(this);

        muserSafe = findViewById(R.id.id_updateuserinfo_safe);
        muserAddress = findViewById(R.id.id_updateuserinfo_address);
        muserSafe.setOnClickListener(this);
        muserAddress.setOnClickListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.id_updateuserinfo_safe:
            Intent modifyPwdIntent = new Intent(this,
                    ModifyPasswordActivity.class);
            startActivity(modifyPwdIntent);
            break;
        case R.id.id_updateuserinfo_address:
            Intent addressIntent = new Intent(this, UserAddressActivity.class);
            addressIntent.putExtra("where", 0);
            startActivity(addressIntent);
            break;
        case R.id.id_updateuserinfo_userinfo_exit:
            exit();
            startActivity(new Intent(this, BossBuyActivity.class));
            this.finish();
            break;
        case R.id.id_updateuserinfo_userinfo_usericon:
            showDialog(DIALOG_USERICON);
//            CameraUtils.openCameraOrPicture(this, PICK_PIC);
            break;
        case R.id.id_updateuserinfo_userinfo_nickname:
            showDialog(DIALOG_NICKNAME);
            break;
        default:
            break;
        }
    }

    private void exit() {
        UserInfoBean userInfo = new UserInfoBean();
        SharedPreferenceUtils.saveCurrentUserInfo(userInfo, this, false);
    }

    private String bitmaptoString(Bitmap bitmap) {
        if (bitmap == null)
            return "";
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 30, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    private void sendEditUserinfo() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        final Gson gson = new Gson();
        final String iconString = bitmaptoString(userPhoto);
        EditUserInfoBean userInfo = new EditUserInfoBean(muserName.getText()
                .toString(), iconString, mnickName.getText().toString());
        GsonEditUserCmd gsonEditUserCmd = new GsonEditUserCmd("editUserInfo",
                userInfo);
        String param = gson.toJson(gsonEditUserCmd);
        requestParams.put("json", param);

        client.post(webUrl, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
                progressDialog.dismiss();
            }

            @Override
            public void onStart() {
                super.onStart();
                progressDialog = ProgressDialog.show(
                        UpdateUserInfoActivity.this,
                        getResources()
                                .getString(R.string.modify_progress_title),
                        getResources().getString(
                                R.string.modify_progress_message), true, false);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                LogUtils.d("editUserinfo result=" + new String(arg2));
                try {
                    GsonLoginBack gsonLoginBack = gson.fromJson(
                            new String(arg2), new TypeToken<GsonLoginBack>() {
                            }.getType());
                    String result = gsonLoginBack.getResult();
                    String resultNote = gsonLoginBack.getResultNote();
                    if ("1".equals(result)) {// fail
                        Toast.makeText(getApplicationContext(), resultNote,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), resultNote,
                                Toast.LENGTH_SHORT).show();
                        updateUserinfoAfterModify(gsonLoginBack.getUserinfoBean());
                    }
                } catch (NullPointerException e) {
                    Log.e("mikes", "update user info error:", e);
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                    Throwable arg3) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.modify_failure),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserinfoAfterModify(UserInfoBean userInfo) {
        Picasso.with(this).load(userInfo.getUserIcon()).into(muserIcon);
        userInfo.setLogin(true);
        userInfo.setScore(muserScore.getText().toString());
        LogUtils.d("update user info end =" + userInfo.toString());
        SharedPreferenceUtils.saveCurrentUserInfo(userInfo, this, true);
        this.finish();
    }
}
