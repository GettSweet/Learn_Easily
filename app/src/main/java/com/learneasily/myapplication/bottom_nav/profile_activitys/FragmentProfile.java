package com.learneasily.myapplication.bottom_nav.profile_activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.learneasily.myapplication.R;
import com.learneasily.myapplication.databinding.FragmentProfileBinding;

import okhttp3.OkHttpClient;

public class FragmentProfile extends Fragment {

    private TextView nameSurnameTextView;
    private TextView emailTextView;
    private Button editButton;
    private FragmentProfileBinding binding;
    private ImageButton go_stngs;
    private Uri filePath;
    private final OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "http://95.189.105.66:8000";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setupUI(view);

        return view;
    }

    private void setupUI(View view) {
        nameSurnameTextView = view.findViewById(R.id.name_surname);
        emailTextView = view.findViewById(R.id.email_profile);

        final ProgressBar loadingProgressBar = view.findViewById(R.id.loadingProgressBar);
        editButton = view.findViewById(R.id.edit_btn);
        go_stngs = view.findViewById(R.id.go_settings);

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditActivity.class);
            startActivity(intent);
        });

        go_stngs.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });
    }
}
