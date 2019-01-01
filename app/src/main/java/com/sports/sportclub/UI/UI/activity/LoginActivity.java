package com.sports.sportclub.UI.UI.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sports.sportclub.DataModel.User;
import com.sports.sportclub.R;
import com.sports.sportclub.api.BmobService;
import com.sports.sportclub.api.Client;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import rx.Subscription;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;



public class LoginActivity extends AppCompatActivity{


    TextView tv_name;
    TextView tv_content;
    android.widget.ImageView imageView;
    private UserInfo mInfo;
    public static Tencent mTencent;
    public static String mAppid="1106062414";

    private User current_user = null;
    private String username;
    private String password;
    private JSONObject jsonObject = null;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private List<String> mPermissionList = new ArrayList<String>();


     /**
     * 友盟开始授权(登入)
     */
    public static void shareLoginUmeng(final Activity activity, SHARE_MEDIA share_media_type) {

        UMShareAPI.get(activity).getPlatformInfo(activity, share_media_type, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
//                android.util.Log.e(TAG, "onStart授权开始: ");
                Toast.makeText(activity, "授权开始", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, java.util.Map<String, String> map) {
                //sdk是6.4.5的,但是获取值的时候用的是6.2以前的(access_token)才能获取到值,未知原因
                Toast.makeText(activity, "授权完成", Toast.LENGTH_LONG).show();
                String uid = map.get("uid");
                String openid = map.get("openid");//微博没有
                String unionid = map.get("unionid");//微博没有
                String access_token = map.get("access_token");
                String refresh_token = map.get("refresh_token");//微信,qq,微博都没有获取到
                String expires_in = map.get("expires_in");
                String name = map.get("name");//名称
                String gender = map.get("gender");//性别
                String iconurl = map.get("iconurl");//头像地址

//                Log.e(TAG, "onStart授权完成: " + openid);
//                Log.e(TAG, "onStart授权完成: " + unionid);
//                Log.e(TAG, "onStart授权完成: " + access_token);
//                Log.e(TAG, "onStart授权完成: " + refresh_token);
//                Log.e(TAG, "onStart授权完成: " + expires_in);
//                Log.e(TAG, "onStart授权完成: " + uid);
//                Log.e(TAG, "onStart授权完成: " + name);
//                Log.e(TAG, "onStart授权完成: " + gender);
//                Log.e(TAG, "onStart授权完成: " + iconurl);
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                Toast.makeText(activity, "授权失败", Toast.LENGTH_LONG).show();
//                android.util.Log.e(TAG, "onError: " + "授权失败");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                Toast.makeText(activity, "授权取消", Toast.LENGTH_LONG).show();
//                Log.e(TAG, "onError: " + "授权取消");
            }
        });
    }

     /**
     * 友盟取消授权（登入）
     */
    public void umengDeleteOauth(SHARE_MEDIA share_media_type) {

        UMShareAPI.get(this).deleteOauth(this, share_media_type, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                //开始授权
//                android.util.Log.e(TAG, "onStart: ");
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, java.util.Map<String, String> map) {
                //取消授权成功 i=1
//                android.util.Log.e(TAG, "onComplete: ");
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                //授权出错

//                android.util.Log.e(TAG, "onError: ");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                //取消授权

//                android.util.Log.e(TAG, "onCancel: ");
            }
        });
    }


     private void onClickLogin() {
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "all", loginListener);
        }
    }
    /**
     * 获取登录QQ腾讯平台的权限信息(用于访问QQ用户信息)
     * @param jsonObject
     */
    public static void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!android.text.TextUtils.isEmpty(token) && !android.text.TextUtils.isEmpty(expires)
                    && !android.text.TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch(Exception e) {
        }
    }
    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {
                @Override
                public void onError(UiError e) {
                }
                @Override
                public void onComplete(final Object response) {
                    android.os.Message msg = new android.os.Message();
                    msg.obj = response;
                    android.util.Log.i("tag", response.toString());
                    msg.what = 0;
                    //mHandler.sendMessage(msg);
                }
                @Override
                public void onCancel() {
                }
            };
            mInfo = new UserInfo(this, mTencent.getQQToken());
            mInfo.getUserInfo(listener);

        }
    }
     /**
     * 继承的到BaseUiListener得到doComplete()的方法信息
     */
    IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {//得到用户的ID  和签名等信息  用来得到用户信息
            android.util.Log.i("lkei",values.toString());
            initOpenidAndToken(values);
            updateUserInfo();
        }
    };
    /***
     * QQ平台返回返回数据个体 loginListener的values
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode,resultCode,data,loginListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            if (null == response) {
                Toast.makeText(LoginActivity.this, "登录失败",Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Toast.makeText(LoginActivity.this, "登录失败",Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(LoginActivity.this, "登录成功",Toast.LENGTH_LONG).show();
            doComplete((JSONObject)response);
        }

        protected void doComplete(JSONObject values) {

        }
        @Override
        public void onError(UiError e) {
            //登录错误
        }

        @Override
        public void onCancel() {
            // 运行完成
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        //初始化友盟SDK
        com.umeng.commonsdk.UMConfigure.init(this, "", "Shutang", com.umeng.commonsdk.UMConfigure.DEVICE_TYPE_PHONE, "");
        UMShareAPI.get(this);//初始化sd
        //开启debug模式，方便定位错误，具体错误检查方式可以查看
        //http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
        Config.DEBUG = true;
        com.umeng.socialize.common.QueuedWork.isUseThreadPool = true;
        //三方获取用户资料时是否每次都要进行授权
        com.umeng.socialize.UMShareConfig config = new com.umeng.socialize.UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMShareAPI.get(this).setShareConfig(config);

        //微信
        PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
        //新浪微博(第三个参数为回调地址)
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad",
                "http://sns.whalecloud.com/sina2/callback");



        //初始化bmob服务
        Bmob.initialize(this, "5fad9f2543ffa83e56155a46398d6ede");


            findViewById(R.id.new_login_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickLogin();
//                    Toast.makeText(LoginActivity.this,"该功能未开放",Toast.LENGTH_LONG).show();
                  }
            });
            if (mTencent == null) {
            mTencent = Tencent.createInstance(mAppid, this);
            }
//
//            findViewById(R.id.weixin_login_btn).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                 umengDeleteOauth(SHARE_MEDIA.WEIXIN);
//                //开始授权
//                shareLoginUmeng(LoginActivity.this, SHARE_MEDIA.WEIXIN);
//
//                //shareLoginUmeng(this, SHARE_MEDIA.SINA);
//
//                }
//            });
//
//            findViewById(R.id.weibo_login_btn).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                     umengDeleteOauth(SHARE_MEDIA.SINA);
//                //开始授权
//                    shareLoginUmeng(LoginActivity.this, SHARE_MEDIA.SINA);
//
//                //shareLoginUmeng(this, SHARE_MEDIA.SINA);
//                  }
//            });



            //设置下划线
            TextView forget_text = findViewById(R.id.forget_text);
            forget_text.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

            //设置监听
            forget_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (!mTencent.isSessionValid()) {
//                             mTencent.login(this, "all", loginListener);
//                        }
                    Toast.makeText(LoginActivity.this,"该功能未开放",Toast.LENGTH_LONG).show();
                }
            });



            TextView signup_text = findViewById(R.id.register_text);
            signup_text.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            signup_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(intent);
                }
            });
        }







    //登陆按钮的跳转
    public void onClickSignin(View view) {
        EditText username_input = findViewById(R.id.username_input);
        EditText password_input = findViewById(R.id.password_input);

        username = username_input.getText().toString();
        password = password_input.getText().toString();

        //使用retrofit实现登录请求
        BmobService service = Client.retrofit.create(BmobService.class);
        Call<ResponseBody> call = service.getUser(username,password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code() == 200){
                    showmsg("登陆成功");
                    try {
                        String str =  response.body().string();
                        jsonObject = new JSONObject(str);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (Build.VERSION.SDK_INT >= 23) {
                        for (int i = 0; i < PERMISSIONS_STORAGE.length; i++) {
                            int checkCallPhonePermission = ContextCompat.checkSelfPermission(LoginActivity.this, PERMISSIONS_STORAGE[i]);
                            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                                mPermissionList.add(PERMISSIONS_STORAGE[i]);
                            }
                        }
                        if (mPermissionList.size() > 0) {
                            ActivityCompat.requestPermissions(LoginActivity.this,
                                    PERMISSIONS_STORAGE, 222);
                        } else {
                            jump2main(jsonObject);
                        }

                    }

                }
                else if(response.code() == 400) {
                    showmsg("用户名或密码错误");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showmsg(t.getMessage());
            }
        });




        //bmob内部封装的登陆方法
//        current_user = new BmobUser();
//        current_user.setPassword(password);
//        current_user.setUsername(userEmail);
//        current_user.login(new SaveListener<BmobUser>() {
//
//            @Override
//            public void done(BmobUser user, BmobException e) {
//                if(e == null){
//                    showmsg("登陆成功");
//                    jump2main();
//                }
//                else{
//                    showmsg(e.getMessage().toString());
//                }
//            }
//        });

    }

    public boolean Validation(User user){

        return false;
    }

    //显示信息
    public void showmsg(String msg){
        Toast.makeText(LoginActivity.this,msg,Toast.LENGTH_LONG).show();
    }
    //跳转至主界面
    public void jump2main(JSONObject jsonObject){

        try {
            //Object id = jsonObject.get("objectId");
            Object token = jsonObject.get("sessionToken");
            Object user_name = jsonObject.get("username");
            if(jsonObject == null)
                return;
            current_user = new User();
            current_user.setUsername(user_name.toString());
            current_user.setSessionToken(token.toString());
            current_user.setPassword(password);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        Intent intent = new Intent(this,navigationActivity.class);
        //intent.putExtra("username",username);
        intent.putExtra("user",current_user);
        startActivity(intent);
        //verifyStoragePermissions(this);
        finish();

    }

//    public static void verifyStoragePermissions(Activity activity) {
//        int permission = ActivityCompat.checkSelfPermission(activity,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE);
//        }
//    }

    /**
     * 该方法判定获取权限的结果
     * 若失败，则不能开启定位
     * 若成功，则正确开启定位
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            //就像onActivityResult一样这个地方就是判断你是从哪来的。
            case 222:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    jump2main(jsonObject);
                } else {
                    // Permission Denied
                    Toast.makeText(LoginActivity.this, "相机开启失败", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
