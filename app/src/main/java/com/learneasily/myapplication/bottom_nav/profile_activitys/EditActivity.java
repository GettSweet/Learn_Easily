package com.learneasily.myapplication.bottom_nav.profile_activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.learneasily.myapplication.R;

public class EditActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editSurname;
    private Button editBtn;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Инициализация элементов интерфейса, аутентификации и базы данных Firebase
        editName = findViewById(R.id.edit_name);
        editSurname = findViewById(R.id.edit_surname);
        editBtn = findViewById(R.id.edit_btn);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        // Обработчик нажатия на кнопку

        Button backButton = findViewById(R.id.edit_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Получение ID текущего пользователя
                String userId = auth.getCurrentUser().getUid();

                // Получение новых значений из полей
                String newName = editName.getText().toString();
                String newSurname = editSurname.getText().toString();


                // Проверка наличия значений и обновление данных в базе данных Firebase
                if (!newName.isEmpty()) {
                    databaseReference.child("Users").child(userId).child("name").setValue(newName);
                }
                if (!newSurname.isEmpty()) {
                    databaseReference.child("Users").child(userId).child("surname").setValue(newSurname);
                }

                onBackPressed();

            }
        });
    }
}