package com.learneasily.myapplication.bottom_nav.profile_activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.learneasily.myapplication.R;
import com.learneasily.myapplication.databinding.FragmentProfileBinding;

import java.io.IOException;

public class FragmentProfile extends Fragment {

    private TextView nameSurnameTextView;
    private TextView emailTextView;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private Button editButton;
    private Uri filePath;
    private FragmentProfileBinding binding;
    private ImageButton go_stngs;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        loadUserInfo();

        setupUI(view);

        return view;
    }

    private void setupUI(View view) {
        binding.profileImageView.setOnClickListener(v -> selectImage());
        nameSurnameTextView = view.findViewById(R.id.name_surname);
        emailTextView = view.findViewById(R.id.email_profile); // Инициализация emailTextView

        final ProgressBar loadingProgressBar = view.findViewById(R.id.loadingProgressBar);
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        editButton = view.findViewById(R.id.edit_btn);
        go_stngs = view.findViewById(R.id.go_settings);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                handleDataChange(dataSnapshot, loadingProgressBar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Обработка ошибок при чтении данных из Firebase
            }
        });

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditActivity.class);
            startActivity(intent);
        });

        go_stngs.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void handleDataChange(DataSnapshot dataSnapshot, ProgressBar loadingProgressBar) {
        String name = dataSnapshot.child("name").getValue(String.class);
        String surname = dataSnapshot.child("surname").getValue(String.class);
        String email = dataSnapshot.child("email").getValue(String.class); // Добавлено получение email

        loadingProgressBar.setVisibility(View.VISIBLE);
        nameSurnameTextView.setVisibility(View.INVISIBLE);
        emailTextView.setVisibility(View.INVISIBLE);

        if (name != null && surname != null) {
            String fullName = name + " " + surname;
            nameSurnameTextView.setText(fullName);
        }

        if (email != null) {
            emailTextView.setText(email);
        }

        // Если и nameSurnameTextView, и emailTextView не пусты, скрываем ProgressBar
        if (name != null && surname != null && email != null) {
            loadingProgressBar.setVisibility(View.INVISIBLE);
            nameSurnameTextView.setVisibility(View.VISIBLE);
            emailTextView.setVisibility(View.VISIBLE);
        }
    }

    public void loadUserInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        handleUserInfo(snapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Обработка ошибок при чтении данных из Firebase
                    }
                });
    }

    private void handleUserInfo(DataSnapshot snapshot) {
        String profileImage = snapshot.child("profileImage").getValue(String.class);

        if (profileImage != null && !profileImage.isEmpty()) {
            Glide.with(requireContext()).load(profileImage).into(binding.profileImageView);
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> pickActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    handleImageSelectionResult(result);
                }
            });

    private void handleImageSelectionResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData() != null) {
            filePath = result.getData().getData();

            try {
                handleImageBitmapSelection(MediaStore.Images.Media.getBitmap(
                        requireContext().getContentResolver(),
                        filePath
                ));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("ActivityResult", "Image selected, calling uploadImage()");
            uploadImage();
        }
    }

    private void handleImageBitmapSelection(Bitmap bitmap) {
        binding.profileImageView.setImageBitmap(bitmap);
    }

    private void uploadImage() {
        if (filePath != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseStorage.getInstance().getReference().child("images/" + uid)
                    .putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> handleImageUploadSuccess(uid))
                    .addOnFailureListener(e -> handleImageUploadFailure(e));
        }
    }

    private void handleImageUploadSuccess(String uid) {
        Toast.makeText(getContext(), "Фото успешно загружено", Toast.LENGTH_LONG).show();

        FirebaseStorage.getInstance().getReference().child("images/" + uid).getDownloadUrl()
                .addOnSuccessListener(uri -> handleImageDownloadSuccess(uri))
                .addOnFailureListener(e -> handleImageDownloadFailure(e));
    }

    private void handleImageUploadFailure(Exception e) {
        Log.e("UploadImage", "Failed to upload image: " + e.getMessage());
    }

    private void handleImageDownloadSuccess(Uri uri) {
        Log.d("UploadImage", "Image URL: " + uri.toString());

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .child("profileImage").setValue(uri.toString());
    }

    private void handleImageDownloadFailure(Exception e) {
        Log.e("UploadImage", "Failed to get image URL: " + e.getMessage());
    }
}
