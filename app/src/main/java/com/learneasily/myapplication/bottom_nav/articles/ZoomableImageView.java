package com.learneasily.myapplication.bottom_nav.articles;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.ScaleGestureDetector;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatImageView;

public class ZoomableImageView extends AppCompatImageView {

    private Matrix matrix;
    private float[] matrixValues;

    private float currentScale = 1f; // Текущий масштаб
    private static final float MIN_SCALE = 1f; // Минимальный масштаб
    private static final float MAX_SCALE = 5f; // Максимальный масштаб

    private ScaleGestureDetector scaleGestureDetector;

    public ZoomableImageView(Context context) {
        super(context);
        init();
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZoomableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        matrix = new Matrix();
        matrixValues = new float[9];
        setScaleType(ScaleType.MATRIX);

        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();

            // Обновляем текущий масштаб
            float newScale = currentScale * scaleFactor;

            // Ограничиваем масштаб
            if (newScale > MAX_SCALE) {
                scaleFactor = MAX_SCALE / currentScale;
            } else if (newScale < MIN_SCALE) {
                scaleFactor = MIN_SCALE / currentScale;
            }

            // Применяем масштаб
            matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            currentScale *= scaleFactor;

            // Убедимся, что изображение остаётся в пределах экрана
            fixTranslation();

            // Устанавливаем обновлённую матрицу
            setImageMatrix(matrix);

            return true;
        }
    }

    private void fixTranslation() {
        RectF bounds = getTransformedRect();
        float dx = 0, dy = 0;

        // Ограничиваем перемещение по горизонтали
        if (bounds.width() > getWidth()) {
            if (bounds.left > 0) dx = -bounds.left;
            if (bounds.right < getWidth()) dx = getWidth() - bounds.right;
        } else {
            dx = (getWidth() - bounds.width()) / 2 - bounds.left;
        }

        // Ограничиваем перемещение по вертикали
        if (bounds.height() > getHeight()) {
            if (bounds.top > 0) dy = -bounds.top;
            if (bounds.bottom < getHeight()) dy = getHeight() - bounds.bottom;
        } else {
            dy = (getHeight() - bounds.height()) / 2 - bounds.top;
        }

        // Применяем сдвиги
        matrix.postTranslate(dx, dy);

        // Устанавливаем матрицу
        setImageMatrix(matrix);
    }

    private RectF getTransformedRect() {
        RectF rect = new RectF();
        if (getDrawable() != null) {
            rect.set(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }
}
