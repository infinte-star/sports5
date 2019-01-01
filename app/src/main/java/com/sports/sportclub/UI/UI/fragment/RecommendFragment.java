package com.sports.sportclub.UI.UI.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sports.sportclub.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecommendFragment extends Fragment {

    private WebView web_view;
    private String Url = "https://www.baidu.com/";

    public RecommendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        web_view = view.findViewById(R.id.recommed_web);
        web_view.loadUrl(Url);
        initWebView();
        web_view.setWebViewClient(new WebViewClient());

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void initWebView(){
        WebSettings setting = web_view.getSettings();
        setting.setJavaScriptEnabled(true);//支持Js
        setting.setCacheMode(WebSettings.LOAD_DEFAULT);//缓存模式
        //是否支持画面缩放，默认不支持
        setting.setBuiltInZoomControls(true);
        setting.setSupportZoom(true);
        //是否显示缩放图标，默认显示
        setting.setDisplayZoomControls(false);
        //设置网页内容自适应屏幕大小
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);

    }

}
