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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class FragmentNews extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private View loadingView;
    private View errorView;
    private ArticleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        loadingView = view.findViewById(R.id.loading_view);
        errorView = view.findViewById(R.id.error_view);
        View retryButton = errorView.findViewById(R.id.retry_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout.setOnRefreshListener(this::fetchArticles);
        retryButton.setOnClickListener(v -> fetchArticles());

        fetchArticles();
    }

    private void fetchArticles() {
        showLoading(true);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getArticles().enqueue(new Callback<List<ArticleResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ArticleResponse>> call, @NonNull Response<List<ArticleResponse>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    showError(false);

                    adapter = new ArticleAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                } else {
                    showError(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ArticleResponse>> call, @NonNull Throwable t) {
                showLoading(false);
                showError(true);
            }
        });
    }

    private void showLoading(boolean isLoading) {
        loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        errorView.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showError(boolean show) {
        errorView.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}