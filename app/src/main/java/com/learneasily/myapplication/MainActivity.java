package com.learneasily.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.learneasily.myapplication.adapter.AdapterViewPager;
import com.learneasily.myapplication.bottom_nav.FragmentLearn;
import com.learneasily.myapplication.bottom_nav.FragmentNews;
import com.learneasily.myapplication.bottom_nav.FragmentTasks;
import com.learneasily.myapplication.bottom_nav.profile_activitys.FragmentProfile;
import com.learneasily.myapplication.start_activitys.LoginActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 pagerMain;
    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private BottomNavigationView bntview;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация компонентов
        pagerMain = findViewById(R.id.pagerMain);
        bntview = findViewById(R.id.bntview);

        // Добавление фрагментов в список
        fragmentArrayList.add(new FragmentNews());
        fragmentArrayList.add(new FragmentLearn());
        fragmentArrayList.add(new FragmentTasks());
        fragmentArrayList.add(new FragmentProfile());

        // Настройка адаптера ViewPager
        AdapterViewPager adapterViewPager = new AdapterViewPager(this, fragmentArrayList);
        pagerMain.setAdapter(adapterViewPager);

        // Синхронизация ViewPager с BottomNavigationView
        pagerMain.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bntview.setSelectedItemId(R.id.home);
                        break;
                    case 1:
                        bntview.setSelectedItemId(R.id.learn);
                        break;
                    case 2:
                        bntview.setSelectedItemId(R.id.tasks);
                        break;
                    case 3:
                        bntview.setSelectedItemId(R.id.profile);
                        break;
                }
                super.onPageSelected(position);
            }
        });

        // Настройка обработки нажатий в BottomNavigationView
        bntview.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    pagerMain.setCurrentItem(0);
                } else if (itemId == R.id.learn) {
                    pagerMain.setCurrentItem(1);
                } else if (itemId == R.id.tasks) {
                    pagerMain.setCurrentItem(2);
                } else if (itemId == R.id.profile) {
                    pagerMain.setCurrentItem(3);
                }
                return true;
            }
        });
    }
}
