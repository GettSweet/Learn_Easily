package com.learneasily.myapplication.start_activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.learneasily.myapplication.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText email_register;
    private EditText password_register;
    private EditText name_register;
    private EditText surname_register;
    private Button btn_register;

    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ImageButton yourImageButton = findViewById(R.id.backLogin);

        yourImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        email_register = findViewById(R.id.email_register);
        password_register = findViewById(R.id.password_register);
        name_register = findViewById(R.id.name_register);
        surname_register = findViewById(R.id.surname_register);
        btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_register.getText().toString();
                String password = password_register.getText().toString();
                String name = name_register.getText().toString();
                String surname = surname_register.getText().toString();

                if (email.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Не все поля заполнены", Toast.LENGTH_LONG).show();
                } else if (!isValidEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "Некорректный формат почты", Toast.LENGTH_LONG).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Добавляем информацию в базу данных
                                        String uid = mAuth.getCurrentUser().getUid();
                                        ref.child("Users").child(uid).child("email").setValue(email);
                                        ref.child("Users").child(uid).child("password").setValue(password);
                                        ref.child("Users").child(uid).child("name").setValue(name);
                                        ref.child("Users").child(uid).child("surname").setValue(surname);
                                        ref.child("Users").child(uid).child("profileImage").setValue("");

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(RegisterActivity.this, "Повторите попытку", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
    // Метод для проверки правильности формата электронной почты
    private boolean isValidEmail(String email) {
        // Используем класс Patterns для проверки формата почты
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}