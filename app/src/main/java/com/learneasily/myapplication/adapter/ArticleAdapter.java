package com.learneasily.myapplication.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

        // Установка относительного времени
        holder.create_time.setText(getRelativeTime(article.getCreate_Time()));

        holder.author.setText(String.format("%s %s", article.getAuthorName(), article.getAuthorSurname()));

        // Обработка хештегов и ссылок в содержимом статьи
        String contentText = article.getContent();
        SpannableString spannableContent = new SpannableString(contentText);

        // Поиск хештегов (#)
        Pattern hashtagPattern = Pattern.compile("#\\w+");
        Matcher hashtagMatcher = hashtagPattern.matcher(contentText);

        while (hashtagMatcher.find()) {
            int start = hashtagMatcher.start();
            int end = hashtagMatcher.end();
            spannableContent.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableContent.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Toast.makeText(widget.getContext(), "Хештег: " + spannableContent.subSequence(start, end), Toast.LENGTH_SHORT).show();
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // Поиск ссылок (http:// или https://)
        Pattern urlPattern = Patterns.WEB_URL;
        Matcher urlMatcher = urlPattern.matcher(contentText);

        while (urlMatcher.find()) {
            int start = urlMatcher.start();
            int end = urlMatcher.end();
            spannableContent.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableContent.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    String url = spannableContent.subSequence(start, end).toString();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    widget.getContext().startActivity(intent);
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        holder.content.setText(spannableContent);
        holder.content.setMovementMethod(LinkMovementMethod.getInstance());

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



    public static String getRelativeTime(String isoTime) {
        try {
            // Создаем форматтер для ISO 8601
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Устанавливаем часовой пояс UTC
            Date serverDate = isoFormat.parse(isoTime);

            if (serverDate == null) return "Некорректное время";

            // Получаем текущее время в UTC
            long currentTimeMillis = System.currentTimeMillis();

            // Разница времени в миллисекундах
            long diffInMillis = currentTimeMillis - serverDate.getTime();

            long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis);
            if (seconds < 60) return "только что";

            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
            if (minutes < 60) return minutes + " минут назад";

            long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
            if (hours < 24) return hours + " часов назад";

            long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
            if (days < 7) return days + " дней назад";

            SimpleDateFormat dateFormatter;
            if (days < 365) {
                dateFormatter = new SimpleDateFormat("d MMMM", Locale.getDefault());
            } else {
                dateFormatter = new SimpleDateFormat("d MMMM yyyy 'года'", Locale.getDefault());
            }

            // Возвращаем форматированную дату
            return dateFormatter.format(serverDate);
        } catch (ParseException e) {
            return "Некорректное время";
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
        TextView create_time, author, content;
        GridLayout imageGrid;
        ImageView authorAvatar;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            create_time = itemView.findViewById(R.id.create_time);
            author = itemView.findViewById(R.id.articleAuthor);
            content = itemView.findViewById(R.id.articleContent);
            imageGrid = itemView.findViewById(R.id.imageGrid);
            authorAvatar = itemView.findViewById(R.id.authorAvatar);
        }
    }
}
