package com.learneasily.myapplication.bottom_nav.articles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.learneasily.myapplication.R;
import com.learneasily.myapplication.adapter.ArticleAdapter;
import com.learneasily.myapplication.api.ApiClient;
import com.learneasily.myapplication.api.ApiService;
import com.learneasily.myapplication.api.ArticleResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentNews extends Fragment {

    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Устанавливаем слушатель для свайпа
        swipeRefreshLayout.setOnRefreshListener(this::fetchArticles);

        // Загружаем статьи
        fetchArticles();
    }

    private void fetchArticles() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getArticles().enqueue(new Callback<List<ArticleResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ArticleResponse>> call, @NonNull Response<List<ArticleResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new ArticleAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                }
                swipeRefreshLayout.setRefreshing(false); // Завершаем анимацию обновления
            }

            @Override
            public void onFailure(@NonNull Call<List<ArticleResponse>> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false); // Завершаем анимацию обновления
                // Обработка ошибки
            }
        });
    }
}
