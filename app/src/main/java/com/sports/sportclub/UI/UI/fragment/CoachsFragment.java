package com.sports.sportclub.UI.UI.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.sports.sportclub.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CoachsFragment extends Fragment {

    private ListView listView;
    private View view;

    public CoachsFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //获取当前View
        View view = inflater.inflate(R.layout.fragment_coachs,container,false);
        //再通过View获取ListView
        listView = view.findViewById(R.id.coach_list);
        //设置SimpleAdapter
        SimpleAdapter adapter=new SimpleAdapter(getActivity(),DataList(),R.layout.coach_item,
                new String[]{"coach_photo","coach_name","coach_introduction"},
                new int[]{R.id.coach_photo_image,R.id.coach_name_text,R.id.coach_introduction_text});


        listView.setAdapter(adapter);
        return view;
    }

    //填充数据列表
    public List<Map<String,Object>> DataList(){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("coach_photo",R.drawable.coach_6);
        map.put("coach_name","Sun Yang");
        map.put("coach_introduction","15810853703");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("coach_photo",R.drawable.coach_1);
        map.put("coach_name","Zhang Yongping");
        map.put("coach_introduction","15810853703");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("coach_photo",R.drawable.coach_2);
        map.put("coach_name","Li Xingyuan");
        map.put("coach_introduction","exercise coach");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("coach_photo",R.drawable.coach_3);
        map.put("coach_name","He Yalun");
        map.put("coach_introduction","basketball coach");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("coach_photo",R.drawable.coach_4);
        map.put("coach_name","Zhao Zihan");
        map.put("coach_introduction","tennis coach");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("coach_photo",R.drawable.coach_5);
        map.put("coach_name","Sun Shou");
        map.put("coach_introduction","fat coach");
        list.add(map);
        return list;
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        ListView listView = getActivity().findViewById(R.id.coach_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Intent.ACTION_DIAL);
                String phone = (String)DataList().get(position).get("coach_introduction");
                Uri data=Uri.parse("tel:"+phone);
                intent.setData(data);
                startActivity(intent);

            }
        });
    }

    public void Call(View view){

        Intent intent=new Intent(Intent.ACTION_DIAL);
        Uri data=Uri.parse("tel:"+"17735676828");
        intent.setData(data);
        startActivity(intent);

    }
}
