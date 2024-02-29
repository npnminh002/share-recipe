package doan.npnm.sharerecipe.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import doan.npnm.sharerecipe.R;

public class CornerLinearLayout extends LinearLayout {

    private float cornerRadius;
    private int strokeColor;
    private int strokeWidth;
    private int[] gradientColors;

    public CornerLinearLayout(Context context) {
        this(context, null);
    }

    public CornerLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomLinearLayout);
        cornerRadius = typedArray.getDimension(R.styleable.CustomLinearLayout_cornerRadius,
                getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._5sdp));
        strokeColor = typedArray.getColor(R.styleable.CustomLinearLayout_strokeColor,
                ContextCompat.getColor(context, R.color.textcolor2));
        strokeWidth = typedArray.getDimensionPixelSize(R.styleable.CustomLinearLayout_strokeWidth,
                getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp));
        int gradientColorsResourceId = typedArray.getResourceId(R.styleable.CustomLinearLayout_gradientColors, 0);
        if (gradientColorsResourceId != 0) {
            gradientColors = getResources().getIntArray(gradientColorsResourceId);
        }
        typedArray.recycle();

        GradientDrawable backgroundDrawable = new GradientDrawable();
        //backgroundDrawable.setColor(ContextCompat.getColor(context, R.color.white));
        backgroundDrawable.setCornerRadius(cornerRadius);
        backgroundDrawable.setStroke(strokeWidth, strokeColor);
        if (gradientColors != null && gradientColors.length > 0) {
            backgroundDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            backgroundDrawable.setColors(gradientColors);
        }
        setBackground(backgroundDrawable);
    }

    public void setCornerRadius(float radius) {
        cornerRadius = radius;
        GradientDrawable backgroundDrawable = (GradientDrawable) getBackground();
        if (backgroundDrawable != null) {
            backgroundDrawable.setCornerRadius(radius);
        }
    }

    public void setStrokeColor(int color) {
        strokeColor = color;
        GradientDrawable backgroundDrawable = (GradientDrawable) getBackground();
        if (backgroundDrawable != null) {
            backgroundDrawable.setStroke(strokeWidth, strokeColor);
        }
    }

    public void setStrokeWidth(int width) {
        strokeWidth = width;
        GradientDrawable backgroundDrawable = (GradientDrawable) getBackground();
        if (backgroundDrawable != null) {
            backgroundDrawable.setStroke(width, strokeColor);
        }
    }

    public void setGradientColors(int[] colors) {
        gradientColors = colors;
        GradientDrawable backgroundDrawable = (GradientDrawable) getBackground();
        if (backgroundDrawable != null && gradientColors != null && gradientColors.length > 0) {
            backgroundDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            backgroundDrawable.setColors(gradientColors);
        }
    }
}
