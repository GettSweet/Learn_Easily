package com.learneasily.myapplication.adapter;

import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.learneasily.myapplication.R;
import com.learneasily.myapplication.api.ApiService;
import com.learneasily.myapplication.api.AppConfig;
import com.learneasily.myapplication.api.ArticleResponse;
import com.learneasily.myapplication.api.UserResponse;
import com.learneasily.myapplication.bottom_nav.articles.FullscreenImageActivity;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private final List<ArticleResponse> articles;
    private final SparseArray<UserResponse> userCache = new SparseArray<>();

    public ArticleAdapter(List<ArticleResponse> articles) {
        this.articles = articles;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        ArticleResponse article = articles.get(position);
        holder.title.setText(article.getTitle());
        holder.author.setText(String.format("%s %s", article.getAuthorName(), article.getAuthorSurname()));
        holder.content.setText(article.getContent());

        // Очищаем GridLayout перед заполнением
        holder.imageGrid.removeAllViews();

        List<String> imageUrls = null;
        if (article.getImages() != null) {
            imageUrls = article.getImages().stream()
                    .map(ArticleResponse.ArticleImage::getImage)
                    .collect(Collectors.toList());
        }

        // Ограничиваем количество отображаемых изображений
        if (imageUrls != null && !imageUrls.isEmpty()) {
            int maxImages = Math.min(imageUrls.size(), 10); // Максимум 10 изображений
            holder.imageGrid.setColumnCount(2);
            int rowCount = (int) Math.ceil(maxImages / 2.0); // Высчитываем количество строк
            holder.imageGrid.setRowCount(rowCount);

            for (int i = 0; i < maxImages; i++) {
                String imageUrl = imageUrls.get(i);
                String fullImageUrl = AppConfig.BASE_URL + imageUrl;

                ImageView imageView = new ImageView(holder.itemView.getContext());

                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.width = 0;
                layoutParams.height = 200; // Фиксированная высота
                layoutParams.columnSpec = GridLayout.spec(i % holder.imageGrid.getColumnCount(), 1f);
                layoutParams.rowSpec = GridLayout.spec(i / holder.imageGrid.getColumnCount(), 1f);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(layoutParams);
                Glide.with(holder.itemView.getContext())
                        .load(fullImageUrl)
                        .placeholder(R.drawable.error_image)
                        .error(R.drawable.error_image)
                        .override(200, 200) // Уменьшение размера для оптимизации
                        .centerCrop()
                        .into(imageView);

                holder.imageGrid.addView(imageView);

                // Открытие изображения в полноэкранном режиме при клике
                imageView.setOnClickListener(v -> {
                    Intent intent = new Intent(holder.itemView.getContext(), FullscreenImageActivity.class);
                    intent.putExtra("image_url", fullImageUrl);
                    holder.itemView.getContext().startActivity(intent);
                });
            }
        }

        // Получение аватарки автора
        int authorId = article.getAuthor();
        UserResponse cachedUser = userCache.get(authorId);
        if (cachedUser != null) {
            loadAuthorAvatar(holder, cachedUser.getAvatarUrl());
        } else {
            ApiService apiService = AppConfig.getApiService();
            apiService.getUserDetails(authorId).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UserResponse user = response.body();
                        userCache.put(authorId, user); // Кэшируем данные пользователя
                        loadAuthorAvatar(holder, user.getAvatarUrl());
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    Log.e("ArticleAdapter", "Ошибка при загрузке данных пользователя: " + t.getMessage());
                }
            });
        }
    }

    private void loadAuthorAvatar(ArticleViewHolder holder, String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(avatarUrl)
                    .placeholder(R.drawable.placeholder_avatar) // Плейсхолдер для аватарки
                    .error(R.drawable.error_avatar) // Изображение в случае ошибки
                    .circleCrop() // Круглая обрезка изображения
                    .into(holder.authorAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, content;
        GridLayout imageGrid;
        ImageView authorAvatar;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.articleTitle);
            author = itemView.findViewById(R.id.articleAuthor);
            content = itemView.findViewById(R.id.articleContent);
            imageGrid = itemView.findViewById(R.id.imageGrid);
            authorAvatar = itemView.findViewById(R.id.authorAvatar);
        }
    }
}
