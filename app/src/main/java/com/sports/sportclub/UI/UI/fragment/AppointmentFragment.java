package com.sports.sportclub.UI.UI.fragment;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.sports.sportclub.R;
import com.sports.sportclub.UI.UI.activity.mapActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.framed.FrameReader;


/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentFragment extends Fragment {


    //sug检索
    private SuggestionSearch mSuggestionSearch;
    //补全输入
    private AutoCompleteTextView autoCompleteTextView;
    //补全适配器
    private ArrayAdapter arrayAdapter;

    List<String> list = new ArrayList<>();
    List<SuggestionResult.SuggestionInfo> listinfo = new ArrayList<>();
    List<String> lists = new ArrayList<>();
    List<String> listjl = new ArrayList<>();


    Button datapickBtn,timepickBtn;

    public AppointmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);
//        Button findButton = view.findViewById(R.id.find_button);
//        findButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment fragment = new HomeFragment();
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.replace(R.id.frame_content,fragment);
//                ft.addToBackStack(null);
//                ft.commit();
//            }
//        });




        autoCompleteTextView = view.findViewById(R.id.autoinput);
//        String [] content = new String[]{"123","456","789"};
//        List<String> test = new ArrayList<>();
//        test.add("123");
//        test.add("1234");
//        test.add("12345");
//        ArrayAdapter arrayAdapter = new ArrayAdapter(
//                getActivity(),android.R.layout.simple_list_item_1,test);
//        autoCompleteTextView.setAdapter(arrayAdapter);
        arrayAdapter = new ArrayAdapter(
                getActivity(),android.R.layout.simple_list_item_1);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String endPoint = autoCompleteTextView.getText().toString();
                mSuggestionSearch = SuggestionSearch.newInstance();
                mSuggestionSearch.setOnGetSuggestionResultListener(suglistener);
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(endPoint)
                        .city("北京"));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        autoCompleteTextView.setAdapter(arrayAdapter);

        datapickBtn = view.findViewById(R.id.button2);
        timepickBtn = view.findViewById(R.id.button3);

        datapickBtn.setOnClickListener(v -> {
            DatePickerDialog datePicker=new DatePickerDialog(getContext(), (view1, year, monthOfYear, dayOfMonth) ->

                Toast.makeText(getActivity(), year+"year "+(monthOfYear+1)+"month "+dayOfMonth+"day", Toast.LENGTH_SHORT).show()
                              , 2013, 7, 20);
                    datePicker.show();

        });

        timepickBtn.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(getContext(), (view12, hourOfDay, minute) -> Toast.makeText(getActivity(), hourOfDay+"hour "+minute+"minute", Toast.LENGTH_SHORT).show(),18,25,true);
            timePicker.show();


        });







        return view;
    }

    OnGetSuggestionResultListener suglistener = new OnGetSuggestionResultListener() {


        @Override
        public void onGetSuggestionResult(SuggestionResult msg) {
            // TODO Auto-generated method stub
            if (msg == null || msg.getAllSuggestions() == null) {
                Toast.makeText(getActivity(), "未检索到当前地址",Toast.LENGTH_SHORT).show();
                return;
            }

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
            arrayAdapter.clear();
            for (SuggestionResult.SuggestionInfo info : msg.getAllSuggestions()) {
                if (info.pt == null) continue;
                Log.e("info.ccity", "info.city" + info.city + "info.district" + info.district + "info.key" + info.key);
                listinfo.add(info);
                list.add(info.key);
                lists.add(info.city + info.district + info.key);
                DecimalFormat df = new DecimalFormat("######0");
                String distance = df.format(DistanceUtil.getDistance(listinfo.get(0).pt, info.pt));
                listjl.add(distance);
                arrayAdapter.add(info.key);
            }

            arrayAdapter.notifyDataSetChanged();


//            adapter_list_Address = new Adapter_list_Address(getActivity(), list, lists, listjl);
//            lvAddress.setAdapter(adapter_list_Address);
//            adapter_list_Address.notifyDataSetChanged();
            if (listinfo.size() == 0) {
                Toast.makeText(getActivity(), "未检索到当前地址", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    //处理退出信息的Handler
    Handler mhandler = new Handler(){

        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            arrayAdapter = new ArrayAdapter(
                    getActivity(),android.R.layout.simple_list_item_1,list);
            autoCompleteTextView.setAdapter(arrayAdapter);
        }

    };
}
