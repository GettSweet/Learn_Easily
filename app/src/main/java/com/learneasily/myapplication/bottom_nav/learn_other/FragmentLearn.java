package com.learneasily.myapplication.bottom_nav.learn_other;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import com.learneasily.myapplication.R;
import com.learneasily.myapplication.bottom_nav.learn_other.CouplesScheduleActivity;

public class FragmentLearn extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn, container, false);

        Button couplesScheduleButton = view.findViewById(R.id.couples_schedule);
        couplesScheduleButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CouplesScheduleActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
