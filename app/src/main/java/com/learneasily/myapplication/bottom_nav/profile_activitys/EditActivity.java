package com.learneasily.myapplication.bottom_nav.profile_activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import com.learneasily.myapplication.R;

public class EditActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editSurname;
    private Button editBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Инициализация элементов интерфейса, аутентификации и базы данных Firebase
        editName = findViewById(R.id.edit_name);
        editSurname = findViewById(R.id.edit_surname);
        editBtn = findViewById(R.id.edit_btn);

        // Обработчик нажатия на кнопку

        Button backButton = findViewById(R.id.edit_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}