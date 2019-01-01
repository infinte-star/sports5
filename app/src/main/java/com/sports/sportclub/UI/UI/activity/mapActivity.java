package com.sports.sportclub.UI.UI.activity;


import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBaiduNaviManager;

//注意AlertDialog不能导入v7的包，不然会报找不到资源的错误
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;


import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.sports.sportclub.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.sports.sportclub.overlayutil.PoiOverlay;
import com.sports.sportclub.overlayutil.OverlayManager;
import com.sports.sportclub.overlayutil.BikingRouteOverlay;
import com.sports.sportclub.overlayutil.DrivingRouteOverlay;
import com.sports.sportclub.overlayutil.WalkingRouteOverlay;

import static cn.bmob.v3.Bmob.getApplicationContext;


/**
     * A simple {@link AppCompatActivity} subclass.
 */
public class mapActivity extends AppCompatActivity {

    //baiduMap的View
    private MapView mMapView;
    //地图实体
    private BaiduMap mBaiduMap;
    //当前标记
    private BitmapDescriptor mCurrentMarker;
    //定位模式分为三种
    private MyLocationConfiguration.LocationMode mCurrentMode;
    public LocationClient mLocationClient = null;
    //自定义定位监听器
    public BDAbstractLocationListener myListener = new MyLocationListener();
    //Poi检索
    private PoiSearch mPoiSearch = null;
    //步行路线规划
    private RoutePlanSearch mSearch;
    //弹出单选框
    AlertDialog dialog;
    //地图模式
    enum MAP_MODE{
        MAP_NORMAL, MAP_SATELLITE, MAP_NONE
    }
    //当前地图模式
    private MAP_MODE cur_map_mode = MAP_MODE.MAP_NORMAL;
    //路线规划方式
    enum ROUTE_MODE{
        walking, riding, driving
    }
    //当前出行方式
    private ROUTE_MODE cur_route_mode = ROUTE_MODE.walking;
    //路线规划方式
    enum LOCATION_MODE{
        NORMAL, FOLLOWING, COMPASS
    }
    //当前出行方式
    private LOCATION_MODE cur_location_mode = LOCATION_MODE.NORMAL;

    //三种覆盖物
    private WalkingRouteOverlay walkingRouteOverlay = null;
    private BikingRouteOverlay bikingRouteOverlay = null;
    private DrivingRouteOverlay drivingRouteOverlay = null;

    //热力图开关
    boolean Hot_Map_Open = false;

    //sug检索
    private SuggestionSearch mSuggestionSearch;
    //补全输入
    private AutoCompleteTextView autoCompleteTextView;
    //补全适配器
    private ArrayAdapter arrayAdapter;


    //我好笨
    String position;
    public mapActivity() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        EditText position_text = findViewById(R.id.target);

        Button searchBtn = findViewById(R.id.buttonserach);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPoiSearch = PoiSearch.newInstance();
                position = position_text.getText().toString();
                mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
                mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city("北京")
                        .keyword(position)
                        .pageNum(10));
            }
        });
        Button routeBtn = findViewById(R.id.buttonroute);
        routeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String start = "西二旗地铁站";
                String end = "百度科技园";
                String city = "北京";
                if (city == null || city.equals("")) {
                    city = "北京";
                }
                routePlan(start, end, city);

            }
        });

        autoCompleteTextView = findViewById(R.id.endpoint);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String endPoint = autoCompleteTextView.getText().toString();
                mSuggestionSearch = SuggestionSearch.newInstance();
                mSuggestionSearch.setOnGetSuggestionResultListener(suglistener);
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                    .keyword(endPoint)
                        .city("北京"));
            }
        });


        mMapView = findViewById(R.id.mmap);


        mBaiduMap = mMapView.getMap();
        //mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;//定位跟随态
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;   //默认为 LocationMode.NORMAL 普通态
        //mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;  //定位罗盘态

        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        initLocation();
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();

