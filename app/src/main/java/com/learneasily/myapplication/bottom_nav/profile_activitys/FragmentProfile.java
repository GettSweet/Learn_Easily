package com.learneasily.myapplication.bottom_nav.profile_activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.learneasily.myapplication.R;
import com.learneasily.myapplication.api.ApiService;
import com.learneasily.myapplication.api.RetrofitClient;
import com.learneasily.myapplication.api.UserResponse;
import com.learneasily.myapplication.databinding.FragmentProfileBinding;

import org.jetbrains.annotations.Nullable;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentProfile extends Fragment {

    private TextView nameSurnameTextView;
    private TextView emailTextView;
    private Button editButton;
    private FragmentProfileBinding binding;
    private ImageButton go_stngs;
    private static final String BASE_URL = "http://192.168.0.10:8000";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setupUI(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.uploadAvatarButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId != -1) {
            loadUserData(userId);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            uploadAvatar(selectedImageUri);
        }
    }

    private void loadUserData(int userId) {
        ApiService apiService = RetrofitClient.getClient(BASE_URL).create(ApiService.class);

        apiService.getUserDetails(userId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();

                    nameSurnameTextView.setText(user.getName() + " " + user.getSurname());
                    emailTextView.setText(user.getEmail());

                    Glide.with(requireContext())
                            .load(user.getAvatarUrl())
                            .placeholder(R.drawable.placeholder_avatar)
                            .error(R.drawable.error_avatar)
                            .into(binding.profileImageView);
                } else {
                    Toast.makeText(getContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadAvatar(Uri avatarUri) {
        ProgressBar progressBar = binding.loadingProgressBar;
        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(getContext(), "Не удалось получить ID пользователя", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        String filePath = getRealPathFromURI(avatarUri);
        if (filePath == null) {
            Toast.makeText(getContext(), "Не удалось получить путь к файлу", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        File file = new File(filePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part avatarPart = MultipartBody.Part.createFormData("avatar", file.getName(), requestBody);

        ApiService apiService = RetrofitClient.getClient(BASE_URL).create(ApiService.class);
        apiService.uploadAvatar(userId, avatarPart).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Аватар загружен", Toast.LENGTH_SHORT).show();
                    loadUserData(userId); // Обновление данных пользователя
                } else {
                    Toast.makeText(getContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = requireContext().getContentResolver().query(contentUri, proj, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void setupUI(View view) {
        nameSurnameTextView = view.findViewById(R.id.name_surname);
        emailTextView = view.findViewById(R.id.email_profile);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("user_name", "Имя");
        String surname = sharedPreferences.getString("user_surname", "Фамилия");
        String email = sharedPreferences.getString("user_email", "Email");

        nameSurnameTextView.setText(name + " " + surname);
        emailTextView.setText(email);

        nameSurnameTextView.setVisibility(View.VISIBLE);
        emailTextView.setVisibility(View.VISIBLE);
    }
}