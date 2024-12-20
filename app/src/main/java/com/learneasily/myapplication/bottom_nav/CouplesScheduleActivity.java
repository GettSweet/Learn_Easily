package com.learneasily.myapplication.bottom_nav;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.learneasily.myapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CouplesScheduleActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    private final String[] mondayAndThursdaySchedule = {
            "09:00:00-09:45:00", "09:50:00-10:35:00", "10:40:00-11:25:00", "11:35:00-12:20:00",
            "12:45:00-13:30:00", "13:40:00-14:25:00",
            "14:30:00-15:15:00", "15:25:00-16:10:00", "16:15:00-17:00:00"
    };
    private final String[] tuesdayWednesdayFridaySchedule = {
            "09:00:00-09:45:00", "09:50:00-10:35:00", "10:45:00-11:30:00", "11:35:00-12:20:00",
            "12:45:00-13:30:00", "13:35:00-14:20:00",
            "14:30:00-15:15:00", "15:20:00-16:05:00", "16:10:00-16:55:00", "17:00:00-17:45:00"
    };

    private final String[] saturdaySchedule = {
            "09:00:00-10:10:00", "10:20:00-11:30:00", "11:40:00-12:50:00", "13:00:00-14:10:00"
    };

    private Runnable updateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_couples_schedule_activity);

        TextView currentCoupleStatus = findViewById(R.id.current_couple_status);
        TextView nextCoupleStatus = findViewById(R.id.next_couple_status);
        TextView breakTime = findViewById(R.id.break_time);
        ImageButton backButton = findViewById(R.id.back_button);
        ImageButton openScheduleButton = findViewById(R.id.open_schedule_button);
        openScheduleButton.setOnClickListener(v -> showScheduleBottomSheet());

        backButton.setOnClickListener(v -> onBackPressed());

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateSchedule(currentCoupleStatus, nextCoupleStatus, breakTime);
                handler.postDelayed(this, 1000); // Обновляем каждую секунду
            }
        };
        handler.post(updateRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
    }

    private void showScheduleBottomSheet() {
        // Создаем BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_schedule, null);

        LinearLayout scheduleContainer = bottomSheetView.findViewById(R.id.schedule_container);

        // Дни недели и расписание
        String[] days = {"Понедельник и Четверг", "Вторник, Среда, Пятница", "Суббота"};
        String[][] schedules = {mondayAndThursdaySchedule, tuesdayWednesdayFridaySchedule, saturdaySchedule};

        for (int i = 0; i < days.length; i++) {
            // Карточка дня недели
            LinearLayout dayCard = new LinearLayout(this);
            dayCard.setOrientation(LinearLayout.VERTICAL);
            dayCard.setPadding(16, 16, 16, 16);
            dayCard.setBackground(getResources().getDrawable(R.drawable.card_background_2));
            dayCard.setElevation(4f);
            LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardLayoutParams.setMargins(8, 8, 8, 8); // Отступы для карточек
            dayCard.setLayoutParams(cardLayoutParams);

            // Заголовок дня
            TextView dayTitle = new TextView(this);
            dayTitle.setText(days[i]);
            dayTitle.setTextSize(18);
            dayTitle.setTextColor(getResources().getColor(R.color.purple_800));
            dayTitle.setTypeface(ResourcesCompat.getFont(this, R.font.montserrat_semibold));
            dayTitle.setPadding(8, 8, 8, 8); // Внутренние отступы заголовка
            dayCard.addView(dayTitle);

            // Список пар
            for (String timeRange : schedules[i]) {
                TextView timeItem = new TextView(this);
                timeItem.setText(formatTimeRange(timeRange));
                timeItem.setTextSize(16);
                timeItem.setTextColor(getResources().getColor(R.color.gray1));
                timeItem.setTypeface(ResourcesCompat.getFont(this, R.font.montserrat_light));
                timeItem.setPadding(8, 4, 8, 4); // Внутренние отступы текста
                dayCard.addView(timeItem);
            }

            // Добавляем карточку в контейнер
            scheduleContainer.addView(dayCard);

            // Разделитель между днями
            if (i < days.length - 1) {
                View divider = new View(this);
                LinearLayout.LayoutParams dividerLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        12
                );
                dividerLayoutParams.setMargins(0, 8, 0, 8); // Отступы для разделителя
                divider.setLayoutParams(dividerLayoutParams);
                divider.setBackgroundColor(getResources().getColor(R.color.transparent));
                scheduleContainer.addView(divider);
            }
        }

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


    private void updateSchedule(TextView currentCouple, TextView nextCouple, TextView breakTime) {
        Calendar now = Calendar.getInstance();
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);

        String[] schedule = getScheduleByDay(dayOfWeek);
        if (schedule == null) {
            currentCouple.setText("Сегодня выходной!");
            nextCouple.setText("");
            breakTime.setText("");
            return;
        }

        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(now.getTime());

        for (int i = 0; i < schedule.length; i++) {
            String[] timeRange = schedule[i].split("-");
            String startTime = timeRange[0].trim();
            String endTime = timeRange[1].trim();

            if (isTimeInRange(currentTime, startTime, endTime)) {
                currentCouple.setText("Текущий урок: " + formatTimeRange(schedule[i]));
                if (i < schedule.length - 1) {
                    nextCouple.setText("Следующий урок: " + formatTimeRange(schedule[i + 1]));
                } else {
                    nextCouple.setText("Это последний урок на сегодня.");
                }
                breakTime.setText("До конца урока: " + calculateTimeDifference(currentTime, endTime));
                return;
            }

            if (currentTime.compareTo(startTime) < 0) {
                currentCouple.setText("Сейчас нет пар.");
                nextCouple.setText("Следующий урок: " + formatTimeRange(schedule[i]));
                breakTime.setText("Перерыв: " + calculateTimeDifference(currentTime, startTime));
                return;
            }
        }

        currentCouple.setText("Сегодня пары закончились.");
        nextCouple.setText("");
        breakTime.setText("");
    }

    private String[] getScheduleByDay(int dayOfWeek) {
        if (dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.THURSDAY) {
            return mondayAndThursdaySchedule;
        } else if (dayOfWeek == Calendar.TUESDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.FRIDAY) {
            return tuesdayWednesdayFridaySchedule;
        } else if (dayOfWeek == Calendar.SATURDAY) {
            return saturdaySchedule;
        }
        return null;
    }

    private boolean isTimeInRange(String currentTime, String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Calendar current = Calendar.getInstance();
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();

            current.setTime(sdf.parse(currentTime));
            start.setTime(sdf.parse(startTime));
            end.setTime(sdf.parse(endTime));

            return !current.before(start) && !current.after(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String formatTimeRange(String timeRange) {
        String[] parts = timeRange.split("-");
        if (parts.length == 2) {
            String start = parts[0].trim().substring(0, 5); // HH:mm
            String end = parts[1].trim().substring(0, 5);   // HH:mm
            return start + "-" + end;
        }
        return timeRange;
    }

    private String calculateTimeDifference(String currentTime, String targetTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Calendar current = Calendar.getInstance();
            Calendar target = Calendar.getInstance();

            current.setTime(sdf.parse(currentTime));
            target.setTime(sdf.parse(targetTime));

            if (current.after(target)) {
                return "00:00:00";
            }

            long difference = (target.getTimeInMillis() - current.getTimeInMillis()) / 1000;

            long hours = difference / 3600;
            long minutes = (difference % 3600) / 60;
            long seconds = difference % 60;

            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "00:00:00";
    }

    private String getFullScheduleText() {
        StringBuilder fullSchedule = new StringBuilder();
        fullSchedule.append("Понедельник и Четверг:\n");
        for (String slot : mondayAndThursdaySchedule) {
            fullSchedule.append(formatTimeRange(slot)).append("\n");
        }
        fullSchedule.append("\nВторник, Среда, Пятница:\n");
        for (String slot : tuesdayWednesdayFridaySchedule) {
            fullSchedule.append(formatTimeRange(slot)).append("\n");
        }
        fullSchedule.append("\nСуббота:\n");
        for (String slot : saturdaySchedule) {
            fullSchedule.append(formatTimeRange(slot)).append("\n");
        }
        return fullSchedule.toString();
    }
}
