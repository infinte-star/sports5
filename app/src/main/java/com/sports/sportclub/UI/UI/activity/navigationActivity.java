package com.sports.sportclub.UI.UI.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.sports.sportclub.DataModel.User;
import com.sports.sportclub.UI.UI.fragment.AppointmentFragment;
import com.sports.sportclub.UI.UI.fragment.CoachsFragment;
import com.sports.sportclub.UI.UI.fragment.FavoriteFragment;
import com.sports.sportclub.UI.UI.fragment.FindFragment;
import com.sports.sportclub.UI.UI.fragment.HomeFragment;
import com.sports.sportclub.R;
import com.sports.sportclub.UI.UI.fragment.ImagePagerFragment;
import com.sports.sportclub.UI.UI.fragment.RecommendFragment;
import com.sports.sportclub.UI.UI.fragment.SchedulFragment;

import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
//import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

public class navigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private User curr_user = null;

    private boolean isExist = false;   //判断是否退出
    private String [] titles = {"Home","Announcement","Schedule","Favorite","Appointment"};
    public static int currentPosition;
    private static final String KEY_CURRENT_POSITION = "com.sports.sportclub.gridtopager.key.currentPosition";

    private BmobUser bmobUser;

    NavigationView navigationView = null;

    //处理退出信息的Handler
    Handler exit_handler = new Handler(){

        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            isExist = false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left_navigation);
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0);
            // Return here to prevent adding additional GridFragments when changing orientation.
            curr_user = (User) savedInstanceState.getSerializable("user");
            return;
        }

        SDKInitializer.initialize(getApplicationContext());


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //设置滑动监听，利用DrawLayout以及ActionBarDrawerToggle
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //设置导航监听
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.navigation_home);

        //设置底部导航按钮
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        View headerLayout = navigationView.getHeaderView(0);

        curr_user = (User) getIntent().getSerializableExtra("user");

        TextView nav_username = headerLayout.findViewById(R.id.nav_username);
        nav_username.setText(curr_user.getUsername());

        //设置初始片段为HomeFragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_content,new HomeFragment());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.left_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //处理左侧导航抽屉点击事件
        int id = item.getItemId();
        int position = -1;
        Fragment fragment = null;
        if (id == R.id.nav_home) {
            // Handle the home action
            fragment = new HomeFragment();
            position = 0;
        } else if (id == R.id.nav_announcement) {
            Intent intent = new Intent(navigationActivity.this,mapActivity.class);
            startActivity(intent);
            navigationView.setCheckedItem(R.id.navigation_home);
            return true;
        } else if (id == R.id.nav_schedul) {
            fragment = new SchedulFragment();
            position = 2;
        } else if (id == R.id.nav_favorite) {
            fragment = new FavoriteFragment();
            position = 3;
        } else if (id == R.id.nav_appointment) {
            fragment = new AppointmentFragment();
            position = 4;
        } else if (id == R.id.nav_share) {


            Toast.makeText(navigationActivity.this,"分享到QQ空间",Toast.LENGTH_LONG).show();
            shareToQQzone();
//            DrawerLayout drawer = findViewById(R.id.drawer_layout);
//            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_send) {
            Toast.makeText(navigationActivity.this,"分享",Toast.LENGTH_LONG).show();
            onClickShare();
//            DrawerLayout drawer = findViewById(R.id.drawer_layout);
//            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        if(navigationView == null)
            navigationView = findViewById(R.id.nav_view);
        //if(navigationView)
        //navigationView.setCheckedItem(R.id.navigation_home);
        changeFragment(R.id.frame_content,fragment);
        setActionBarTitle(position,1);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void onClickShare() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
                QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "要分享的标题");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "要分享的摘要");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,
                "http://blog.csdn.net/DickyQie/article/list/1");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
                "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "切切歆语");
        params.putString(QQShare.SHARE_TO_QQ_EXT_INT, "其他附加功能");

        LoginActivity.mTencent.shareToQQ(navigationActivity.this, params, new BaseUiListener1());
    }
    //回调接口  (成功和失败的相关操作)
    private class BaseUiListener1 implements IUiListener {
        @Override
        public void onComplete(Object response) {
            doComplete(response);
        }

        protected void doComplete(Object values) {
        }

        @Override
        public void onError(UiError e) {
        }

        @Override
        public void onCancel() {
        }
    }
    @SuppressWarnings("unused")
    private void shareToQQzone() {
        try {
            final Bundle params = new Bundle();
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
                    QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "sport club share");
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "my share");
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL,
                    "http://blog.csdn.net/DickyQie/article/list/1");
            ArrayList<String> imageUrls = new ArrayList<String>();
            imageUrls.add("http://media-cdn.tripadvisor.com/media/photo-s/01/3e/05/40/the-sandbar-that-links.jpg");
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
            params.putInt(QzoneShare.SHARE_TO_QQ_EXT_INT,
                    QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
            Tencent mTencent = Tencent.createInstance("1106062414",
                    navigationActivity.this);
            mTencent.shareToQzone(navigationActivity.this, params,
                    new BaseUiListener1());
        } catch (Exception e) {
        }
    }

    //设置底端导航按钮响应事件
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        //打开侧滑功能
                        changeFragment(R.id.frame_content,new HomeFragment());
