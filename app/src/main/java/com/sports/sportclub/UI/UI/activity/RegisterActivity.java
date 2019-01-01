package com.sports.sportclub.UI.UI.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.sportclub.R;
import com.sports.sportclub.api.BmobService;
import com.sports.sportclub.api.Client;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView back = findViewById(R.id.backToLoginText);
        back.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });


    }

    public void onClickSignup(View view) {
        //注册功能的实现
        EditText username_input = findViewById(R.id.username_input);
        EditText password_input = findViewById(R.id.password_input);
        EditText ensure_password_input = findViewById(R.id.ensure_password_input);
        String username = username_input.getText().toString();
        String password = password_input.getText().toString();
        String ensure_password = ensure_password_input.getText().toString();

        if(!password.equals(ensure_password)){
            showmsg("两次输入密码不一致!");
            return;
        }

        //bmob内部封装的注册方法
//        BmobUser new_user = new BmobUser();
//        new_user.setPassword(password);
//        new_user.setUsername(username);
//        new_user.signUp(new SaveListener<BmobUser>() {
//
//            @Override
//            public void done(BmobUser user, BmobException e) {
//                if(e == null){
//                    showmsg("注册成功");
//                    BmobUser.logOut();
//                    jump2login();
//                }
//                else{
//                    showmsg(e.getMessage().toString());
//                }
//            }
//        });

        //创建注册用户模板
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username",username);
            jsonObject.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //将json转为请求体
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),jsonObject.toString());

        //使用retrofit发送请求
        BmobService service = Client.retrofit.create(BmobService.class);
        Call<ResponseBody> call = service.postUser(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if(response.code() == 201){
                        showmsg("注册成功");
                        jump2login();
                    }
                    else if(response.code() == 400) {
                        showmsg("该用户名已存在");
                    }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showmsg(t.getMessage());
            }
        });

    }
    //显示信息
    public void showmsg(String msg){
        Toast.makeText(RegisterActivity.this,msg,Toast.LENGTH_LONG).show();
    }
    //跳转到主界面
    public void jump2login(){
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
