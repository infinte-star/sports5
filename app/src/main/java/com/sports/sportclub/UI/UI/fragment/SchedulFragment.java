package com.sports.sportclub.UI.UI.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sports.sportclub.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SchedulFragment extends Fragment {


    public SchedulFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedul, container, false);
    }

}