//        mSuggestionSearch = SuggestionSearch.newInstance();
//        mSuggestionSearch.setOnGetSuggestionResultListener(suglistener);
//        mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
//                .keyword("百度")
//                .city("北京"));


        Button switchBtn = findViewById(R.id.buttonswitch);
        switchBtn.setOnClickListener(v -> {
            switch (cur_location_mode) {
                case NORMAL:
                    cur_location_mode = LOCATION_MODE.COMPASS;
                    mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                    break;
                case COMPASS:
                    cur_location_mode = LOCATION_MODE.FOLLOWING;
                    mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                    break;
                case FOLLOWING:
                    cur_location_mode = LOCATION_MODE.NORMAL;
                    mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                    break;
            }
        });

        Button mapmodeBtn = findViewById(R.id.buttonchoose_map_mode);
        mapmodeBtn.setOnClickListener(v -> {
            //初始化单选框
            createDialog();
            dialog.show();
        });

        Button hotmapBtn = findViewById(R.id.button_hot_map);
        hotmapBtn.setOnClickListener(v -> {
            if (Hot_Map_Open) {
                Hot_Map_Open = false;
                mBaiduMap.setTrafficEnabled(false);
            } else {
                Hot_Map_Open = true;
                mBaiduMap.setTrafficEnabled(true);
            }
        });

    }

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
        @Override
        public void onGetPoiResult(PoiResult result){
            //获取POI检索结果
            {
                //如果搜索到的结果不为空，并且没有错误
                if (result != null && result.error == PoiResult.ERRORNO.NO_ERROR) {
                    MyOverLay overlay = new MyOverLay(mBaiduMap, mPoiSearch);//这传入search对象，因为一般搜索到后，点击时方便发出详细搜索
                    //设置数据,这里只需要一步，
                    overlay.setData(result);
                    //添加到地图
                    overlay.addToMap();
                    //将显示视图拉倒正好可以看到所有POI兴趣点的缩放等级
                    overlay.zoomToSpan();//计算工具
                    //设置标记物的点击监听事件
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    //mPoiSearch.destroy();
                } else {
                    Toast.makeText(mapActivity.this, "搜索不到你需要的信息！", Toast.LENGTH_SHORT).show();
                }
            }
        }

        public void onGetPoiDetailResult(PoiDetailResult result){
            //获取Place详情页检索结果
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
        //获取步行线路规划结果
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(mapActivity.this, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                removeOverlay();
                walkingRouteOverlay = new WalkingRouteOverlay(
                        mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(walkingRouteOverlay);
                walkingRouteOverlay.setData(result.getRouteLines().get(0));
                walkingRouteOverlay.addToMap();
                walkingRouteOverlay.zoomToSpan();
                mSearch.destroy();
                //Toast.makeText(GuideActivity.this,"点击图标会有指示哦～",
                //        Toast.LENGTH_SHORT).show();
            }
        }
        //获取综合公共交通线路规划结果
        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
        }
        //获取**跨城**综合公共交通线路规划结果
        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
        }
        //获取驾车线路规划结果
        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(mapActivity.this, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            }
            if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                removeOverlay();
                drivingRouteOverlay = new DrivingRouteOverlay(
                        mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(drivingRouteOverlay);
                drivingRouteOverlay.setData(drivingRouteResult.getRouteLines().get(0));
                drivingRouteOverlay.addToMap();
                drivingRouteOverlay.zoomToSpan();
                mSearch.destroy();
                //Toast.makeText(GuideActivity.this,"点击图标会有指示哦～",
                //        Toast.LENGTH_SHORT).show();
            }
        }
        //室内路线规划结果
        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
        }
        //获取普通骑行路规划结果
        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
            if (bikingRouteResult == null || bikingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(mapActivity.this, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            }
            if (bikingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (bikingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                removeOverlay();
                bikingRouteOverlay = new BikingRouteOverlay(
                        mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(bikingRouteOverlay);
                bikingRouteOverlay.setData(bikingRouteResult.getRouteLines().get(0));
                bikingRouteOverlay.addToMap();
                bikingRouteOverlay.zoomToSpan();
                mSearch.destroy();
                //Toast.makeText(GuideActivity.this,"点击图标会有指示哦～",
                //        Toast.LENGTH_SHORT).show();
            }
        }
    };

    OnGetSuggestionResultListener suglistener = new OnGetSuggestionResultListener() {


        @Override
        public void onGetSuggestionResult(SuggestionResult msg) {
            // TODO Auto-generated method stub
            if (msg == null || msg.getAllSuggestions() == null) {
                Toast.makeText(mapActivity.this, "未检索到当前地址",Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> list = new ArrayList<>();
            List<SuggestionResult.SuggestionInfo> listinfo = new ArrayList<>();
            List<String> lists = new ArrayList<>();
            List<String> listjl = new ArrayList<>();

            if (list != null) {
                list.clear();
            }

            if (lists != null) {
                lists.clear();
            }

            if (listjl != null) {
                listjl.clear();
            }

            if (listinfo != null) {
                listinfo.clear();
            }


            for (SuggestionResult.SuggestionInfo info : msg.getAllSuggestions()) {
                if (info.pt == null) continue;
                Log.e("info.ccity", "info.city" + info.city + "info.district" + info.district + "info.key" + info.key);
                listinfo.add(info);
                list.add(info.key);
                lists.add(info.city + info.district + info.key);
                DecimalFormat df = new DecimalFormat("######0");
                String distance = df.format(DistanceUtil.getDistance(listinfo.get(0).pt, info.pt));
                listjl.add(distance);
            }

            arrayAdapter = new ArrayAdapter(
                    mapActivity.this,android.R.layout.simple_list_item_1,list);
            autoCompleteTextView.setAdapter(arrayAdapter);
//            adapter_list_Address = new Adapter_list_Address(getActivity(), list, lists, listjl);
//            lvAddress.setAdapter(adapter_list_Address);
//            adapter_list_Address.notifyDataSetChanged();
            if (listinfo.size() == 0) {
                Toast.makeText(mapActivity.this, "未检索到当前地址", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    private void initLocation(){

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span=1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        // option.setIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        //  option.setWifiValidTime(5*60*1000);
        //可选，7.2版本新增能力，如果您设置了这个接口，首次启动定位时，会先判断当前WiFi是否超出有效期，超出有效期的话，会先重新扫描WiFi，然后再定位

        mLocationClient.setLocOption(option);
    }

    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            //获取定位结果
            StringBuffer sb = new StringBuffer(256);

            sb.append("time : ");
            sb.append(location.getTime());    //获取定位时间

            sb.append("\nerror code : ");
            sb.append(location.getLocType());    //获取类型类型

            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());    //获取纬度信息

            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());    //获取经度信息

            sb.append("\nradius : ");
            sb.append(location.getRadius());    //获取定位精准度


            if (location.getLocType() == BDLocation.TypeGpsLocation) {

                // GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());    // 单位：公里每小时

                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());    //获取卫星数

                sb.append("\nheight : ");
                sb.append(location.getAltitude());    //获取海拔高度信息，单位米

                sb.append("\ndirection : ");
                sb.append(location.getDirection());    //获取方向信息，单位度

                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\ndescribe : ");
                sb.append("gps定位成功");


            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {

                // 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\noperationers : ");
                sb.append(location.getOperators());    //获取运营商信息

                sb.append("\ndescribe : ");
                sb.append("网络定位成功");

            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

                // 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");

            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");

            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");


            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());    //位置语义化信息
            List<Poi> list = location.getPoiList();    // POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            mBaiduMap.setMyLocationData(locData);

            // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
            mCurrentMarker = BitmapDescriptorFactory
                    .fromResource(R.drawable.position);

            MyLocationConfiguration config = new MyLocationConfiguration(
                    mCurrentMode, true, mCurrentMarker);
            mBaiduMap.setMyLocationConfiguration(config);

            Log.i("BaiduLocationApiDem", sb.toString());
        }
    }

    public class MyOverLay extends PoiOverlay {
        /**
         * 构造函数
         */
        PoiSearch poiSearch;
        public MyOverLay(BaiduMap baiduMap, PoiSearch poiSearch) {
            super(mBaiduMap);
            this.poiSearch = poiSearch;
        }
        /**
         * 覆盖物被点击时
         */
        @Override
        public boolean onPoiClick(int i) {
            //获取点击的标记物的数据
            PoiInfo poiInfo = getPoiResult().getAllPoi().get(i);
            Log.e("TAG", poiInfo.name + "   " + poiInfo.address + "   " + poiInfo.phoneNum);
            //  发起一个详细检索,要使用uid
            poiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(poiInfo.uid));
            return true;
        }
    }

//    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {
//        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
//            super(baiduMap);
//        }
//        @Override
//        public BitmapDescriptor getStartMarker() {
//            if (true) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.position2);
//            }
//            return null;
//        }
//        @Override
//        public BitmapDescriptor getTerminalMarker() {
//            if (true) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.position2);
//            }
//            return null;
//        }
//    }
//
//    private class MyBikingRouteOverlay extends BikingRouteOverlay {
//        public MyBikingRouteOverlay(BaiduMap baiduMap) {
//            super(baiduMap);
//        }
//        @Override
//        public BitmapDescriptor getStartMarker() {
//            if (true) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.position2);
//            }
//            return null;
//        }
//        @Override
//        public BitmapDescriptor getTerminalMarker() {
//            if (true) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.position2);
//            }
//            return null;
//        }
//    }
//
//    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {
//        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
//            super(baiduMap);
//        }
//        @Override
//        public BitmapDescriptor getStartMarker() {
//            if (true) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.position2);
//            }
//            return null;
//        }
//        @Override
//        public BitmapDescriptor getTerminalMarker() {
//            if (true) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.position2);
//            }
//            return null;
//        }
//    }

    public void routePlan(String start, String end, String city) {
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(listener);
        // 起点与终点
        PlanNode stNode = PlanNode.withCityNameAndPlaceName(city, start);
        PlanNode enNode = PlanNode.withCityNameAndPlaceName(city, end);

       // DistanceUtil.getDistance()

        // 路线规划
        createRouteDialog(stNode,enNode);
        dialog.show();
    }


    @Override
    public void onStop()
    {
        super.onStop();
        // 停止定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBaiduMap.clear();
        mMapView.onDestroy();
        if(mPoiSearch != null)
            mPoiSearch.destroy();
        mMapView = null;
    }

    public void createDialog(){
        final String items[] = {"普通地图", "卫星图", "空白地图"};
        dialog = new AlertDialog.Builder(mapActivity.this)
                .setIcon(R.drawable.position)//设置标题的图片
                .setTitle("请选择地图模式")//设置对话框的标题
                .setSingleChoiceItems(items, 0, (dialog, which) -> {
                    switch (which){
                        case 0:
                            cur_map_mode = MAP_MODE.MAP_NORMAL;
                            break;
                        case 1:
                            cur_map_mode = MAP_MODE.MAP_SATELLITE;
                            break;
                        case 2:
                            cur_map_mode = MAP_MODE.MAP_NONE;
                            break;
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("确定", (dialog, which) -> {
                    switch (cur_map_mode){
                        case MAP_NORMAL:
                            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                            break;
                        case MAP_SATELLITE:
                            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                            break;
                        case MAP_NONE:
                            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
                            break;
                    }
                    dialog.dismiss();
                }).create();
    }

    public void createRouteDialog(PlanNode stNode, PlanNode enNode){
        final String items[] = {"步行", "骑行", "驾车"};
        dialog = new AlertDialog.Builder(mapActivity.this)
                .setIcon(R.drawable.position)//设置标题的图片
                .setTitle("请选择出行模式")//设置对话框的标题
                .setSingleChoiceItems(items, 0, (dialog, which) -> {
                    switch (which){
                        case 0:
                            cur_route_mode = ROUTE_MODE.walking;
                            break;
                        case 1:
                            cur_route_mode = ROUTE_MODE.riding;
                            break;
                        case 2:
                            cur_route_mode = ROUTE_MODE.driving;
                            break;
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("确定", (dialog, which) -> {
                    switch (cur_route_mode){
                        case walking:
                            mSearch.walkingSearch(new WalkingRoutePlanOption().from(
                                    stNode).to(enNode));
                            break;
                        case riding:
                            mSearch.bikingSearch(new BikingRoutePlanOption().from(
                                    stNode).to(enNode));
                            break;
                        case driving:
                            mSearch.drivingSearch(new DrivingRoutePlanOption().from(
                                    stNode).to(enNode));
                            break;
                    }
                    dialog.dismiss();
                }).create();
    }

    private void removeOverlay(){
        if(walkingRouteOverlay != null)
            walkingRouteOverlay.removeFromMap();
        if(bikingRouteOverlay != null)
            bikingRouteOverlay.removeFromMap();
        if(drivingRouteOverlay != null)
            drivingRouteOverlay.removeFromMap();
    }

}
