package com.learneasily.myapplication.bottom_nav.profile_activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.learneasily.myapplication.R;
import com.learneasily.myapplication.api.ApiService;
import com.learneasily.myapplication.api.AppConfig;
import com.learneasily.myapplication.api.RetrofitClient;
import com.learneasily.myapplication.api.UserResponse;
import com.learneasily.myapplication.databinding.FragmentProfileBinding;
import com.learneasily.myapplication.start_activitys.LoginActivity;
import com.yalantis.ucrop.UCrop;

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
    private TextView teacherTextView;
    private FragmentProfileBinding binding;
    private ImageButton go_stngs;
    private Handler handler = new Handler();
    private Runnable periodicUpdate;
    private TextView idTextView;
    private Runnable dataUpdater;
    private static final int UPDATE_INTERVAL = 10000;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setupUI(view);

        handler = new Handler();
        dataUpdater = new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                int userId = sharedPreferences.getInt("user_id", -1);
                if (userId != -1) {
                    idTextView.setText("id: " + userId);
                    loadUserData(userId);
                }
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
        handler.post(dataUpdater);

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(() -> {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt("user_id", -1);
            if (userId != -1) {
                loadUserData(userId);
            }
            swipeRefreshLayout.setRefreshing(false);
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(periodicUpdate);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && dataUpdater != null) {
            handler.removeCallbacks(dataUpdater);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.profileImageView.setOnClickListener(v -> {
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

    private void startCropActivity(Uri sourceUri) {
        File avatarsDir = new File(Environment.getExternalStorageDirectory(), "avatars");
        if (!avatarsDir.exists()) {
            avatarsDir.mkdirs(); // создаем директорию, если она не существует
        }
        File destinationFile = new File(avatarsDir, "cropped_image.jpg");
        Uri destinationUri = Uri.fromFile(destinationFile);

        UCrop uCrop = UCrop.of(sourceUri, destinationUri);

        // Настройки кадрирования
        UCrop.Options options = new UCrop.Options();
        options.setToolbarTitle("Кадрирование");
        options.setFreeStyleCropEnabled(false); // Свободное кадрирование
        options.setShowCropFrame(true); // Показываем рамку кадрирования
        options.setShowCropGrid(false); // Отключаем сетку
        options.setHideBottomControls(true); // Скрываем нижние контролы (кнопки изменения формы)

        // Кастомные цвета
        options.setToolbarColor(ContextCompat.getColor(getContext(), R.color.appleBlue));
        options.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.appleDarkBlue));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(getContext(), R.color.appleLightBlue));
        options.setCropGridColor(ContextCompat.getColor(getContext(), R.color.appleLightBlue));
        options.setCropFrameColor(ContextCompat.getColor(getContext(), R.color.appleDarkBlue));

        uCrop.withAspectRatio(1, 1);

        uCrop.withOptions(options);
        uCrop.start(getActivity());
    }



    private void loadUserData(int userId) {
        idTextView.setText("id: " + userId);
        ApiService apiService = RetrofitClient.getClient(AppConfig.BASE_URL).create(ApiService.class);
        ProgressBar progressBar = binding.loadingProgressBar;
        apiService.getUserDetails(userId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();
                    nameSurnameTextView.setText(user.getName() + " " + user.getSurname());
                    emailTextView.setText(user.getEmail());

                    if (user.isTeacher()) {
                        teacherTextView.setVisibility(View.VISIBLE);
                        teacherTextView.setText("Вы преподаватель");
                    } else {
                        teacherTextView.setVisibility(View.VISIBLE);
                        teacherTextView.setText("Вы студент группы " + user.getGroup());
                    }

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
                progressBar.setVisibility(View.GONE);
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
            Toast.makeText(getContext(), "Не удалось получить ID пользователя", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        String filePath = getRealPathFromURI(avatarUri);
        if (filePath == null) {
            Toast.makeText(getContext(), "Не удалось получить путь к файлу", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        File file = new File(filePath);

        // Создание параметров
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));
        MultipartBody.Part avatarPart = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

        ApiService apiService = RetrofitClient.getClient(AppConfig.BASE_URL).create(ApiService.class);
        apiService.uploadAvatar(userIdPart, avatarPart).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Аватар успешно загружен", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Ошибка загрузки аватара: " + response.code(), Toast.LENGTH_LONG).show();
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
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.logout_confirmation_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            performLogout();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void performLogout() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void setupUI(View view) {
        nameSurnameTextView = view.findViewById(R.id.name_surname);
        emailTextView = view.findViewById(R.id.email_profile);
        teacherTextView = view.findViewById(R.id.teacher_profile);
        idTextView = view.findViewById(R.id.id_profile);

        ImageButton logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());

        ImageButton goStngsButton = view.findViewById(R.id.go_settings);
        goStngsButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SettingsActivity.class);
            startActivity(intent);
        });

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("user_name", "Имя");
        String surname = sharedPreferences.getString("user_surname", "Фамилия");
        String email = sharedPreferences.getString("user_email", "Email");
        String teacher = sharedPreferences.getString("teacher_email", "Преподаватель");


        nameSurnameTextView.setText(name + " " + surname);
        emailTextView.setText(email);
        teacherTextView.setText(teacher);

        nameSurnameTextView.setVisibility(View.VISIBLE);
        teacherTextView.setVisibility(View.VISIBLE);
        emailTextView.setVisibility(View.VISIBLE);
    }
}