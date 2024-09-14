package com.learneasily.myapplication.bottom_nav;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learneasily.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentNews extends Fragment {

    private RecyclerView recyclerView;
    private ArticlesAdapter articlesAdapter;
    private List<Article> articlesList;
    private Button addArticleButton;

    public FragmentNews() {
        // Пустой конструктор
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);

        addArticleButton = rootView.findViewById(R.id.add_article_button);
        recyclerView = rootView.findViewById(R.id.recycler_view_articles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        articlesList = new ArrayList<>();
        articlesAdapter = new ArticlesAdapter(articlesList);
        recyclerView.setAdapter(articlesAdapter);

        // Проверка прав доступа пользователя
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());

            userRef.child("isTrusted").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean isTrusted = dataSnapshot.getValue(Boolean.class);
                    if (isTrusted != null && isTrusted) {
                        addArticleButton.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Обработка ошибок
                }
            });
        }

        // Загрузка данных о статьях из Firebase
        loadArticles();

        return rootView;
    }

    private void loadArticles() {
        DatabaseReference articlesRef = FirebaseDatabase.getInstance().getReference("Articles");

        articlesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                articlesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Article article = snapshot.getValue(Article.class);
                    articlesList.add(article);
                }
                articlesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок
            }
        });
    }
}