//                        ((DrawerLayout)findViewById(R.id.drawer_layout))
//                                .setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        setActionBarTitle(-1,1);
                        navigationView.setCheckedItem(R.id.navigation_home);
                        return true;
                    case R.id.navigation_dashboard:
                        //禁用侧滑功能
                        changeFragment(R.id.frame_content,new RecommendFragment());
//                        ((DrawerLayout)findViewById(R.id.drawer_layout))
//                                .setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        setActionBarTitle(-1,2);
                        return true;
                    case R.id.navigation_coaches:
                        //禁用侧滑功能
                        changeFragment(R.id.frame_content,new CoachsFragment());
//                        ((DrawerLayout)findViewById(R.id.drawer_layout))
//                                .setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        setActionBarTitle(-1,3);
                        return true;
                    case R.id.navigation_find:
                        //禁用侧滑功能
                        changeFragment(R.id.frame_content,new FindFragment());
//                        ((DrawerLayout)findViewById(R.id.drawer_layout))
//                                .setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        setActionBarTitle(-1,4
                        );
                        return true;
                }
                return false;
            };

    /**
     * 切换片段所用方法
     * @param currentLayout 当前页面布局
     * @param targetFragment  目标片段
     */
    public void changeFragment(int currentLayout, Fragment targetFragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(currentLayout,targetFragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public boolean onKeyDown(int keyNode, KeyEvent event){
//        if (JCVideoPlayer.backPress()) { return false; }
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_content);
        /*
         *判断当前位置
         *若是处于推荐页，则需要进行页面返回，再处理退出
          * 若是处于其他页面，则在两秒内双击两次返回键退出程序
         */
        if(fragment instanceof RecommendFragment){
            WebView webView = fragment.getView().findViewById(R.id.recommed_web);
            if(webView.canGoBack()){
                webView.goBack();
            } else if(!isExist){
                isExist = true;
                Toast.makeText(navigationActivity.this,"再按一次退出应用",Toast.LENGTH_LONG).show();
                //延迟两秒发送信息给handler
                exit_handler.sendEmptyMessageDelayed(0,2000);
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        }
        else if(fragment instanceof ImagePagerFragment){
            String nnn = "";
            return false;
        }

        //对其余界面的处理
        else{
            if(!isExist) {
                isExist = true;
                Toast.makeText(navigationActivity.this, "再按一次退出应用", Toast.LENGTH_LONG).show();
                exit_handler.sendEmptyMessageDelayed(0, 2000);
            }
            else{
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        }
        return false;
    }

    private void setActionBarTitle(int position, int page_num){
        Toolbar toolbar = findViewById(R.id.toolbar);
        if(page_num == 1){
            if(position == -1){
                toolbar.setTitle("主页");
                return;
            }
            toolbar.setTitle(titles[position]);
        }else if(page_num == 2){
            toolbar.setTitle("推荐");
        }else if(page_num == 3){
            toolbar.setTitle("教练");
        }else
            toolbar.setTitle("发现");


    }

    public void onClickLogout(View view){

        BmobUser.logOut();

        Intent intent = new Intent(navigationActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, currentPosition);
        outState.putSerializable("user",curr_user);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onSaveInstanceState(inState);
        this.curr_user = (User) inState.getSerializable("user");

    }

    @Override
    protected void onStart() {
        super.onStart();
        //if(navigationView != null)
            //
    }
    public void Call(View view){

        Intent intent=new Intent(Intent.ACTION_DIAL);
        Uri data=Uri.parse("tel:"+"18801279687");
        intent.setData(data);
        startActivity(intent);

    }

    public void doSendSMSTo(View view){
//        if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)){
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+"18801279687"));
            intent.putExtra("sms_body", "张永平傻吊");
            startActivity(intent);
//        }
    }
    public void sendEmail(View view){
        Intent data=new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:948525147@qq.com"));
        data.putExtra(Intent.EXTRA_SUBJECT, "这是标题");
        data.putExtra(Intent.EXTRA_TEXT, "这是内容");
        startActivity(data);
    }


}
