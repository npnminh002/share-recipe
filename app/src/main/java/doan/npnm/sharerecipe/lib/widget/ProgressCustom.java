package doan.npnm.sharerecipe.lib.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import doan.npnm.sharerecipe.R;

public class ProgressCustom extends BaseChart {
    private int defBackground;
    private int defTint;
    private float maxVal;
    private float minVal;
    private float currentVal;
    private boolean isAutoAnimation;
    private boolean isGradient = false;
    private int startColor = Color.parseColor("#2196F3");
    private int endColor = Color.parseColor("#9C27B0");

    private int timeAnim = 5;
    private ValueAnimator progressAnimator;

    public ProgressCustom(Context context) {
        super(context);
        init();
    }

    public ProgressCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        getTypedArr(context, attrs, 0);
    }

    public ProgressCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        getTypedArr(context, attrs, defStyleAttr);
    }

    private void init() {
        defBackground = Color.GRAY;
        defTint = Color.BLUE;
        maxVal = 100.0f;
        minVal = 0.0f;
        currentVal = 50.0f;
        isAutoAnimation = false;
    }

    @Override
    protected void getTypedArr(Context context, AttributeSet attrs, int defStyleAttr) {
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressCustom);
        defBackground = typedArray.getColor(R.styleable.ProgressCustom_progressBackground, defBackground);
        defTint = typedArray.getColor(R.styleable.ProgressCustom_progressTint, defTint);
        maxVal = typedArray.getFloat(R.styleable.ProgressCustom_maxValue, maxVal);
        minVal = typedArray.getFloat(R.styleable.ProgressCustom_minValue, minVal);
        currentVal = typedArray.getFloat(R.styleable.ProgressCustom_currentValue, currentVal);
        isAutoAnimation = typedArray.getBoolean(R.styleable.ProgressCustom_autoAnimate, isAutoAnimation);
        isGradient = typedArray.getBoolean(R.styleable.ProgressCustom_gradient, isGradient);
        startColor = typedArray.getColor(R.styleable.ProgressCustom_gradientStart, startColor);
        endColor = typedArray.getColor(R.styleable.ProgressCustom_gradientEnd, endColor);

        typedArray.recycle();

        if (isAutoAnimation) {
            startAutoAnimation();
        }
    }

    public interface OnProgressListener {
        void onEnd();

        void onProgress(float progess);
    }

    private OnProgressListener progressListener;

    public ProgressCustom progressEvent(OnProgressListener event) {
        this.progressListener = event;
        invalidate();
        return this;
    }

    private boolean isRestart = false;

    public ProgressCustom restartAnim(boolean restart) {
        this.isRestart = restart;
        invalidate();
        return this;
    }

    private void startAutoAnimation() {
        if (progressAnimator != null && progressAnimator.isRunning()) {
            progressAnimator.cancel();
        }

        progressAnimator = ValueAnimator.ofFloat(minVal, maxVal);
        progressAnimator.setDuration(timeAnim * 1000);
        progressAnimator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            if (!isRestart) {
                if (animatedValue == 100) {
                    progressAnimator.cancel();
                    progressListener.onEnd();
                }

            }
            setCurrentValue(animatedValue);
        });

        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (progressListener != null && !progressAnimator.isRunning()) {
                    progressListener.onEnd();
                }
            }
        });

        progressAnimator.setRepeatMode(ValueAnimator.RESTART);
        progressAnimator.setRepeatCount(ValueAnimator.INFINITE);

        progressAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
    }

    public ProgressCustom endAnimate() {
        if (progressAnimator != null) {
            progressAnimator.cancel();
            progressListener.onEnd();
        }
        return this;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        drawProgress(canvas);
    }


    private void drawProgress(Canvas canvas) {
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(defBackground);
        backgroundPaint.setStyle(Paint.Style.FILL);
        drawRoundRectPath(canvas, new RectF(0, 0, width, height), height / 2, true, true, true, true, backgroundPaint);

        Paint progressPaint = new Paint();
        if (isGradient) {
            Shader shader = new LinearGradient(0, 0, width, height, new int[]{startColor, endColor}, null, Shader.TileMode.CLAMP);
            progressPaint.setShader(shader);
        } else {
            progressPaint.setColor(defTint);
        }
        progressPaint.setStyle(Paint.Style.FILL);

        float progressWidth = calculateProgressWidth();
        float progressPercentage = calculateProgressPercentage();

        if (currentVal != 0) {
            if (progressListener != null) {
                progressListener.onProgress(progressPercentage);
            }
            drawRoundRectPath(canvas, new RectF(0, 0, progressWidth, height), height / 2, true, true, true, true, progressPaint);
        }
    }

    private float calculateProgressWidth() {
        float percentage = (currentVal - minVal) / (maxVal - minVal);
        return getWidth() * percentage;
    }

    private float calculateProgressPercentage() {
        return (currentVal - minVal) / (maxVal - minVal) * 100;
    }

    public ProgressCustom timeAnim(int second) {
        this.timeAnim = second;
        invalidate();
        return this;
    }

    public ProgressCustom gradientTint(int startColor, int endColor) {
        isGradient = true;
        this.startColor = startColor;
        this.endColor = endColor;
        invalidate();
        return this;
    }

    public ProgressCustom setMaxValue(float maxValue) {
        this.maxVal = maxValue;
        invalidate();
        return this;
    }

    public ProgressCustom setMinValue(float minValue) {
        this.minVal = minValue;
        invalidate();
        return this;
    }

    public ProgressCustom setCurrentValue(float currentValue) {
        this.currentVal = currentValue;
        invalidate();
        return this;
    }


}
